package com.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.Keys
import groovy.json.JsonSlurper
import java.text.SimpleDateFormat
import com.kms.katalon.core.webui.driver.DriverFactory
import java.util.Date
import java.io.File
import java.util.Arrays
import java.nio.file.Files

class MainValidatorBasic {

	private static boolean logWritten = false
	private static String logFileName = ""
	private static String screenshotPrefix = ""
	private static String screenshotDirPath = ""

	private static void prepareLogging(calcId) {
		if (!logWritten || !logFileName) {
			// Re-initialize if filename is empty
			String freshTimestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
			logFileName = "${calcId}_${freshTimestamp}.txt"
			screenshotPrefix = "screenshot_${freshTimestamp}"
			screenshotDirPath = RunConfiguration.getProjectDir() + "/Screenshots/${calcId}"
			new File(screenshotDirPath).mkdirs()

			String logDirPath = RunConfiguration.getProjectDir() + "/failedLogs"
			File logDir = new File(logDirPath)
			if (!logDir.exists()) {
				logDir.mkdirs()
				println "📁 Created failedLogs directory: ${logDirPath}"
			}

			logWritten = true
			println "✅ Logging initialized: ${logFileName} in ${logDirPath}"
		}
	}


	private static void logStep(String message) {
		prepareLogging("budget03")
		println message
		try {
			def logFile = new File(RunConfiguration.getProjectDir() + "/failedLogs/${logFileName}")
			logFile << message + "\n"
		} catch (Exception e) {
			println "❌ Failed to write to log file: ${e.message}"
		}
	}

	static void captureFailure(String step, Exception e, String locator = "", String actual = "", String expected = "") {
		prepareLogging("budget03")
		String idPart = locator ? locator.replaceAll("[^a-zA-Z0-9]+", "_") : "unknown"
		String screenshotPath = "${screenshotDirPath}/${screenshotPrefix}_${step}_${idPart}_failure.png"

		if (true) {
			try {
				WebUI.takeScreenshot(screenshotPath)
				logStep("❌ ${step} → ${e.message} (screenshot: ${screenshotPath})")
			} catch (Exception ex) {
				logStep("❌ ${step} → ${e.message} (screenshot capture failed: ${ex.message})")
			}
		} else {
			logStep("❌ ${step} → ${e.message} (screenshot skipped in headless mode)")
		}

		if (actual || expected) {
			logStep("Expected: '${expected}' but found: '${actual}'")
		}
		KeywordUtil.markFailed("Failure at ${step}: ${e.message}")
	}

	private static void openBrowserWithHeadlessSupport() {
		if (true) {
			String browserType = 'chrome'

			if (browserType == 'firefox') {
				File profileDir = Files.createTempDirectory("ff-profile").toFile()
				FirefoxOptions options = new FirefoxOptions()
				options.addArguments("--headless")
				options.addArguments("--width=1920")
				options.addArguments("--height=1080")
				options.addArguments("--profile")
				options.addArguments(profileDir.getAbsolutePath())
				DriverFactory.changeWebDriver(new FirefoxDriver(options))
			} else {
				ChromeOptions options = new ChromeOptions()
				options.addArguments('--headless=new')
				options.addArguments('--disable-gpu')
				options.addArguments('--window-size=1920,1080')
				options.addArguments('--no-sandbox')
				options.addArguments('--disable-dev-shm-usage')
				File tempUserDataDir = Files.createTempDirectory("chrome-user-data").toFile()
				options.addArguments("--user-data-dir=" + tempUserDataDir.getAbsolutePath())
				DriverFactory.changeWebDriver(new ChromeDriver(options))
			}
		} else {
			WebUI.openBrowser('')
		}
	}

	private static String getTextWithRetries(TestObject to, int retries = 5) {
		String actualText = ""
		while (retries-- > 0) {
			actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
			if (actualText?.trim()) break
				WebUI.delay(0.2)
		}
		return actualText
	}

	@Keyword
	static void runFromJson(String calcId) {
		String runtimeId = calcId.contains('spouse') ? calcId.replace('spouse', '') : calcId

		openBrowserWithHeadlessSupport()

		String url = "https://qa.awsc.leadfusion.com/response/qatest/calc/${runtimeId}?newuser"
		int maxRetries = 3
		int attempt = 0
		boolean success = false

		while (attempt < maxRetries && !success) {
			try {
				attempt++
				WebUI.comment("Attempt $attempt to navigate to URL")
				WebUI.navigateToUrl(url)
				success = true
			} catch (Exception e) {
				WebUI.comment("Navigation failed on attempt $attempt: ${e.message}")
				if (attempt == maxRetries) throw e
				WebUI.delay(2)
			}
		}

		def normalize = { str ->
			str?.toString()
					?.replaceAll("[\t\n\r]+", " ")
					?.replaceAll("\\s+", " ")
					?.replaceAll("\\s+,", ",")
					?.replaceAll("\\s+\\.", ".")
					?.trim() ?: ""
		}

		Set<String> uppercaseSuffixes = ["-accordion", "-tab-label"]
		Set<String> uppercasePrefixes = [
			"lf_tab",
			"crossfield_button",
			"lf_add_this_account_label",
			"lf_next_steps_label"
		]
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
		]
		Set<String> delayAfterClickSuffixes = ["-accordion"]
		Set<String> delayBeforeClickSuffixes = ["-toggle-header"]
		Set<String> EnterOverClickPrefixes = ["lf_view_data_table"]

		String filePath = RunConfiguration.getProjectDir() + "/Include/resources/${calcId}.json"
		def json = new JsonSlurper().parse(new File(filePath))

		for (def step : json) {
			for (def entry : step.entrySet()) {
				String command = entry.key
				def details = entry.value
				String locator = details.containsKey("id") ? details.id : ""
				String value = details.containsKey("value") ? details.value : ""

				def xpathTextPattern = ~/^\s*@?xpath="?\/@id="(.+?)"\/text\(\)\s*"?$/
				if (locator ==~ xpathTextPattern) {
					locator = (locator =~ xpathTextPattern)[0][1]
				}

				TestObject to = new TestObject(locator)
				if (locator.startsWith("xpath=")) {
					to.addProperty("xpath", ConditionType.EQUALS, locator.replace("xpath=", ""))
				} else if (locator.startsWith("//")) {
					to.addProperty("xpath", ConditionType.EQUALS, locator)
				} else if (locator.startsWith("css=")) {
					to.addProperty("css", ConditionType.EQUALS, locator.replace("css=", ""))
				} else if (locator.startsWith("link=")) {
					to.addProperty("xpath", ConditionType.EQUALS, "//a[text()='${locator.replace("link=", "")}']")
				} else if (locator) {
					to.addProperty("id", ConditionType.EQUALS, locator)
				}

				WebUI.comment("▶ Command: $command | Locator: $locator | Value: $value")

				try {
					switch (command) {
						case 'delay':
							try {
								int seconds = value.isInteger() ? value.toInteger() : value.toString().toDouble().toInteger()
								WebUI.comment("⏱ Delaying for ${seconds} second(s)")
								WebUI.delay(seconds)
							} catch (Exception e) {
								WebUI.comment("⚠ Invalid delay value: '${value}' – skipping delay.")
							}
							break
						case 'click':
						case 'clickAndWait':
							if (delayBeforeClickSuffixes.any { locator.endsWith(it) }) {
								WebUI.delay(1.5)
							}
							WebUI.executeJavaScript("arguments[0].scrollIntoView({block: 'center'});", Arrays.asList(WebUI.findWebElement(to)))
							WebUI.delay(0.3)
							if (!locator.startsWith("lf_view_data_table")) {
								WebUI.waitForElementVisible(to, 1, FailureHandling.OPTIONAL)
								WebUI.waitForElementClickable(to, 1, FailureHandling.OPTIONAL)
							}

							if (locator.endsWith("-accordion")) {
								try {
									WebUI.sendKeys(to, Keys.chord(Keys.ENTER))
								} catch (Exception e) {
									try {
										WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(to)))
									} catch (Exception ex) {
										captureFailure(command, ex, locator)
									}
								}
							} else {
								boolean clicked = false
								try {
									if (locator.startsWith("lf_view_data_table")) {
										try {
											WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(to)))
											WebUI.delay(1)
										} catch (Exception ex) {
											captureFailure(command, ex, locator)
										}
									} else {
										WebUI.click(to, FailureHandling.OPTIONAL)
									}

									clicked = true
								} catch (Exception e) {
									// WebUI.comment("⚠️ Normal click failed — intercepted, falling back to JS click for: $locator")
								}
								if (!clicked) {
									try {
										WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(to)))
									} catch (Exception ex) {
										captureFailure(command, ex, locator)
									}
								}
							}
							if (
							delayAfterClickPrefixes.any { locator.startsWith(it) } ||
									delayAfterClickSuffixes.any {
										locator.endsWith(it)
									}
									) {
								if (locator.startsWith("lf_graph_tabs_lf_container") ||
										locator.startsWith("lf_help_tab") ||
										locator.startsWith("lf-results-tab-label")) {
									WebUI.delay(2)
								} else {
									WebUI.delay(0.8)
								}
							}
							break
						case 'verifyTitle':
							String actualTitle = WebUI.getWindowTitle()
							boolean passed = WebUI.verifyMatch(actualTitle.trim(), value.trim(), false, FailureHandling.CONTINUE_ON_FAILURE)
							if (!passed) {
								captureFailure("verifyTitle", new Exception("Title mismatch"), "title", actualTitle, value)
							}
							break

						case 'waitForText':
						case 'verifyText':
							String actualText = ""
							try {
								boolean requiresUppercase = uppercaseSuffixes.any { locator.endsWith(it) } || uppercasePrefixes.any { locator.startsWith(it) }
								if (locator.endsWith("-error-message")) {
									actualText = WebUI.getAttribute(to, "innerText")
									WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
								} else if (requiresUppercase) {
									actualText = getTextWithRetries(to, 5)
									WebUI.verifyMatch(actualText.trim(), actualText.trim().toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE)
								} else if (locator.startsWith("lf_help") || locator.endsWith("-label")) {
									actualText = getTextWithRetries(to, 5)
									WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
								} else {
									actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
									if (!actualText?.trim()) {
										actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)
									}

									if (locator == 'lf_answer' || locator == 'lf_amortization_title') {
										int retries = 6
										while (retries-- > 0) {
											if (normalize(actualText) == normalize(value)) break
												WebUI.delay(0.5)
											actualText = WebUI.getText(to, FailureHandling.OPTIONAL)
											if (!actualText?.trim()) {
												actualText = WebUI.getAttribute(to, "textContent", FailureHandling.OPTIONAL)
											}
										}
									}

									WebUI.verifyMatch(normalize(actualText), normalize(value), false, FailureHandling.CONTINUE_ON_FAILURE)
								}
							} catch (Exception e) {
								if (!actualText) actualText = "[Could not retrieve text]"
								WebUI.comment("✖ Failed to verify text on '${locator}': ${e.message}")
								captureFailure(command, e, locator, actualText, value)
							}
							break

						case 'verifyValue':
							String actualVal = WebUI.getAttribute(to, 'value', FailureHandling.OPTIONAL)
							try {
								WebUI.verifyElementAttributeValue(to, 'value', value, 1, FailureHandling.CONTINUE_ON_FAILURE)
							} catch (Exception e) {
								captureFailure(command, e, locator, actualVal, value)
							}
							break

						case 'verifyAttribute':
						case 'waitForAttribute':
							try {
								WebUI.waitForElementAttributeValue(to, 'aria-expanded', value, 8, FailureHandling.CONTINUE_ON_FAILURE)
							} catch (Exception e) {
								captureFailure(command, e, locator)
							}
							break
						case 'setTextAndWait':
						case 'setText':
						case 'type':
							try {
								String tag = WebUI.executeJavaScript("return arguments[0].tagName.toLowerCase();", Arrays.asList(WebUI.findWebElement(to)))?.toString()
								String inputType = WebUI.executeJavaScript("return arguments[0].type;", Arrays.asList(WebUI.findWebElement(to)))?.toString()

								if (inputType == 'range') {
									WebUI.executeJavaScript("arguments[0].value = ${value}; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));", Arrays.asList(WebUI.findWebElement(to)))
									WebUI.delay(0.5)
								} else {
									WebUI.sendKeys(to, Keys.chord(Keys.CONTROL, 'a'), FailureHandling.OPTIONAL)
									WebUI.sendKeys(to, Keys.chord(Keys.BACK_SPACE), FailureHandling.OPTIONAL)
									WebUI.setText(to, value, FailureHandling.OPTIONAL)
									WebUI.sendKeys(to, Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)
								}
							} catch (Exception e) {
								captureFailure(command, e, locator, "", value)
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
									captureFailure(command, e, locator)
								}
							} catch (Exception e) {
								captureFailure(command, e, locator)
							}
							WebUI.delay(0.5)
							break

						case 'selectWindow':
							try {
								WebUI.switchToWindowIndex(1)
							} catch (Exception e) {
								captureFailure(command, e, locator)
							}
							break

						case 'close':
							try {
								if (locator.toLowerCase().contains("win_")) {
									WebUI.closeWindowIndex(1)
									WebUI.switchToWindowIndex(0)
								} else {
									WebUI.click(to)
								}
							} catch (Exception e) {
								captureFailure(command, e, locator)
							}
							break


						default:
							WebUI.comment("⚠ Unsupported command: $command")
					}
				} catch (Exception e) {
					KeywordUtil.markWarning("⚠️ Step failed but continued. Reason: ${e.message}")
					captureFailure(command, e, locator)
				}
			}
		}

		WebUI.closeBrowser()
	}
}
