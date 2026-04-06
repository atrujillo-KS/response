package com.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.exception.StepFailedException

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

import groovy.json.JsonSlurper

import java.text.SimpleDateFormat
import java.text.Normalizer
import java.util.Calendar
import java.util.Date
import java.nio.file.Files
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import java.util.Arrays

import java.util.regex.Pattern
import java.util.logging.Level
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.logging.LogType

// Response Script
class MainValidator {

	// -------------------- Version --------------------
	public static final String VERSION = "1.4.4"

	// -------------------- Per-thread state (safe for parallel suite collections) --------------------
	private static final ThreadLocal<Boolean> TL_LOGGING_READY = ThreadLocal.withInitial { false }
	private static final ThreadLocal<String>  TL_CALC_ID       = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_LOG_FILE      = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_SHOT_PREFIX   = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_SHOT_DIR      = new ThreadLocal<>()
	private static final ThreadLocal<Integer> TL_FAILURES      = ThreadLocal.withInitial { 0 }
	private static final ThreadLocal<Boolean> TL_HAD_FAILURE   = ThreadLocal.withInitial { false }

	private static final ThreadLocal<Integer> TL_SHOT_SEQ      = ThreadLocal.withInitial { 1 }
	private static final ThreadLocal<Integer> TL_RELOAD_COUNT  = ThreadLocal.withInitial { 0 }
	private static final ThreadLocal<File>    TL_TEMP_PROFILE  = new ThreadLocal<>()
	private static final ThreadLocal<BufferedWriter> TL_LOG_WRITER = new ThreadLocal<>()

	// -------------------- Cached runtime flags (evaluated once per run, not per step) --------------------
	private static final ThreadLocal<Boolean> TL_IS_STUDIO    = new ThreadLocal<>()
	private static final ThreadLocal<Boolean> TL_IS_DEBUGGING  = new ThreadLocal<>()
	private static final ThreadLocal<Boolean> TL_IS_PAUSE_STEP = new ThreadLocal<>()
	private static final ThreadLocal<Boolean> TL_IS_HEADLESS   = new ThreadLocal<>()

	// -------------------- Cached date formatters (avoid repeated allocation) --------------------
	private static final ThreadLocal<SimpleDateFormat> TL_FMT_TIMESTAMP = ThreadLocal.withInitial { new SimpleDateFormat("yyyyMMdd_HHmmss") }
	private static final ThreadLocal<SimpleDateFormat> TL_FMT_TIME_MS   = ThreadLocal.withInitial { new SimpleDateFormat("HH:mm:ss.SSS") }
	private static final ThreadLocal<SimpleDateFormat> TL_FMT_DATETIME  = ThreadLocal.withInitial { new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") }

	private static void logNetworkErrorsFromPerformanceLog() {
		try {
			def drv = DriverFactory.getWebDriver()
			if (drv == null) {
				WebUI.comment("⚠ Network log: WebDriver is null")
				return
			}

			def logs = drv.manage().logs().get(LogType.PERFORMANCE)
			if (logs == null) return

				def slurper = new groovy.json.JsonSlurper()

			logs.each { entry ->
				try {
					String raw = entry.message
					// Skip entries that can't match either method (avoid expensive JSON parse)
					if (!raw.contains("Network.loadingFailed") && !raw.contains("Network.responseReceived")) return

						def outer = slurper.parseText(raw)
					def msg = outer?.message
					String method = msg?.method?.toString()
					def params = msg?.params

					// 1) Hard network failures
					if (method == "Network.loadingFailed") {
						String url = params?.requestId ? (params?.requestId?.toString()) : ""
						String err = params?.errorText?.toString() ?: "loadingFailed"
						boolean canceled = Boolean.TRUE.equals(params?.canceled)
						WebUI.comment("❌ NETWORK.loadingFailed | canceled=${canceled} | error=${err} | requestId=${url}")
						return
					}

					// 2) HTTP response codes (4xx/5xx)
					if (method == "Network.responseReceived") {
						def resp = params?.response
						Integer status = resp?.status instanceof Number ? ((Number) resp.status).intValue() : null
						if (status != null && status >= 400) {
							String url = resp?.url?.toString() ?: ""
							String statusText = resp?.statusText?.toString() ?: ""
							WebUI.comment("❌ HTTP ${status} ${statusText} | ${url}")
						}
					}
				} catch (ignored) {
					// ignore per-entry parse errors
				}
			}
		} catch (Throwable t) {
			WebUI.comment("⚠ Could not read performance logs: ${t.message}")
		}
	}


	private static int reloadCount() {
		return TL_RELOAD_COUNT.get()
	}
	private static void incReloadCount() {
		TL_RELOAD_COUNT.set(TL_RELOAD_COUNT.get() + 1)
	}
	private static void resetReloadCount() {
		TL_RELOAD_COUNT.set(0)
	}

	private static int nextShotSeq() {
		int n = TL_SHOT_SEQ.get()
		TL_SHOT_SEQ.set(n + 1)
		return n
	}

	private static boolean loggingReady() {
		Boolean.TRUE.equals(TL_LOGGING_READY.get())
	}
	private static void setLoggingReady(boolean v) {
		TL_LOGGING_READY.set(v)
	}

	private static String calcId() {
		TL_CALC_ID.get() ?: (GlobalVariable.calcId ?: "unknown")
	}
	private static void setCalcId(String v) {
		TL_CALC_ID.set(v)
	}

	private static String logFile() {
		TL_LOG_FILE.get()
	}
	private static void setLogFile(String v) {
		TL_LOG_FILE.set(v)
	}

	private static String shotPrefix() {
		TL_SHOT_PREFIX.get()
	}
	private static void setShotPrefix(String v) {
		TL_SHOT_PREFIX.set(v)
	}

	private static String shotDir() {
		TL_SHOT_DIR.get()
	}
	private static void setShotDir(String v) {
		TL_SHOT_DIR.set(v)
	}

	private static int failures() {
		TL_FAILURES.get()
	}
	private static void resetFailures() {
		TL_FAILURES.set(0)
	}
	private static void incFailures() {
		TL_FAILURES.set(TL_FAILURES.get() + 1)
	}

	private static boolean hadFailure() {
		Boolean.TRUE.equals(TL_HAD_FAILURE.get())
	}
	private static void setHadFailure() {
		TL_HAD_FAILURE.set(true)
	}

	// Remember the result of the most recent run on this thread
	private static final ThreadLocal<Boolean> TL_LAST_RESULT = ThreadLocal.withInitial {
		false
	}
	private static void setLastRunFailedFlag(boolean v) {
		TL_LAST_RESULT.set(v)
	}
	static boolean lastRunFailed() {
		return Boolean.TRUE.equals(TL_LAST_RESULT.get())
	}

	/** Remove all ThreadLocal values, flush log writer, and delete temp browser profile directory. */
	private static void cleanupThreadLocals() {
		// Flush and close the log writer
		try {
			BufferedWriter w = TL_LOG_WRITER.get()
			if (w != null) {
				w.flush(); w.close()
			}
		} catch (Throwable ignored) {}

		// Delete temp browser profile directory
		try {
			File tempDir = TL_TEMP_PROFILE.get()
			if (tempDir != null && tempDir.exists()) {
				tempDir.deleteDir()
			}
		} catch (Throwable ignored) {}

		// Note: TL_LAST_RESULT is intentionally NOT cleaned up — callers check lastRunFailed() after run completes
		[TL_LOGGING_READY, TL_CALC_ID, TL_LOG_FILE, TL_SHOT_PREFIX,
			TL_SHOT_DIR, TL_FAILURES, TL_HAD_FAILURE, TL_SHOT_SEQ,
			TL_RELOAD_COUNT, TL_TEMP_PROFILE, TL_LOG_WRITER,
			TL_FMT_TIMESTAMP, TL_FMT_TIME_MS, TL_FMT_DATETIME,
			TL_IS_STUDIO, TL_IS_DEBUGGING, TL_IS_PAUSE_STEP, TL_IS_HEADLESS].each {
			it.remove()
		}
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

	// -------------------- URL Resolver (prevents null/response/null/...) --------------------
	private static String resolveBaseUrl() {
		String single = gv("RESPONSE_BASE_URL", null)
		if (single) return single.replaceAll("/+\$", "")

		String legacyUrl = gv("URL", null)
		String clientId  = gv("clientId", null)
		if (legacyUrl && clientId) {
			return "${legacyUrl.replaceAll('/+\$','')}/response/${clientId}".replaceAll("/+\$", "")
		}

		String scheme  = gv("RESPONSE_SCHEME", "https")
		String host    = gv("RESPONSE_HOST", null)
		String context = gv("RESPONSE_CONTEXT", "qatest")
		if (host) return "${scheme}://${host}/response/${context}".replaceAll("/+\$", "")

		return null
	}

	// -------------------- Logging --------------------
	private static void prepareLogging() {
		String cid      = calcId()
		String safeCalc = (cid ?: "unknown").replaceAll("[^a-zA-Z0-9._-]+", "_")
		String threadTag = "_t" + Thread.currentThread().getId()

		if (!loggingReady() || logFile() == null) {
			String ts = TL_FMT_TIMESTAMP.get().format(new Date())
			setLogFile("${safeCalc}_${ts}${threadTag}.txt")
			setShotPrefix("screenshot_${ts}${threadTag}")

			String shotsDir = RunConfiguration.getProjectDir() + "/Screenshots/${safeCalc}${threadTag}"
			new File(shotsDir).mkdirs()
			setShotDir(shotsDir)

			String logDirPath = RunConfiguration.getProjectDir() + "/failedLogs"
			File logDir = new File(logDirPath)
			if (!logDir.exists()) {
				logDir.mkdirs()
				println "📁 Created failedLogs directory: ${logDirPath}"
			}

			setLoggingReady(true)
			println "✅ Logging initialized: ${logFile()} (calcId=${safeCalc})"
		}
	}

	private static void logStep(String message) {
		if (!loggingReady()) prepareLogging()
		println message
		try {
			BufferedWriter w = TL_LOG_WRITER.get()
			if (w == null) {
				w = new BufferedWriter(new FileWriter(
				RunConfiguration.getProjectDir() + "/failedLogs/${logFile()}", true))
				TL_LOG_WRITER.set(w)
			}
			w.write(message)
			w.newLine()
			w.flush()
		} catch (Exception e) {
			println "❌ Failed to write to log file: ${e.message}"
		}
	}

	/**
	 * @param softOnly  when true, log + set flag but do NOT throw (used during retryable attempts)
	 */
	static void captureFailure(String step, Exception e, String locator = "", String actual = "", String expected = "", boolean softOnly = false) {
		if (!loggingReady()) prepareLogging()

		// --- Soft mode: log to our file ONLY, no WebUI/KeywordUtil calls ---
		// This prevents Katalon from recording a failure step in its internal report,
		// which would mark the test as failed even if a later retry passes.
		if (softOnly) {
			logStep("❌ [soft] ${step} → ${e.message}")
			if (actual || expected) {
				logStep("  Expected: '${expected}' but found: '${actual}'")
			}
			return
		}

		setHadFailure()

		String fileName = buildShotName(shotPrefix(), step ?: "step", locator ?: "loc")
		String screenshotPath = "${shotDir()}/${fileName}"

		try {
			WebUI.takeScreenshot(screenshotPath)
			logStep("❌ ${step} → ${e.message} (screenshot: ${screenshotPath})")
		} catch (Exception ex) {
			logStep("❌ ${step} → ${e.message} (screenshot capture failed: ${ex.message})")
		}

		if (actual || expected) {
			logStep("Expected: '${expected}' but found: '${actual}'")
		}

		// DEBUG MODE: only when running in Studio, do NOT hard-stop
		if (isRunningInStudio() && !isHeadless()) {
			incFailures()
			logStep("🟡 DEBUG(STUDIO): continuing after failure (step='${step}', locator='${locator}')")

			String msg =
			"Failure (debug continue)\n" +
			"Step: ${step}\n" +
			"Locator: ${locator}\n" +
			"Reason: ${e.message}\n" +
			((expected || actual) ? ("\nExpected:\n${expected}\n\nActual:\n${actual}\n") : "")

			// best-effort default command based on the failing step name
			String defCmd = "verifyText"
			if ((step ?: "").toLowerCase().contains("settext")) defCmd = "setTextAndWait"
			if ((step ?: "").toLowerCase().contains("verifyvalue")) defCmd = "verifyValue"

			studioPauseWithRunner(
			"MainValidator - Step Failed",
			msg,
			defCmd,
			locator ?: "",
			expected ?: "",
			calcId()
			)

			return
		}

		// Normal mode (or not Studio): Hard-stop immediately
		throw new StepFailedException("Failure at ${step}: ${e.message}", e)
	}

	// -------------------- Browser --------------------
	private static void openBrowserWithHeadlessSupport() {
		if (isHeadless()) {
			String browserType = (gv("browser", "chrome") as String).toLowerCase()

			if (browserType == 'firefox') {
				File profileDir = Files.createTempDirectory("ff-profile").toFile()
				TL_TEMP_PROFILE.set(profileDir)
				FirefoxOptions options = new FirefoxOptions()
				options.addArguments("--headless")
				options.addArguments("--width=1280")
				options.addArguments("--height=1000")
				options.addArguments("--profile")
				options.addArguments(profileDir.getAbsolutePath())
				DriverFactory.changeWebDriver(new FirefoxDriver(options))
			} else {
				ChromeOptions options = new ChromeOptions()
				options.addArguments("--headless=new")
				options.addArguments("--disable-gpu")
				options.addArguments("--window-size=1280,1000")
				options.addArguments("--force-device-scale-factor=1")
				options.addArguments("--no-sandbox")
				options.addArguments("--disable-dev-shm-usage")
				options.setExperimentalOption("w3c", true)

				LoggingPreferences logPrefs = new LoggingPreferences()
				logPrefs.enable("performance", Level.ALL)
				options.setCapability("goog:loggingPrefs", logPrefs)

				File tempUserDataDir = Files.createTempDirectory("chrome-user-data").toFile()
				TL_TEMP_PROFILE.set(tempUserDataDir)
				options.addArguments("--user-data-dir=" + tempUserDataDir.getAbsolutePath())

				DriverFactory.changeWebDriver(new ChromeDriver(options))
			}

			try {
				WebUI.setViewPortSize(1200, 1080)
			} catch (ignored) {}
		} else {
			WebUI.openBrowser('')
			try {
				WebUI.setViewPortSize(1200, 1080)
			} catch (ignored) {}
		}
	}

	// -------------------- Shared JS snippets --------------------
	private static final String JS_READ_TEXT = '''
        var el = arguments[0];
        if (!el) return '';
        var t = (el.innerText || el.textContent || '').trim();
        t = t.replace(/[\\u200B\\u200C\\u200D\\u2060\\uFEFF\\u00AD]/g,'');
        return t;'''

	private static final String JS_READ_TEXT_SCROLL = '''
        var el = arguments[0];
        if (!el) return '';
        try { if (el.scrollIntoView) el.scrollIntoView({block:'center'}); } catch(e){}
        var t = (el.innerText || el.textContent || '').trim();
        t = t.replace(/[\\u200B\\u200C\\u200D\\u2060\\uFEFF\\u00AD]/g,'');
        return t;'''

	// -------------------- Pre-compiled patterns for normalize() --------------------
	private static final Pattern PAT_ZERO_WIDTH  = Pattern.compile('[\u200B\u200C\u200D\u2060\uFEFF\u00AD]')
	private static final Pattern PAT_WHITESPACE  = Pattern.compile('[\\t\\n\\r]+')
	private static final Pattern PAT_MULTI_SPACE = Pattern.compile('\\s+')
	private static final Pattern PAT_SPACE_COMMA = Pattern.compile('\\s+,')
	private static final Pattern PAT_SPACE_DOT   = Pattern.compile('\\s+\\.')

	// -------------------- Helpers --------------------
	private static String normalize(String str) {
		if (str == null) return ""
		String s = Normalizer.normalize(str.toString(), Normalizer.Form.NFKC)
		s = PAT_ZERO_WIDTH.matcher(s).replaceAll("")
		s = s.replace('\u00A0' as char, ' ' as char)
		.replace("\u00E2\u0080\u008B", "")
		s = PAT_WHITESPACE.matcher(s).replaceAll(" ")
		s = PAT_MULTI_SPACE.matcher(s).replaceAll(" ")
		s = PAT_SPACE_COMMA.matcher(s).replaceAll(",")
		s = PAT_SPACE_DOT.matcher(s).replaceAll(".")
		return s.trim()
	}

	// Return the first VISIBLE element that matches a CSS selector (polls up to timeoutSec)
	private static WebElement firstVisibleByCss(String css, int timeoutSec = 10) {
		long end = System.currentTimeMillis() + (timeoutSec * 1000L)
		while (System.currentTimeMillis() < end) {
			WebElement el = (WebElement) WebUI.executeJavaScript("""
        var css = arguments[0];
        var nodes = document.querySelectorAll(css);
        function isVisible(e){
          if (!e) return false;
          var rect = e.getBoundingClientRect();
          var style = window.getComputedStyle(e);
          if (rect.width <= 0 || rect.height <= 0) return false;
          if (style.display === 'none' || style.visibility === 'hidden' || style.opacity === '0') return false;
          var p = e;
          while (p){
            var ps = window.getComputedStyle(p);
            if (ps.display === 'none' || ps.visibility === 'hidden') return false;
            p = p.parentElement;
          }
          return true;
        }
        for (var i = 0; i < nodes.length; i++) {
          if (isVisible(nodes[i])) return nodes[i];
        }
        return null;
      """, Arrays.asList(css))
			if (el != null) return el
			Thread.sleep(200)
		}
		return null
	}

	private static String jsText(WebElement el) {
		if (el == null) return ""
		return (String) WebUI.executeJavaScript(
		"var e=arguments[0]; return (e.innerText||e.textContent||'').trim();",
		Arrays.asList(el)
		)
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

	private static String slug(String s) {
		if (!s) return "na"
		String out = s.replaceAll('[^a-zA-Z0-9._-]+', '_')
		.replaceAll('_+', '_')
		.replaceAll('^_+|_+\\$', '')
		if (out.isEmpty()) out = 'na'
		return out
	}

	/** Build a compact file name: <prefix>_<seq>_<step16>_<id32>_<hash8>.png */
	private static String buildShotName(String prefix, String step, String locator) {
		int seq = nextShotSeq()
		String stepPart = slug(step).take(16)
		String idPart   = slug(locator).take(32)
		String hash     = hexHash8(locator ?: step ?: "")
		return "${prefix}_${seq}_${stepPart}_${idPart}_${hash}.png"
	}

	private static String getTextWithRetries(TestObject to, int retries = 5) {
		String actualText = ""
		while (retries-- > 0) {
			actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
			if (actualText?.trim()) break
			Thread.sleep(200)
		}
		return actualText
	}

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
          try {
            ['keydown','keyup'].forEach(function(t){
              el.dispatchEvent(new KeyboardEvent(t, {key:'Tab', bubbles:true}));
            });
          } catch(e){}
          if (blurAfter && el.blur) {
            try { el.dispatchEvent(new FocusEvent('focusout', {bubbles:true})); } catch(e){}
            el.blur();
          }
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

	private static String readValue(TestObject to) {
		try {
			def el = WebUI.findWebElement(to, 2)
			if (el == null) return ""
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

	private static Map waitUntilValueEquals(TestObject to, String expected, int timeoutMs = 6000, int intervalMs = 150) {
		long end = System.currentTimeMillis() + timeoutMs
		String normalizedExpected = normalize(expected ?: "")
		String last = ""
		while (System.currentTimeMillis() < end) {
			last = readValue(to)
			if (normalize(last) == normalizedExpected) {
				return [ok: true, last: last]
			}
			Thread.sleep(intervalMs)
		}
		return [ok: false, last: last]
	}

	private static String readText(TestObject to) {
		try {
			String t = WebUI.getText(to, FailureHandling.OPTIONAL)
			if (!t?.trim()) t = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)
			return (t ?: "").toString()
		} catch (Throwable ignore) {
			return ""
		}
	}

	/**
	 * Wait until element's text equals expected (normalized). Returns ok + last read.
	 */
	private static Map waitUntilTextEquals(TestObject to, String expected, int timeoutMs = 6000, int intervalMs = 150) {
		long end = System.currentTimeMillis() + timeoutMs
		String normalizedExpected = normalize(expected ?: "")
		String last = ""
		while (System.currentTimeMillis() < end) {
			last = readText(to)
			if (normalize(last) == normalizedExpected) {
				return [ok: true, last: last]
			}
			Thread.sleep(intervalMs)
		}
		return [ok: false, last: last]
	}

	/**
	 * Wait until the text stops changing for `stableCount` consecutive polls.
	 * Useful when you don't know exact final value timing (server calc), but you
	 * want to avoid reading mid-update.
	 */
	private static Map waitUntilTextStable(TestObject to, int timeoutMs = 6000, int intervalMs = 150, int stableCount = 3) {
		long end = System.currentTimeMillis() + timeoutMs
		String last = null
		int same = 0
		while (System.currentTimeMillis() < end) {
			String cur = normalize(readText(to))
			if (last != null && cur == last) same++ else same = 0
			last = cur
			if (same >= (stableCount - 1)) {
				return [ok: true, last: last]
			}
			Thread.sleep(intervalMs)
		}
		return [ok: false, last: last ?: ""]
	}

	private static String readSelectedLabel(TestObject to) {
		try {
			def el = WebUI.findWebElement(to, 2)
			if (el == null) return ""
			def js = '''
        var s = arguments[0];
        if (!s) return '';
        if (s.tagName && s.tagName.toLowerCase() !== 'select') return '';
        var opt = s.options && s.selectedIndex >= 0 ? s.options[s.selectedIndex] : null;
        return opt ? (opt.textContent || opt.innerText || '').trim() : '';
      '''
			return ((String) WebUI.executeJavaScript(js, [el]) ?: "").trim()
		} catch (ignored) {
			return ""
		}
	}

	private static Map waitUntilSelectedLabelEquals(TestObject to, String expectedLabel, int timeoutMs = 6000, int intervalMs = 150) {
		long end = System.currentTimeMillis() + timeoutMs
		String normalizedExpected = normalize(expectedLabel ?: "")
		String last = ""
		while (System.currentTimeMillis() < end) {
			last = readSelectedLabel(to)
			if (normalize(last) == normalizedExpected) {
				return [ok: true, last: last]
			}
			Thread.sleep(intervalMs)
		}
		return [ok: false, last: last]
	}

	// -------------------- Busy loader watchdog --------------------
	private static boolean isBusyOverlayVisibleJs() {
		try {
			return (Boolean) WebUI.executeJavaScript('''
        var el = document.getElementById('lf-ajax-loader');
        if (!el) return false;
        var s = window.getComputedStyle(el);
        if (!s) return false;
        if (s.display === 'none' || s.visibility === 'hidden' || s.opacity === '0') return false;
        var r = el.getBoundingClientRect();
        return (r.width > 0 && r.height > 0);
      ''', null)
		} catch (Throwable t) {
			return false
		}
	}

	/** Wait until overlay is NOT visible (JS visibility), up to timeoutSec. */
	private static void waitForBusyOverlayToClear(int timeoutSec = 10) {
		long end = System.currentTimeMillis() + (timeoutSec * 1000L)
		while (System.currentTimeMillis() < end) {
			try {
				if (!isBusyOverlayVisibleJs()) return
			} catch (ignored) {
				return
			}
			Thread.sleep(150)
		}
	}

	/**
	 * Observe up to maxObserveMs.
	 * If overlay is not visible at the start => not stuck (false).
	 * If it becomes not visible within the window => not stuck (false).
	 * If it stays visible the whole window => stuck (true).
	 */
	private static boolean busyOverlayStuckAfterWindow(long maxObserveMs = 4000L, int pollMs = 150) {
		if (!isBusyOverlayVisibleJs()) return false
		long end = System.currentTimeMillis() + maxObserveMs
		while (System.currentTimeMillis() < end) {
			if (!isBusyOverlayVisibleJs()) return false
			Thread.sleep(pollMs)
		}
		return isBusyOverlayVisibleJs()
	}

	// -------------------- NavigateTo with retry --------------------
	private static void navigateToWithRetry(String url, int maxRetries = 3) {
		int attempt = 0
		while (attempt++ < maxRetries) {
			try {
				WebUI.comment("🌐 navigateTo attempt ${attempt}/${maxRetries} → ${url}")
				logStep("🌐 navigateTo attempt ${attempt}/${maxRetries} → ${url}")

				hardReloadToUrl(url)
				return
			} catch (Throwable e) {
				WebUI.comment("⚠ navigateTo failed: ${e.message}")
				logStep("⚠ navigateTo failed: ${e.message}")
				if (attempt >= maxRetries) throw e
				WebUI.delay(2)
			}
		}
	}

	// -------------------- Retry log --------------------
	private static volatile boolean retryLogDirCreated = false
	private static void logRetry(String message) {
		try {
			String retryLogDir = RunConfiguration.getProjectDir() + "/retriedLogs"
			if (!retryLogDirCreated) {
				new File(retryLogDir).mkdirs()
				retryLogDirCreated = true
			}
			if (!loggingReady()) prepareLogging()
			def f = new File(retryLogDir + "/" + TL_LOG_FILE.get())
			f << (message + "\n")
		} catch (Throwable ignored) {}
	}

	private static void logReload(String reason, String url, int attempt, int max) {
		String msg = "🔁 RELOAD HIT | attempt ${attempt}/${max} | reason=${reason} | url=${url} | ts=" +
		TL_FMT_TIME_MS.get().format(new Date())
		WebUI.comment(msg)
		logStep(msg)
	}

	private static void hardReloadToUrl(String url) {
		try {
			WebUI.comment("🔄 HARD reload: WebDriver.navigate().to(url) → " + url)
			logStep("🔄 HARD reload: WebDriver.navigate().to(url) → " + url)

			DriverFactory.getWebDriver().navigate().to(url)
			WebUI.delay(1.5)
			return
		} catch (Throwable t1) {
			WebUI.comment("⚠ HARD reload navigate().to failed: ${t1.message}")
			logStep("⚠ HARD reload navigate().to failed: ${t1.message}")

			try {
				WebUI.comment("🔄 HARD reload: window.location.replace(url)")
				logStep("🔄 HARD reload: window.location.replace(url)")
				WebUI.executeJavaScript("window.location.replace(arguments[0]);", Arrays.asList(url))
				WebUI.delay(1.5)
			} catch (Throwable t2) {
				WebUI.comment("⚠ replace(url) failed: ${t2.message} → fallback refresh()")
				logStep("⚠ replace(url) failed: ${t2.message} → fallback refresh()")
				WebUI.refresh()
				WebUI.delay(1.5)
			}
		}
	}

	private static boolean isHeadless() {
		Boolean cached = TL_IS_HEADLESS.get()
		if (cached != null) return cached
		boolean v = Boolean.TRUE.equals(gv("isHeadless", false))
		TL_IS_HEADLESS.set(v)
		return v
	}

	// -------------------- Debug / Studio-only pause --------------------
	private static boolean isDebugging() {
		Boolean cached = TL_IS_DEBUGGING.get()
		if (cached != null) return cached
		boolean v = Boolean.TRUE.equals(gv("debugging", false))
		TL_IS_DEBUGGING.set(v)
		return v
	}
	private static boolean isPauseOnStep() {
		Boolean cached = TL_IS_PAUSE_STEP.get()
		if (cached != null) return cached
		boolean v = Boolean.TRUE.equals(gv("pauseOnStep", false))
		TL_IS_PAUSE_STEP.set(v)
		return v
	}

	/** Simple Studio pause (used when pauseOnStep=true). */
	private static void studioPause(String message) {
		if (!isRunningInStudio()) return
		if (isHeadless()) return
		try {
			javax.swing.JOptionPane.showMessageDialog(null, message ?: "Paused", "MainValidator", javax.swing.JOptionPane.INFORMATION_MESSAGE)
		} catch (Throwable ignored) {}
	}

	private static boolean isRunningInStudio() {
		Boolean cached = TL_IS_STUDIO.get()
		if (cached != null) return cached
		try {
			def src = RunConfiguration.getExecutionSource()
			boolean v = (src != null && src.toString().toUpperCase().contains("STUDIO"))
			TL_IS_STUDIO.set(v)
			return v
		} catch (Throwable t) {
			TL_IS_STUDIO.set(false)
			return false
		}
	}

	// -------------------- Ad-hoc runner dialog (Studio only) --------------------
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
				try {
					java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit()
					3.times {
						toolkit.beep(); Thread.sleep(200)
					}
				} catch (ignored) {}

				int choice = javax.swing.JOptionPane.showOptionDialog(
				null,
				panel,
				title ?: "MainValidator",
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
		} catch (Throwable t) {
			println "Pause requested but dialog unavailable: ${t.message}"
		}
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

	private static String evaluateJsDateExpression(String script) {
		if (!script?.contains("var d = new Date()")) return script

		Calendar cal = Calendar.getInstance()
		cal.setTime(new Date())

		int day = cal.get(Calendar.DAY_OF_MONTH)
		int month = cal.get(Calendar.MONTH) + 2 // next month
		int yearOffset = 1900
		def matcher = (script =~ /getYear\(\)\s*\+\s*(\d+)/)
		if (matcher.find()) yearOffset = matcher.group(1).toInteger()

		if (month > 12) {
			month -= 12; cal.add(Calendar.YEAR, 1)
		}

		int year = cal.get(Calendar.YEAR) - 1900 + yearOffset
		String paddedDay = day < 10 ? "0${day}" : "${day}"
		String paddedMonth = month < 10 ? "0${month}" : "${month}"

		return "${paddedMonth}/${paddedDay}/${year}"
	}

	// -------------------- Main runner --------------------
	@Keyword
	static void runFromJson(String calcIdParam) {
		String calcName = (calcIdParam ?: "").trim()
		setCalcId(calcName)
		prepareLogging()

		logStep("🚀 MainValidator v${VERSION} | calcId=${calcName ?: 'unknown'}")
		WebUI.comment("🚀 MainValidator v${VERSION} | calcId=${calcName ?: 'unknown'}")

		boolean hadAnyFailure = false
		boolean softFailMode = false   // when true, failures log but don't throw (retries remaining)
		def fail = {
			String step, Exception e, String locator = "", String actual = "", String expected = "" ->
			hadAnyFailure = true
			captureFailure(step, e, locator, actual, expected, softFailMode)
		}

		def warnStep = {
			String cmd, Object msg, String loc = "", String actual = "", String expected = "" ->
			String message = (msg instanceof Throwable) ? ((Throwable) msg).message : (msg?.toString() ?: "")
			if (softFailMode) {
				// Don't register warnings in Katalon during retryable attempts
				logStep("⚠ [soft] Warning on '${loc}': ${cmd}: ${message}")
			} else {
				KeywordUtil.markWarning("▶ Command: ${cmd} | Locator: ${loc} | Value: ${expected ?: ''}")
				KeywordUtil.markWarning("⚠ Warning on '${loc}': Failure at ${cmd}: ${message}" +
				((actual != null && actual.toString() != "") ? " | Actual: '${actual}'" : "") +
				((expected != null && expected.toString() != "") ? " | Expected: '${expected}'" : ""))
			}
		}

		// classifiers
		Set<String> uppercaseSuffixes = ["-accordion", "-tab-label"] as Set
		Set<String> uppercasePrefixes = [
			"lf_tab",
			"crossfield_button",
			"lf_add_this_account_label",
			"lf_next_steps_label"
		] as Set

		Set<String> delayAfterClickPrefixes = [
			"lf_tables_links",
			"lf_help_label",
			"lf-results-tab-label",
			"lf_help_tab",
			"lf_next_steps_label",
			"lf_answer_more_info",
			"lf_tab_basic",
			"lf_tab_advanced",
			"lf-email-submit",
			"lf_graph_tabs_lf_container",
			"lf_add_this_account_label"
		] as Set
		Set<String> delayAfterClickSuffixes = ["-accordion"] as Set

		// ======= TEMPLATE DATES (CustomDate*) =======
		Map<String, String> templateVars = [:]

		def applyTemplates = {
			String s ->
			if (!s) return s
			return s.replaceAll(/\{\{(\w+)\}\}/) {
				all, key ->
				templateVars.containsKey(key) ? templateVars[key] : all
			}
		}

		def createCustomDateVar = {
			String name, Map cfg ->
			String dataFormat = (cfg?.dataFormat ?: "Date").toString()
			String inSpec     = (cfg?.futureDateIn ?: "Years,Months,Days").toString()
			String valSpec    = (cfg?.futureValue  ?: "0,0,0").toString()

			List<String> dims = inSpec.split(/\s*,\s*/).collect {
				it?.trim()?.toLowerCase() ?: ""
			}
			List<Integer> vals = valSpec.split(/\s*,\s*/).collect {
				s2 ->
				try {
					Integer.parseInt(s2?.trim() ?: "0")
				} catch (Throwable ignore) {
					0
				}
			}

			int years = 0, months = 0, days = 0
			for (int i = 0; i < dims.size() && i < vals.size(); i++) {
				switch (dims[i]) {
					case 'years':  years  = vals[i]; break
					case 'months': months = vals[i]; break
					case 'days':   days   = vals[i]; break
				}
			}

			WebUI.comment("🧩 ${name}: dims=${dims} vals=${vals} → +Y=${years}, +M=${months}, +D=${days}")

			Calendar cal = Calendar.getInstance()
			cal.add(Calendar.YEAR, years)
			cal.add(Calendar.MONTH, months)
			cal.add(Calendar.DAY_OF_MONTH, days)

			String out = dataFormat.equalsIgnoreCase("Date")
			? new SimpleDateFormat("MM/dd/yyyy").format(cal.getTime())
			: new SimpleDateFormat(dataFormat).format(cal.getTime())

			templateVars[name] = out
			WebUI.comment("🧩 Created template var ${name} = ${out}")
		}
		// ============================================

		// ======= STEP EXECUTOR =======
		def executeStep = {
			String command, Map details ->
			String locator = details.containsKey("id") ? details.id?.toString() : ""
			String value   = details.containsKey("value") ? details.value?.toString() : ""

			// unwrap weird xpath text pattern -> treat as id
			def xpathTextPattern = ~/^\s*@?xpath="?\/@id="(.+?)"\/text\(\)\s*"?$/
			if (locator ==~ xpathTextPattern) locator = (locator =~ xpathTextPattern)[0][1]

			value = applyTemplates(value)

			// normalize dynamic Highcharts locators
			def hcInput = locator ?: ""

			if (!(hcInput =~ /starts-with\(@id,\s*['"]highcharts-data-table-/)) {
				def hcMatcher = (hcInput =~ /(?i)^\s*xpath=\s*\/\/table\[\s*@id\s*=\s*['"]highcharts-data-table-\d+['"]\s*]\s*(\/.*)\s*$/)
				if (hcMatcher.matches()) {
					locator = "xpath=//table[starts-with(@id,'highcharts-data-table-')]${hcMatcher.group(1)}"
				}
			}

			if (locator?.contains("hc-linkto-highcharts-data-table-")) {
				locator = "css=[id^='hc-linkto-highcharts-data-table-']"
			}

			TestObject to = buildTO(locator)

			WebUI.comment("▶ Command: ${command} | Locator: ${locator} | Value: ${value}")
			if (isRunningInStudio() && isPauseOnStep()) {
				studioPause("Paused after step\nCommand: ${command}\nLocator: ${locator}\nValue: ${value}")
			}

			switch (command) {

				// -------------------- NavigateTo --------------------
				case 'navigateTo':
				try {
					String targetUrl = (value?.trim() ? value.trim() : (locator?.trim() ?: ""))
					if (!targetUrl) throw new Exception("navigateTo missing url (id/value)")

					navigateToWithRetry(targetUrl, 3)

					// small settle
					WebUI.delay(0.2)
				} catch (Exception e) {
					fail("navigateTo", e, locator ?: "navigateTo", "", value ?: "")
				}
				break

				case 'delay':
				try {
					int seconds = value?.isInteger() ? value.toInteger() : (value?.toDouble()?.toInteger() ?: 0)
					WebUI.comment("⏱ Delaying for ${seconds} second(s)")
					WebUI.delay(seconds)
				} catch (Exception e) {
					WebUI.comment("⚠ Invalid delay value: '${value}' – skipping delay.")
				}
				break

				case 'click':
				case 'clickAndWait':
				// Skip clicking <option> elements directly
				if (locator?.startsWith("xpath=//option[@value")) {
					WebUI.comment("↪ Skipping click on <option> element (use select instead): ${locator}")
					return
				}
				try {
					def we = WebUI.findWebElement(to)
					WebUI.executeJavaScript("arguments[0].scrollIntoView({block:'center'});", [we])
					WebUI.delay(0.2)

					boolean isSpecial =
					locator.startsWith("lf_add_this_account") ||
					locator.startsWith("lf_view_data_table")  ||
					locator == "lf_help_tab" ||
					locator.endsWith("_link") || locator.endsWith("-link") ||
					locator.endsWith("_expand") ||
					locator.endsWith("-tab-label") || locator.endsWith("_tab_label") ||
					locator.endsWith("_tooltip") || locator.contains("tooltip")

					if (!isSpecial) {
						WebUI.waitForElementVisible(to, 2, FailureHandling.OPTIONAL)
						try {
							def el = WebUI.findWebElement(to, 2)
							if (!(el?.isDisplayed() && el?.isEnabled())) WebUI.delay(0.2)
						} catch (ignored) {}
					}

					// Wait for overlay to be gone BEFORE clicking
					waitForBusyOverlayToClear(10)

					int tries = 0
					Exception lastErr = null
					while (tries++ < 3) {
						try {
							if (isSpecial) {
								def el = WebUI.findWebElement(to, 5)
								WebUI.executeJavaScript("arguments[0].click();", [el])
							} else {
								WebUI.click(to, FailureHandling.OPTIONAL)
							}
							lastErr = null
							break
						} catch (org.openqa.selenium.ElementClickInterceptedException ice) {
							waitForBusyOverlayToClear(10)
							WebUI.delay(0.2)
							lastErr = ice
						} catch (Exception e) {
							try {
								def el = WebUI.findWebElement(to, 5)
								WebUI.executeJavaScript("arguments[0].click();", [el])
								lastErr = null
								break
							} catch (Exception ex) {
								lastErr = ex
								waitForBusyOverlayToClear(10)
								WebUI.delay(0.2)
							}
						}
					}

					if (lastErr != null) fail(command, lastErr, locator)

					if (delayAfterClickPrefixes.any {
						locator.startsWith(it)
					} ||
					delayAfterClickSuffixes.any {
						locator.endsWith(it)
					} ||
					locator == "lf_help_tab") {
						WebUI.delay(2)
					}
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				case 'verifyTitle':
				try {
					String actualTitle = WebUI.getWindowTitle()
					boolean ok = (normalize(actualTitle) == normalize(value))
					if (!ok) fail("verifyTitle", new Exception("Title mismatch"), "title", actualTitle ?: "", value ?: "")
				} catch (Exception e) {
					fail("verifyTitle", e, "title")
				}
				break

				case 'waitForText':
				case 'verifyText':
				String actualText = ""
				try {
					// Pre-wait: if server calc / async render, avoid reading mid-update
					waitForBusyOverlayToClear(10)

					// CSS locators that may match multiple charts; pick the first VISIBLE one
					if (locator != null && locator.startsWith("css=")) {
						String cssSel = locator.substring(4)
						WebElement el = firstVisibleByCss(cssSel, 10)
						if (el == null) {
							fail(command, new Exception("No visible element found for CSS selector within timeout"), locator)
							break
						}
						actualText = jsText(el)
						boolean okCss = (normalize(actualText) == normalize(value))
						if (!okCss) fail(command, new Exception("Text mismatch (visible CSS match)"), locator, actualText ?: "", value ?: "")
						break
					}

					boolean requiresUppercase =
					uppercaseSuffixes.any {
						locator.endsWith(it)
					} ||
					uppercasePrefixes.any {
						locator.startsWith(it)
					}

					boolean isErrorMsg  = locator.endsWith("-error-message")
					boolean isLabelLike = locator.startsWith("lf_help") ||
					locator.endsWith("_label") ||
					locator.endsWith("-label")

					// Hidden error messages: require presence, not visibility; read via JS
					if (isErrorMsg) {
						boolean present = WebUI.waitForElementPresent(to, 10, FailureHandling.OPTIONAL)
						if (!present) {
							fail(command, new Exception("Error message element not present after 10s"), locator)
							break
						}

						def el = null
						try {
							el = WebUI.findWebElement(to, 5)
						} catch (ignored) {}
						actualText = el ? (String) WebUI.executeJavaScript(JS_READ_TEXT, [el]) : ""

						if (!actualText) {
							int tries = 6
							while (tries-- > 0 && !actualText) {
								Thread.sleep(250)
								try {
									el = WebUI.findWebElement(to, 2)
								} catch (ignored) {}
								actualText = el ? (String) WebUI.executeJavaScript(JS_READ_TEXT, [el]) : ""
							}
						}

						boolean okErr = (normalize(actualText) == normalize(value))
						if (!okErr) fail(command, new Exception("Text mismatch (hidden error)"), locator, actualText ?: "", value ?: "")
						break
					}

					// Highcharts caption special case
					if (locator == "css=table[id^='highcharts-data-table-'] > caption") {
						def txt = (String) WebUI.executeJavaScript(
						"var c=document.querySelector(\"table[id^='highcharts-data-table-'] > caption\");" +
						"return c ? (c.innerText||c.textContent||'').trim() : '';",
						null
						)
						boolean okCap = (normalize(txt) == normalize(value))
						if (!okCap) fail(command, new Exception("Highcharts caption mismatch"), locator, txt ?: "", value ?: "")
						break
					}

					boolean ok

					if (requiresUppercase) {
						actualText = getTextWithRetries(to, 5)
						ok = (normalize(actualText).toUpperCase() == normalize(value).toUpperCase())
					} else if (locator == "lf-email-message") {
						WebUI.waitForElementVisible(to, 10, FailureHandling.OPTIONAL)
						int retries = 20
						while (retries-- > 0) {
							actualText = WebUI.getText(to, FailureHandling.OPTIONAL)?.trim()
							if (!actualText) actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)?.trim()
							if (actualText) break
							WebUI.delay(0.5)
						}

						boolean okEmail = (normalize(actualText) == normalize(value))
						if (!okEmail) fail(command, new Exception("Async email message text mismatch"), locator, actualText ?: "", value ?: "")
						break
					} else if (isLabelLike) {
						def el = null
						try {
							el = WebUI.findWebElement(to, 5)
						} catch (ignored) {}
						actualText = el ? (String) WebUI.executeJavaScript(JS_READ_TEXT_SCROLL, [el]) : ""
						if (!actualText) {
							int tries = 6
							while (tries-- > 0 && !actualText) {
								Thread.sleep(250)
								try {
									el = WebUI.findWebElement(to, 2)
								} catch (ignored) {}
								actualText = el ? (String) WebUI.executeJavaScript(JS_READ_TEXT_SCROLL, [el]) : ""
							}
						}
						ok = (normalize(actualText) == normalize(value))
					} else {
						actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
						if (!actualText?.trim()) actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)

						// Poll for async updates (longer timeout for answer/amortization fields)
						if (normalize(actualText) != normalize(value)) {
							int pollMs = (locator == 'lf_answer' || locator == 'lf_amortization_title') ? 10000 : 6000
							def waitedTxt = waitUntilTextEquals(to, value, pollMs, 150)
							if (waitedTxt.ok) actualText = waitedTxt.last ?: actualText
						}
						ok = (normalize(actualText) == normalize(value))
					}

					if (!ok) fail(command, new Exception("Text mismatch"), locator, actualText ?: "", value ?: "")
				} catch (Exception e) {
					if (!actualText) actualText = "[Could not retrieve text]"
					if (!softFailMode) WebUI.comment("✖ Failed to verify text on '${locator}': ${e.message}")
					fail(command, e, locator, actualText, value ?: "")
				}
				break

				case 'verifyValue':
				try {
					waitForBusyOverlayToClear(10)

					if (!WebUI.waitForElementVisible(to, 7, FailureHandling.OPTIONAL)) {
						fail(command, new Exception("Element not visible after 7s"), locator)
						return
					}

					// First read
					String actualVal = readValue(to)

					// If mismatch, wait for it to become expected (server calc timing)
					if (normalize(actualVal) != normalize(value ?: "")) {
						def waited = waitUntilValueEquals(to, value?.toString() ?: "", 6000, 150)
						actualVal = waited.last ?: actualVal
					}

					boolean ok = (normalize(actualVal) == normalize(value ?: ""))

					if (!ok) {
						fail(command, new Exception("Value mismatch"), locator, actualVal, value ?: "")
					}
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				case 'verifyAttribute':
				case 'waitForAttribute':
				try {
					if (!WebUI.waitForElementVisible(to, 10, FailureHandling.OPTIONAL)) {
						fail(command, new Exception("Element not visible after 10s"), locator)
						return
					}
					boolean okAttr = WebUI.waitForElementAttributeValue(to, 'aria-expanded', value, 8, FailureHandling.OPTIONAL)
					String actualAttr = WebUI.getAttribute(to, 'aria-expanded', FailureHandling.OPTIONAL) ?: ""
					if (!okAttr || normalize(actualAttr) != normalize(value ?: "")) {
						fail(command, new Exception("Attribute mismatch"), locator, actualAttr, value ?: "")
					}
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				case 'setTextAndWait':
				case 'setText':
				case 'type':
				try {
					// ✅ Wait for loader/overlay to be gone BEFORE interacting with the field
					waitForBusyOverlayToClear(10)

					def el = WebUI.findWebElement(to, 5)
					if (el == null) {
						fail(command, new Exception("Element not found"), locator, "", value?.toString() ?: "")
						break
					}

					String inputType = WebUI.executeJavaScript("return arguments[0].type || '';", [el])?.toString()

					if ("range".equalsIgnoreCase(inputType)) {
						jsSetRangeValue(to, value?.toString() ?: "")
					} else {
						jsSetInputValue(to, value?.toString() ?: "", true)
					}

					// ✅ If the app shows the overlay AFTER input events, wait again before asserting
					waitForBusyOverlayToClear(10)

					int waitMs = (command == 'setTextAndWait') ? 6000 : 2500
					def waited = waitUntilValueEquals(to, value?.toString() ?: "", waitMs, 150)
					String last = waited.last ?: ""

					WebUI.comment("✔ ${command} applied | Locator: ${locator} | Expected: '${value}' | Actual: '${last}'")
					if (!waited.ok) warnStep(command, "Value did not match after change", locator, last ?: "", value?.toString() ?: "")
				} catch (Exception e) {
					fail(command, e, locator, "", value?.toString() ?: "")
				}
				break

				case 'select':
				try {
					TestObject overlay = new TestObject('busyOverlay')
					overlay.addProperty("id", ConditionType.EQUALS, "lf-ajax-loader")
					WebUI.waitForElementNotVisible(overlay, 5, FailureHandling.OPTIONAL)

					String label = value?.toString()?.replace("label=", "") ?: ""
					WebUI.selectOptionByLabel(to, label, false, FailureHandling.OPTIONAL)

					def waited = waitUntilSelectedLabelEquals(to, label, 6000, 150)
					String lastSel = waited.last ?: ""
					WebUI.comment("✔ select applied | Locator: ${locator} | Expected label: '${label}' | Actual selected: '${lastSel}'")
					if (!waited.ok) warnStep(command, "Selected label did not match after change", locator, lastSel ?: "", label ?: "")
				} catch (Exception e) {
					fail(command, e, locator)
				}
				WebUI.delay(0.2)
				break

				case 'selectWindow':
				try {
					WebUI.switchToWindowIndex(1)
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				case 'close':
				try {
					if (locator?.toLowerCase()?.contains("win_")) {
						WebUI.closeWindowIndex(1)
						WebUI.switchToWindowIndex(0)
					} else {
						WebUI.click(to)
					}
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				case 'verifyDate':
				try {
					String actualDate = WebUI.getAttribute(to, 'value', FailureHandling.OPTIONAL)
					if (!actualDate?.trim()) actualDate = WebUI.getText(to, FailureHandling.OPTIONAL)
					boolean okDate = (normalize(actualDate) == normalize(value))
					if (!okDate) fail(command, new Exception("Date mismatch"), locator, actualDate ?: "", value ?: "")
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				case 'setDate':
				try {
					String outVal = value
					if (value?.contains("var d = new Date()")) outVal = evaluateJsDateExpression(value)
					WebElement element = WebUI.findWebElement(to, 10)
					WebUI.executeJavaScript(
					"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('blur'));",
					[element, outVal]
					)
					WebUI.delay(0.5)
				} catch (Exception e) {
					fail(command, e, locator, "", value ?: "")
				}
				break

				case 'highChartsLabels':
				try {
					int index = details.containsKey("index") ? (details.index as int) : 0
					String css = locator.startsWith("css=") ? locator.replace("css=", "") : locator
					String js = 'return document.querySelectorAll(arguments[0])[arguments[1]];'
					WebElement labelEl = (WebElement) WebUI.executeJavaScript(js, Arrays.asList(css, index))

					String actualLabel = labelEl?.getText() ?: labelEl?.getAttribute("textContent")
					boolean okHc = (normalize(actualLabel) == normalize(value))
					if (!okHc) fail(command, new Exception("Highcharts label mismatch"), locator, actualLabel ?: "", value ?: "")
				} catch (Exception e) {
					fail(command, e, locator)
				}
				break

				default:
				WebUI.comment("⚠ Unsupported command: ${command}")
			}
		}
		// ======= END EXECUTOR =======

		try {
			// --- load JSON ---
			String filePath = RunConfiguration.getProjectDir() + "/Include/resources/${calcIdParam}.json"
			def jsonOriginal = new JsonSlurper().parse(new File(filePath)) as List

			// --- navigateTo override (first step) ---
			String overrideUrl = ""
			if (jsonOriginal[0].containsKey("navigateTo")) {
				def navObj = jsonOriginal[0].navigateTo
				overrideUrl = navObj?.value?.toString() ?: navObj?.id?.toString() ?: ""
				if (overrideUrl) jsonOriginal.remove(0)
			}

			// --- preprocess CustomDate* steps and remove them ---
			int idx = 0
			while (idx < jsonOriginal.size()) {
				def step = jsonOriginal[idx] as Map
				if (!step) {
					idx++; continue
				}
				String key = step.keySet().iterator().next()
				if (key ==~ /^CustomDate\w*$/) {
					def cfg = step[key] as Map
					try {
						createCustomDateVar(key, cfg ?: [:])
					} catch (Exception e) {
						WebUI.comment("⚠ Failed to create ${key}: ${e.message}")
					}
					jsonOriginal.remove(idx)
					continue
				}
				idx++
			}

			// --- open browser + URL build/log ---
			openBrowserWithHeadlessSupport()

			String url
			if (overrideUrl && (overrideUrl.startsWith("http://") || overrideUrl.startsWith("https://"))) {
				url = overrideUrl
			} else {
				String base = resolveBaseUrl()
				if (!base) {
					WebUI.comment(
					"❌ Base URL is not configured. Checked:\n" +
					"  RESPONSE_BASE_URL=" + (gv("RESPONSE_BASE_URL", null) ?: "<null>") + "\n" +
					"  URL=" + (gv("URL", null) ?: "<null>") + "  clientId=" + (gv("clientId", null) ?: "<null>") + "\n" +
					"  RESPONSE_SCHEME=" + gv("RESPONSE_SCHEME", "https") +
					"  RESPONSE_HOST=" + (gv("RESPONSE_HOST", null) ?: "<null>") +
					"  RESPONSE_CONTEXT=" + gv("RESPONSE_CONTEXT", "qatest")
					)
					KeywordUtil.markFailedAndStop(
					"Base URL is not configured. Set GlobalVariable.RESPONSE_BASE_URL " +
					"or (URL and clientId), or (RESPONSE_SCHEME/RESPONSE_HOST/RESPONSE_CONTEXT)."
					)
				}

				String runtimeId = calcName.contains("spouse") ? calcName.replace("spouse","") : calcName
				if (runtimeId.startsWith("pft10")) {
					// PFT 1.0: tools/{context}/{calcId}/tool.fcs
					runtimeId = runtimeId.substring("pft10".length())
					String context = gv("RESPONSE_CONTEXT", "qatest")
					String scheme  = gv("RESPONSE_SCHEME", "https")
					String host    = gv("RESPONSE_HOST", null) ?: base.replaceAll("^https?://", "").replaceAll("/.*", "")
					url = "${scheme}://${host}/tools/${context}/${runtimeId}/tool.fcs?newuser"
				} else if (runtimeId.contains("print")) {
					runtimeId = runtimeId.substring(runtimeId.indexOf("print") + "print".length())
					url = "${base}/print/${runtimeId}?newuser"
				} else {
					url = "${base}/calc/${runtimeId}?newuser"
				}
			}

			logStep("🌐 Opening URL: ${url}")
			WebUI.comment("🌐 Opening URL: ${url}")

			// --- Retry loop: up to 3 attempts when headless, 1 when in Studio ---
			int maxTestRetries = isHeadless() ? 3 : 1

			for (int testAttempt = 1; testAttempt <= maxTestRetries; testAttempt++) {

				// reset failure flags for this attempt
				hadAnyFailure = false
				softFailMode = (testAttempt < maxTestRetries)
				TL_SHOT_SEQ.set(1)

				if (testAttempt > 1) {
					String retryMsg = "🔁 RETRY attempt ${testAttempt}/${maxTestRetries} | calcId=${calcName ?: 'unknown'}"
					logStep(retryMsg)
					logRetry(retryMsg)
					WebUI.comment(retryMsg)

					// best-effort cleanup before retry
					try {
						WebUI.deleteAllCookies()
						WebUI.executeJavaScript("window.localStorage.clear();", null)
						WebUI.executeJavaScript("window.sessionStorage.clear();", null)
					} catch (ignored) {}
				}

				// Shallow copy the JSON steps so each attempt gets a fresh list
				def json = jsonOriginal.collect {
					new LinkedHashMap((Map) it)
				}

				boolean attemptFailed = false

				try {

					int reloadAttempts = 0
					int maxReloads = 2

					while (true) {
						WebUI.comment("🌐 Navigate attempt ${reloadAttempts + 1} → ${url}")
						logStep("🌐 Navigate attempt ${reloadAttempts + 1} → ${url}")

						WebUI.navigateToUrl(url)
						WebUI.delay(0.3)
						logNetworkErrorsFromPerformanceLog()

						boolean stuck = busyOverlayStuckAfterWindow(10000L, 150)
						if (!stuck) {
							WebUI.comment("✅ Loader not stuck (not visible or cleared within 10000ms) — proceeding")
							logStep("✅ Loader not stuck (not visible or cleared within 10000ms) — proceeding")
							break
						}

						reloadAttempts++
						incReloadCount()
						logReload("lf-ajax-loader still visible after 10000ms", url, reloadAttempts, maxReloads)

						if (reloadAttempts >= maxReloads) {
							throw new StepFailedException("Busy overlay stuck after ${maxReloads} reload attempts.")
						}

						hardReloadToUrl(url)
					}

					// --- control-flow helpers (defined once, not per iteration) ---
					def ctrlKeyOf = {
						Map s ->
						if (!s) return null
						def k = s.keySet().iterator().next()
						return (k in ['if', 'elseIf', 'endIf']) ? k : null
					}

					def evalCondition = {
						Map cond ->
						String loc = cond.id?.toString() ?: ""
						String expected = (cond.Value ?: cond.value ?: "").toString()
						try {
							TestObject to = buildTO(loc)
							String actual = WebUI.getText(to, FailureHandling.OPTIONAL)
							if (!actual?.trim()) actual = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)
							return normalize(actual) == normalize(expected)
						} catch (ignored) {
							return false
						}
					}

					// --- execute steps ---
					int i = 0
					while (i < json.size()) {
						// If a failure was captured during this attempt, break early for retry
						if (hadAnyFailure && testAttempt < maxTestRetries) break

						Map step = (Map) json[i]
						String command = step.keySet().iterator().next()
						def details = step[command]

						if (command == 'if') {
							boolean matched = evalCondition(details as Map)
							i++

							if (matched) {
								while (i < json.size()) {
									Map s = (Map) json[i]
									String ck = ctrlKeyOf(s)
									if (ck == 'elseIf') {
										int depth = 0; i++
										while (i < json.size()) {
											String k = ctrlKeyOf((Map) json[i])
											if (k == 'if') depth++
											if (k == 'endIf') {
												if (depth == 0) {
													i++; break
												} else {
													depth--
												}
											}
											i++
										}
										break
									} else if (ck == 'endIf') {
										i++
										break
									} else {
										String k2 = s.keySet().iterator().next()
										def d2 = s[k2]
										if (d2 instanceof Map && d2.containsKey('value')) d2.value = applyTemplates(d2.value?.toString())
										executeStep(k2, (Map) d2)
										i++
									}
								}
								continue
							} else {
								while (i < json.size()) {
									Map s = (Map) json[i]
									String ck = ctrlKeyOf(s)
									if (ck == 'elseIf') {
										boolean ok = evalCondition((Map) s['elseIf'])
										i++
										if (ok) {
											while (i < json.size()) {
												Map s2 = (Map) json[i]
												String ck2 = ctrlKeyOf(s2)
												if (ck2 == 'elseIf' || ck2 == 'endIf') break
												String k2 = s2.keySet().iterator().next()
												def d2 = s2[k2]
												if (d2 instanceof Map && d2.containsKey('value')) d2.value = applyTemplates(d2.value?.toString())
												executeStep(k2, (Map) d2)
												i++
											}
											int depth = 0
											while (i < json.size()) {
												String k = ctrlKeyOf((Map) json[i])
												if (k == 'if') depth++
												if (k == 'endIf') {
													if (depth == 0) {
														i++; break
													} else {
														depth--
													}
												}
												i++
											}
											break
										} else {
											continue
										}
									} else if (ck == 'endIf') {
										i++
										break
									} else {
										i++
									}
								}
								continue
							}
						} else if (command == 'elseIf' || command == 'endIf') {
							i++
							continue
						}

						if (details instanceof Map && details.containsKey('value')) {
							details.value = applyTemplates(details.value?.toString())
						}
						executeStep(command, (Map) details)
						i++
					}
				} catch (StepFailedException sfe) {
					attemptFailed = true
					String failMsg = "❌ Test attempt ${testAttempt}/${maxTestRetries} failed: ${sfe.message}"
					logStep(failMsg)
					logRetry(failMsg)
					WebUI.comment(failMsg)

					if (testAttempt >= maxTestRetries) {
						// All retries exhausted
						break
					}
					// Will retry on next iteration
					continue
				}

				// Check if this attempt had soft failures
				if (hadAnyFailure) {
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
					String ts = TL_FMT_DATETIME.get().format(new Date())
					String warnMsg = "⚠ WARNING: Test PASSED on retry attempt ${testAttempt}/${maxTestRetries} | calcId=${calcName ?: 'unknown'} | ${ts}"
					logStep(warnMsg)
					logRetry(warnMsg)
					WebUI.comment(warnMsg)
					KeywordUtil.logInfo(warnMsg)
				}
				// Test passed — break out of retry loop
				break
			} // end retry loop

			// --- enforce red step if any prior failure happened ---
			if (!(isRunningInStudio() && !isHeadless())) {
				if (hadAnyFailure) {
					KeywordUtil.markFailedAndStop("MainValidator completed with failures after retries. See failedLogs/${TL_LOG_FILE.get()}")
				} else {
					KeywordUtil.markPassed("MainValidator PASSED | calcId=${calcName ?: 'unknown'}")
				}
			} else {
				WebUI.comment("🟡 DEBUG(STUDIO): run completed with failures=${failures()} (not stopping)")
			}

			setLastRunFailedFlag(hadAnyFailure)
		} finally {
			try {
				def finalDriver = DriverFactory.getWebDriver()
				if (finalDriver != null) {
					try { finalDriver.quit() } catch (Throwable ignored) {}
				}
			} catch (ignored) {}
			try {
				WebUI.closeBrowser()
			} catch (ignored) {}
			cleanupThreadLocals()
		}
	}
}
