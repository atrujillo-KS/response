package com.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.configuration.RunConfiguration

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.net.HttpURLConnection
import java.text.SimpleDateFormat

/**
 * CC V2 API Test Runner
 *
 * Reads test definitions from ccv2_tests.json and executes them sequentially.
 * Each test: authenticate -> run steps (POST/GET/PATCH/DELETE) -> verify responses.
 *
 * Usage in Test Case:
 *   import com.utils.CCV2Runner as CC
 *   CustomKeywords.'com.utils.CCV2Runner.runTest'('PAM_Category_ALL_Endpoints')
 *   assert CC.lastRunFailed() == false
 */
class CCV2Runner {

	public static final String VERSION = "1.0.0"

	// --- Per-thread state ---
	private static final ThreadLocal<Boolean> TL_LAST_FAILED = ThreadLocal.withInitial { false }
	private static final ThreadLocal<String>  TL_LOG_FILE    = new ThreadLocal<>()
	private static final ThreadLocal<Boolean> TL_LOG_READY   = ThreadLocal.withInitial { false }

	static boolean lastRunFailed() { Boolean.TRUE.equals(TL_LAST_FAILED.get()) }

	// --- Logging ---
	private static void prepareLogging(String testName) {
		if (TL_LOG_READY.get()) return
		String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
		String safe = testName.replaceAll('[^a-zA-Z0-9._-]+', '_')
		TL_LOG_FILE.set("ccv2_${safe}_${ts}.txt")
		File dir = new File(RunConfiguration.getProjectDir() + "/failedLogs")
		if (!dir.exists()) dir.mkdirs()
		TL_LOG_READY.set(true)
	}

	private static void log(String msg) {
		println msg
		try {
			if (TL_LOG_FILE.get()) {
				new File(RunConfiguration.getProjectDir() + "/failedLogs/${TL_LOG_FILE.get()}").append(msg + "\n")
			}
		} catch (ignored) {}
	}

	// --- OAuth2 token ---
	private static String getAccessToken(String baseUrl, String tokenPath, String authHeader) {
		log("=== Requesting Access Token ===")
		String url = "${baseUrl}${tokenPath}"

		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection()
		conn.setRequestMethod("POST")
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
		conn.setRequestProperty("Authorization", authHeader)
		conn.setDoOutput(true)
		conn.outputStream.write("grant_type=client_credentials".getBytes("UTF-8"))
		conn.outputStream.flush()

		int code = conn.responseCode
		String body = (code >= 200 && code < 300) ? conn.inputStream.text : (conn.errorStream?.text ?: "")
		conn.disconnect()

		if (code != 200) {
			throw new Exception("Token request failed: HTTP ${code} - ${body}")
		}

		def json = new JsonSlurper().parseText(body)
		String token = json.access_token
		if (!token) throw new Exception("No access_token in response: ${body}")
		log("   Token acquired (${token.take(20)}...)")
		return token
	}

	// --- HTTP request helper ---
	private static Map doRequest(String method, String url, String token, Map bodyMap = null) {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection()
		conn.setRequestMethod(method == "PATCH" ? "POST" : method)
		if (method == "PATCH") {
			conn.setRequestProperty("X-HTTP-Method-Override", "PATCH")
		}
		conn.setRequestProperty("Content-Type", "application/json")
		conn.setRequestProperty("Authorization", "Bearer ${token}")
		conn.setConnectTimeout(30000)
		conn.setReadTimeout(30000)

		if (bodyMap != null && method != "GET") {
			conn.setDoOutput(true)
			String jsonBody = JsonOutput.toJson(bodyMap)
			conn.outputStream.write(jsonBody.getBytes("UTF-8"))
			conn.outputStream.flush()
		}

		int code = conn.responseCode
		String responseBody = ""
		try {
			responseBody = (code >= 200 && code < 400) ? conn.inputStream.text : (conn.errorStream?.text ?: "")
		} catch (Exception e) {
			try { responseBody = conn.errorStream?.text ?: "" } catch (ignored) {}
		}
		conn.disconnect()

		def parsed = null
		try { parsed = new JsonSlurper().parseText(responseBody) } catch (ignored) {}

		return [status: code, body: responseBody, json: parsed]
	}

	// --- Variable substitution in strings and maps ---
	private static String substituteVars(String s, Map<String, Object> vars) {
		if (!s || !vars) return s
		String result = s
		vars.each { k, v ->
			result = result.replace('${' + k + '}', v?.toString() ?: "")
		}
		return result
	}

	private static Object substituteDeep(Object obj, Map<String, Object> vars) {
		if (obj instanceof String) return substituteVars((String) obj, vars)
		if (obj instanceof Map) {
			Map result = [:]
			((Map) obj).each { k, v -> result[substituteVars(k.toString(), vars)] = substituteDeep(v, vars) }
			return result
		}
		if (obj instanceof List) {
			return ((List) obj).collect { substituteDeep(it, vars) }
		}
		return obj
	}

	// --- Extract value from nested JSON using dot-path with array filter support ---
	// Supports: "data.categories[0].id", "data.categories[?name=='Test'].id", "data.status"
	private static Object extractPath(Object json, String path) {
		if (json == null || !path) return null

		String[] parts = splitPath(path)
		Object current = json

		for (String part : parts) {
			if (current == null) return null

			// Array index: field[0]
			def indexMatch = (part =~ /^(.+?)\[(\d+)\]$/)
			if (indexMatch.matches()) {
				String field = indexMatch[0][1]
				int idx = indexMatch[0][2] as int
				current = (current instanceof Map) ? current[field] : null
				if (current instanceof List && idx < ((List) current).size()) {
					current = ((List) current)[idx]
				} else {
					return null
				}
				continue
			}

			// Array filter: field[?key=='value'] or field[?key==varRef]
			def filterMatch = (part =~ /^(.+?)\[\?(.+?)==['\"]?(.*?)['\"]?\]$/)
			if (filterMatch.matches()) {
				String field = filterMatch[0][1]
				String filterKey = filterMatch[0][2]
				String filterVal = filterMatch[0][3]
				current = (current instanceof Map) ? current[field] : null
				if (current instanceof List) {
					current = ((List) current).find { item ->
						if (item instanceof Map) {
							String actual = item[filterKey]?.toString() ?: ""
							return actual == filterVal
						}
						return false
					}
				} else {
					return null
				}
				continue
			}

			// Escaped dot in key (e.g. "example\.com")
			String cleanPart = part.replace('\\', '')
			current = (current instanceof Map) ? current[cleanPart] : null
		}

		return current
	}

	// Split path on dots but respect escaped dots and brackets
	private static String[] splitPath(String path) {
		List<String> parts = []
		StringBuilder current = new StringBuilder()
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i)
			if (c == '\\' as char && i + 1 < path.length() && path.charAt(i + 1) == '.' as char) {
				current.append('\\.')
				i++
			} else if (c == '.' as char) {
				if (current.length() > 0) parts.add(current.toString())
				current = new StringBuilder()
			} else {
				current.append(c)
			}
		}
		if (current.length() > 0) parts.add(current.toString())
		return parts as String[]
	}

	// --- Main runner ---
	@Keyword
	static void runTest(String testName) {
		TL_LAST_FAILED.set(false)
		prepareLogging(testName)

		log("=== CCV2Runner v${VERSION} | test=${testName} ===")

		boolean failed = false

		try {
			// Load test definitions
			String filePath = RunConfiguration.getProjectDir() + "/Include/resources/ccv2_tests.json"
			def config = new JsonSlurper().parse(new File(filePath))

			String baseUrl = config.config.baseUrl
			String tokenUrl = config.config.tokenUrl
			String authHeader = config.config.authHeader

			// Find the test by name
			def test = config.tests.find { it.name == testName }
			if (!test) {
				throw new Exception("Test '${testName}' not found in ccv2_tests.json")
			}

			// Step 1: Get access token
			String token = getAccessToken(baseUrl, tokenUrl, authHeader)

			// Extracted variables (carry between steps)
			Map<String, Object> vars = [:]

			// Execute each step
			List steps = test.steps as List
			for (int i = 0; i < steps.size(); i++) {
				Map step = steps[i] as Map
				String stepName = step.name ?: "Step ${i + 1}"
				String method = step.method ?: "GET"
				String path = substituteVars(step.path as String, vars)
				String fullUrl = "${baseUrl}${path}"

				log("\n--- STEP ${i + 1}: ${stepName} [${method}] ---")
				log("   URL: ${fullUrl}")

				// Substitute variables in request body
				Map bodyMap = step.body ? (Map) substituteDeep(step.body, vars) : null
				if (bodyMap) log("   Body: ${JsonOutput.toJson(bodyMap)}")

				// Execute request
				Map response = doRequest(method, fullUrl, token, bodyMap)
				log("   Response: HTTP ${response.status}")
				if (response.body) {
					String preview = response.body.toString()
					if (preview.length() > 500) preview = preview.take(500) + "..."
					log("   Body: ${preview}")
				}

				// Verify expected status
				Map expect = (step.expect ?: [:]) as Map
				if (expect.status) {
					int expectedStatus = expect.status as int
					if (response.status != expectedStatus) {
						log("   FAIL: Expected HTTP ${expectedStatus}, got ${response.status}")
						failed = true
						break
					}
					log("   PASS: HTTP ${response.status}")
				}

				// Verify expected body values
				if (expect.body && response.json) {
					Map bodyExpect = expect.body as Map
					boolean bodyOk = true
					bodyExpect.each { String exPath, Object exVal ->
						String resolvedPath = substituteVars(exPath, vars)
						Object actual = extractPath(response.json, resolvedPath)

						String exValStr = (exVal instanceof String) ? substituteVars((String) exVal, vars) : exVal?.toString()
						String actualStr = actual?.toString() ?: ""

						if (exVal instanceof List) {
							boolean match = (actual instanceof List) && (actual as List).collect { it?.toString() } == (exVal as List).collect { it?.toString() }
							if (!match) {
								log("   FAIL: ${resolvedPath} expected=${exVal}, actual=${actual}")
								bodyOk = false
							} else {
								log("   PASS: ${resolvedPath} = ${actual}")
							}
						} else if (actualStr != exValStr) {
							log("   FAIL: ${resolvedPath} expected='${exValStr}', actual='${actualStr}'")
							bodyOk = false
						} else {
							log("   PASS: ${resolvedPath} = '${actualStr}'")
						}
					}
					if (!bodyOk) {
						failed = true
						break
					}
				}

				// Verify absent values
				if (expect.bodyAbsent && response.json) {
					Map absentExpect = expect.bodyAbsent as Map
					absentExpect.each { String exPath, Object _ ->
						String resolvedPath = substituteVars(exPath, vars)
						Object actual = extractPath(response.json, resolvedPath)
						if (actual != null) {
							log("   FAIL: ${resolvedPath} should be absent but found: ${actual}")
							failed = true
						} else {
							log("   PASS: ${resolvedPath} is absent (as expected)")
						}
					}
					if (failed) break
				}

				// Extract variables for next steps
				if (step.extract && response.json) {
					Map extracts = step.extract as Map
					extracts.each { String varName, String exPath ->
						String resolvedPath = substituteVars(exPath, vars)
						Object value = extractPath(response.json, resolvedPath)
						if (value != null) {
							vars[varName] = value
							log("   Extracted: ${varName} = ${value}")
						} else {
							log("   WARN: Could not extract '${varName}' from path '${resolvedPath}'")
						}
					}
				}
			}
		} catch (Exception e) {
			log("FATAL: ${e.message}")
			failed = true
		}

		TL_LAST_FAILED.set(failed)

		if (failed) {
			log("\n=== TEST FAILED: ${testName} ===")
			KeywordUtil.markFailedAndStop("CCV2 test '${testName}' failed. See failedLogs/${TL_LOG_FILE.get()}")
		} else {
			log("\n=== TEST PASSED: ${testName} ===")
			KeywordUtil.markPassed("CCV2 test '${testName}' PASSED")
		}
	}

	/** Run all tests in the JSON file */
	@Keyword
	static void runAll() {
		String filePath = RunConfiguration.getProjectDir() + "/Include/resources/ccv2_tests.json"
		def config = new JsonSlurper().parse(new File(filePath))
		List<String> failed = []

		config.tests.each { test ->
			String name = test.name
			try {
				TL_LAST_FAILED.set(false)
				runTest(name)
				if (lastRunFailed()) failed.add(name)
			} catch (Exception e) {
				failed.add(name)
				println "Test '${name}' failed: ${e.message}"
			}
		}

		if (failed) {
			KeywordUtil.markFailedAndStop("CCV2: ${failed.size()} test(s) failed: ${failed.join(', ')}")
		} else {
			KeywordUtil.markPassed("CCV2: All ${config.tests.size()} tests passed")
		}
	}
}
