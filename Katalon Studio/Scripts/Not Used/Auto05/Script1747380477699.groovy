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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto05?newuser')

WebUI.verifyElementText(findTestObject('Auto05/h1_How much will my vehicle payments be'), 'How much will my vehicle payments be?')

WebUI.click(findTestObject('Auto05/span_Help'))

WebUI.verifyElementText(findTestObject('Auto05/p_The size of your monthly payment depends _941d26'), 'The size of your monthly payment depends on loan amount, loan term, and interest rate. Loan amount equals vehicle purchase price minus down payment, rebate (if applicable), and net trade-in value. Net trade-in value is equal to the vehicle trade-in value minus the amount owed on the vehicle.')

WebUI.click(findTestObject('Auto05/button_X'))

WebUI.verifyElementText(findTestObject('Auto05/span_Monthly payment 875'), 'Monthly payment: $875')

WebUI.click(findTestObject('Auto05/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto05/th_Loan amount'), 'Loan amount')

WebUI.verifyElementText(findTestObject('Auto05/td_38,000'), '$38,000')

WebUI.verifyElementText(findTestObject('Auto05/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto05/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto05/button_Basic'), 'BASIC')

WebUI.verifyElementText(findTestObject('Auto05/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto05/label_Down payment'), 'Down payment')

WebUI.verifyElementPresent(findTestObject('Auto05/a_i'), 1)

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementPresent(findTestObject('Auto05/a_i_1'), 1)

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto05/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.verifyElementText(findTestObject('Auto05/button_Advanced'), 'ADVANCED')

WebUI.click(findTestObject('Auto05/button_Advanced'))

WebUI.verifyElementText(findTestObject('Auto05/label_Cash rebate'), 'Cash rebate')

WebUI.verifyElementPresent(findTestObject('Auto05/a_i_1_2'), 1)

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 0 and 1,000,000_Auto__0bf210'), '')

WebUI.verifyElementText(findTestObject('Auto05/label_Trade-in value'), 'Trade-in value')

WebUI.click(findTestObject('Auto05/div_i'))

WebUI.verifyElementText(findTestObject('Auto05/span_Trade-in value'), 'Trade-in value')

WebUI.verifyElementText(findTestObject('Auto05/p_The price you can expect to be offered fo_07f6bc'), 'The price you can expect to be offered for your used car if you sell it to a dealer as part of buying a new car.')

WebUI.click(findTestObject('Auto05/button_X_lf-tooltip-close'))

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 0 and 200,000_Auto_Tr_2137e8'), '')

WebUI.verifyElementText(findTestObject('Auto05/label_Amount owed on trade-in'), 'Amount owed on trade-in')

WebUI.verifyElementText(findTestObject('Auto05/input_Must be between 0 and 200,000_Auto_Ou_c3de58'), '')

WebUI.click(findTestObject('Auto05/button_Basic'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 0 and 1,000,000_Globa_761fb6'), '32,850')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 0 and 249,999_Global__5d75c9'), '3,000')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 1 and 360 months_Glob_4576f3'), '24')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 0.00 and 50.00_Global_9435e8'), '8.12')

WebUI.sendKeys(findTestObject('Auto05/button_Advanced'), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 0 and 1,000,000_Auto__0bf210'), '800')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 0 and 200,000_Auto_Tr_2137e8'), '5,890')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto05/input_Must be between 0 and 200,000_Auto_Ou_c3de58'), '4')
WebUI.sendKeys(findTestObject('Auto05/input_Must be between 0 and 200,000_Auto_Ou_c3de58'), Keys.chord(Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto05/span_Monthly payment 1,049'), 'Monthly payment: $1,049')

WebUI.click(findTestObject('Auto05/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto05/td_23,164'), '$23,164')

WebUI.verifyElementText(findTestObject('Auto05/td_2,010'), '$2,010')

WebUI.click(findTestObject('Auto05/button_Show details'))

WebUI.closeBrowser()

