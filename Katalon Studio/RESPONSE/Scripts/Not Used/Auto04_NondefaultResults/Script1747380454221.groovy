import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword as WebUIAbstractKeyword
import org.openqa.selenium.Keys as Keys

// ===== Open Browser and Navigate =====
WebUI.openBrowser('')

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto04?newuser')

// ===== Vehicle Financing Inputs =====
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 0 and 1,000,000_Globa_761fb6'), 
    '25,999')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 0 and 249,999_Global__5d75c9'), 
    '1,500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 1 and 360 months_Glob_4576f3'), 
    '36')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 50.00_Global_9435e8'), 
    '6.21')

// ===== Advanced Options Tab =====
WebUI.sendKeys(findTestObject('Auto04/Page_Leadfusion Auto Calculator/button_Advanced'), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 1 and 30_Auto_YearsOwned'), 
    '8')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 90.00_Global_609372'), 
    '21.87')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 100.00_Globa_c7d20a'), 
    '2.56')

// Press Enter to trigger calculation
WebUI.sendKeys(findTestObject('Auto04/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 100.00_Globa_c7d20a'), 
    Keys.chord(Keys.ENTER))

// ===== Verify Result Summary =====
WebUI.verifyElementText(findTestObject('Auto03/Page_Leadfusion Auto Calculator/span_The Lease option will cost you 8,143 l_51942f'), 
    'Paying cash will cost you $1,603 less than financing over 36 months')

// ===== Close Browser =====
WebUI.closeBrowser()

