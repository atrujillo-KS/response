package com.utils

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil

class DetailsVerifier {

	static void verifyResultsTable(def layout) {
		List<String> errors = []

		// Click the "Show Details" button
		TestObject showDetailsBtn = new TestObject('lf_answer_more_info')
		showDetailsBtn.addProperty('id', ConditionType.EQUALS, 'lf_answer_more_info')
		WebUI.verifyElementPresent(showDetailsBtn, 5)

		// Retry to get non-empty text (especially for headless)
		String actualText = ''
		int retries = 5
		for (int i = 0; i < retries; i++) {
			actualText = WebUI.getText(showDetailsBtn).trim()
			KeywordUtil.logInfo("Attempt ${i + 1}: Show Details button text = '${actualText}'")
			if (actualText) break
				WebUI.delay(1)
		}

		if (!actualText.equalsIgnoreCase("Show details")) {
			throw new Exception("Expected 'Show details' text, but got '${actualText}'")
		} else {
			KeywordUtil.logInfo("🔎 Show Details button text = '${actualText}'")
		}

		WebUI.click(showDetailsBtn)
		KeywordUtil.logInfo("🔎 Clicked 'Show Details' button")

		// If layout.results.text exists, verify it and skip table logic
		if (layout.results.containsKey("text")) {
			TestObject answerText = new TestObject("lf_answer_text")
			answerText.addProperty("id", ConditionType.EQUALS, "lf_answer_text")
			WebUI.verifyElementPresent(answerText, 5)
			String actualAnswer = WebUI.getText(answerText).trim()
			String expectedAnswer = layout.results.text.trim()
			if (actualAnswer != expectedAnswer) {
				errors << "❌ Answer text mismatch: expected '${expectedAnswer}', got '${actualAnswer}'"
			} else {
				KeywordUtil.logInfo("✅ Verified lf_answer_text = '${actualAnswer}'")
			}
			if (!errors.isEmpty()) {
				errors.each { KeywordUtil.logInfo(it) }
				throw new Exception("Answer text verification failed with ${errors.size()} error(s).")
			}
			return
		}

		// Verify results table and caption
		TestObject table = new TestObject('lf-results-table-main')
		table.addProperty('id', ConditionType.EQUALS, 'lf-results-table-main')
		WebUI.verifyElementPresent(table, 5)
		TestObject caption = new TestObject('lf-results-table-caption')
		caption.addProperty('xpath', ConditionType.EQUALS, "//*[@id='lf-results-table-main']//caption")
		WebUI.verifyElementText(caption, 'Results details')

		// Verify column headers if present and defined
		if (layout.results.containsKey('columnHeaders') && layout.results.columnHeaders instanceof List && layout.results.columnHeaders.size() > 0) {
			List<String> columnHeaders = layout.results.columnHeaders
			boolean headerFound = false

			for (int i = 0; i < columnHeaders.size(); i++) {
				String expectedHeader = columnHeaders[i]?.trim()
				if (!expectedHeader) continue

					TestObject headerObj = new TestObject("lf-results-header-${i}")
				headerObj.addProperty('xpath', ConditionType.EQUALS, "//table[@id='lf-results-table-main']//thead//th[${i + 1}]")

				try {
					WebUI.verifyElementPresent(headerObj, 2)
					String actualHeader = WebUI.getText(headerObj).trim()
					if (actualHeader != expectedHeader) {
						errors << "❌ Header mismatch at column ${i + 1}: expected '${expectedHeader}', got '${actualHeader}'"
					} else {
						KeywordUtil.logInfo("✅ Verified column header ${i + 1}: '${actualHeader}'")
					}
					headerFound = true
				} catch (Exception e) {
					errors << "⚠️ Header not found at column ${i + 1}: expected '${expectedHeader}'"
				}
			}

			if (!headerFound) {
				KeywordUtil.logInfo("⚠️ No column headers found. Continuing without header validation.")
				errors.removeAll { it.startsWith('⚠️ Header not found') } // Don't fail test on optional headers
			}
		}

		// Verify field labels and values
		def resultFieldGroups = layout.results.fields
		def resultData = layout.results_fields

		for (List group : resultFieldGroups) {
			for (int i = 0; i < group.size(); i++) {
				String fieldName = group[i]
				def config = resultData[fieldName]
				if (!config) continue

					// Only verify label for the first field in the group
					if (i == 0) {
						String labelId = "lf_${fieldName}_result_label"
						TestObject labelObj = new TestObject(labelId)
						labelObj.addProperty('id', ConditionType.EQUALS, labelId)
						WebUI.verifyElementPresent(labelObj, 5)
						String actualLabel = WebUI.getText(labelObj).trim()
						String expectedLabel = config.fieldLabel?.trim()
						if (actualLabel != expectedLabel) {
							errors << "❌ Label mismatch: ${labelId} → expected '${expectedLabel}', got '${actualLabel}'"
						} else {
							KeywordUtil.logInfo("✅ Verified label ${labelId} = '${actualLabel}'")
						}
					}

				// Verify value for all fields
				String valueId = "lf_${fieldName}_result_value"
				TestObject valueObj = new TestObject(valueId)
				valueObj.addProperty('id', ConditionType.EQUALS, valueId)
				WebUI.verifyElementPresent(valueObj, 5)
				String actualValue = WebUI.getText(valueObj).trim()
				String expectedValue = config.value?.trim()
				if (actualValue != expectedValue) {
					errors << "❌ Value mismatch: ${valueId} → expected '${expectedValue}', got '${actualValue}'"
				} else {
					KeywordUtil.logInfo("✅ Verified value ${valueId} = '${actualValue}'")
				}
			}
		}

		WebUI.click(showDetailsBtn)

		if (!errors.isEmpty()) {
			errors.each { KeywordUtil.logInfo(it) }
			throw new Exception("Results table verification failed with ${errors.size()} error(s).")
		}
	}
}
