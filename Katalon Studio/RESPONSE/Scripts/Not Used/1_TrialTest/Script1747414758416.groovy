import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.Keys as Keys

// Open browser and navigate to calculator
WebUI.openBrowser('')

WebUI.navigateToUrl('https://qa.awsc.leadfusion.com/response/qatest/calc/auto03')

// ===== Vehicle Financing Inputs =====
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 1,000,000_Globa_761fb6'), 
    '120,000')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 249,999_Global__5d75c9'), 
    '10,000')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 1 and 360 months_Glob_4576f3'), 
    '60')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 50.00_Global_9435e8'), 
    '6.89')

// ===== Purchase Assumptions Tab =====
WebUI.sendKeys(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/a_Purchase Assumptions'), Keys.chord(
        Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 99.00_Global_4e88e3'), 
    '7.75')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 30 years_Auto_V_3f7ed3'), 
    '3')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 1,000,000_Auto__0bf210'), 
    '2,000')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 100,000_Auto_Ot_4fdd98'), 
    '200')

// ===== Lease Tab =====
WebUI.sendKeys(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/a_Lease'), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 100,000_Auto_Le_0deb64'), 
    '350')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 100,000_Auto_Le_beb52f'), 
    '15,000')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 1 and 360 months_Auto_1a347a'), 
    '36')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 10,000_Auto_Lea_137eaa'), 
    '500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0 and 100,000_Auto_Le_2b96d9'), 
    '2,500')

// ===== Taxes & Depreciation Tab =====
WebUI.sendKeys(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/a_Taxes  Depreciation'), Keys.chord(
        Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 90.00_Global_609372'), 
    '21.12')

WebUI.selectOptionByValue(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/select_Low                                A_f43cd9'), 
    '0', true)

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 100.00_Globa_c7d20a'), 
    '3.55')

// Press Enter to trigger calculation
WebUI.sendKeys(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 100.00_Globa_c7d20a'), 
    Keys.chord(Keys.ENTER))

// ===== Verify Result Summary =====
WebUI.verifyElementText(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/span_The Lease option will cost you 8,143 l_51942f'), 
    'The Lease option will cost you $8,143 less on average each year.')

// ===== Show Details and Verify Table Data =====
WebUI.click(findTestObject('Object Repository/Auto03/Page_Leadfusion Auto Calculator/button_Show details'))

// Optionally close browser at the end
WebUI.closeBrowser()

