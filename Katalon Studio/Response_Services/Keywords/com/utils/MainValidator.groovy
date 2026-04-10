package com.utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.exception.StepFailedException

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.text.SimpleDateFormat
import java.net.URLEncoder
import java.text.DecimalFormat
import java.math.RoundingMode
import java.util.Calendar
import java.util.TimeZone

// Response Services Script
class MainValidator {

	// ---------- Per-thread fail flags (safe for parallel Suite Collections) ----------
	private static final ThreadLocal<Boolean> TL_LAST_FAILED =
	ThreadLocal.withInitial({ false } as java.util.function.Supplier<Boolean>)
	static boolean lastRunFailed() {
		Boolean.TRUE.equals(TL_LAST_FAILED.get())
	}
	private static void resetRunFlag()       {
		TL_LAST_FAILED.set(false)
	}
	private static void markLastRunFailed()  {
		TL_LAST_FAILED.set(true)
	}

	// Strong, deterministic fail counter (in addition to the boolean flag)
	private static final ThreadLocal<Integer> TL_FAIL_COUNT =
	ThreadLocal.withInitial({ 0 } as java.util.function.Supplier<Integer>)
	private static void incFailCount()   {
		TL_FAIL_COUNT.set(TL_FAIL_COUNT.get() + 1)
	}
	private static int  failCount()      {
		return TL_FAIL_COUNT.get()
	}
	private static void resetFailCount() {
		TL_FAIL_COUNT.set(0)
	}


	// ---------- constants ----------
	private static final String TEST_TZ = 'America/Los_Angeles'

	// ---------- Safe GlobalVariable helpers ----------
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

	// ---------- logging ----------
	private static boolean logInitialized = false
	private static String logFileName = ''

	private static void prepareLogging(String calcId) {
		if (logInitialized && logFileName) return
			String ts = new SimpleDateFormat('yyyyMMdd_HHmm').format(new Date())
		logFileName = "${calcId}_${ts}.txt"
		File logDir = new File(RunConfiguration.getProjectDir() + '/failedLogs')
		if (!logDir.exists()) logDir.mkdirs()
		logInitialized = true
		println "Logging initialized: ${logFileName}"
	}

	// Save JSON body to file (pretty if possible)
	private static void saveJson(String tag, String raw) {
		try {
			String pretty = raw
			try {
				pretty = JsonOutput.prettyPrint(raw)
			} catch (ignored) {}
			String base = logFileName?.replaceFirst(/\.txt$/, '') ?: ('resp_' + System.currentTimeMillis())
			File out = new File(RunConfiguration.getProjectDir() + "/failedLogs/${base}__${tag}.json")
			out.text = pretty
			logStep("📄 Saved ${tag} response to file: ${out.name}")
		} catch (Exception e) {
			logStep("⚠️ Could not save ${tag} response: ${e.message}")
		}
	}

	// Writes to file AND Test Case log (chunked so it shows up reliably)
	private static void logPreview(String tag, String raw, int maxChunk = 1200) {
		if (raw == null) {
			logStep("(${tag}) response is null")
			try {
				KeywordUtil.logInfo("(${tag}) response is null")
			} catch (ignored) {}
			return
		}
		String banner = "===== ${tag} response preview (${raw.length()} chars) ====="
		logStep(banner)
		try {
			KeywordUtil.logInfo(banner)
		} catch (ignored) {}

		int i = 0
		while (i < raw.length()) {
			int j = Math.min(i + maxChunk, raw.length())
			String chunk = raw.substring(i, j)
			logStep(chunk)                                   // file
			try {
				KeywordUtil.logInfo(chunk)
			} catch (ignored) {}  // TC log
			i = j
		}

		String end = "===== END ${tag} response preview (${raw.length()} chars) ====="
		logStep(end)
		try {
			KeywordUtil.logInfo(end)
		} catch (ignored) {}
	}

	private static void logStep(String message) {
		println message
		try {
			new File(RunConfiguration.getProjectDir() + "/failedLogs/${logFileName}") << (message + '\n')
		} catch (Exception e) {
			println "Failed to write log: ${e.message}"
		}
	}

	// ---------- Create a RED sub-step without stopping ----------
	private static void recordRedStep(String ctx, String field, String attr, String expected, String actual) {
		try {
			// Create a red verification sub-step; continue so we collect all failures
			WS.verifyEqual(actual, expected, FailureHandling.CONTINUE_ON_FAILURE)
		} catch (Throwable ignored) {
			// Framework already logged the red step
		}
	}

	// mark fail + log + red sub-step (but DO NOT stop here)
	private static void captureFailure(String ctx, String fieldName, String attrName, String expected, String actual) {
		markLastRunFailed()
		incFailCount()  // <- ensure end-of-run sees there was a failure

		String msg = "❌ ${ctx} → ${fieldName}${attrName ? '.'+attrName : ''}  Expected='${expected}'  Actual='${actual}'"
		logStep(msg)
		KeywordUtil.markFailed(msg)
		logStep('marked as failed YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY')
		try {
			KeywordUtil.logInfo(msg)
		} catch (ignored) {}

		// red sub-step (non-stopping)
		try {
			WS.verifyEqual(actual, expected, FailureHandling.CONTINUE_ON_FAILURE)
		} catch (ignored) {}
		// optional: markFailed is okay, it doesn’t stop
	}

	private static void captureWarning(String ctx, String fieldName, String attrName, String expected, String actual) {
		String msg = "⚠ ${ctx} → ${fieldName}${attrName ? '.'+attrName : ''}  Expected='${expected}'  Actual='${actual}'"
		logStep(msg)
		try {
			KeywordUtil.markWarning(msg)
		} catch (ignored) {}
		// Note: do NOT call WS.verifyEqual(...) here (that would create a red step)
		// and do NOT markLastRunFailed() or incFailCount().
	}


	// ---------- Cookie + header utilities ----------
	private static String extractCookieHeader(def response) {
		try {
			def map = response?.getHeaderFields()
			if (map instanceof Map) {
				List<String> cookies = []
				map.each { k, List vals ->
					if (k != null && k.toString().equalsIgnoreCase('Set-Cookie')) {
						(vals ?: []).each { String v ->
							if (!v) return
								int semi = v.indexOf(';')
							String pair = semi > 0 ? v.substring(0, semi) : v
							if (pair) cookies << pair.trim()
						}
					}
				}
				if (!cookies.isEmpty()) {
					String ck = cookies.unique().join('; ')
					logStep("Built Cookie from Map headers: '${ck}'")
					return ck
				}
			}
		} catch (Throwable ignore) {}

		try {
			List<TestObjectProperty> headers = (List<TestObjectProperty>) response?.getHeaderProperties()
			if (headers != null) {
				List<String> cookies = headers.findAll { h ->
					h?.getName()?.equalsIgnoreCase('Set-Cookie')
				}.collect { h ->
					String v = h?.getValue() ?: ''
					int semi = v.indexOf(';')
					(semi > 0 ? v.substring(0, semi) : v).trim()
				}.findAll { it }

				if (!cookies.isEmpty()) {
					String ck = cookies.unique().join('; ')
					logStep("Built Cookie from List headers: '${ck}'")
					return ck
				}
			}
		} catch (Throwable ignore) {}

		logStep('No Set-Cookie found; continuing without a Cookie header.')
		return null
	}

	private static String bodySessionId(String bodyText) {
		if (!(bodyText instanceof String)) return null
		def m = bodyText =~ /"jsessionid"\s*:\s*"([^"]+)"/
		return m.find() ? m.group(1) : null
	}

	// ---------- HTTP helpers ----------
	private static String buildIntegerPattern(String actualStr) {
		boolean useGrouping = (actualStr != null && actualStr.contains(','))
		boolean negParens   = (actualStr != null && actualStr.contains('(') && actualStr.contains(')'))
		String base = useGrouping ? '#,##0' : '0'
		return negParens ? (base + ';(' + base + ')') : base
	}

	private static boolean isIntegerLikeFormat(String s) {
		return s != null && s.equalsIgnoreCase('Integer')
	}

	private static int percentageMinScale(String s) {
		def m = (s =~ /(?i)^percentage(\d+)?$/)
		if (m.matches()) {
			String g = m.group(1)
			return (g && !g.isEmpty()) ? Integer.parseInt(g) : 2
		}
		return 2
	}

	private static RequestObject makePostQueryReq(String baseUrl, Map<String,String> fixedParams, List<Map> changeFields = [], Map<String,String> extraHeaders = [:]) {
		String url = buildFullUrl(baseUrl, fixedParams, changeFields)
		RequestObject req = new RequestObject(url)
		req.setRestUrl(url)
		req.setRestRequestMethod('POST')

		List<TestObjectProperty> headers = [
			new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
		]
		extraHeaders?.each { k, v -> headers << new TestObjectProperty(k, ConditionType.EQUALS, v) }
		req.setHttpHeaderProperties(headers)
		return req
	}

	private static String buildFullUrl(String baseUrl, Map<String,String> fixedParams, List<Map> changeFields) {
		Map<String,String> q = [:]
		if (fixedParams) q.putAll(fixedParams.findAll { it.key != null })
		(changeFields ?: []).each { Map kv ->
			String k = kv?.keySet()?.first()
			if (k != null) q[k] = (kv[k] ?: '').toString()
		}
		if (q.isEmpty()) return baseUrl
		String query = q.collect { k, v ->
			if (v == null || v == '') "${URLEncoder.encode(k, 'UTF-8')}"
			else "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v, 'UTF-8')}"
		}.join('&')
		return "${baseUrl}?${query}"
	}

	// ---------- prevent user data from overwriting control keys ----------
	private static List<Map> normalizeChangeListForQuery(List<Map> rawList) {
		List<Map> out = []
		(rawList ?: []).each { Map entry ->
			String key = entry?.keySet()?.first()
			def val = (key ? entry[key] : null)
			if (!key) return

				if (val instanceof Map) {
					String df = String.valueOf(val?.dataFormat ?: '')
					if ('date'.equalsIgnoreCase(df)) {
						String units  = String.valueOf(val?.futureDateIn ?: 'Days')
						String amount = String.valueOf(val?.futureValue   ?: '0')
						String dateStr = computeFutureDateString(units, amount)
						logStep("changeFieldsDefault: computed date for '${key}' = ${dateStr} (units='${units}', amount='${amount}')")
						out << [(key): dateStr]
					} else if (val?.containsKey('value')) {
						out << [(key): String.valueOf(val.value)]
					} else {
						out << [(key): String.valueOf(val)]
					}
				} else {
					out << [(key): (val == null ? '' : String.valueOf(val))]
				}
		}
		return filterChangeFields(out)
	}

	private static List<Map> filterChangeFields(List<Map> cf) {
		Set<String> reserved = [
			'toolPage',
			'newuser',
			'json',
			'nolog'
		] as Set
		List<Map> out = []
		(cf ?: []).each { Map m ->
			String k = m?.keySet()?.first()
			if (k && !reserved.contains(k)) out << [(k): m[k]]
		}
		return out
	}

	// ---------- Normalization helpers ----------
	private static String addCurrencySymbolToMatchActual(String formatted, String actStr) {
		if (actStr == null) return formatted
		String a = actStr.trim()
		// Accounting negatives with $ inside the parens: "($149.01)"
		if (a.startsWith('($') && a.endsWith(')')) {
			String inner = formatted
			if (inner.startsWith('(') && inner.endsWith(')')) {
				inner = inner.substring(1, inner.length() - 1)
			}
			return '($' + inner + ')'
		}
		// Plain positive with currency symbol prefix: "$0.00"
		if (a.startsWith('$')) {
			return '$' + formatted
		}
		return formatted
	}

	private static boolean isCurrencyFormat(String s) {
		return (s ?: '').toLowerCase().startsWith('currency')
	}

	private static int currencyMinScale(String s) {
		if (!isCurrencyFormat(s)) return 0
		def m = (s =~ /(?i)^currency(\d+)?$/)
		if (m.matches()) {
			String g = m.group(1)
			return (g != null && !g.isEmpty()) ? Integer.parseInt(g) : 0
		}
		if ('currencywithcents'.equalsIgnoreCase(s)) return 2
		return 0
	}

	private static String buildCurrencyPattern(String actualStr, int scale) {
		boolean negParens = (actualStr != null && actualStr.contains('(') && actualStr.contains(')'))
		String base = '#,##0' + (scale > 0 ? '.' + ('0' * scale) : '')
		return negParens ? (base + ';(' + base + ')') : base
	}

	private static String normalizeLabel(String s) {
		if (s == null) return null
		String out = s
				.replace('\u00A0',' ')
				.replaceAll('\\\\\"', '"')
				.replace('&quot;', '"')
				.trim()
		return out
	}

	private static BigDecimal parseBigDecimalSafe(Object v) {
		if (v == null) return null
		try {
			String s = v.toString().trim()
			if (s.isEmpty()) return null
			s = s.replace('−','-').replace('–','-') // unicode minuses
			boolean parenNeg = s.startsWith('(') && s.endsWith(')')
			s = s.replace('\u00A0',' ')
					.replaceAll('[^0-9.\\-]', '')
					.trim()
			if (s.isEmpty()) return null
			BigDecimal num = new BigDecimal(s)
			return parenNeg ? num.negate() : num
		} catch (Throwable ignore) {
			return null
		}
	}

	private static int decimalPlaces(String s) {
		if (s == null) return 0
		int dot = s.indexOf('.')
		return (dot >= 0) ? Math.max(0, s.length() - dot - 1) : 0
	}

	private static String buildPatternFor(String actualStr, boolean groupingDefault, int minScaleDefault) {
		boolean useGrouping = (actualStr != null && actualStr.contains(',')) ? true : groupingDefault
		int scale = (actualStr != null) ? decimalPlaces(actualStr) : minScaleDefault
		if (scale < minScaleDefault) scale = minScaleDefault
		boolean negParens = (actualStr != null && actualStr.contains('(') && actualStr.contains(')'))
		String base = useGrouping ? '#,##0' : '0'
		if (scale > 0) base += '.' + ('0' * scale)
		return negParens ? (base + ';(' + base + ')') : base
	}

	// ---------- runtime token storage ----------
	private static Map<String,String> runtimeTokens = [:]
	private static void clearRuntimeTokens() {
		runtimeTokens = [:]
	}
	private static String replaceTokens(String s) {
		if (s == null) return null
		String out = s
		runtimeTokens.each { k, v -> out = out.replace("{{${k}}}", v) }
		return out
	}

	private static String computeFutureDateString(String units, String amountStr) {
		int amt = 0
		try {
			amt = Integer.parseInt(String.valueOf(amountStr).trim())
		} catch (Throwable ignore) {}
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TEST_TZ))
		String u = (units ?: '').toLowerCase()
		if (u.startsWith('day'))      cal.add(Calendar.DAY_OF_YEAR, amt)
		else if (u.startsWith('month')) cal.add(Calendar.MONTH, amt)
		else if (u.startsWith('year'))  cal.add(Calendar.YEAR, amt)
		else                           cal.add(Calendar.DAY_OF_YEAR, amt)
		SimpleDateFormat sdf = new SimpleDateFormat('MM/dd/yyyy')
		sdf.setTimeZone(TimeZone.getTimeZone(TEST_TZ))
		return sdf.format(cal.getTime())
	}

	// === NEW: multi-dimension "Years,Months,Days" helper for answers ===
	private static String computeFutureDateStringMulti(Object unitsSpec, Object valuesSpec) {
		String inSpec  = String.valueOf(unitsSpec ?: "").trim()
		String valSpec = String.valueOf(valuesSpec ?: "").trim()
		List<String> dims = inSpec.split(/\s*,\s*/).collect { (it ?: "").toLowerCase() }
		List<Integer> vals = valSpec.split(/\s*,\s*/).collect { s ->
			try {
				Integer.parseInt((s ?: "0").trim())
			} catch (Throwable ignore) {
				0
			}
		}
		int years = 0, months = 0, days = 0
		for (int i = 0; i < Math.min(dims.size(), vals.size()); i++) {
			String d = dims[i]; int v = vals[i]
			if (d.startsWith("year"))   years  += v
			else if (d.startsWith("month")) months += v
			else if (d.startsWith("day"))   days   += v
		}
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TEST_TZ))
		cal.add(Calendar.YEAR, years)
		cal.add(Calendar.MONTH, months)
		cal.add(Calendar.DAY_OF_MONTH, days)
		SimpleDateFormat sdf = new SimpleDateFormat('MM/dd/yyyy')
		sdf.setTimeZone(TimeZone.getTimeZone(TEST_TZ))
		return sdf.format(cal.getTime())
	}

	// Pre-compute runtime tokens (e.g., {{futureDate}}) from expectations
	private static void computeRuntimeTokensFromExpectations(List<List<Map>> lists) {
		lists?.each { List<Map> lst ->
			(lst ?: []).each { Map entry ->
				String fname = entry?.keySet()?.first()
				Map meta = (fname ? (entry[fname] as Map) : null)
				if (!meta) return
					def v = meta['value']
				if (v != null && v.toString().equalsIgnoreCase('futureDate')) {
					String units = String.valueOf(meta['futureDateIn'] ?: 'Days')
					String amount = String.valueOf(meta['futureValue'] ?: '0')
					String dt = computeFutureDateString(units, amount)
					runtimeTokens['futureDate'] = dt
					logStep("Token computed: futureDate = ${dt}  (units='${units}', amount='${amount}') for field '${fname}'")
				}
			}
		}
	}

	// === NEW: harvest dates from defaultAnswer / nonDefaultAnswer into tokens (e.g., answerDate, answerDate2)
	private static void computeAnswerTokensFromCase(Map caseObj) {
		def harvest = { Object block ->
			if (!(block instanceof List)) return
				block.each { item ->
					if (!(item instanceof Map)) return
						String tokenName = item.keySet()?.first()
					if (!tokenName) return
						// skip the { "textLabel":"..." } entries
						if ("textlabel".equalsIgnoreCase(tokenName)) return
						def cfg = item[tokenName]
					if (!(cfg instanceof Map)) return
						Map meta = (Map) cfg
					String df = String.valueOf(meta.get("dataFormat") ?: "")
					if (!"date".equalsIgnoreCase(df)) return
						def inSpec  = meta.get("futureDateIn")
					def valSpec = meta.get("futureValue")
					if (inSpec == null || valSpec == null) return
						String dt = computeFutureDateStringMulti(inSpec, valSpec)
					runtimeTokens[tokenName] = dt
					logStep("Token (answer) computed: ${tokenName} = ${dt} (in='${inSpec}', val='${valSpec}')")
				}
		}
		harvest(caseObj?.defaultAnswer)
		harvest(caseObj?.nonDefaultAnswer)
	}

	private static String normalizeExpectedValue(Object expected, Object actual, Map actField) {
		String actStr = (actual == null) ? '' : actual.toString()
		String expStr = (expected == null) ? '' : expected.toString()

		String dataType   = String.valueOf(actField?.dataType ?: '')
		String dataFormat = String.valueOf(actField?.dataFormat ?: '')

		if ('string'.equalsIgnoreCase(dataType)) {
			return expStr
		}

		if (isCurrencyFormat(dataFormat)) {
			BigDecimal num = parseBigDecimalSafe(expStr)
			if (num != null) {
				int scale = currencyMinScale(dataFormat)
				String pattern = buildCurrencyPattern(actStr, scale)
				DecimalFormat df = new DecimalFormat(pattern)
				df.setRoundingMode(RoundingMode.HALF_UP)
				String out = df.format(num)
				return addCurrencySymbolToMatchActual(out, actStr)
			}
			return expStr
		}

		if ('integer'.equalsIgnoreCase(dataFormat) || 'integer'.equalsIgnoreCase(dataType)) {
			BigDecimal num = parseBigDecimalSafe(expStr)
			if (num != null) {
				String pattern = buildIntegerPattern(actStr)
				DecimalFormat df = new DecimalFormat(pattern)
				df.setRoundingMode(RoundingMode.HALF_UP)
				return df.format(num)
			}
			return expStr
		}

		if ('double'.equalsIgnoreCase(dataType)) {
			BigDecimal num = parseBigDecimalSafe(expStr)
			if (num != null) {
				boolean formatNone = 'none'.equalsIgnoreCase(dataFormat) || dataFormat.trim().isEmpty()
				int minScale = formatNone ? 0 : 2
				boolean groupingDefault = (actStr != null && actStr.contains(','))
				String pattern = buildPatternFor(actStr, groupingDefault, minScale)
				DecimalFormat df = new DecimalFormat(pattern)
				df.setRoundingMode(RoundingMode.HALF_UP)
				return df.format(num)
			}
			return expStr
		}

		BigDecimal expNum = parseBigDecimalSafe(expStr)
		BigDecimal actNum = parseBigDecimalSafe(actStr)
		if (expNum != null && actNum != null) {
			boolean groupingDefault = (actStr != null && actStr.contains(','))
			String pattern = buildPatternFor(actStr, groupingDefault, decimalPlaces(actStr))
			DecimalFormat df = new DecimalFormat(pattern)
			df.setRoundingMode(RoundingMode.HALF_UP)
			return df.format(expNum)
		}

		return expStr
	}

	// ---------- Error message extraction ----------
	private static List<String> collectErrors(Map resp) {
		List<String> errs = []
		def em = resp?.lf_response?.tool?.toolPage?.toolPageContent?.errorMessages
		if (!em) return errs

		def header = (em?.textLabel ?: '').toString()
		if (header) errs << header

		def body = em?.errorMessage
		if (!body) return errs

		if (body instanceof Map) {
			def t = (body?.textLabel ?: body?.toString() ?: '').toString()
			if (t) errs << t
		} else if (body instanceof List) {
			body.each { item ->
				if (item instanceof Map) {
					def t = item?.textLabel ?: item?.toString()
					if (t) errs << t.toString()
				} else if (item != null) {
					errs << item.toString()
				}
			}
		} else {
			errs << body.toString()
		}
		return errs
	}

	private static boolean logAndHandleErrors(Map resp, String ctx, boolean failFast = true) {
		List<String> errs = collectErrors(resp)
		if (errs.isEmpty()) return false

		String bannerStart = "===== ${ctx} :: SERVER ERRORS DETECTED ====="
		String bannerEnd   = '===== END SERVER ERRORS ====='
		KeywordUtil.logInfo(bannerStart); logStep(bannerStart)
		errs.eachWithIndex { String e, int i ->
			String line = "ERR[${i}] ${e}"
			KeywordUtil.logInfo(line); logStep(line)
			// also log a red sub-step per server error
			captureFailure(ctx, "server.error[${i}]", 'message', '[no error]', e)
		}
		KeywordUtil.logInfo(bannerEnd); logStep(bannerEnd)

		// just mark the run as failed; final stop occurs at end
		markLastRunFailed()
		return true
	}

	// ---------- Field extraction ----------
	private static String canonId(String s) {
		if (s == null) return null
		return s.replace('\u00A0',' ').trim()
	}

	private static Map<String, Map<String,Object>> extractFields(def respJson) {
		def raw = respJson?.lf_response?.tool?.toolPage?.toolPageContent?.field
		Map<String, Map<String,Object>> out = [:]

		List items
		if (raw instanceof List) {
			items = (List) raw
		} else if (raw instanceof Map) {
			items = [(Map) raw]
		} else {
			items = []
		}

		logStep("DEBUG extractFields: field node type=${raw?.getClass()?.simpleName ?: 'null'}; count=${items.size()}")

		items.each { def f ->
			String rawName = (f?.dataElementName ?: f?.name ?: f?.fieldName ?: f?.id)?.toString()
			String name    = canonId(rawName)
			if (!name) return
				out[name] = [
					value       : f?.value,
					dataFormat  : f?.dataFormat,
					dataType    : f?.dataType,
					defaultValue: f?.defaultValue,
					minimumValue: f?.minimumValue,
					maximumValue: f?.maximumValue,
					textLabel   : f?.textLabel
				]
		}
		return out
	}

	// ---------- Assertions ----------
	private static void assertFields(Map<String, Map<String,Object>> actualFields,
			List<Map> expectedList,
			String ctx) {
		if (!expectedList || expectedList.isEmpty()) {
			logStep("No expected fields for ${ctx}")
			return
		}

		boolean showAll       = gv('showAllAttributes', false)
		boolean dbg           = gv('debugFields', true)
		int     previewCount  = gv('debugKeyPreview', 20) ?: 20

		logStep("Validating ${ctx} fields (${expectedList.size()})")
		logStep("${ctx} actual fields size = ${actualFields?.size() ?: 0}")

		Map<String,String> idxTight = [:]
		Map<String,String> idxLoose = [:]
		List<String> allKeys = (actualFields?.keySet() ?: []) as List<String>
		allKeys.each { String k ->
			String t = canonTight(k)
			if (!idxTight.containsKey(t)) idxTight[t] = k
			String l = canonLoose(k)
			if (!idxLoose.containsKey(l)) idxLoose[l] = k
		}

		if (dbg) {
			def preview = allKeys.take(previewCount).join(', ')
			logStep("DEBUG: First ${Math.min(previewCount, allKeys.size())} actual keys -> ${preview}")
		}

		expectedList.eachWithIndex { Map m, int idxExp ->
			String expName = m?.keySet()?.first()
			Map expMeta = (expName ? (m[expName] as Map) : null) ?: [:]

			if (!expName) {
				if (showAll) logStep("${ctx} [index ${idxExp}] has no field name; skipping.")
				return
			}

			String tightKey = idxTight[canonTight(expName)]
			Map act = (tightKey != null) ? actualFields[tightKey] : null
			String usedName = tightKey

			if (act == null) {
				String looseKey = idxLoose[canonLoose(expName)]
				if (looseKey != null) {
					act = actualFields[looseKey]
					usedName = looseKey
					if (dbg) logStep("DEBUG: Alias matched (loose) '${expName}' -> '${usedName}'")
				}
			}

			if (act == null) {
				if (dbg) {
					String expT = canonTight(expName)
					String expL = canonLoose(expName)
					logStep("DEBUG MISS: '${expName}'")
					logStep("  canonTight='${expT}'")
					logStep("  canonLoose='${expL}'")
					logStep("  codepoints(expected)=${dumpCodepoints(expName)}")
				}
				def preview = (actualFields?.keySet() ?: []).take(previewCount).join(', ')
				logStep("Missing field '${expName}'. First keys: ${preview}")
				captureFailure(ctx, expName, '', '[present]', '')
				return
			}

			List<String> attrs = [
				'value',
				'dataFormat',
				'dataType',
				'defaultValue',
				'minimumValue',
				'maximumValue',
				'textLabel'
			]

			attrs.each { String attr ->
				if (!expMeta.containsKey(attr)) {
					if (showAll) logStep("${ctx} '${expName}.${attr}' not provided in expected JSON; skipping.")
					return
				}
				def expected = expMeta[attr]
				def actual   = act[attr]

				if (attr == 'value' && expected != null && expected.toString().equalsIgnoreCase('futureDate')) {
					String computed = runtimeTokens['futureDate']
					if (computed != null) expected = computed
				} else if (attr == 'value') {
					expected = normalizeExpectedValue(expected, actual, act)
				}

				if ((String.valueOf(expected) ?: '') != (String.valueOf(actual) ?: '')) {
					captureFailure(ctx, usedName, attr, String.valueOf(expected), String.valueOf(actual))
				} else {
					logStep("· ${ctx} '${usedName}.${attr}' OK  Expected='${expected}'  Actual='${actual}'")
				}
			}
		}
	}

	private static String canonTight(String s) {
		s == null ? '' : s.replace('\u00A0',' ').trim()
	}
	private static String canonLoose(String s) {
		if (s == null) return ''
		String t = s.replace('\u00A0',' ').trim().toLowerCase()
		return t.replaceAll('[^\\p{Alnum}]', '')
	}
	private static String dumpCodepoints(String s) {
		if (s == null) return 'null'
		StringBuilder sb = new StringBuilder()
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i)
			String vis = (c == ' ') ? 'SP' : String.valueOf(c)
			sb.append("['").append(vis).append("':U+")
					.append(String.format('%04X', (int)c)).append(']')
			if (i < s.length() - 1) sb.append(' ')
		}
		return sb.toString()
	}

	// ---------- Entry point ----------
	@Keyword
	static void runFromJson(String calcId, boolean includeNonDefault = false) {
		resetRunFlag()
		resetFailCount()
		prepareLogging(calcId)
		clearRuntimeTokens()

		try {
			String baseUrl      = gv('baseUrl', 'https://qa.awsc.leadfusion.com')
			String siteId       = gv('siteid',  'qatest')
			String defaultEmail = 'lffem2@gmail.com'

			String filePath = RunConfiguration.getProjectDir() + "/Include/resources/${calcId}.json"
			logStep("Reading JSON file: ${filePath}")
			def root = new JsonSlurper().parse(new File(filePath))

			Map firstCase = {
				def c = (root?.cases instanceof List && !root.cases.isEmpty()) ? root.cases[0] : [:]
				if (c?.cases instanceof List && !c.cases.isEmpty()) return (Map) c.cases[0]
				return (Map) c
			}.call()

			Map c0 = firstCase

			// === NEW: compute tokens like {{answerDate}}, {{answerDate2}} from answer blocks ===
			computeAnswerTokensFromCase(c0)

			List<Map> expInitial          = (c0.initialFields      ?: []) as List<Map>
			List<Map> changeFieldsDefault = (c0.changeFieldsDefault ?: []) as List<Map>
			List<Map> expDefault          = (c0.defaultResults     ?: []) as List<Map>
			List<Map> expNonDef           = (c0.nonDefaultResults  ?: []) as List<Map>
			List<Map> changeFields        = (c0.changeFields       ?: []) as List<Map>
			String    expectedTitle       = (c0.title ?: '') as String

			def readLabels = { v ->
				if (v == null) return []
				if (v instanceof List) {
					List<String> out = []
					v.each { it ->
						if (it instanceof String) {
							if (it.trim()) out << it
						}
						else if (it instanceof Map) {
							def s = it?.textLabel
							if (s instanceof String && s.trim()) out << s
						}
					}
					return out
				}
				if (v instanceof Map) {
					def s = v?.textLabel
					return (s instanceof String && s.trim()) ? [s] : []
				}
				return []
			}
			def readExpectedLabels = { Map c, String primary, String fallback ->
				def p = readLabels(c[primary])
				if (!p.isEmpty()) return p
				return readLabels(c[fallback])
			}
			List<String> expDefLbls = readExpectedLabels(c0, 'defaultTextLabels', 'defaultAnswer')
			List<String> expNdLbls  = readExpectedLabels(c0, 'nonDefaultTextLabels', 'nonDefaultAnswer')

			def readExpectedColumnLabels = { Map c, String key -> readLabels(c[key]) }
			List<String> expDefCols = readExpectedColumnLabels(c0, 'DefaultColumnTextLabels')
			List<String> expNdCols  = readExpectedColumnLabels(c0, 'NonDefaultColumnTextLabels')

			logStep("initialFields=${expInitial.size()}  defaultResults=${expDefault.size()}  nonDefaultResults=${expNonDef.size()}")
			logStep("defaultLabels=${expDefLbls.size()}  nonDefaultLabels=${expNdLbls.size()}")
			logStep("defaultColumnLabels=${expDefCols.size()}  nonDefaultColumnLabels=${expNdCols.size()}")
			logStep("changeFields=${changeFields.size()}  expectedTitle='${expectedTitle}'")

			// Pre-compute runtime tokens (e.g., {{futureDate}}) from expectations
			computeRuntimeTokensFromExpectations([
				expInitial,
				expDefault,
				expNonDef
			])

			String toolBase = "${baseUrl}/tools/${siteId}/${calcId}/tool.fcs"

			// ---- 1) INITIAL (default) ----
			Map<String,String> initDefaultParams = [
				newuser:'', json:'', toolPage:'initial',
				'UserEmailAddress.EmailAddress': defaultEmail
			]
			String initDefaultUrl = buildFullUrl(toolBase, initDefaultParams, [])
			logStep("REQUEST URL (INITIAL): ${initDefaultUrl}")
			logStep('Header: Content-Type = application/json')

			def initDefaultReq  = makePostQueryReq(toolBase, initDefaultParams, [])
			def initDefaultRes  = WS.sendRequest(initDefaultReq)
			WS.verifyEqual(initDefaultRes.getStatusCode(), 200)

			String cookieHeaderDef = extractCookieHeader(initDefaultRes)

			String initDefaultBody = initDefaultRes.getResponseText()
			logPreview('INITIAL', initDefaultBody)
			saveJson('INITIAL', initDefaultBody)

			logStep("INITIAL (default) Status: ${initDefaultRes.getStatusCode()}")
			logStep("Body jsessionid (INITIAL default): ${bodySessionId(initDefaultBody) ?: '(none)'}")

			def initDefaultJson = new JsonSlurper().parseText(initDefaultBody)
			logAndHandleErrors(initDefaultJson, "${calcId} INITIAL", true) // mark, continue

			def email = initDefaultJson?.lf_response?.tool?.user?.email
			String emailid = (email?.emailid ?: '').toString()
			String masked  = (email?.maskedemail ?: '').toString()
			String formID  = (email?.formID ?: '').toString()

			if (!emailid) captureWarning("${calcId} INITIAL", 'email.emailid', 'notEmpty', '[not empty]', String.valueOf(emailid))
			else logStep("· ${calcId} INITIAL email.emailid OK ('${emailid}')")

			if (masked != 'lf*****@gmail.com')
				captureWarning("${calcId} INITIAL", 'email.maskedemail', 'equals', 'lf*****@gmail.com', String.valueOf(masked))
			else
				logStep("· ${calcId} INITIAL email.maskedemail OK ('${masked}')")

			if (!formID) captureWarning("${calcId} INITIAL", 'email.formID', 'notEmpty', '[not empty]', String.valueOf(formID))
			else logStep("· ${calcId} INITIAL email.formID OK ('${formID}')")


			String actualTitleInit = initDefaultJson?.lf_response?.tool?.title
			if (!((actualTitleInit ?: '').contains(expectedTitle))) {
				captureFailure("${calcId} INITIAL", 'title', 'contains', expectedTitle, actualTitleInit ?: '')
			} else {
				logStep("· ${calcId} INITIAL title contains OK ('${expectedTitle}')")
			}

			if (!expInitial.isEmpty()) {
				Map<String, Map<String,Object>> initFields = extractFields(initDefaultJson)
				assertFields(initFields, expInitial, "${calcId} INITIAL")
			} else {
				logStep('No initialFields in JSON; skipping INITIAL assertions.')
			}

			// ---- 2) RESULTS (default) ----
			Map<String,String> defParams = [ json:'', toolPage:'results' ]
			List<Map> cfDefaultPayload = normalizeChangeListForQuery(changeFieldsDefault)
			String defUrl = buildFullUrl(toolBase, defParams, cfDefaultPayload)
			logStep("REQUEST URL (RESULTS DEFAULT): ${defUrl}")

			Map<String,String> defHeaders = cookieHeaderDef ? [ 'Cookie': cookieHeaderDef ] : [:]

			def defReq  = makePostQueryReq(toolBase, defParams, cfDefaultPayload, defHeaders)
			def defRes  = WS.sendRequest(defReq)
			WS.verifyEqual(defRes.getStatusCode(), 200)

			String defBody = defRes.getResponseText()
			logPreview('RESULTS_DEFAULT', defBody)
			saveJson('RESULTS_DEFAULT', defBody)

			logStep("RESULTS (default) Status: ${defRes.getStatusCode()}")
			logStep("Body jsessionid (RESULTS default): ${bodySessionId(defBody) ?: '(none)'}  | Reused-Cookie='${cookieHeaderDef ?: '(none)'}'")

			def defJson = new JsonSlurper().parseText(defBody)
			logAndHandleErrors(defJson, "${calcId} RESULTS DEFAULT", true)

			String uidDefault = (defJson?.lf_response?.tool?.user?.uid ?: '').toString()
			if (!uidDefault) captureFailure("${calcId} RESULTS DEFAULT", 'user.uid', 'notEmpty', '[not empty]', uidDefault)
			else logStep("· ${calcId} RESULTS DEFAULT user.uid OK ('${uidDefault}')")

			if (!expDefault.isEmpty()) {
				Map<String, Map<String,Object>> defFields = extractFields(defJson)
				assertFields(defFields, expDefault, "${calcId} RESULTS DEFAULT")
			} else {
				logStep('No defaultResults in JSON; skipping RESULTS (DEFAULT).')
			}

			if (!expDefCols.isEmpty()) {
				def actualCols = (defJson?.lf_response?.tool?.toolPage?.toolPageContent?.columnTextLabel ?: [])
				List<String> labels = []
				if (actualCols instanceof List) {
					actualCols.each { it ->
						if (it instanceof Map) {
							def v = it?.textLabel
							if (v instanceof String && v.trim()) labels << v
						} else if (it instanceof String) {
							if (it.trim()) labels << it
						}
					}
				} else if (actualCols instanceof Map) {
					def v = actualCols?.textLabel
					if (v instanceof String && v.trim()) labels << v
				}
				assertTextLabels(labels, expDefCols, "${calcId} RESULTS DEFAULT columnTextLabel")
			}

			if (!expDefLbls.isEmpty()) {
				def tl = defJson?.lf_response?.tool?.toolPage?.toolPageContent?.textLabel
				List<String> labels = []
				if (tl instanceof Map) {
					def v = tl?.textLabel; if (v instanceof String && v.trim()) labels << v
				} else if (tl instanceof List) {
					tl.each { item ->
						if (item instanceof Map) {
							def v = item?.textLabel; if (v instanceof String && v.trim()) labels << v
						} else if (item instanceof String) {
							if (item.trim()) labels << item
						}
					}
				}
				assertTextLabels(labels, expDefLbls, "${calcId} RESULTS DEFAULT")
			}

			// ---- 3) NON-DEFAULT (results; apply changeFields) ----
			if (includeNonDefault && (!expNonDef.isEmpty() || !changeFields.isEmpty())) {
				Map<String,String> ndResHeaders = cookieHeaderDef ? [ 'Cookie': cookieHeaderDef ] : [:]
				Map<String,String> ndResParams = [ json:'', toolPage:'results' ]
				List<Map> cfPayload = normalizeChangeListForQuery(changeFields)

				String ndResUrl = buildFullUrl(toolBase, ndResParams, cfPayload)
				logStep("REQUEST URL (RESULTS NON-DEFAULT): ${ndResUrl}")

				def ndResReq = makePostQueryReq(toolBase, ndResParams, cfPayload, ndResHeaders)
				def ndRes    = WS.sendRequest(ndResReq)
				WS.verifyEqual(ndRes.getStatusCode(), 200)

				String ndResBody = ndRes.getResponseText()
				logPreview('RESULTS_NON_DEFAULT', ndResBody)
				saveJson('RESULTS_NON_DEFAULT', ndResBody)

				logStep("RESULTS (non-default) Status: ${ndRes.getStatusCode()}")
				logStep("Body jsessionid (RESULTS non-default): ${bodySessionId(ndResBody) ?: '(none)'} | Reused-Cookie='${ndResHeaders['Cookie'] ?: '(none)'}'")

				def ndJson = new JsonSlurper().parseText(ndResBody)
				logAndHandleErrors(ndJson, "${calcId} RESULTS NON-DEFAULT", true)

				String uidNd = (ndJson?.lf_response?.tool?.user?.uid ?: '').toString()
				if (!uidNd) captureFailure("${calcId} RESULTS NON-DEFAULT", 'user.uid', 'notEmpty', '[not empty]', uidNd)
				else logStep("· ${calcId} RESULTS NON-DEFAULT user.uid OK ('${uidNd}')")

				if (!expNonDef.isEmpty()) {
					Map<String, Map<String,Object>> ndFields = extractFields(ndJson)
					assertFields(ndFields, expNonDef, "${calcId} RESULTS NON-DEFAULT")
				} else {
					logStep('No nonDefaultResults expected; skipped field assertions.')
				}

				if (!expNdCols.isEmpty()) {
					def cl = ndJson?.lf_response?.tool?.toolPage?.toolPageContent?.columnTextLabel
					List<String> labels = []
					if (cl instanceof List) {
						cl.each { item ->
							if (item instanceof Map) {
								def v = item?.textLabel; if (v instanceof String && v.trim()) labels << v
							} else if (item instanceof String) {
								if (item.trim()) labels << item
							}
						}
					} else if (cl instanceof Map) {
						def v = cl?.textLabel; if (v instanceof String && v.trim()) labels << v
					}
					assertTextLabels(labels, expNdCols, "${calcId} RESULTS NON-DEFAULT columnTextLabel")
				}

				if (!expNdLbls.isEmpty()) {
					def tl2 = ndJson?.lf_response?.tool?.toolPage?.toolPageContent?.textLabel
					List<String> labels2 = []
					if (tl2 instanceof Map) {
						def v = tl2?.textLabel; if (v instanceof String && v.trim()) labels2 << v
					} else if (tl2 instanceof List) {
						tl2.each { item ->
							if (item instanceof Map) {
								def v = item?.textLabel; if (v instanceof String && v.trim()) labels2 << v
							} else if (item instanceof String) {
								if (item.trim()) labels2 << item
							}
						}
					}
					assertTextLabels(labels2, expNdLbls, "${calcId} RESULTS NON-DEFAULT")
				}
			}

			logStep("Completed ${calcId}")

			boolean hasAnyFailure = (failCount() > 0) || lastRunFailed()
			if (hasAnyFailure) {
				String summary = "❌ ${calcId} failed: ${failCount()} assertion(s). See failedLogs/${logFileName}"
				logStep(summary)
				// Deterministic failure that marks THIS Test Case failed
				throw new com.kms.katalon.core.exception.StepFailedException(summary)
			} else {
				logStep("Summary for ${calcId}: failures=${failCount()}, lastRunFailed=${hasAnyFailure}")
			}

			try {
				KeywordUtil.logInfo("Summary for ${calcId}: failures=${failCount()}, lastRunFailed=${hasAnyFailure}")
			} catch (ignored) {}
		} catch (StepFailedException sfe) {
			// let it bubble to mark the test failed
			throw sfe
		} catch (Throwable t) {
			markLastRunFailed()
			logStep("Unexpected error in ${calcId}: ${t.message}")
			throw t
		}
	}

	// ---------- labels assertion ----------
	private static void assertTextLabels(List<String> actual, List<String> expected, String ctx) {
		if (!expected || expected.isEmpty()) {
			logStep("No expected text labels for ${ctx}")
			return
		}
		logStep("Validating ${ctx} text labels (${expected.size()})")
		logStep("${ctx} actual labels size = ${actual.size()}")

		int n = Math.max(actual.size(), expected.size())
		for (int i = 0; i < n; i++) {
			String expRaw = i < expected.size() ? expected[i] : null
			String actRaw = i < actual.size()   ? actual[i]   : null
			if (expRaw == null) {
				KeywordUtil.logInfo("${ctx} extra actual label[${i}]: '${actRaw}'")
				continue
			}
			if (actRaw == null) {
				captureFailure(ctx, "textLabel[${i}]", 'missing', expRaw, '')
				continue
			}

			String expTok = replaceTokens(expRaw)
			String exp = normalizeLabel(expTok)
			String act = normalizeLabel(actRaw)

			if (exp != act) {
				KeywordUtil.logInfo("DEBUG textLabel[${i}] normalize: expected='${expRaw}' -> '${exp}' | actual='${actRaw}' -> '${act}'")
				captureFailure(ctx, "textLabel[${i}]", 'mismatch', exp, act)
			} else {
				logStep("· ${ctx} label[${i}] OK (Expected='${exp}' | Actual='${act}')")
			}
		}
	}
}
