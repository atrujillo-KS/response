import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto02')

WebUI.verifyElementText(findTestObject('h1_lf_title_label'), 'How much will depreciation cost me?')

WebUI.verifyElementText(findTestObject('span_Help'), 'Help')

WebUI.click(findTestObject('span_Help'))

WebUI.verifyElementText(findTestObject('h2_Help'), 'Help')

WebUI.verifyElementText(findTestObject('p_lf_help_0'), 'The annual depreciation expense is calculated by applying a depreciation rate (average, high or low) to the new vehicle. Total depreciation is calculated for the number of years you expect to own the vehicle.')

WebUI.delay(2)

WebUI.click(findTestObject('button_lf_help_Container_close_btn'))

WebUI.verifyElementText(findTestObject('span_lf_answer'), 'Depreciation will cost you $20,020')

WebUI.verifyElementText(findTestObject('button_Next Steps'), 'NEXT STEPS')

WebUI.click(findTestObject('button_Next Steps'))

WebUI.waitForElementVisible(findTestObject('h2_lf_next_steps_zoneA_title'), 10)

WebUI.verifyElementText(findTestObject('h2_lf_next_steps_zoneA_title'), 'Next Steps')

//WebUI.verifyElementAttributeValue(findTestObject('h2_lf_next_steps_zoneA_title'), 'text', 'Next Steps', 1)
WebUI.verifyElementText(findTestObject('span_lf-next-steps-zoneA-content'), 'These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.')

WebUI.click(findTestObject('button_lf_next_steps_zoneA_close_btn'))

WebUI.verifyElementText(findTestObject('label_Enable color patterns'), 'Enable color patterns')

String ariaLabel = WebUI.getAttribute(findTestObject('graph_1_description'), 'aria-label')

WebUI.verifyMatch(ariaLabel, 'Column chart of the first year\'s depreciation cost and total depreciation cost.',
	false)

WebUI.verifyElementVisible(findTestObject('button_View_Data_Table'))

WebUI.click(findTestObject('button_View_Data_Table'))
WebUI.click(findTestObject('button_View_Data_Table'))

WebUI.verifyElementText(findTestObject('text_lf-legend-item-1'), 'First Year')

WebUI.verifyElementText(findTestObject('text_lf-legend-item-2'), 'Total')

WebUI.verifyElementText(findTestObject('label_lf_Global_AutoPurchasePrice'), 'Purchase price')

WebUI.verifyElementText(findTestObject('input_lf_Global_AutoPurchasePrice'), '')

WebUI.verifyElementAttributeValue(findTestObject('input_lf_Global_AutoPurchasePrice'), 'value', '40,000', 1)

WebUI.verifyElementText(findTestObject('Auto02/div_Vehicle age'), 
    'Vehicle age (years)')

WebUI.verifyElementText(findTestObject('Auto02/input_lf_Auto_VehicleAgeInYears'), 
    '')

WebUI.verifyElementText(findTestObject('Auto02/label_Years you will own vehicle'), 
    'Years you will own vehicle')

WebUI.verifyElementText(findTestObject('Auto02/input_lf_Auto_YearsOwned'), '')

WebUI.verifyElementText(findTestObject('Auto02/label_Future depreciation'), 'Future depreciation')

WebUI.click(findTestObject('Auto02/div_i'))

//WebUI.waitForElementVisible(findTestObject('Auto02/font_Depreciation'), 10)
WebUI.verifyElementText(findTestObject('Auto01/span_Depreciation'), 'Depreciation')

WebUI.verifyElementText(findTestObject('Auto01/p_The decline in an assets value over the c_40befa'), 'The decline in an asset\'s value over the course of its useful life due to wear and tear and obsolescence.')

//WebUI.verifyElementText(findTestObject('Auto02/font_Depreciation'), 'Depreciation')
WebUI.click(findTestObject('Auto02/button_X'))

WebUI.verifyElementText(findTestObject('Auto02/b_Terms of use'), 'Terms of use')

WebUI.verifyElementClickable(findTestObject('Auto02/b_Terms of use'))

WebUI.verifyElementText(findTestObject('Auto02/small_lf-copyright'), '© 2025 Leadfusion')

WebUI.closeBrowser()

