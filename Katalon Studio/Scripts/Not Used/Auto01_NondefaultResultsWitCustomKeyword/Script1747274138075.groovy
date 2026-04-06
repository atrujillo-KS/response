import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// Open browser and navigate to the URL
WebUI.openBrowser('')
WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto01')
WebUI.waitForPageLoad(2000)

// Use the custom keyword to set values
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Purchase_Price'), '652340')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Down_Payment'), '2000')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Interest_Rate'), '7.12')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 1 and 360_Global_Auto_8b1711'), '36')

// Expand New Vehicle Cost
WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_New Vehicle Cost'), Keys.chord(Keys.ENTER))
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Mon_338554'), '56')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Mon_47d184'), '150')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 100,000_Auto_Re_473d05'), '550')

// Expand Used Vehicle Loan
WebUI.waitForElementClickable(findTestObject('Object Repository/Auto01/a_New Vehicle Cost'), 100)
WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_Used Vehicle Loan'), Keys.chord(Keys.ENTER))
WebUI.waitForElementClickable(findTestObject('Object Repository/input_Must be between 0 and 250,000_Auto_Us_f9ca7d'), 100)
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 250,000_Auto_Us_f9ca7d'), '36000')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 249,999_Auto_Us_3b2be1'), '4500')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0.00 and 50.00_Auto_U_2a486d'), '7.21')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 1 and 360_Auto_UsedVe_6f7dab'), '36')

// Expand Used Vehicle Cost
WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_Used Vehicle Cost'), Keys.chord(Keys.ENTER))
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 1 and 30_Auto_YearsUs_d82219'), '10')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Use_096b3c'), '67')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Use_6ce169'), '90')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0 and 100,000_Auto_Us_48739a'), '300')

// Expand Taxes & Depreciation
WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_Taxes  Depreciation'), Keys.chord(Keys.ENTER))
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0.00 and 90.00_Global_609372'), '23.67')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '3.25')

// Select value from dropdown
WebUI.selectOptionByValue(findTestObject('Object Repository/select_Auto_FutureDepreciation'), '2', true)

// Final actions
WebUI.click(findTestObject('Object Repository/button_Show details'))

WebUI.verifyElementText(findTestObject('Object Repository/span_100,270'), '$124,781')
WebUI.verifyElementText(findTestObject('Object Repository/span_4,210'), '$5,243')
WebUI.verifyElementText(findTestObject('Object Repository/span_20,039'), '$20,116')
WebUI.verifyElementText(findTestObject('Object Repository/span_976'), '$976')
WebUI.verifyElementText(findTestObject('Object Repository/span_73,563'), '$73,847')
WebUI.verifyElementText(findTestObject('Object Repository/span_3,624'), '$3,624')
WebUI.verifyElementText(findTestObject('Object Repository/span_460,745'), '$418,687')
WebUI.verifyElementText(findTestObject('Object Repository/span_28,062'), '$29,276')

WebUI.click(findTestObject('Object Repository/button_Show details'))

// Close the browser at the end of the test
WebUI.closeBrowser()
