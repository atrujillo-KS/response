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
import helpers.InputUtils as InputUtils
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor

WebUI.openBrowser('')

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto01')

WebUI.waitForPageLoad(2000)

clearAndType(findTestObject('Object Repository/input_Purchase_Price'), '652340')

clearAndType(findTestObject('Object Repository/input_Down_Payment'), '2000')

clearAndType(findTestObject('Object Repository/input_Interest_Rate'), '7.12')

clearAndType(findTestObject('Object Repository/input_Must be between 1 and 360_Global_Auto_8b1711'), '36')

// Fresh find and click
WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_New Vehicle Cost'), Keys.chord(Keys.ENTER))

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Mon_338554'), '56')

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Mon_47d184'), '150')

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 100,000_Auto_Re_473d05'), '550')

WebUI.waitForElementClickable(findTestObject('Object Repository/Auto01/a_New Vehicle Cost'), 100)

WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_Used Vehicle Loan'), Keys.chord(Keys.ENTER))

WebUI.waitForElementClickable(findTestObject('Object Repository/input_Must be between 0 and 250,000_Auto_Us_f9ca7d'), 100)

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 250,000_Auto_Us_f9ca7d'), '36000')

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 249,999_Auto_Us_3b2be1'), '4500')

clearAndType(findTestObject('Object Repository/input_Must be between 0.00 and 50.00_Auto_U_2a486d'), '7.21')

clearAndType(findTestObject('Object Repository/input_Must be between 1 and 360_Auto_UsedVe_6f7dab'), '36')

WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_Used Vehicle Cost'), Keys.chord(Keys.ENTER))

clearAndType(findTestObject('Object Repository/input_Must be between 1 and 30_Auto_YearsUs_d82219'), '10')

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Use_096b3c'), '67')

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 10,000_Auto_Use_6ce169'), '90')

clearAndType(findTestObject('Object Repository/input_Must be between 0 and 100,000_Auto_Us_48739a'), '300')

WebUI.sendKeys(findTestObject('Object Repository/Auto01/a_Taxes  Depreciation'), Keys.chord(Keys.ENTER))

clearAndType(findTestObject('Object Repository/input_Must be between 0.00 and 90.00_Global_609372'), '23.67')

clearAndType(findTestObject('Object Repository/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '3.25')

WebUI.selectOptionByValue(findTestObject('Object Repository/select_Auto_FutureDepreciation'), '2', true)

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

def clearAndType(TestObject obj, String value) {
    WebUI.sendKeys(obj, Keys.chord(Keys.CONTROL, 'a'))

    WebUI.sendKeys(obj, Keys.chord(Keys.BACK_SPACE))

    WebUI.setText(obj, value)
}

// Close the browser at the end of the test
WebUI.closeBrowser()