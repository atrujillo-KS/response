package com.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.json.JsonSlurper
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

import java.nio.file.Files
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * GS_Validator - optimized + adds Studio debug modal (Run step / Continue / Stop)
 * and adds a "Loading stuck" watchdog for .lf-loading-text staying Loading > 6s.
 *
 * Controls:
 *  - GlobalVariable.debugging   (boolean) : enables debug behavior
 *  - GlobalVariable.pauseOnStep (boolean) : pauses after every step (Studio only, not headless)
 *  - GlobalVariable.isHeadless  (boolean) : headless mode disables modal UI
 *  - GlobalVariable.browser     (string)  : "chrome" or "firefox"
 *  - GlobalVariable.URL / GlobalVariable.clientId used for default URL build
 */
class MainValidator {

	// -------------------- Version --------------------
	public static final String VERSION = "1.6.1"

	// -------------------- Global retry settings for verification commands --------------------
	private static final int VERIFY_RETRY_TIMEOUT_MS = 5000
	private static final int VERIFY_RETRY_INTERVAL_MS = 200

	// -------------------- Per-thread run state (parallel-safe) --------------------
	private static final ThreadLocal<Boolean> TL_HAD_FAILURE   = ThreadLocal.withInitial { false }
	private static final ThreadLocal<Boolean> TL_LAST_FAILED   = ThreadLocal.withInitial { false }
	private static final ThreadLocal<String>  TL_LOG_FILE      = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_SHOT_PREFIX   = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_SHOT_DIR      = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_CALC_ID       = new ThreadLocal<>()
	private static final ThreadLocal<Integer> TL_SHOT_SEQ      = ThreadLocal.withInitial { 1 }

	// per-thread stored values for ${var} placeholders
	private static final ThreadLocal<Map<String,String>> TL_STORED = ThreadLocal.withInitial { [:] as Map<String,String> }

	// Loading watchdog
	private static final ThreadLocal<ScheduledExecutorService> TL_LOAD_WATCH = new ThreadLocal<>()
	private static final ThreadLocal<Long> TL_LOADING_SINCE_MS = ThreadLocal.withInitial { 0L }
	private static final ThreadLocal<Boolean> TL_LOADING_REPORTED = ThreadLocal.withInitial { false }
	private static final ThreadLocal<Long> TL_LOADING_LAST_HEARTBEAT_MS = ThreadLocal.withInitial { 0L }

	private static final ThreadLocal<ConcurrentLinkedQueue<String>> TL_WATCHDOG_LOGS =
	ThreadLocal.withInitial { new ConcurrentLinkedQueue<String>() }

	private static final ThreadLocal<Long> TL_LAST_FLUSH_MS =
	ThreadLocal.withInitial { 0L }

	// Main-thread modal request (set by watchdog thread, consumed by main)
	private static final ThreadLocal<String> TL_PENDING_LOADING_MODAL_MSG =
	ThreadLocal.withInitial { "" }

	// Retry tracking
	private static final ThreadLocal<Integer> TL_RETRY_ATTEMPT  = ThreadLocal.withInitial { 0 }
	private static final ThreadLocal<Integer> TL_RETRY_MAX      = ThreadLocal.withInitial { 3 }

	// Temp browser profile dir (for cleanup)
	private static final ThreadLocal<File> TL_TEMP_PROFILE_DIR = new ThreadLocal<>()

	// -------------------- Public helper for Test Cases --------------------
	static boolean lastRunFailed() {
		return Boolean.TRUE.equals(TL_LAST_FAILED.get())
	}

	// -------------------- Safe GlobalVariable getter --------------------
	private static <T> T gv(String name, T defVal = null) {
		try {
			def v = internal.GlobalVariable."$name"
			if (v == null) return defVal
			if (v instanceof CharSequence && v.toString().trim().isEmpty()) return defVal
			return (T) v
		} catch (MissingPropertyException ignore) {
			return defVal
		}
	}

	private static boolean isHeadless() {
		return Boolean.TRUE.equals(gv("isHeadless", false))
	}
	private static boolean isDebugging() {
		return Boolean.TRUE.equals(gv("debugging", false))
	}
	private static boolean isPauseOnStep() {
		return Boolean.TRUE.equals(gv("pauseOnStep", false))
	}

	private static boolean isRunningInStudio() {
		try {
			def src = RunConfiguration.getExecutionSource()
			return (src != null && src.toString().toUpperCase().contains("STUDIO"))
		} catch (Throwable t) {
			return false
		}
	}

	// -------------------- Logging --------------------
	private static String calcId() {
		return TL_CALC_ID.get() ?: (GlobalVariable.calcId ?: "unknown")
	}
	private static void setCalcId(String v) {
		TL_CALC_ID.set(v ?: "unknown")
	}

	private static int nextShotSeq() {
		int n = TL_SHOT_SEQ.get()
		TL_SHOT_SEQ.set(n + 1)
		return n
	}

	private static String slug(String s) {
		if (!s) return "na"
		String out = s.replaceAll('[^a-zA-Z0-9._-]+', '_')
				.replaceAll('_+', '_')
				.replaceAll('^_+|_+\\$', '')
		return out ? out : "na"
	}

	private static String hexHash8(String src) {
		try {
			def md = java.security.MessageDigest.getInstance("MD5")
			md.update((src ?: "").getBytes("UTF-8"))
			byte[] d = md.digest()
			StringBuilder sb = new StringBuilder()
			for (int i = 0; i < 4; i++) sb.append(String.format("%02x", d[i]))
			return sb.toString()
		} catch (Throwable t) {
			return "00000000"
		}
	}

	/** <prefix>_<seq>_<step16>_<id32>_<hash8>.png */
	private static String buildShotName(String prefix, String step, String locator) {
		int seq = nextShotSeq()
		String stepPart = slug(step).take(16)
		String idPart   = slug(locator).take(32)
		String hash     = hexHash8(locator ?: step ?: "")
		return "${prefix}_${seq}_${stepPart}_${idPart}_${hash}.png"
	}

	private static void prepareLogging() {
		if (TL_LOG_FILE.get() != null && TL_SHOT_PREFIX.get() != null && TL_SHOT_DIR.get() != null) return

			String safeCalc = slug(calcId())
		String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
		String threadTag = "_t" + Thread.currentThread().getId()

		TL_LOG_FILE.set("${safeCalc}_${ts}${threadTag}.txt")
		TL_SHOT_PREFIX.set("screenshot_${ts}${threadTag}")

		String shotsDir = RunConfiguration.getProjectDir() + "/Screenshots/${safeCalc}${threadTag}"
		new File(shotsDir).mkdirs()
		TL_SHOT_DIR.set(shotsDir)

		String logDirPath = RunConfiguration.getProjectDir() + "/failedLogs"
		new File(logDirPath).mkdirs()

		println "✅ Logging initialized: ${TL_LOG_FILE.get()} (calcId=${safeCalc})"
	}

	private static void logStep(String message) {
		prepareLogging()
		println message
		try {
			def f = new File(RunConfiguration.getProjectDir() + "/failedLogs/${TL_LOG_FILE.get()}")
			f << (message + "\n")
		} catch (Throwable ignored) {}
	}

	// -------------------- NavigateTo helpers (ADDED) --------------------
	private static void hardReloadToUrl(String url) {
		try {
			WebDriver d = DriverFactory.getWebDriver()
			if (d != null) {
				d.navigate().to(url)
				WebUI.delay(0.8)
				return
			}
		} catch (Throwable t1) {
			// fall through
		}

		try {
			WebUI.executeJavaScript("window.location.replace(arguments[0]);", Arrays.asList(url))
			WebUI.delay(0.8)
		} catch (Throwable t2) {
			WebUI.navigateToUrl(url)
			WebUI.delay(0.8)
		}
	}

	private static void navigateToWithRetry(String url, int maxRetries = 3) {
		int attempt = 0
		while (attempt++ < maxRetries) {
			try {
				WebUI.comment("🌐 navigateTo attempt ${attempt}/${maxRetries} → ${url}")
				logStep("🌐 navigateTo attempt ${attempt}/${maxRetries} → ${url}")

				// Use a harder navigation than WebUI.navigateToUrl alone (handles some SPA/redirect oddities better)
				hardReloadToUrl(url)

				// reset loading episode state (prevents stale carryover across navigations)
				TL_LOADING_SINCE_MS.set(0L)
				TL_LOADING_REPORTED.set(false)
				TL_LOADING_LAST_HEARTBEAT_MS.set(0L)

				return
			} catch (Throwable e) {
				WebUI.comment("⚠ navigateTo failed: ${e.message}")
				logStep("⚠ navigateTo failed: ${e.message}")
				if (attempt >= maxRetries) throw e
				WebUI.delay(2)
			}
		}
	}

	// -------------------- Text normalization --------------------
	private static String normalize(String str) {
		if (str == null) return ""
		String s = Normalizer.normalize(str.toString(), Normalizer.Form.NFKC)
		s = s
				.replaceAll(/\u200B|\u200C|\u200D|\u2060|\uFEFF|\u00AD/, "")
				.replace('\u00A0' as char, ' ' as char)
				.replace("â€‹", "")
				.replaceAll(/[\t\n\r]+/, " ")
				.replaceAll(/\s+/, " ")
				.replaceAll(/\s+,/, ",")
				.replaceAll(/\s+\./, ".")
				.trim()
		return s
	}

	// -------------------- Placeholder resolution --------------------
	private static String evaluateJsDateExpression(String script) {
		if (!script?.contains("var d = new Date()")) return script

		Calendar cal = Calendar.getInstance()
		cal.setTime(new Date())

		int day = cal.get(Calendar.DAY_OF_MONTH)
		int month = cal.get(Calendar.MONTH) + 2
		int yearOffset = 1900
		def matcher = (script =~ /getYear\(\)\s*\+\s*(\d+)/)
		if (matcher.find()) yearOffset = matcher.group(1).toInteger()

		if (month > 12) {
			month -= 12
			cal.add(Calendar.YEAR, 1)
		}

		int year = cal.get(Calendar.YEAR) - 1900 + yearOffset
		String paddedDay = day < 10 ? "0${day}" : "${day}"
		String paddedMonth = month < 10 ? "0${month}" : "${month}"

		return "${paddedMonth}/${paddedDay}/${year}"
	}

	private static String resolvePlaceholders(String value) {
		if (!value?.contains("\${")) return value

		Map<String,String> stored = TL_STORED.get()
		int openIndex = -1
		int braceDepth = 0
		StringBuilder result = new StringBuilder()

		for (int i = 0; i < value.length(); i++) {
			if (value[i] == '$' && i + 1 < value.length() && value[i + 1] == '{') {
				openIndex = i
				braceDepth = 1
				i++
				continue
			}
			if (openIndex >= 0) {
				if (value[i] == '{') braceDepth++
				else if (value[i] == '}') {
					braceDepth--
					if (braceDepth == 0) {
						String placeholder = value.substring(openIndex + 2, i)
						String evaluated = stored.containsKey(placeholder) ? stored[placeholder] : evaluateJsDateExpression(placeholder)
						result.append(evaluated)
						openIndex = -1
						continue
					}
				}
				continue
			}
			result.append(value[i])
		}
		return result.toString()
	}

	// -------------------- TestObject builder --------------------
	private static TestObject buildTO(String locator) {
		TestObject to = new TestObject(locator ?: "")
		if (!locator) return to

		if (locator.startsWith("xpath=")) {
			to.addProperty("xpath", ConditionType.EQUALS, locator.replace("xpath=", ""))
		} else if (locator.startsWith("//")) {
			to.addProperty("xpath", ConditionType.EQUALS, locator)
		} else if (locator.startsWith("css=")) {
			to.addProperty("css", ConditionType.EQUALS, locator.replace("css=", ""))
		} else if (locator.startsWith("link=")) {
			to.addProperty("xpath", ConditionType.EQUALS, "//a[text()='${locator.replace("link=", "")}']")
		} else {
			to.addProperty("id", ConditionType.EQUALS, locator)
		}
		return to
	}

	/** Convert a Katalon TestObject back to a Selenium By locator */
	private static By toSeleniumBy(TestObject to) {
		def prop = to.getProperties()?.find { it.isActive() }
		if (!prop) return By.id(to.getObjectId())
		switch (prop.getName()) {
			case "xpath": return By.xpath(prop.getValue())
			case "css":   return By.cssSelector(prop.getValue())
			case "id":    return By.id(prop.getValue())
			default:      return By.xpath("//*[@${prop.getName()}='${prop.getValue()}']")
		}
	}

	// -------------------- Browser --------------------

	/** Hard cleanup: kill any stale driver, watchdog, and temp profile from a previous run */
	private static void forceCleanup() {
		// Stop watchdog first — it polls the driver
		try { stopLoadingWatchdog() } catch (Throwable ignored) {}

		// Kill stale driver
		try {
			WebDriver oldDriver = DriverFactory.getWebDriver()
			if (oldDriver != null) {
				try { oldDriver.quit() } catch (Throwable ignored) {}
			}
		} catch (Throwable ignored) {}
		try { WebUI.closeBrowser() } catch (Throwable ignored) {}

		// Delete previous temp profile dir
		deleteTempProfileDir()
	}

	private static void deleteTempProfileDir() {
		try {
			File dir = TL_TEMP_PROFILE_DIR.get()
			if (dir != null && dir.exists()) {
				dir.deleteDir()
			}
			TL_TEMP_PROFILE_DIR.remove()
		} catch (Throwable ignored) {}
	}

	private static void openBrowserWithHeadlessSupport() {
		boolean headless = isHeadless()
		String browserType = (gv("browser", "chrome") as String).toLowerCase()

		if (headless) {
			if (browserType == "firefox") {
				File profileDir = Files.createTempDirectory("ff-profile").toFile()
				TL_TEMP_PROFILE_DIR.set(profileDir)
				FirefoxOptions options = new FirefoxOptions()
				options.addArguments("--headless")
				options.addArguments("--width=1920")
				options.addArguments("--height=1080")
				options.addArguments("--profile")
				options.addArguments(profileDir.getAbsolutePath())
				DriverFactory.changeWebDriver(new FirefoxDriver(options))
			} else {
				ChromeOptions options = new ChromeOptions()
				options.addArguments("--headless=new")
				options.addArguments("--disable-gpu")
				options.addArguments("--window-size=1920,1080")
				options.addArguments("--no-sandbox")
				options.addArguments("--disable-dev-shm-usage")
				File tempUserDataDir = Files.createTempDirectory("chrome-user-data").toFile()
				TL_TEMP_PROFILE_DIR.set(tempUserDataDir)
				options.addArguments("--user-data-dir=" + tempUserDataDir.getAbsolutePath())
				DriverFactory.changeWebDriver(new ChromeDriver(options))
			}
		} else {
			WebUI.openBrowser("")
			try {
				WebUI.setViewPortSize(1200, 1080)
			} catch (ignored) {}
		}
	}

	// -------------------- Loading watchdog helpers --------------------
	private static void enqueueWatchdogLog(String msg) {
		try {
			TL_WATCHDOG_LOGS.get().offer(msg)
			// Background thread is fine for file IO
			logStep(msg)
		} catch (Throwable ignored) {}
	}

	/** Flush queued watchdog logs on the MAIN test thread so Katalon shows them */
	private static void flushWatchdogLogs(boolean force = false) {
		try {
			long now = System.currentTimeMillis()
			long last = TL_LAST_FLUSH_MS.get()
			if (!force && (now - last) < 250) return
				TL_LAST_FLUSH_MS.set(now)

			def q = TL_WATCHDOG_LOGS.get()
			while (true) {
				String msg = q.poll()
				if (!msg) break
					KeywordUtil.logInfo(msg)  // shows in Log Viewer reliably
				try {
					WebUI.comment(msg)
				} catch (ignored) {}
			}
		} catch (Throwable ignored) {}
	}

	private static boolean isLoadingVisibleNow(WebDriver driver) {
		try {
			if (driver == null) return false
			if (!(driver instanceof JavascriptExecutor)) return false

			String js = '''
				(function(){
				  try {
					function isVisible(el){
					  if(!el) return false;
					  const s = window.getComputedStyle(el);
					  if(!s) return false;
					  if(s.display==='none' || s.visibility==='hidden' || Number(s.opacity)===0) return false;
					  const r = el.getBoundingClientRect();
					  if(!r || r.width<=0 || r.height<=0) return false;
					  return true;
					}

					const nodes = document.querySelectorAll('.lf-loading-text');
					if(!nodes || !nodes.length) return false;

					for(let i=0;i<nodes.length;i++){
					  const el = nodes[i];
					  if(!isVisible(el)) continue;
					  let t = (el.innerText || el.textContent || '');
					  t = t.replace(/[\\u200B\\u200C\\u200D\\u2060\\uFEFF\\u00AD]/g,'').trim();
					  if(/^loading\\b/i.test(t)) return true;
					}
					return false;
				  } catch(e){ return false; }
				})();
			'''
			def out = ((JavascriptExecutor) driver).executeScript(js)
			return (out instanceof Boolean) ? ((Boolean) out) : false
		} catch (Throwable ignored) {
			return false
		}
	}

	private static String tryDriverScreenshot(String stepName, String locatorHint) {
		try {
			WebDriver driver = DriverFactory.getWebDriver()
			if (driver == null) return ""
			if (!(driver instanceof TakesScreenshot)) return ""

			prepareLogging()
			String fileName = buildShotName(TL_SHOT_PREFIX.get(), stepName ?: "loading", locatorHint ?: "lf-loading-text")
			String shotPath = "${TL_SHOT_DIR.get()}/${fileName}"

			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)
			new File(shotPath).parentFile.mkdirs()
			Files.copy(src.toPath(), new File(shotPath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
			return shotPath
		} catch (Throwable ignored) {
			return ""
		}
	}

	private static void startLoadingWatchdog() {
		if (TL_LOAD_WATCH.get() != null) return

			final long ownerThreadId = Thread.currentThread().getId()

		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor({ r ->
			Thread t = new Thread(r, "lf-loading-watchdog-ownerT" + ownerThreadId)
			t.setDaemon(true)
			return t
		} as java.util.concurrent.ThreadFactory)

		TL_LOAD_WATCH.set(ses)

		ses.scheduleAtFixedRate({
			try {
				WebDriver driver = DriverFactory.getWebDriver()
				if (driver == null) return

					boolean loadingNow = isLoadingVisibleNow(driver)
				long now = System.currentTimeMillis()

				if (loadingNow) {
					long since = TL_LOADING_SINCE_MS.get()

					// log the moment Loading first appears (once per episode)
					if (since <= 0L) {
						TL_LOADING_SINCE_MS.set(now)
						TL_LOADING_REPORTED.set(false)
						TL_LOADING_LAST_HEARTBEAT_MS.set(0L)

						String msg = "⏳ LOADING STARTED | .lf-loading-text visible | ts=" +
								new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())

						enqueueWatchdogLog(msg)
						return
					}

					long elapsed = now - since

					// heartbeat log (throttled to every 1s)
					long lastBeat = TL_LOADING_LAST_HEARTBEAT_MS.get()
					if (lastBeat <= 0L || (now - lastBeat) >= 1000L) {
						TL_LOADING_LAST_HEARTBEAT_MS.set(now)
						enqueueWatchdogLog("… loading heartbeat | elapsed=${elapsed}ms")
					}

					// stuck logic (once per episode)
					if (elapsed >= 6000L && !Boolean.TRUE.equals(TL_LOADING_REPORTED.get())) {
						TL_LOADING_REPORTED.set(true)

						String msg = "⚠ LOADING STUCK > 6s | .lf-loading-text visible ~${elapsed}ms | ts=" +
								new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())

						enqueueWatchdogLog(msg)

						String shot = tryDriverScreenshot("loading_stuck", "lf-loading-text")
						if (shot) enqueueWatchdogLog("📸 loading-stuck screenshot: ${shot}")

						// Don't show UI here; ask main thread to do it
						if (isRunningInStudio() && isDebugging() && !isHeadless()) {
							TL_PENDING_LOADING_MODAL_MSG.set(msg)
						}
					}
				} else {
					long since = TL_LOADING_SINCE_MS.get()

					// log the moment Loading clears (once per episode)
					if (since > 0L) {
						long elapsed = now - since
						String msg = "✅ LOADING CLEARED | duration=${elapsed}ms | ts=" +
								new SimpleDateFormat("HH:mm:ss.SSS").format(new Date())
						enqueueWatchdogLog(msg)
					}

					TL_LOADING_SINCE_MS.set(0L)
					TL_LOADING_REPORTED.set(false)
					TL_LOADING_LAST_HEARTBEAT_MS.set(0L)
				}
			} catch (Throwable ignored) {
				// watchdog must never crash the run
			}
		}, 250, 250, TimeUnit.MILLISECONDS)
	}

	private static void stopLoadingWatchdog() {
		try {
			def ses = TL_LOAD_WATCH.get()
			if (ses != null) ses.shutdownNow()
		} catch (ignored) {}

		TL_LOAD_WATCH.remove()
		TL_LOADING_SINCE_MS.remove()
		TL_LOADING_REPORTED.remove()
		TL_LOADING_LAST_HEARTBEAT_MS.remove()
		TL_WATCHDOG_LOGS.remove()
		TL_LAST_FLUSH_MS.remove()
		TL_PENDING_LOADING_MODAL_MSG.remove()
	}

	// -------------------- Studio pause helpers --------------------
	private static void studioPause(String message) {
		if (!isRunningInStudio()) return
			if (isHeadless()) return
			try {
				javax.swing.JOptionPane.showMessageDialog(
						null,
						message ?: "Paused",
						"GS_Validator",
						javax.swing.JOptionPane.INFORMATION_MESSAGE
						)
			} catch (Throwable ignored) {}
	}

	/**
	 * Studio-only modal: Run step / Continue / Stop
	 * IMPORTANT: "Stop" throws StepFailedException so the test case FAILS immediately.
	 */
	private static void studioPauseWithRunner(String title, String reason, String defaultCommand, String defaultId, String defaultValue, String toolId) {
		if (!isRunningInStudio()) return
			if (isHeadless()) return

			try {
				def cmdBox = new javax.swing.JComboBox([
					"verifyText",
					"setTextAndWait",
					"verifyValue"
				] as String[])
				cmdBox.setSelectedItem(defaultCommand ?: "verifyText")

				def toolIdField = new javax.swing.JTextField(toolId ?: "", 40)
				toolIdField.setEditable(false)

				def idField = new javax.swing.JTextField(defaultId ?: "", 40)

				def valueArea = new javax.swing.JTextArea(defaultValue ?: "", 6, 50)
				valueArea.setLineWrap(true)
				valueArea.setWrapStyleWord(true)
				def valueScroll = new javax.swing.JScrollPane(valueArea)

				def reasonArea = new javax.swing.JTextArea(reason ?: "", 8, 50)
				reasonArea.setEditable(false)
				reasonArea.setLineWrap(true)
				reasonArea.setWrapStyleWord(true)
				def reasonScroll = new javax.swing.JScrollPane(reasonArea)

				def panel = new javax.swing.JPanel(new java.awt.GridBagLayout())
				def gbc = new java.awt.GridBagConstraints()
				gbc.insets = new java.awt.Insets(6, 6, 6, 6)
				gbc.fill = java.awt.GridBagConstraints.HORIZONTAL

				gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0
				panel.add(new javax.swing.JLabel("Reason"), gbc)
				gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1
				panel.add(reasonScroll, gbc)

				gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0
				panel.add(new javax.swing.JLabel("ToolId"), gbc)
				gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1
				panel.add(toolIdField, gbc)

				gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0
				panel.add(new javax.swing.JLabel("Command"), gbc)
				gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1
				panel.add(cmdBox, gbc)

				gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0
				panel.add(new javax.swing.JLabel("id"), gbc)
				gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1
				panel.add(idField, gbc)

				gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0
				panel.add(new javax.swing.JLabel("value (expected)"), gbc)
				gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1
				panel.add(valueScroll, gbc)

				while (true) {
					int choice = javax.swing.JOptionPane.showOptionDialog(
							null,
							panel,
							title ?: "GS_Validator",
							javax.swing.JOptionPane.DEFAULT_OPTION,
							javax.swing.JOptionPane.INFORMATION_MESSAGE,
							null,
							[
								"Run step",
								"Continue",
								"Stop"
							] as Object[],
							"Continue"
							)

					if (choice == 0) {
						String cmd = cmdBox.getSelectedItem()?.toString()
						String loc = idField.getText()?.toString()
						String exp = valueArea.getText()?.toString()

						def res = runAdHocStep(cmd, loc, exp)

						String msg =
								"Ad-hoc result: " + (res.ok ? "PASS" : "FAIL") + "\n\n" +
								"Command: ${cmd}\n" +
								"Locator: ${loc}\n\n" +
								(res.error ? ("Error: ${res.error}\n\n") : "") +
								"Expected:\n${res.expected}\n\n" +
								"Actual:\n${res.actual}"

						javax.swing.JOptionPane.showMessageDialog(
								null,
								msg,
								"Ad-hoc step result",
								res.ok ? javax.swing.JOptionPane.INFORMATION_MESSAGE : javax.swing.JOptionPane.WARNING_MESSAGE
								)
						continue
					}

					if (choice == 2) {
						throw new StepFailedException("Stopped by user from Studio debug dialog")
					}

					return
				}
			} catch (StepFailedException sfe) {
				throw sfe
			} catch (Throwable t) {
				println "Pause requested but dialog unavailable: ${t.message}"
			}
	}

	// -------------------- Ad-hoc runner actions --------------------
	private static String readValue(TestObject to) {
		try {
			def el = WebUI.findWebElement(to, 2)
			if (el == null) return ""
			def v = WebUI.getAttribute(to, "value", FailureHandling.OPTIONAL)
			if (v != null && v.toString().trim() != "") return v.toString()
			def js = '''
				var el = arguments[0];
				if (!el) return '';
				if (typeof el.value !== 'undefined' && el.value !== null) return (''+el.value);
				return (el.innerText || el.textContent || '');
			'''
			return ((String) WebUI.executeJavaScript(js, [el]) ?: "").trim()
		} catch (Throwable t) {
			return ""
		}
	}

	// -------------------- Text retrieval with retries --------------------
	private static String getTextWithRetries(TestObject to, String expected = null, int timeoutMs = VERIFY_RETRY_TIMEOUT_MS, int intervalMs = VERIFY_RETRY_INTERVAL_MS) {
		String actualText = ""
		String normExpected = (expected != null) ? normalize(expected) : null
		long startMs = System.currentTimeMillis()
		long deadline = startMs + timeoutMs
		int attempt = 0

		while (System.currentTimeMillis() < deadline) {
			attempt++
			actualText = WebUI.getText(to, FailureHandling.OPTIONAL) ?: ""
			if (!actualText.trim()) {
				actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL) ?: ""
			}
			// Fallback: if getText() returned partial content (e.g. headless Chrome
			// strips child span text), use JS innerText which includes all descendants
			if (normExpected != null && normalize(actualText) != normExpected) {
				try {
					WebDriver driver = com.kms.katalon.core.webui.driver.DriverFactory.getWebDriver()
					WebElement el = driver.findElement(toSeleniumBy(to))
					String jsText = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText", el) ?: ""
					if (jsText.trim() && normalize(jsText) != normalize(actualText)) {
						actualText = jsText
					}
				} catch (Exception ignored) {}
			}

			long elapsed = System.currentTimeMillis() - startMs

			if (normExpected != null) {
				if (normalize(actualText) == normExpected) {
					if (attempt > 1) {
						String msg = "✅ getTextWithRetries matched on attempt ${attempt} (${elapsed}ms) | expected='${expected}' | actual='${actualText}'"
						logStep(msg)
						WebUI.comment(msg)
					}
					return actualText
				}
				String msg = "🔄 getTextWithRetries attempt ${attempt} (${elapsed}ms) | expected='${expected}' | actual='${actualText}'"
				logStep(msg)
				WebUI.comment(msg)
			} else {
				if (actualText.trim()) {
					if (attempt > 1) {
						String msg = "✅ getTextWithRetries got text on attempt ${attempt} (${elapsed}ms) | actual='${actualText}'"
						logStep(msg)
						WebUI.comment(msg)
					}
					return actualText
				}
				String msg = "🔄 getTextWithRetries attempt ${attempt} (${elapsed}ms) | waiting for any text"
				logStep(msg)
				WebUI.comment(msg)
			}

			Thread.sleep(intervalMs)
		}

		long elapsed = System.currentTimeMillis() - startMs
		String msg = "⏱ getTextWithRetries timed out after ${attempt} attempt(s) (${elapsed}ms) | expected='${expected ?: "(any)"}' | last actual='${actualText}'"
		logStep(msg)
		WebUI.comment(msg)

		return actualText
	}

	private static Map waitUntilValueEquals(TestObject to, String expected, int timeoutMs = 6000, int intervalMs = 150) {
		long end = System.currentTimeMillis() + timeoutMs
		String last = ""
		while (System.currentTimeMillis() < end) {
			last = readValue(to)
			if (normalize(last) == normalize(expected ?: "")) return [ok: true, last: last]
			WebUI.delay(intervalMs / 1000.0)
		}
		return [ok: false, last: last]
	}

	private static void jsSetInputValue(TestObject to, String val, boolean blurAfter = true) {
		def el = WebUI.findWebElement(to, 5)
		WebUI.executeJavaScript('''
			(function(el, val, blurAfter){
			  try {
				if (el.scrollIntoView) el.scrollIntoView({block:'center'});
				if (document.activeElement !== el && el.focus) el.focus();
				var proto = el.constructor && el.constructor.prototype || HTMLInputElement.prototype;
				var desc  = Object.getOwnPropertyDescriptor(proto, 'value');
				if (desc && desc.set) { desc.set.call(el, val); } else { el.value = val; }
				el.dispatchEvent(new Event('input',  {bubbles:true}));
				el.dispatchEvent(new Event('change', {bubbles:true}));
				try { el.dispatchEvent(new FocusEvent('focusout', {bubbles:true})); } catch(e){}
				if (blurAfter && el.blur) el.blur();
			  } catch(e){}
			})(arguments[0], arguments[1], arguments[2]);
		''', Arrays.asList(el, val?.toString() ?: "", blurAfter))
	}

	private static void jsSetRangeValue(TestObject to, String val) {
		def el = WebUI.findWebElement(to, 5)
		WebUI.executeJavaScript(
				"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true})); arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
				Arrays.asList(el, val?.toString() ?: "")
				)
	}

	private static String clickWithRetry(WebElement el, String locator, WebDriver driver, int timeoutMs = 8000, int intervalMs = 200) {
		long startMs = System.currentTimeMillis()
		long deadline = startMs + timeoutMs
		int attempt = 0

		while (System.currentTimeMillis() < deadline) {
			attempt++
			long elapsed = System.currentTimeMillis() - startMs

			if (attempt > 1) {
				boolean ready = false
				try {
					ready = el.isDisplayed() && el.isEnabled()
				} catch (org.openqa.selenium.StaleElementReferenceException stale) {
					// Element went stale — re-find it
					try {
						el = driver.findElement(By.id(locator))
						ready = el.isDisplayed() && el.isEnabled()
					} catch (Throwable ignored2) {}
				} catch (Throwable ignored) {}

				if (!ready) {
					String msg = "🔄 clickWithRetry attempt ${attempt} (${elapsed}ms) | not yet clickable | locator='${locator}'"
					logStep(msg)
					WebUI.comment(msg)
					Thread.sleep(intervalMs)
					continue
				}
			}

			try {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el)
				el.click()
				elapsed = System.currentTimeMillis() - startMs
				String msg = attempt > 1
						? "✅ clickWithRetry succeeded (native) on attempt ${attempt} (${elapsed}ms) | locator='${locator}'"
						: "🖱 click (native) | locator='${locator}'"
				logStep(msg)
				WebUI.comment(msg)
				return "native"
			} catch (Throwable t1) {
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", el)
					elapsed = System.currentTimeMillis() - startMs
					String msg = attempt == 1
							? "🖱 click (JS fallback on first attempt) | native error='${t1.message}' | locator='${locator}'"
							: "✅ clickWithRetry succeeded (JS fallback) on attempt ${attempt} (${elapsed}ms) | native error='${t1.message}' | locator='${locator}'"
					logStep(msg)
					WebUI.comment(msg)
					return "js"
				} catch (Throwable t2) {
					elapsed = System.currentTimeMillis() - startMs
					String msg = "🔄 clickWithRetry attempt ${attempt} (${elapsed}ms) | both click methods failed | native='${t1.message}' | js='${t2.message}' | locator='${locator}'"
					logStep(msg)
					WebUI.comment(msg)
					Thread.sleep(intervalMs)
				}
			}
		}

		long elapsed = System.currentTimeMillis() - startMs
		throw new Exception("clickWithRetry timed out after ${attempt} attempt(s) (${elapsed}ms) | locator='${locator}'")
	}

	private static Map runAdHocStep(String command, String locator, String expectedOrValue) {
		String actual = ""
		String err = ""
		String expected = expectedOrValue ?: ""

		try {
			TestObject to = buildTO(locator)

			switch ((command ?: "").trim()) {
				case "verifyText":
					actual = WebUI.getText(to, FailureHandling.OPTIONAL) ?: ""
					if (!actual?.trim()) actual = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL) ?: ""
					return [ok: (normalize(actual) == normalize(expected)), actual: actual, expected: expected, error: ""]

				case "verifyValue":
					actual = WebUI.getAttribute(to, "value", FailureHandling.OPTIONAL) ?: ""
					return [ok: (normalize(actual) == normalize(expected)), actual: actual, expected: expected, error: ""]

				case "setTextAndWait":
					def el = WebUI.findWebElement(to, 5)
					if (el == null) return [ok: false, actual: "<not found>", expected: expected, error: "Element not found"]

					String inputType = WebUI.executeJavaScript("return arguments[0].type || '';", [el])?.toString()
					if ("range".equalsIgnoreCase(inputType)) jsSetRangeValue(to, expected)
					else jsSetInputValue(to, expected, true)

					def waited = waitUntilValueEquals(to, expected, 6000, 150)
					actual = waited.last ?: ""
					return [ok: waited.ok, actual: actual, expected: expected, error: ""]

				default:
					return [ok: false, actual: "", expected: expected, error: "Unsupported ad-hoc command: ${command}"]
			}
		} catch (Throwable t) {
			err = t.message ?: t.toString()
		}

		return [ok: false, actual: actual, expected: expected, error: err]
	}

	// -------------------- Percent handling --------------------
	private static String normalizeWholePercent(String pctStr) {
		try {
			return (new java.math.BigDecimal(pctStr)).stripTrailingZeros().toPlainString()
		}
		catch (Exception ignore) {
			return pctStr
		}
	}

	private static String toFractional(String wholePct) {
		try {
			return new java.math.BigDecimal(wholePct)
					.divide(new java.math.BigDecimal("100"))
					.stripTrailingZeros()
					.toPlainString()
		} catch (Exception e) {
			return "0"
		}
	}

	private static boolean looksLikePercent(String displayed, String targetWholePct) {
		if (displayed == null) return false
		String norm = displayed.replace("%", "").trim()
		try {
			return new java.math.BigDecimal(norm)
					.compareTo(new java.math.BigDecimal(normalizeWholePercent(targetWholePct))) == 0
		} catch (e) {
			return norm == targetWholePct
		}
	}

	private static void setPercentViaJs(TestObject to, String val) {
		def el = WebUI.findWebElement(to)
		String js = '''
			(function(el, val){
			  try {
				if (el.scrollIntoView) el.scrollIntoView({block:'center'});
				if (document.activeElement !== el && el.focus) el.focus();
				var proto = el.constructor && el.constructor.prototype || HTMLInputElement.prototype;
				var desc  = Object.getOwnPropertyDescriptor(proto, 'value');
				if (desc && desc.set) { desc.set.call(el, val); } else { el.value = val; }
				el.dispatchEvent(new Event('input',  {bubbles:true}));
				el.dispatchEvent(new Event('change', {bubbles:true}));
				if (el.blur) el.blur();
			  } catch(e){}
			})(arguments[0], arguments[1]);
		'''
		WebUI.executeJavaScript(js, Arrays.asList(el, val))
	}

	private static void setPercentField(TestObject to, String pctStr) {
		WebUI.waitForElementVisible(to, 10, FailureHandling.STOP_ON_FAILURE)
		WebUI.waitForElementClickable(to, 10, FailureHandling.STOP_ON_FAILURE)
		WebUI.executeJavaScript("arguments[0].scrollIntoView({block:'center'});", Arrays.asList(WebUI.findWebElement(to)))
		WebUI.click(to, FailureHandling.OPTIONAL)

		String whole = normalizeWholePercent(pctStr)
		setPercentViaJs(to, whole)
		WebUI.delay(0.15)
		String v = WebUI.getAttribute(to, "value") ?: ""
		if (looksLikePercent(v, pctStr)) return

			String fractional = toFractional(pctStr)
		setPercentViaJs(to, fractional)
		WebUI.delay(0.15)
	}

	// -------------------- Highcharts helpers --------------------
	private static WebElement findOneVisible(String css, int timeoutSec = 8) {
		WebDriver driver = DriverFactory.getWebDriver()
		long deadline = System.currentTimeMillis() + (timeoutSec * 1000L)
		while (System.currentTimeMillis() < deadline) {
			List<WebElement> els = driver.findElements(By.cssSelector(css))
			WebElement hit = els?.find { e ->
				try {
					e != null && e.isDisplayed() && e.getRect()?.height > 0 && e.getRect()?.width > 0
				}
				catch (ignored) {
					false
				}
			}
			if (hit != null) return hit
			Thread.sleep(200)
		}
		return null
	}

	private static WebElement findOneInDom(String css, int timeoutSec = 8) {
		WebDriver driver = DriverFactory.getWebDriver()
		long deadline = System.currentTimeMillis() + (timeoutSec * 1000L)
		while (System.currentTimeMillis() < deadline) {
			List<WebElement> els = driver.findElements(By.cssSelector(css))
			if (els) return els[0]
			Thread.sleep(200)
		}
		return null
	}

	private static String getHighchartsCaptionText(int timeoutSec = 8) {
		WebElement table = findOneVisible("table[id^='highcharts-data-table-']", timeoutSec)
		if (table == null) return null
		try {
			WebElement caption = table.findElement(By.cssSelector(":scope > caption"))
			return (caption?.getText() ?: caption?.getAttribute("textContent"))?.trim()
		} catch (Throwable ignore) {
			return null
		}
	}

	// -------------------- Retry log --------------------
	private static void logRetry(String message) {
		try {
			String retryLogDir = RunConfiguration.getProjectDir() + "/retriedLogs"
			new File(retryLogDir).mkdirs()
			prepareLogging()
			def f = new File(retryLogDir + "/" + TL_LOG_FILE.get())
			f << (message + "\n")
		} catch (Throwable ignored) {}
	}

	// -------------------- Failure handling --------------------
	private static void markHadFailure() {
		TL_HAD_FAILURE.set(true)
		TL_LAST_FAILED.set(true)
	}

	static void captureFailure(String step, Exception e, String locator = "", String actual = "", String expected = "") {
		markHadFailure()
		prepareLogging()

		String shotPath = ""
		try {
			String fileName = buildShotName(TL_SHOT_PREFIX.get(), step ?: "step", locator ?: "loc")
			shotPath = "${TL_SHOT_DIR.get()}/${fileName}"
			WebUI.takeScreenshot(shotPath)
		} catch (ignored) {}

		String msg = "❌ ${step} → ${e.message}" + (shotPath ? " (screenshot: ${shotPath})" : "")
		WebUI.comment(msg)
		logStep(msg)

		if (actual || expected) {
			logStep("Expected: '${expected}' but found: '${actual}'")
		}

		if (isRunningInStudio() && !isHeadless()) {
			String reason =
					"Failure (debug)\n" +
					"Step: ${step}\n" +
					"Locator: ${locator}\n" +
					"Reason: ${e.message}\n" +
					((expected || actual) ? ("\nExpected:\n${expected}\n\nActual:\n${actual}\n") : "")

			String defCmd = "verifyText"
			if ((step ?: "").toLowerCase().contains("settext")) defCmd = "setTextAndWait"
			if ((step ?: "").toLowerCase().contains("verifyvalue")) defCmd = "verifyValue"

			studioPauseWithRunner(
					"GS_Validator - Step Failed",
					reason,
					defCmd,
					locator ?: "",
					expected ?: "",
					calcId()
					)
			return
		}

		// If retries are available, don't throw StepFailedException (Katalon records it permanently).
		// Just set the failure flag and return — the step loop will break early and the retry loop handles the rest.
		int currentAttempt = TL_RETRY_ATTEMPT.get() ?: 1
		int maxRetries = TL_RETRY_MAX.get() ?: 1
		if (isHeadless() && currentAttempt < maxRetries) {
			logStep("⚠ Failure captured (attempt ${currentAttempt}/${maxRetries}, will retry) | ${step}: ${e.message}")
			return
		}

		throw new StepFailedException("Failure at ${step}: ${e.message}", e)
	}

	// -------------------- Main runner --------------------
	@Keyword
	static void runFromJson(String calcId) {
		// Hard cleanup from any previous run (stale driver, watchdog, temp dirs)
		forceCleanup()

		// reset thread state
		TL_HAD_FAILURE.set(false)
		TL_LAST_FAILED.set(false)
		TL_LOG_FILE.remove()
		TL_SHOT_PREFIX.remove()
		TL_SHOT_DIR.remove()
		TL_SHOT_SEQ.set(1)
		TL_STORED.get().clear()
		TL_RETRY_ATTEMPT.set(0)

		setCalcId(calcId ?: "unknown")
		prepareLogging()

		logStep("🚀 MainValidator v${VERSION} | calcId=${calcId ?: 'unknown'}")
		WebUI.comment("🚀 MainValidator v${VERSION} | calcId=${calcId ?: 'unknown'}")

		String runtimeId = (calcId ?: "").replaceAll("[123]", "")

		String filePath = RunConfiguration.getProjectDir() + "/Include/resources/${calcId}.json"
		def jsonOriginal = new JsonSlurper().parse(new File(filePath))

		// optional navigateTo override as first item
		String overrideUrl = ""
		if (jsonOriginal[0].containsKey("navigateTo")) {
			def navObj = jsonOriginal[0].navigateTo
			overrideUrl = navObj?.value?.toString() ?: navObj?.id?.toString() ?: ""
			if (overrideUrl) jsonOriginal.remove(0)
		}
		openBrowserWithHeadlessSupport()

		int maxTestRetries = isHeadless() ? TL_RETRY_MAX.get() : 1

		try {
			String baseUrl  = gv("URL", "")
			String clientIdVal = gv("clientId", "")
			String defaultUrl = "${baseUrl}/gs/${clientIdVal}/goal/${runtimeId}.html"
			String url = overrideUrl ?: defaultUrl

		for (int testAttempt = 1; testAttempt <= maxTestRetries; testAttempt++) {
			TL_RETRY_ATTEMPT.set(testAttempt)

			// reset failure flags for this attempt
			TL_HAD_FAILURE.set(false)
			TL_LAST_FAILED.set(false)
			TL_SHOT_SEQ.set(1)
			TL_STORED.get().clear()

			if (testAttempt > 1) {
				String retryMsg = "🔁 RETRY attempt ${testAttempt}/${maxTestRetries} | calcId=${calcId ?: 'unknown'}"
				logStep(retryMsg)
				WebUI.comment(retryMsg)

				// best-effort cleanup before retry
				try {
					WebUI.deleteAllCookies()
					WebUI.executeJavaScript("window.localStorage.clear();", null)
					WebUI.executeJavaScript("window.sessionStorage.clear();", null)
				} catch (ignored) {}

				// reset loading watchdog state
				try { stopLoadingWatchdog() } catch (ignored) {}
				TL_LOADING_SINCE_MS.set(0L)
				TL_LOADING_REPORTED.set(false)
				TL_LOADING_LAST_HEARTBEAT_MS.set(0L)
			}

			// Deep copy the JSON steps so each attempt gets a fresh list
			def json = new JsonSlurper().parseText(new groovy.json.JsonBuilder(jsonOriginal).toString())

			boolean attemptFailed = false

			try {

			// navigate with retry
			int maxRetries = 3
			int attempt = 0
			while (attempt++ < maxRetries) {
				try {
					WebUI.comment("🌐 Navigate attempt ${attempt}/${maxRetries} → ${url}")
					logStep("🌐 Navigate attempt ${attempt}/${maxRetries} → ${url}")
					WebUI.navigateToUrl(url)
					break
				} catch (Exception e) {
					WebUI.comment("Navigation failed: ${e.message}")
					logStep("⚠ Navigation failed: ${e.message}")
					if (attempt >= maxRetries) throw e
					WebUI.delay(2)
				}
			}

			// start the loading watchdog AFTER navigation begins
			startLoadingWatchdog()

			// command behavior sets (these are ID-prefix comparisons)
			Set<String> visiblePrefixes = ["lf_assist_title"] as Set
			Set<String> uppercaseSuffixes = [
				"lf-action-title-label",
				"_assistant",
				"lf_assist_title"
			] as Set
			Set<String> uppercasePrefixes = [
				"lf_tab",
				"crossfield_button",
				"lf_add_this_account_label",
				"lf_next_steps_label",
				"lf-action-send-btn-label",
				"lf_apply_now",
				"lf_compare_next_step_btn",
				"lf_assist_title"
			] as Set

			Set<String> delayAfterClickPrefixes = [
				"lf_sendEmailAddress1",
				"lf_message_text"
			] as Set
			Set<String> delayAfterClickSuffixes = ["lf_eval_selected", "patterns_enabled"] as Set
			Set<String> delayBeforeClickSuffixes = ["lf_compare_select"] as Set

			// Step execution
			for (def step : json) {
				if (Boolean.TRUE.equals(TL_HAD_FAILURE.get())) break
				for (def entry : step.entrySet()) {
					if (Boolean.TRUE.equals(TL_HAD_FAILURE.get())) break

					// ✅ Make watchdog logs visible in Studio/console
					flushWatchdogLogs(false)

					// ✅ If watchdog requested a modal, show it on main thread
					String pendingModal = TL_PENDING_LOADING_MODAL_MSG.get()
					if (pendingModal) {
						TL_PENDING_LOADING_MODAL_MSG.set("")
						if (isRunningInStudio() && isDebugging() && !isHeadless()) {
							studioPauseWithRunner(
									"GS_Validator - Loading Stuck",
									pendingModal + "\n\nThis can indicate a stuck transition or pending async call.",
									"verifyText",
									"css=.lf-loading-text",
									"Loading",
									calcId()
									)
						}
					}

					String command = entry.key
					def details = entry.value

					String locator = (details instanceof Map && details.containsKey("id")) ? (details.id?.toString() ?: "") : ""
					String value   = (details instanceof Map && details.containsKey("value")) ? (details.value?.toString() ?: "") : ""

					// unwrap weird xpath text pattern -> treat as id
					def xpathTextPattern = ~/^\s*@?xpath="?\/@id="(.+?)"\/text\(\)\s*"?$/
					if (locator ==~ xpathTextPattern) {
						locator = (locator =~ xpathTextPattern)[0][1]
					}

					if (value?.contains("\${")) value = resolvePlaceholders(value)

					WebUI.comment("▶ Command: ${command} | Locator: ${locator} | Value: ${value}")
					if (isRunningInStudio() && isPauseOnStep() && !isHeadless()) {
						studioPause("Paused after step\nCommand: ${command}\nLocator: ${locator}\nValue: ${value}")
					}

					// special highcharts labels accessor
					if (command == "highChartsLabels") {
						try {
							int index = (details instanceof Map && details.containsKey("index")) ? (details.index as int) : 0
							String css = locator.startsWith("css=") ? locator.replace("css=", "") : locator
							WebElement labelEl = (WebElement) WebUI.executeJavaScript(
									"return document.querySelectorAll(arguments[0])[arguments[1]] || null;",
									Arrays.asList(css, index)
									)
							String actualLabel = labelEl?.getText() ?: labelEl?.getAttribute("textContent")
							boolean okHc = WebUI.verifyMatch((actualLabel ?: "").trim(), (value ?: "").trim(), false, FailureHandling.OPTIONAL)
							if (!okHc) captureFailure(command, new Exception("Highcharts label mismatch"), locator, actualLabel ?: "", value ?: "")
						} catch (Exception e) {
							captureFailure(command, e, locator, "", value ?: "")
						}
						continue
					}

					TestObject to = buildTO(locator)

					try {
						switch (command) {

							// -------------------- NavigateTo (ADDED) --------------------
							// Supports JSON steps like:
							// { "navigateTo": { "id": "https://..." } }
							// or { "navigateTo": { "value": "https://..." } }
							case "navigateTo":
								try {
									String targetUrl = (value?.trim() ? value.trim() : (locator?.trim() ?: ""))
									if (!targetUrl) throw new Exception("navigateTo missing url (id/value)")

									navigateToWithRetry(targetUrl, 3)

									// small settle
									WebUI.delay(0.2)
									flushWatchdogLogs(false)
								} catch (Exception e) {
									captureFailure("navigateTo", e, locator ?: "navigateTo", "", value ?: "")
								}
								break

							case "delay":
								try {
									int seconds = value?.isInteger() ? value.toInteger() : (value?.toDouble()?.toInteger() ?: 0)
									WebUI.comment("⏱ Delaying for ${seconds} second(s)")
									WebUI.delay(seconds)
									flushWatchdogLogs(false)
								} catch (Exception e) {
									WebUI.comment("⚠ Invalid delay value: '${value}' – skipping.")
								}
								break

							case "startCapture":
								try {
									String urlFilter = locator ?: ""
									WebUI.executeJavaScript('''
										(function(filter) {
											window.__netCapture = { requests: [], filter: filter };
											var origOpen = XMLHttpRequest.prototype.open;
											var origSend = XMLHttpRequest.prototype.send;
											XMLHttpRequest.prototype.open = function(method, url) {
												this.__cap = { method: method, url: url };
												return origOpen.apply(this, arguments);
											};
											XMLHttpRequest.prototype.send = function(body) {
												var self = this;
												if (self.__cap && (!filter || self.__cap.url.indexOf(filter) !== -1)) {
													var entry = { method: self.__cap.method, url: self.__cap.url, payload: body, ts: new Date().toISOString() };
													self.addEventListener('load', function() {
														entry.status = self.status;
														try { entry.response = self.responseText; } catch(e) {}
														window.__netCapture.requests.push(entry);
													});
												}
												return origSend.apply(this, arguments);
											};
											var origFetch = window.fetch;
											if (origFetch) {
												window.fetch = function(input, init) {
													var url = typeof input === 'string' ? input : (input.url || '');
													var method = (init && init.method) || 'GET';
													var body = (init && init.body) || null;
													if (!filter || url.indexOf(filter) !== -1) {
														var entry = { method: method, url: url, payload: typeof body === 'string' ? body : JSON.stringify(body), ts: new Date().toISOString() };
														return origFetch.apply(this, arguments).then(function(resp) {
															entry.status = resp.status;
															return resp.clone().text().then(function(t) { entry.response = t; window.__netCapture.requests.push(entry); return resp; });
														});
													}
													return origFetch.apply(this, arguments);
												};
											}
										})(arguments[0]);
									''', Arrays.asList(urlFilter))
									WebUI.comment("🔍 Network capture started | Filter: '${urlFilter ?: "(all)"}'")
								} catch (Exception e) {
									logStep("⚠ startCapture failed: ${e.message}")
								}
								break

							case "logCapture":
								try {
									String capJson = WebUI.executeJavaScript("return JSON.stringify(window.__netCapture ? window.__netCapture.requests : []);", null)
									def capRequests = new groovy.json.JsonSlurper().parseText(capJson ?: "[]")
									if (capRequests.isEmpty()) {
										WebUI.comment("📡 No network requests captured")
									} else {
										capRequests.eachWithIndex { req, i ->
											String msg = "📡 Captured[${i}] ${req.method} ${req.url} → ${req.status}\n    Payload: ${req.payload}\n    Response: ${req.response ?: '(empty)'}"
											logStep(msg)
											WebUI.comment(msg)
										}
									}
								} catch (Exception e) {
									logStep("⚠ logCapture failed: ${e.message}")
								}
								break

							case "clearCapture":
								try {
									WebUI.executeJavaScript("if(window.__netCapture) window.__netCapture.requests = [];", null)
									WebUI.comment("🔍 Network capture cleared")
								} catch (Exception e) {
									logStep("⚠ clearCapture failed: ${e.message}")
								}
								break

							case "click":
							case "clickAndWait":
								try {
									if (delayBeforeClickSuffixes.any { locator.endsWith(it) }) {
										String dbcMsg = "⏳ click | delayBeforeClick for locator='" + locator + "'"
										logStep(dbcMsg)
										WebUI.comment(dbcMsg)
										WebUI.delay(0.4)
									}

									WebDriver driver = DriverFactory.getWebDriver()

									if (locator.startsWith("hc-linkto-highcharts-data-table-")) {
										long hcStart = System.currentTimeMillis()
										boolean hcClicked = false

										// Step 1: try the exact locator directly
										try {
											WebElement exactLink = findOneVisible("#" + locator, 2)
											if (exactLink != null) {
												String exactMsg = "[HC] click exact id | locator='" + locator + "'"
												logStep(exactMsg)
												WebUI.comment(exactMsg)
												clickWithRetry(exactLink, locator, driver)
												hcClicked = true
											}
										} catch (Throwable hcEx1) {
											String failMsg = "[HC] exact id click failed (" + hcEx1.message + ") | falling back to wildcard polling | locator='" + locator + "'"
											logStep(failMsg)
											WebUI.comment(failMsg)
										}

										// Step 2: if exact click failed or element not found, poll with wildcard
										if (!hcClicked) {
											int hcMaxWaitMs = 8000
											int hcIntervalMs = 300
											WebElement link = null

											while (System.currentTimeMillis() - hcStart < hcMaxWaitMs) {
												link = findOneInDom("[id^='hc-linkto-highcharts-data-table-']", 1)
												if (link != null) break
													long hcElapsed = System.currentTimeMillis() - hcStart
												String waitMsg = "[HC] wildcard polling | locator='" + locator + "' | elapsed=" + hcElapsed + "ms"
												logStep(waitMsg)
												WebUI.comment(waitMsg)
												Thread.sleep(hcIntervalMs)
											}

											if (link == null) throw new Exception("highcharts link not found after " + hcMaxWaitMs + "ms | locator='" + locator + "'")

											long hcFoundMs = System.currentTimeMillis() - hcStart
											String hcMsg = "[HC] wildcard click found after " + hcFoundMs + "ms | locator='" + locator + "'"
											logStep(hcMsg)
											WebUI.comment(hcMsg)
											clickWithRetry(link, locator, driver)
										}

										WebUI.delay(0.3)
										break
									}

									Set<String> jsOnlyLocators = ["patterns_enabled"] as Set
									WebElement el = null

									// Pure JS path — bypass findWebElement entirely to avoid stale refs
									if (jsOnlyLocators.contains(locator)) {
										boolean jsClicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
												"var e=document.getElementById(arguments[0]);" +
												"if(e){e.scrollIntoView({block:'center'});" +
												"var evt=new MouseEvent('click',{bubbles:true,cancelable:true,view:window});" +
												"e.dispatchEvent(evt); return true;} return false;", locator)
										if (!jsClicked) throw new Exception("Element not found via JS: " + locator)
										String jsMsg = "🖱 click (JS direct) | locator='" + locator + "'"
										logStep(jsMsg)
										WebUI.comment(jsMsg)
									} else {
										try {
											el = WebUI.findWebElement(to, 8)
										} catch (Throwable findEx) {
											String findFbMsg = "[click] findWebElement failed (" + findEx.message + ") | trying driver.findElement | locator='" + locator + "'"
											logStep(findFbMsg)
											WebUI.comment(findFbMsg)
											try {
												el = DriverFactory.getWebDriver().findElement(By.id(locator))
											} catch (Throwable ignored2) {}
										}
										if (el == null) throw new Exception("Element not found: " + locator)

										boolean useJs = locator.startsWith("lf_view_data_table") ||
												locator.endsWith("_link") ||
												locator.endsWith("_expand") ||
												locator.endsWith("compare_view_as_table") ||
												locator == "lf_results_button"

										if (useJs) {
											((JavascriptExecutor) driver).executeScript(
													"arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", el)
											String jsMsg = "🖱 click (JS forced) | locator='" + locator + "'"
											logStep(jsMsg)
											WebUI.comment(jsMsg)
										} else {
											clickWithRetry(el, locator, driver)
										}
									}

									if (delayAfterClickPrefixes.any { locator.startsWith(it) } ||
											delayAfterClickSuffixes.any { locator.endsWith(it) }) {
										String dacMsg = "⏳ click | delayAfterClick (0.8s) for locator='" + locator + "'"
										logStep(dacMsg)
										WebUI.comment(dacMsg)
										WebUI.delay(0.8)
									} else if (locator == "lf_results_button") {
										String dacMsg = "⏳ click | delayAfterClick (1.5s) for lf_results_button"
										logStep(dacMsg)
										WebUI.comment(dacMsg)
										WebUI.delay(1.5)
									} else {
										WebUI.delay(0.25)
									}

									try {
										boolean stillDisplayed = el?.isDisplayed() ?: false
										boolean stillEnabled   = el?.isEnabled()   ?: false
										String postMsg = "🔍 post-click state | displayed=${stillDisplayed} | enabled=${stillEnabled} | locator='" + locator + "'"
										logStep(postMsg)
										WebUI.comment(postMsg)
									} catch (Throwable postEx) {
										String postMsg = "🔍 post-click state | element no longer accessible (may have navigated) | locator='" + locator + "'"
										logStep(postMsg)
										WebUI.comment(postMsg)
									}

									flushWatchdogLogs(false)
								} catch (Exception e) {
									captureFailure("click", e, locator)
								}
								break

							case "verifyTitle":
								try {
									String expected = (value ?: "").trim()
									String actualTitle = ""
									boolean matched = false
									long vtDeadline = System.currentTimeMillis() + VERIFY_RETRY_TIMEOUT_MS

									while (System.currentTimeMillis() < vtDeadline) {
										actualTitle = (WebUI.getWindowTitle() ?: "").trim()
										if (normalize(actualTitle) == normalize(expected)) {
											matched = true
											break
										}
										Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
									}
									if (!matched) captureFailure("verifyTitle", new Exception("Title mismatch"), "title", actualTitle, expected)
								} catch (Exception e) {
									captureFailure("verifyTitle", e, "title", WebUI.getWindowTitle() ?: "", (value ?: "").toString())
								}
								break

							case "waitForText":
							case "verifyText":
							case "waitForValue":
								String actualText = ""
								try {
									if (locator.contains("highcharts-data-table")) {
										String cap = getHighchartsCaptionText(8)
										if (cap == null) throw new Exception("No Highcharts data table found (id^='highcharts-data-table-')")
										boolean okCap = WebUI.verifyMatch(normalize(cap), normalize(value), false, FailureHandling.OPTIONAL)
										if (!okCap) captureFailure(command, new Exception("Highcharts caption mismatch"), locator, cap, value ?: "")
										break
									}

									if (visiblePrefixes.any { locator.startsWith(it) }) {
										WebDriver driver = DriverFactory.getWebDriver()
										WebElement vis = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(8)).until({ d ->
											List<WebElement> all = d.findElements(By.xpath("//*[@id='" + locator + "']"))
											for (WebElement e : all) {
												try {
													if (e != null && e.isDisplayed() && e.getRect()?.height > 0 && e.getRect()?.width > 0) return e
												} catch (ignored) {}
											}
											return null
										} as java.util.function.Function)

										if (vis == null) throw new Exception("No visible element found for duplicated id '${locator}'")

										actualText = (String) ((JavascriptExecutor) driver).executeScript(
												"return (arguments[0].innerText || arguments[0].textContent || '').trim();",
												vis
												)

										boolean forceUpper = uppercaseSuffixes.any { locator.endsWith(it) } ||
										uppercasePrefixes.any { locator.startsWith(it) }

										String actN = normalize(actualText)
										String expN = normalize(value)
										if (forceUpper) {
											actN = actN.toUpperCase(Locale.US)
											expN = expN.toUpperCase(Locale.US)
										}

										boolean matched = WebUI.verifyMatch(actN, expN, false, FailureHandling.OPTIONAL)
										if (!matched) captureFailure(command, new Exception("Text mismatch"), locator, actualText ?: "", value ?: "")
										break
									}

									boolean requiresUppercase =
											(uppercaseSuffixes.any { locator.endsWith(it) } || uppercasePrefixes.any { locator.startsWith(it) })

									if (locator.endsWith("-error-message")) {
										actualText = WebUI.getAttribute(to, "innerText", FailureHandling.OPTIONAL) ?: ""
										boolean okErr = WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.OPTIONAL)
										if (!okErr) captureFailure(command, new Exception("Text mismatch (error-message)"), locator, actualText ?: "", value ?: "")
										break
									}

									flushWatchdogLogs(false)
									int textTimeoutMs = (command == "waitForText") ? VERIFY_RETRY_TIMEOUT_MS * 3 : VERIFY_RETRY_TIMEOUT_MS
									actualText = getTextWithRetries(to, value, textTimeoutMs)

									String act = normalize(actualText)
									String exp = normalize(value)

									if (requiresUppercase) {
										act = act.toUpperCase(Locale.US)
										exp = exp.toUpperCase(Locale.US)
									}

									boolean ok = WebUI.verifyMatch(act, exp, false, FailureHandling.OPTIONAL)
									if (!ok) captureFailure(command, new Exception("Text mismatch"), locator, actualText ?: "", value ?: "")
								} catch (Exception e) {
									if (!actualText) actualText = "[Could not retrieve text]"
									captureFailure(command, e, locator, actualText, value ?: "")
								}
								break

							case "verifyValue":
								try {
									String want = (value ?: "").toString()
									def vvResult = waitUntilValueEquals(to, want, VERIFY_RETRY_TIMEOUT_MS, VERIFY_RETRY_INTERVAL_MS)
									if (!vvResult.ok) captureFailure(command, new Exception("Value mismatch"), locator, vvResult.last ?: "", want)
								} catch (Exception e) {
									captureFailure(command, e, locator)
								}
								break

							case "verifyChecked":
								try {
									String exp = (value ?: "").toString().trim().toLowerCase()
									boolean expectedChecked = ["true", "1", "yes", "checked"].contains(exp)

									Set<String> jsOnlyCheckLocators = ["patterns_enabled"] as Set
									boolean actualChecked = false
									boolean matched = false
									Exception vcLastEx = null
									long vcDeadline = System.currentTimeMillis() + VERIFY_RETRY_TIMEOUT_MS

									while (System.currentTimeMillis() < vcDeadline) {
										try {
											// For jsOnly elements, read checked state purely via JS — skip isSelected() entirely
											if (jsOnlyCheckLocators.contains(locator)) {
												WebDriver vcDriver = DriverFactory.getWebDriver()
												actualChecked = (Boolean) ((JavascriptExecutor) vcDriver).executeScript(
														"var e=document.getElementById(arguments[0]); return e ? e.checked : false;", locator)
												String dbg = "🔍 verifyChecked JS-only | locator='" + locator + "' | checked=" + actualChecked + " | expected=" + expectedChecked
												logStep(dbg)
												WebUI.comment(dbg)
											} else {
												WebElement el = WebUI.findWebElement(to, 5)
												if ("input".equalsIgnoreCase(el.getTagName())) {
													actualChecked = el.isSelected()
												} else {
													String aria = el.getAttribute("aria-checked")
													if (aria != null) actualChecked = "true".equalsIgnoreCase(aria)
													else {
														List<WebElement> inputs = el.findElements(By.cssSelector("input[type='checkbox'],input[type='radio']"))
														if (inputs) actualChecked = inputs[0].isSelected()
														else throw new Exception("Element is not checkbox/radio and no descendant input found")
													}
												}
											}
											vcLastEx = null
											if (actualChecked == expectedChecked) {
												matched = true
												break
											}
											Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
										} catch (org.openqa.selenium.StaleElementReferenceException stale) {
											vcLastEx = stale
											Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
										}
									}

									if (vcLastEx != null) {
										throw new Exception("verifyChecked gave up due to stale element | locator='${locator}'", vcLastEx)
									}

									if (!matched) {
										captureFailure(command, new Exception("Checkbox checked state mismatch"),
												locator, actualChecked.toString(), expectedChecked.toString())
									}
								} catch (Exception e) {
									captureFailure(command, e, locator)
								}
								break

							case "verifyAttribute":
							case "waitForAttribute":
								try {
									String rawLocator = locator ?: ""
									String baseLocator = rawLocator
									String attrName = "aria-label"

									int at = rawLocator.lastIndexOf('@')
									if (at > 0 && at < rawLocator.length() - 1) {
										baseLocator = rawLocator.substring(0, at)
										attrName = rawLocator.substring(at + 1)
									}

									TestObject attrTO = buildTO(baseLocator)
									String expectedVal = value?.toString() ?: ""

									if (command == "waitForAttribute") {
										int timeout = (details instanceof Map && details.containsKey("timeout")) ? (details.timeout.toString().toInteger()) : 8
										WebUI.waitForElementAttributeValue(attrTO, attrName, expectedVal, timeout, FailureHandling.OPTIONAL)
										String actual = WebUI.getAttribute(attrTO, attrName, FailureHandling.OPTIONAL) ?: ""
										if (normalize(actual) != normalize(expectedVal)) {
											captureFailure(command, new Exception("Attribute mismatch for @${attrName}"), rawLocator, actual, expectedVal)
										}
									} else {
										String actual = ""
										boolean ok = false
										long vaDeadline = System.currentTimeMillis() + VERIFY_RETRY_TIMEOUT_MS

										while (System.currentTimeMillis() < vaDeadline) {
											actual = WebUI.getAttribute(attrTO, attrName, FailureHandling.OPTIONAL) ?: ""
											if (normalize(actual) == normalize(expectedVal)) {
												ok = true
												break
											}
											Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
										}
										if (!ok) captureFailure(command, new Exception("Attribute mismatch for @${attrName}"), rawLocator, actual ?: "[null]", expectedVal)
									}
								} catch (Exception e) {
									captureFailure(command, e, locator, "", value ?: "")
								}
								break

							case "setPercent":
								try {
									setPercentField(to, value.toString())
								}
								catch (Exception e) {
									captureFailure(command, e, locator, "", value ?: "")
								}
								break

							case "setTextAndWait":
							case "setText":
							case "type":
								try {
									WebUI.waitForElementVisible(to, 10, FailureHandling.STOP_ON_FAILURE)
									WebUI.waitForElementClickable(to, 10, FailureHandling.STOP_ON_FAILURE)
									WebUI.click(to, FailureHandling.OPTIONAL)

									WebElement el = WebUI.findWebElement(to, 10)
									String fieldFormat = WebUI.executeJavaScript(
											"return arguments[0].getAttribute('field-format') || '';",
											Arrays.asList(el)
											)?.toString()

									String inputType = WebUI.executeJavaScript(
											"return arguments[0].type || '';",
											Arrays.asList(el)
											)?.toString()

									if ("percent".equalsIgnoreCase(fieldFormat)) {
										setPercentField(to, value.toString())
									} else if ("range".equalsIgnoreCase(inputType)) {
										WebUI.executeJavaScript(
												"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true})); arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
												Arrays.asList(el, value?.toString() ?: "")
												)
										WebUI.delay(0.2)
									} else {
										jsSetInputValue(to, value?.toString() ?: "", true)
										WebUI.sendKeys(to, Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)
									}

									// Auto-validate and retry once if value didn't stick
									int stWaitMs = (command == "setTextAndWait") ? 6000 : 2500
									def stResult = waitUntilValueEquals(to, value?.toString() ?: "", stWaitMs, 150)
									String stExpClean = (value?.toString() ?: "").replaceAll('[\$,%]', '')
									String stActClean = (stResult.last ?: "").replaceAll('[\$,%]', '')
									boolean stMatched = stResult.ok || (normalize(stExpClean) == normalize(stActClean))
									WebUI.comment("✔ ${command} applied | Locator: ${locator} | Expected: '${value}' | Actual: '${stResult.last}'")
									if (!stMatched) {
										logStep("🔄 setText retry | Locator: ${locator} | Expected: '${value}' | Got: '${stResult.last}'")
										if ("percent".equalsIgnoreCase(fieldFormat)) {
											setPercentField(to, value.toString())
										} else if ("range".equalsIgnoreCase(inputType)) {
											WebUI.executeJavaScript(
													"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true})); arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
													Arrays.asList(el, value?.toString() ?: "")
													)
										} else {
											jsSetInputValue(to, value?.toString() ?: "", true)
											WebUI.sendKeys(to, Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)
										}
									}
									flushWatchdogLogs(false)
								} catch (Exception e) {
									captureFailure(command, e, locator, "", value ?: "")
								}
								break

							case "select":
								try {
									String label = (value ?: "").toString().replace("label=", "")
									WebUI.selectOptionByLabel(to, label, false, FailureHandling.OPTIONAL)
									WebUI.delay(0.35)
									flushWatchdogLogs(false)
								} catch (Exception e) {
									captureFailure(command, e, locator, "", value ?: "")
								}
								break

							case "selectWindow":
								try {
									WebUI.switchToWindowIndex(1)
								}
								catch (Exception e) {
									captureFailure(command, e, locator)
								}
								break

							case "close":
								try {
									if (locator?.toLowerCase()?.contains("win_")) {
										WebUI.closeWindowIndex(1)
										WebUI.switchToWindowIndex(0)
									} else {
										WebUI.click(to)
									}
								} catch (Exception e) {
									captureFailure(command, e, locator)
								}
								break

							case "verifyDate":
								try {
									String resolved = resolvePlaceholders(value?.toString() ?: "")
									String actualDate = ""
									boolean ok = false
									long vdDeadline = System.currentTimeMillis() + VERIFY_RETRY_TIMEOUT_MS

									while (System.currentTimeMillis() < vdDeadline) {
										actualDate = WebUI.getAttribute(to, "value", FailureHandling.OPTIONAL) ?: ""
										if (!actualDate.trim()) actualDate = WebUI.getText(to, FailureHandling.OPTIONAL) ?: ""
										if (normalize((actualDate ?: "").trim()) == normalize((resolved ?: "").trim())) {
											ok = true
											break
										}
										Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
									}
									if (!ok) captureFailure(command, new Exception("Date mismatch"), locator, actualDate ?: "", resolved ?: "")
								} catch (Exception e) {
									captureFailure(command, e, locator, "", value ?: "")
								}
								break

							case "setDate":
								try {
									String outVal = evaluateJsDateExpression(value?.toString() ?: "")
									WebElement element = WebUI.findWebElement(to, 10)
									WebUI.executeJavaScript(
											"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('blur'));",
											Arrays.asList(element, outVal)
											)
									WebUI.delay(0.35)
									flushWatchdogLogs(false)
								} catch (Exception e) {
									captureFailure(command, e, locator, "", value ?: "")
								}
								break

							case "storeText":
							case "storeValue":
								try {
									String actual = ""
									int storeTimeoutMs = VERIFY_RETRY_TIMEOUT_MS
									int storeIntervalMs = VERIFY_RETRY_INTERVAL_MS
									long storeStart = System.currentTimeMillis()
									int storeAttempt = 0

									while (System.currentTimeMillis() - storeStart < storeTimeoutMs) {
										storeAttempt++
										actual = WebUI.getAttribute(to, "value", FailureHandling.OPTIONAL) ?: ""
										if (!actual.trim()) {
											actual = WebUI.getText(to, FailureHandling.OPTIONAL) ?: ""
										}
										if (!actual.trim()) {
											actual = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL) ?: ""
										}
										actual = (actual ?: "").trim()

										if (actual) {
											if (storeAttempt > 1) {
												long elapsed = System.currentTimeMillis() - storeStart
												String msg = "✅ ${command} got value on attempt ${storeAttempt} (${elapsed}ms) | locator='${locator}'"
												logStep(msg)
												WebUI.comment(msg)
											}
											break
										}

										long elapsed = System.currentTimeMillis() - storeStart
										String msg = "🔄 ${command} attempt ${storeAttempt} (${elapsed}ms) | value still empty | locator='${locator}'"
										logStep(msg)
										WebUI.comment(msg)
										Thread.sleep(storeIntervalMs)
									}

									if (!actual) {
										throw new Exception("${command} resolved to empty string after ${storeAttempt} attempt(s) for locator='" + locator + "' — element may not be rendered yet or locator is wrong")
									}

									TL_STORED.get()[value.toString()] = actual
									String storeMsg = "✅ Stored '" + actual + "' into variable '" + value + "'"
									logStep(storeMsg)
									WebUI.comment(storeMsg)
								} catch (Exception e) {
									captureFailure(command, e, locator)
								}
								break

							case "checkStoredText":
								try {
									String expected = TL_STORED.get()[value.toString()]
									if (expected == null) throw new Exception("No stored value found for key '${value}'")

									String actual = ""
									boolean ok = false
									long csDeadline = System.currentTimeMillis() + VERIFY_RETRY_TIMEOUT_MS

									while (System.currentTimeMillis() < csDeadline) {
										actual = WebUI.getText(to, FailureHandling.OPTIONAL) ?: ""
										if (!actual.trim()) actual = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL) ?: ""
										actual = (actual ?: "").trim()
										if (normalize(actual) == normalize(expected)) {
											ok = true
											break
										}
										Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
									}
									if (!ok) captureFailure(command, new Exception("Stored text mismatch"), locator, actual, expected)
								} catch (Exception e) {
									captureFailure(command, e, locator)
								}
								break

							case "verifyURL":
								try {
									String expectedUrl = (value ?: "").toString().trim()
									String actualUrl = ""
									boolean ok = false
									long vuDeadline = System.currentTimeMillis() + VERIFY_RETRY_TIMEOUT_MS

									while (System.currentTimeMillis() < vuDeadline) {
										actualUrl = (WebUI.getUrl() ?: "").toString().trim()
										if (expectedUrl.startsWith("http://") || expectedUrl.startsWith("https://")) {
											ok = actualUrl.equalsIgnoreCase(expectedUrl) || actualUrl.startsWith(expectedUrl)
										} else {
											ok = actualUrl.toLowerCase().contains(expectedUrl.toLowerCase())
										}
										if (ok) break
										Thread.sleep(VERIFY_RETRY_INTERVAL_MS)
									}

									String msg = "verifyURL | expected='${expectedUrl}' | actual='${actualUrl}' | match=${ok}"
									logStep(ok ? "✅ ${msg}" : "❌ ${msg}")
									WebUI.comment(ok ? "✅ ${msg}" : "❌ ${msg}")

									if (!ok) {
										captureFailure(command, new Exception("URL mismatch"), "", actualUrl, expectedUrl)
									}
								} catch (Exception e) {
									captureFailure(command, e, "")
								}
								break

							case "refresh":
								WebUI.refresh()
								flushWatchdogLogs(false)
								break

							default:
								WebUI.comment("⚠ Unsupported command: ${command}")
						}
					} catch (StepFailedException sfe) {
						throw sfe
					} catch (Exception e) {
						captureFailure(command, e, locator)
					}
				}
			}

			flushWatchdogLogs(true)

			} catch (StepFailedException sfe) {
				attemptFailed = true
				String failMsg = "❌ Test attempt ${testAttempt}/${maxTestRetries} failed: ${sfe.message}"
				logStep(failMsg)
				logRetry(failMsg)
				WebUI.comment(failMsg)

				if (testAttempt >= maxTestRetries) {
					// All retries exhausted — let final status block handle it
					break
				}
				// Will retry on next iteration
				continue
			}

			// Check if this attempt had soft failures (captured but not thrown in debug mode)
			if (Boolean.TRUE.equals(TL_HAD_FAILURE.get())) {
				attemptFailed = true
				String failMsg = "❌ Test attempt ${testAttempt}/${maxTestRetries} had failures (soft)"
				logStep(failMsg)
				logRetry(failMsg)
				WebUI.comment(failMsg)

				if (testAttempt >= maxTestRetries) {
					// All retries exhausted
					break
				}
				continue
			}

			// This attempt passed
			if (testAttempt > 1) {
				String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				String warnMsg = "⚠ WARNING: Test PASSED on retry attempt ${testAttempt}/${maxTestRetries} | calcId=${calcId ?: 'unknown'} | ${ts}"
				logStep(warnMsg)
				logRetry(warnMsg)
				WebUI.comment(warnMsg)
				KeywordUtil.logInfo(warnMsg)
			}
			// Mark as passed — clear any failure state from previous attempts
			TL_HAD_FAILURE.set(false)
			TL_LAST_FAILED.set(false)
			// Test passed — break out of retry loop
			break

		} // end retry loop

			if (!(isRunningInStudio() && !isHeadless())) {
				if (Boolean.TRUE.equals(TL_HAD_FAILURE.get())) {
					KeywordUtil.markFailedAndStop("GS_Validator completed with failures after ${TL_RETRY_ATTEMPT.get()} attempt(s). See failedLogs/${TL_LOG_FILE.get()}")
				} else {
					// Explicitly mark passed to override any internal failure state from prior retry attempts
					KeywordUtil.markPassed("GS_Validator PASSED" + (TL_RETRY_ATTEMPT.get() > 1 ? " on attempt ${TL_RETRY_ATTEMPT.get()}/${maxTestRetries} (retried)" : "") + " | calcId=${calcId ?: 'unknown'}")
				}
			} else {
				WebUI.comment("🟡 DEBUG(STUDIO): run completed hadFailure=${TL_HAD_FAILURE.get()} (not forcing stop). lastRunFailed=${TL_LAST_FAILED.get()} | attempts=${TL_RETRY_ATTEMPT.get()}")
			}
		} finally {
			try {
				flushWatchdogLogs(true)
			} catch (ignored) {}
			try {
				stopLoadingWatchdog()
			} catch (ignored) {}
			try {
				WebDriver finalDriver = DriverFactory.getWebDriver()
				if (finalDriver != null) {
					try { finalDriver.quit() } catch (Throwable ignored) {}
				}
			} catch (ignored) {}
			try {
				WebUI.closeBrowser()
			} catch (ignored) {}
			deleteTempProfileDir()
			TL_RETRY_ATTEMPT.remove()
		}
	}
}