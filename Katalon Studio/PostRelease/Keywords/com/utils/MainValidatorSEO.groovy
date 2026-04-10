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
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

import groovy.json.JsonSlurper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.io.File
import java.util.Arrays
import java.nio.file.Files
import java.text.Normalizer

// Response Script
class MainValidatorSEO {

	// -------------------- Per-thread state (safe for parallel suite collections) --------------------
	private static final ThreadLocal<Boolean> TL_LOGGING_READY = ThreadLocal.withInitial { false }
	private static final ThreadLocal<String>  TL_CALC_ID       = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_LOG_FILE      = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_SHOT_PREFIX   = new ThreadLocal<>()
	private static final ThreadLocal<String>  TL_SHOT_DIR      = new ThreadLocal<>()
	private static final ThreadLocal<Integer> TL_FAILURES      = ThreadLocal.withInitial { 0 }

	private static final ThreadLocal<Integer> TL_SHOT_SEQ = ThreadLocal.withInitial { 1 }
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

	private static final ThreadLocal<Boolean> TL_HAD_FAILURE = ThreadLocal.withInitial { false }
	private static boolean hadFailure() {
		Boolean.TRUE.equals(TL_HAD_FAILURE.get())
	}
	private static void setHadFailure() {
		TL_HAD_FAILURE.set(true)
	}

	// Remember the result of the most recent run on this thread
	private static final ThreadLocal<Boolean> TL_LAST_RESULT = ThreadLocal.withInitial { false }
	private static void setLastRunFailedFlag(boolean v) {
		TL_LAST_RESULT.set(v)
	}
	static boolean lastRunFailed() {
		return Boolean.TRUE.equals(TL_LAST_RESULT.get())
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
		if (legacyUrl && clientId) return "${legacyUrl.replaceAll('/+\$','')}/response/${clientId}".replaceAll("/+\$", "")

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
			String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
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
		prepareLogging()
		println message
		try {
			def logFileF = new File(RunConfiguration.getProjectDir() + "/failedLogs/${logFile()}")
			logFileF << message + "\n"
		} catch (Exception e) {
			println "❌ Failed to write to log file: ${e.message}"
		}
	}

	static void captureFailure(String step, Exception e, String locator = "", String actual = "", String expected = "") {
		setHadFailure()
		prepareLogging()

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

		// Hard-stop the test immediately
		throw new StepFailedException("Failure at ${step}: ${e.message}", e)
	}

	// -------------------- Browser --------------------
	private static void openBrowserWithHeadlessSupport() {
		if (gv("isHeadless", false)) {
			String browserType = (gv("browser", "chrome") as String).toLowerCase()

			if (browserType == 'firefox') {
				File profileDir = Files.createTempDirectory("ff-profile").toFile()
				FirefoxOptions options = new FirefoxOptions()
				options.addArguments("--headless")
				options.addArguments("--width=1280")
				options.addArguments("--height=1000")
				options.addArguments("--profile")
				options.addArguments(profileDir.getAbsolutePath())
				DriverFactory.changeWebDriver(new FirefoxDriver(options))
			} else {
				ChromeOptions options = new ChromeOptions()

				options.addArguments(
						"--headless=new",
						"--disable-gpu",
						"--window-size=1280,1000",
						"--force-device-scale-factor=1",
						"--no-sandbox",
						"--disable-dev-shm-usage",
						"--disable-http2",
						"--disable-quic",
						"--disable-extensions",
						"--proxy-server=direct://",
						"--proxy-bypass-list=*"
						)

				options.addArguments(
						"user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
						"AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
						)

				// reduce obvious "automation" fingerprints
				options.setExperimentalOption("useAutomationExtension", false)
				options.setExperimentalOption(
						"excludeSwitches",
						Arrays.asList("enable-automation", "load-extension")
						)

				File tempUserDataDir = Files.createTempDirectory("chrome-user-data").toFile()
				options.addArguments("--user-data-dir=" + tempUserDataDir.getAbsolutePath())

				DriverFactory.changeWebDriver(new ChromeDriver(options))
			}

			try {
				WebUI.setViewPortSize(1280, 1000)
			} catch (ignored) {}
		} else {
			WebUI.openBrowser('')
			try {
				WebUI.setViewPortSize(1280, 1000)
			} catch (ignored) {}
		}
	}



	// -------------------- Helpers --------------------
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
            // ensure ancestors are not hidden
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

	// Get trimmed text from an element via JS (safer for SVG/text)
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
			for (int i = 0; i < 4; i++) {
				sb.append(String.format("%02x", d[i]))
			}
			return sb.toString()
		} catch (Throwable t) {
			return "00000000"
		}
	}

	private static void waitForBusyOverlayToClear(int timeoutSec = 10) {
		long end = System.currentTimeMillis() + (timeoutSec * 1000L)
		while (System.currentTimeMillis() < end) {
			try {
				// Returns true if overlay is present AND visible
				boolean overlayVisible = (Boolean) WebUI.executeJavaScript('''
        var el = document.getElementById('lf-busy-overlay');
        if (!el) return false;
        var style = window.getComputedStyle(el);
        var visible = style && style.display !== 'none' && style.visibility !== 'hidden' && el.offsetParent !== null && el.clientHeight > 0 && el.clientWidth > 0;
        return !!visible;
      ''', null)
				if (!overlayVisible) return
			} catch (ignored) {
				return // JS failed? Treat as cleared.
			}
			WebUI.delay(0.15)
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

	private static boolean waitUntilTextEquals(TestObject to, String expected,
			int timeoutMs = 5000, int intervalMs = 150) {
		long end = System.currentTimeMillis() + timeoutMs
		String last = ""
		while (System.currentTimeMillis() < end) {
			def el = null
			try {
				el = WebUI.findWebElement(to, 2)
			} catch (ignored) {}
			if (el != null) {
				// Prefer innerText; fall back to textContent
				last = (String) WebUI.executeJavaScript(
						"return (arguments[0].innerText || arguments[0].textContent || '').trim();",
						[el]
						)
				if (normalize(last) == normalize(expected)) return true
			}
			WebUI.delay(intervalMs / 1000.0)
		}
		KeywordUtil.logInfo("waitUntilTextEquals: last='${last}' expected='${expected}'")
		return false
	}


	// ${ ... } placeholder resolver for simple JS date expressions
	private static String resolvePlaceholders(String value) {
		if (!value?.contains("\${")) return value

		int openIndex = -1
		int braceDepth = 0
		StringBuilder result = new StringBuilder()

		for (int i = 0; i < value.length(); i++) {
			if (value[i] == '$' && i + 1 < value.length() && value[i + 1] == '{') {
				openIndex = i
				braceDepth = 1
				i++ // skip next '{'
				continue
			}
			if (openIndex >= 0) {
				if (value[i] == '{') {
					braceDepth++
				} else if (value[i] == '}') {
					braceDepth--
					if (braceDepth == 0) {
						String jsCode = value.substring(openIndex + 2, i)
						String evaluated = evaluateJsDateExpression(jsCode)
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

	private static String evaluateJsDateExpression(String script) {
		if (!script?.contains("var d = new Date()")) return script

		Calendar cal = Calendar.getInstance()
		cal.setTime(new Date())

		int day = cal.get(Calendar.DAY_OF_MONTH)
		int month = cal.get(Calendar.MONTH) + 2 // next month
		int yearOffset = 1900
		def matcher = (script =~ /getYear\(\)\s*\+\s*(\d+)/)
		if (matcher.find()) {
			yearOffset = matcher.group(1).toInteger()
		}

		if (month > 12) {
			month -= 12
			cal.add(Calendar.YEAR, 1)
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
		setCalcId(calcName)                // <-- drives log/screenshot names
		boolean hadAnyFailure = false
		def fail = { String step, Exception e, String locator = "", String actual = "", String expected = "" ->
			hadAnyFailure = true
			captureFailure(step, e, locator, actual, expected)
		}

		// helpers
		def normalize = { str ->
			if (str == null) return ""
			String s = Normalizer.normalize(str.toString(), Normalizer.Form.NFKC)

			// Remove zero-width & formatting chars and common troublemakers
			s = s
					.replaceAll(/\u200B|\u200C|\u200D|\u2060|\uFEFF|\u00AD/, "") // ZWSP, ZWNJ, ZWJ, WJ, BOM, soft hyphen
					.replace('\u00A0' as char, ' ' as char)                      // NBSP -> space
					.replace("â€‹", "")                                          // mojibake for ZWSP (defensive)

			// Your existing whitespace/cleanup rules
			s = s
					.replaceAll(/[\t\n\r]+/, " ")
					.replaceAll(/\s+/, " ")
					.replaceAll(/\s+,/, ",")
					.replaceAll(/\s+\./, ".")
					.trim()

			return s
		}

		def buildTO = { String locator ->
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
			} else if (locator.startsWith("name=")) {
				to.addProperty("xpath", ConditionType.EQUALS, "//*[@name='${locator.replace("name=", "")}']")
			} else {
				to.addProperty("id", ConditionType.EQUALS, locator)
			}
			return to
		}

		def getTextWithRetries = { TestObject to, int retries = 5 ->
			String actualText = ""
			while (retries-- > 0) {
				actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
				if (actualText?.trim()) break
					Thread.sleep(200)
			}
			return actualText
		}

		// control-flow helpers
		def evalCondition = { Map cond ->
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

		def ctrlKeyOf = { Map step ->
			if (!step) return null
			def k = step.keySet().iterator().next()
			return (k in ['if', 'elseIf', 'endIf']) ? k : null
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
		Set<String> delayBeforeClickSuffixes = ["-toggle-header"] as Set

		// ======= TEMPLATE DATES (CustomDate*) =======
		Map<String,String> templateVars = [:]


		// Predefined dynamic payoff date: today + 20 years (MM/dd/yyyy)
		Calendar calPayoff = Calendar.getInstance()
		calPayoff.add(Calendar.YEAR, 20)
		String payoff20 = new SimpleDateFormat("MM/dd/yyyy").format(calPayoff.getTime())
		templateVars["PayoffDate20"] = payoff20
		WebUI.comment("🧩 Created template var PayoffDate20 = ${payoff20}")

		def applyTemplates = { String s ->
			if (!s) return s
			return s.replaceAll(/\{\{(\w+)\}\}/) { all, key ->
				templateVars.containsKey(key) ? templateVars[key] : all
			}
		}

		def createCustomDateVar = { String name, Map cfg ->
			String dataFormat = (cfg?.dataFormat ?: "Date").toString()
			String inSpec     = (cfg?.futureDateIn ?: "Years,Months,Days").toString()
			String valSpec    = (cfg?.futureValue  ?: "0,0,0").toString()

			List<String> dims = inSpec.split(/\s*,\s*/).collect { it?.trim()?.toLowerCase() ?: "" }
			List<Integer> vals = valSpec.split(/\s*,\s*/).collect { s ->
				try {
					Integer.parseInt(s?.trim() ?: "0")
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

			String out
			if (dataFormat.equalsIgnoreCase("Date")) {
				out = new SimpleDateFormat("MM/dd/yyyy").format(cal.getTime())
			} else {
				out = new SimpleDateFormat(dataFormat).format(cal.getTime())
			}

			templateVars[name] = out
			WebUI.comment("🧩 Created template var ${name} = ${out}")
		}
		// ============================================

		// ======= STEP EXECUTOR =======
		def executeStep = { String command, Map details ->
			String locator = details.containsKey("id") ? details.id?.toString() : ""
			String value   = details.containsKey("value") ? details.value?.toString() : ""

			// unwrap //.../text() pattern
			def xpathTextPattern = ~/^\s*@?xpath="?\/@id="(.+?)"\/text\(\)\s*"?$/
			if (locator ==~ xpathTextPattern) {
				locator = (locator =~ xpathTextPattern)[0][1]
			}

			// apply templates to the value before using it
			value = applyTemplates(value)

			// --- normalize dynamic Highcharts and “view data table” locators ---
			def hcInput = locator ?: ""

			// 1) Rewrite: xpath=//table[@id='highcharts-data-table-0']/<rest>
			//    →       xpath=//table[starts-with(@id,'highcharts-data-table-')]/<rest>
			if (!(hcInput =~ /starts-with\(@id,\s*['"]highcharts-data-table-/)) {
				def hcMatcher = (hcInput =~
						/(?i)^\s*xpath=\s*\/\/table\[\s*@id\s*=\s*['"]highcharts-data-table-\d+['"]\s*]\s*(\/.*)\s*$/)
				if (hcMatcher.matches()) {
					locator = "xpath=//table[starts-with(@id,'highcharts-data-table-')]${hcMatcher.group(1)}"
				}
			}

			// 2) Highcharts “View data table” link/button (dynamic id)
			if (locator?.contains("hc-linkto-highcharts-data-table-")) {
				locator = "css=[id^='hc-linkto-highcharts-data-table-']"
			}


			TestObject to = buildTO(locator)

			WebUI.comment("▶ Command: ${command} | Locator: ${locator} | Value: ${value}")

			switch (command) {
				case 'delay':
					try {
						int seconds = value?.isInteger() ? value.toInteger() : value?.toDouble()?.toInteger() ?: 0
						WebUI.comment("⏱ Delaying for ${seconds} second(s)")
						WebUI.delay(seconds)
					} catch (Exception e) {
						WebUI.comment("⚠ Invalid delay value: '${value}' – skipping delay.")
					}
					break

				case 'click':
				case 'clickAndWait':
					try {
						def we = WebUI.findWebElement(to)
						WebUI.executeJavaScript("arguments[0].scrollIntoView({block:'center'});", [we])
						WebUI.delay(0.2)

						boolean isSpecial =
								locator.startsWith("lf_add_this_account") ||
								locator.startsWith("lf_view_data_table")  ||
								locator == "lf_help_tab" ||               // <- treat help like a special click
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

						// NEW: wait for overlay to be gone BEFORE clicking
						waitForBusyOverlayToClear(10)

						// NEW: robust retry loop for intercepted clicks
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
								// overlay popped back in; wait and retry
								waitForBusyOverlayToClear(10)
								WebUI.delay(0.2)
								lastErr = ice
							} catch (Exception e) {
								// fallback to JS click once
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

						if (delayAfterClickPrefixes.any { locator.startsWith(it) } ||
								delayAfterClickSuffixes.any { locator.endsWith(it) } ||
								locator == "lf_help_tab") {
							// help opens a modal → give it time
							WebUI.delay(2)
						}
					} catch (Exception e) {
						fail(command, e, locator)
					}

					break

				case 'verifyTitle':
					try {
						String actualTitle = WebUI.getWindowTitle()
						boolean ok = WebUI.verifyMatch(actualTitle?.trim() ?: "", value?.trim() ?: "", false, FailureHandling.CONTINUE_ON_FAILURE)
						if (!ok) fail("verifyTitle", new Exception("Title mismatch"), "title", actualTitle ?: "", value ?: "")
					} catch (Exception e) {
						fail("verifyTitle", e, "title")
					}
					break

				case 'waitForText':
				case 'verifyText':
					String actualText = ""   // <-- declare first so ALL branches can use it

					try {
						// --- Handle CSS locators that may match multiple charts; pick the first VISIBLE one
						if (locator != null && locator.startsWith("css=")) {
							String cssSel = locator.substring(4) // strip "css="
							WebElement el = firstVisibleByCss(cssSel, 10) // wait up to 10s for a visible match
							if (el == null) {
								fail(command, new Exception("No visible element found for CSS selector within timeout"), locator)
								break
							}
							actualText = jsText(el)
							boolean okCss = WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
							if (!okCss) fail(command, new Exception("Text mismatch (visible CSS match)"), locator, actualText ?: "", value ?: "")
							break
						}

						boolean requiresUppercase =
								uppercaseSuffixes.any { locator.endsWith(it) } ||
								uppercasePrefixes.any { locator.startsWith(it) }

						boolean isErrorMsg  = locator.endsWith("-error-message")
						boolean isLabelLike = locator.startsWith("lf_help") ||
								locator.endsWith("_label")   ||
								locator.endsWith("-label")   ||
								locator == "lf-email-message"

						// --- 0) Hidden error messages: require presence, not visibility; read via JS ---
						if (isErrorMsg) {
							boolean present = WebUI.waitForElementPresent(to, 10, FailureHandling.OPTIONAL)
							if (!present) {
								fail(command, new Exception("Error message element not present after 10s"), locator); break
							}

							String jsReadHidden = '''
              var el = arguments[0];
              if (!el) return '';
              var t = (el.innerText || el.textContent || '').trim();
              t = t.replace(/[\\u200B\\u200C\\u200D\\u2060\\uFEFF\\u00AD]/g,'');
              return t;
            '''
							def el = null
							try {
								el = WebUI.findWebElement(to, 5)
							} catch (ignored) {}
							actualText = el ? (String) WebUI.executeJavaScript(jsReadHidden, [el]) : ""

							if (!actualText) {
								int tries = 6
								while (tries-- > 0 && !actualText) {
									WebUI.delay(0.25)
									try {
										el = WebUI.findWebElement(to, 2)
									} catch (ignored) {}
									actualText = el ? (String) WebUI.executeJavaScript(jsReadHidden, [el]) : ""
								}
							}

							boolean okErr = WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
							if (!okErr) fail(command, new Exception("Text mismatch (hidden error)"), locator, actualText ?: "", value ?: "")
							break
						}

						// --- 1) Everyone else: require present + visible before reading ---
						boolean present = WebUI.waitForElementPresent(to, 10, FailureHandling.OPTIONAL)
						boolean visible = WebUI.waitForElementVisible(to, 10, FailureHandling.OPTIONAL)
						if (!present || !visible) {
							fail(command, new Exception("Element not visible after 10s"), locator); break
						}

						// --- 2) Highcharts caption special case ---
						if (locator == "css=table[id^='highcharts-data-table-'] > caption") {
							def txt = (String) WebUI.executeJavaScript(
									"var c=document.querySelector(\"table[id^='highcharts-data-table-'] > caption\");" +
									"return c ? (c.innerText||c.textContent||'').trim() : '';", null)
							boolean okCap = WebUI.verifyMatch(txt ?: "", (value ?: "").trim(), false, FailureHandling.CONTINUE_ON_FAILURE)
							if (!okCap) fail(command, new Exception("Highcharts caption mismatch"), locator, txt ?: "", value ?: "")
							break
						}

						boolean ok

						// --- 3) Uppercase-normalized compares ---
						if (requiresUppercase) {
							actualText = getTextWithRetries(to, 5)
							ok = WebUI.verifyMatch(
									normalize(actualText).toUpperCase(),
									normalize(value).toUpperCase(),
									false,
									FailureHandling.CONTINUE_ON_FAILURE
									)
						}
						// --- 4) Labels / help text via JS ---
						else if (isLabelLike) {
							String jsRead = '''
              var el = arguments[0];
              if (!el) return '';
              try { if (el.scrollIntoView) el.scrollIntoView({block:'center'}); } catch(e){}
              var t = (el.innerText || el.textContent || '').trim();
              t = t.replace(/[\\u200B\\u200C\\u200D\\u2060\\uFEFF\\u00AD]/g,'');
              return t;
            '''
							def el = null
							try {
								el = WebUI.findWebElement(to, 5)
							} catch (ignored) {}
							actualText = el ? (String) WebUI.executeJavaScript(jsRead, [el]) : ""
							if (!actualText) {
								int tries = 6
								while (tries-- > 0 && !actualText) {
									WebUI.delay(0.25)
									try {
										el = WebUI.findWebElement(to, 2)
									} catch (ignored) {}
									actualText = el ? (String) WebUI.executeJavaScript(jsRead, [el]) : ""
								}
							}
							ok = WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
						}
						// --- 5) Default path ---
						else {
							if (locator == 'lf_answer') WebUI.delay(2) // requested 2s pre-wait

							actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
							if (!actualText?.trim()) actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)

							if (locator == 'lf_answer' || locator == 'lf_amortization_title') {
								int retries = 6
								while (retries-- > 0 && normalize(actualText) != normalize(value)) {
									WebUI.delay(0.7)
									actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
									if (!actualText?.trim()) actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)
								}
							}

							ok = WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
						}

						if (!ok) {
							fail(command, new Exception("Text mismatch"), locator, actualText ?: "", value ?: "")
						}
					} catch (Exception e) {
						if (!actualText) actualText = "[Could not retrieve text]"
						WebUI.comment("✖ Failed to verify text on '${locator}': ${e.message}")
						fail(command, e, locator, actualText, value ?: "")
					}
					break


				case 'verifyValue':
					try {
						// Make visibility a hard requirement too
						if (!WebUI.waitForElementVisible(to, 10, FailureHandling.OPTIONAL)) {
							fail(command, new Exception("Element not visible after 10s"), locator)
							return
						}
						String actualVal = WebUI.getAttribute(to, 'value', FailureHandling.OPTIONAL) ?: ""
						WebUI.verifyElementAttributeValue(to, 'value', value, 1, FailureHandling.CONTINUE_ON_FAILURE)
						if (normalize(actualVal) != normalize(value ?: "")) {
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
						boolean okAttr = WebUI.waitForElementAttributeValue(to, 'aria-expanded', value, 8, FailureHandling.CONTINUE_ON_FAILURE)
						if (!okAttr) fail(command, new Exception("Attribute mismatch"), locator, "aria-expanded != ${value}", value)
					} catch (Exception e) {
						fail(command, e, locator)
					}
					break
				case 'setTextAndWait':
				case 'setText':
				case 'type':
					try {
						def el = WebUI.findWebElement(to, 5)
						String inputType = WebUI.executeJavaScript("return arguments[0].type || '';", [el])?.toString()

						// Detect whether this is a plain HTML input (e.g. Google search box)
						// vs a React-managed input. Plain inputs have no React internal instance.
						boolean isReact = (Boolean) WebUI.executeJavaScript(
								"var k = Object.keys(arguments[0]).find(function(k){ return k.startsWith('__reactFiber') || k.startsWith('__reactInternalInstance'); }); return !!k;",
								[el]
								)

						if ("range".equalsIgnoreCase(inputType)) {
							// <input type="range"> — set value + fire events
							WebUI.executeJavaScript(
									"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true})); arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
									[el, value?.toString() ?: ""]
									)
						} else if (!isReact) {
							// Plain input (Google search box, etc.) — use standard Katalon setText
							WebUI.clearText(to, FailureHandling.CONTINUE_ON_FAILURE)
							WebUI.setText(to, value?.toString() ?: "", FailureHandling.CONTINUE_ON_FAILURE)
						} else {
							// React-managed input — use the native-setter + event dispatch path
							WebUI.executeJavaScript('''
                (function(el, val){
                  try {
                    if (el.scrollIntoView) el.scrollIntoView({block:'center'});
                    if (document.activeElement !== el && el.focus) el.focus();

                    var proto = el.constructor && el.constructor.prototype || HTMLInputElement.prototype;
                    var desc  = Object.getOwnPropertyDescriptor(proto, 'value');
                    if (desc && desc.set) { desc.set.call(el, val); } else { el.value = val; }

                    el.dispatchEvent(new Event('input',  {bubbles:true}));
                    el.dispatchEvent(new Event('change', {bubbles:true}));

                    ['keydown','keyup'].forEach(function(t){
                      try { el.dispatchEvent(new KeyboardEvent(t, {key:'Tab', bubbles:true})); } catch(e){}
                    });

                    try { el.dispatchEvent(new FocusEvent('focusout', {bubbles:true})); } catch(e){}
                    if (el.blur) el.blur();
                  } catch(e){}
                })(arguments[0], arguments[1]);
              ''', [el, value?.toString() ?: ""])
						}
						WebUI.delay(0.2)
					} catch (Exception e) {
						fail(command, e, locator, "", value?.toString() ?: "")
					}
					break

				case 'select':
					try {
						try {
							TestObject overlay = new TestObject('busyOverlay')
							overlay.addProperty("id", ConditionType.EQUALS, "lf-busy-overlay")
							WebUI.waitForElementNotVisible(overlay, 5, FailureHandling.OPTIONAL)
							WebUI.selectOptionByLabel(to, value.replace("label=", ""), false, FailureHandling.CONTINUE_ON_FAILURE)
						} catch (Exception e) {
							fail(command, e, locator)
						}
					} catch (Exception e) {
						fail(command, e, locator)
					}
					WebUI.delay(0.5)
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
						boolean okDate = WebUI.verifyMatch(actualDate?.trim() ?: "", value?.trim() ?: "", false, FailureHandling.CONTINUE_ON_FAILURE)
						if (!okDate) fail(command, new Exception("Date mismatch"), locator, actualDate ?: "", value ?: "")
					} catch (Exception e) {
						fail(command, e, locator)
					}
					break

				case 'setDate':
					try {
						String outVal = value
						if (value?.contains("var d = new Date()")) {
							Calendar cal = Calendar.getInstance()
							cal.setTime(new Date())
							int day = cal.get(Calendar.DAY_OF_MONTH)
							int month = cal.get(Calendar.MONTH) + 2
							int yearOffset = 1900
							def m = (value =~ /getYear\(\)\s*\+\s*(\d+)/)
							if (m.find()) yearOffset = m.group(1).toInteger()
							if (month > 12) {
								month -= 12; cal.add(Calendar.YEAR, 1)
							}
							int year = cal.get(Calendar.YEAR) - 1900 + yearOffset
							String pd = day < 10 ? "0${day}" : "${day}"
							String pm = month < 10 ? "0${month}" : "${month}"
							outVal = "${pm}/${pd}/${year}"
						}
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
						boolean okHc = WebUI.verifyMatch((actualLabel ?: "").trim(), (value ?: "").trim(), false, FailureHandling.CONTINUE_ON_FAILURE)
						if (!okHc) fail(command, new Exception("Highcharts label mismatch"), locator, actualLabel ?: "", value ?: "")
					} catch (Exception e) {
						fail(command, e, locator)
					}
					break

				case 'navigateTo':
					try {
						String urlToGo = locator ?: value
						WebUI.comment("🌐 navigateTo → " + urlToGo)
						WebUI.navigateToUrl(urlToGo)
						WebUI.delay(1)
					} catch (Exception e) {
						fail(command, e, locator)
					}
					break



				case 'sendKeys':
					try {
						def el = WebUI.findWebElement(to, 10)
						if (value == '${KEY_ENTER}') {
							el.sendKeys(Keys.ENTER)
						} else if (value == '${KEY_TAB}') {
							el.sendKeys(Keys.TAB)
						} else {
							el.sendKeys(value)
						}
					} catch (Exception e) {
						fail(command, e, locator)
					}
					break

				case 'waitForElementPresent':
					try {
						int timeoutMs = value?.isInteger() ? value.toInteger() : 10000
						int timeoutSec = (int)(timeoutMs / 1000)
						if (timeoutSec < 1) timeoutSec = 1
						WebUI.waitForElementPresent(to, timeoutSec, FailureHandling.CONTINUE_ON_FAILURE)
					} catch (Exception e) {
						fail(command, e, locator)
					}
					break

				case 'verifyTextPresent':
					try {
						String searchText = locator
						boolean found = false
						long deadline = System.currentTimeMillis() + 10000L
						while (System.currentTimeMillis() < deadline) {
							found = (Boolean) WebUI.executeJavaScript('''
								var txt = arguments[0];
								var walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);
								while (walker.nextNode()) {
									if (walker.currentNode.nodeValue && walker.currentNode.nodeValue.indexOf(txt) !== -1) return true;
								}
								return false;
							''', Arrays.asList(searchText))
							if (found) break
								Thread.sleep(300)
						}
						if (!found) {
							fail(command, new Exception("Text not present on page: '" + searchText + "'"), locator, "", searchText)
						}
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
			def json = new JsonSlurper().parse(new File(filePath)) as List

			// --- navigateTo override (first step) ---
			String overrideUrl = ""

			// Accept navigateTo.{id} OR navigateTo.{value} as first-step override
			if (json && json[0] instanceof Map && json[0].containsKey("navigateTo")) {
				def nav = json[0].navigateTo
				if (nav.id) {
					overrideUrl = nav.id.toString()
					json.remove(0)
				} else if (nav.value) {
					overrideUrl = nav.value.toString()
					json.remove(0)
				}
			}

			// --- preprocess CustomDate* steps and remove them ---
			int idx = 0
			while (idx < json.size()) {
				def step = json[idx] as Map
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
					json.remove(idx)
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

				// Helpful one-time debug if base is null
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

				// Only the URL needs a spouse-stripped id
				String runtimeId = calcName.contains("spouse") ? calcName.replace("spouse","") : calcName
				url = "${base}/calc/${runtimeId}?newuser"
			}

			logStep("🌐 Opening URL: ${url}")
			WebUI.comment("🌐 Opening URL: ${url}")

			int attempt = 0, maxRetries = 1
			boolean success = false
			while (attempt < maxRetries && !success) {
				try {
					attempt++
					WebUI.comment("Attempt ${attempt} → ${url}")
					WebUI.navigateToUrl(url)
					success = true
				} catch (Exception e) {
					WebUI.comment("Navigation failed on attempt ${attempt}: ${e.message}")
					if (attempt == maxRetries) throw e
					WebUI.delay(2)
				}
			}

			// --- execute with control flow (if / elseIf / endIf) ---
			int i = 0
			while (i < json.size()) {
				Map step = (Map) json[i]
				String command = step.keySet().iterator().next()
				def details = step[command]

				if (command == 'if') {
					boolean matched = evalCondition(details as Map)
					i++ // move past 'if'

					if (matched) {
						while (i < json.size()) {
							Map s = (Map) json[i]
							String ck = ctrlKeyOf(s)
							if (ck == 'elseIf') {
								// skip to matching endIf
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
								i++ // consume endIf
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
						// scan elseIf; run first match; then jump to endIf
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
								i++ // nothing matched
								break
							} else {
								i++
							}
						}
						continue
					}
				} else if (command == 'elseIf' || command == 'endIf') {
					i++ // stray, skip
					continue
				}

				// normal step with template expansion
				if (details instanceof Map && details.containsKey('value')) {
					details.value = applyTemplates(details.value?.toString())
				}
				executeStep(command, (Map) details)
				i++
			}

			// --- enforce red step if any prior failure happened ---
			if (!(isRunningInStudio() && isDebugging())) {
				WebUI.verifyEqual(hadAnyFailure, false, FailureHandling.STOP_ON_FAILURE)
			} else {
				WebUI.comment("🟡 DEBUG(STUDIO): run completed with failures=${failures()} (not stopping)")
			}
		} finally {
			try {
				//WebUI.closeBrowser()
			} catch (ignored) {}
		}
	}
}
