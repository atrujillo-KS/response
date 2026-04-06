import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// Open browser and navigate to the URL
WebUI.openBrowser('')
WebUI.navigateToUrl('https://qa.awsc.leadfusion.com/response/qatest/calc/auto02')
WebUI.waitForPageLoad(2000)

// Use the custom keyword to set text values
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto02/Page_Leadfusion Auto Calculator/input_PurchasePrice'), '56000')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto02/Page_Leadfusion Auto Calculator/input_VehicleAge'), '7')
CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Object Repository/Auto02/Page_Leadfusion Auto Calculator/input_YearsOwned'), '10')

// Select option from dropdown
WebUI.selectOptionByValue(findTestObject('Object Repository/Auto02/Page_Leadfusion Auto Calculator/select_FutureDepreciation'), '2', true)

// Verification point
WebUI.verifyElementText(findTestObject('Object Repository/Auto02/Page_Leadfusion Auto Calculator/span_Answer'), 
    'Depreciation will cost you $41,951')

// Close browser
WebUI.closeBrowser()
