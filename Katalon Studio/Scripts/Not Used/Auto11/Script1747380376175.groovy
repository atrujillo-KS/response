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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto11?newuser')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'What vehicle can I afford?')

WebUI.click(findTestObject('Auto11/span_Help'))

WebUI.verifyElementText(findTestObject('Auto11/p_This tool helps you determine the price o_aff119'), 'This tool helps you determine the price of a vehicle you can afford, based on your down payment, value of your trade-in, cash rebates available, and loan terms.')

WebUI.verifyElementText(findTestObject('Auto11/p_Key assumptions of this tool include'), 'Key assumptions of this tool include:')

WebUI.verifyElementText(findTestObject('Auto11/p_The calculated vehicle cost represents th_eac687'), 'The calculated vehicle cost represents the maximum sticker price that you can afford based on your inputs. Sales tax and other fees that might be associated with the purchase of a vehicle are not included in the total.')

WebUI.verifyElementText(findTestObject('Auto11/p_Trade-in value and cash rebate are added _efdf5d'), 'Trade-in value and cash rebate are added to the down payment of the vehicle.')

WebUI.verifyElementText(findTestObject('Auto11/button_X'), 'X')

WebUI.click(findTestObject('Auto11/button_X'))

WebUI.click(findTestObject('Auto11/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto01/th_Loan amount'), 'Loan amount')

WebUI.verifyElementText(findTestObject('Auto11/td_19,540'), '$19,540')

WebUI.mouseOver(findTestObject('Auto11/th_Down payment'))

WebUI.verifyElementText(findTestObject('Auto11/td_2,000'), '$2,000')

WebUI.verifyElementText(findTestObject('Auto11/text_Loan amount'), 'Loan amount')

WebUI.verifyElementText(findTestObject('Auto11/text_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto11/div_Breakdown'), 'Breakdown')

WebUI.verifyElementText(findTestObject('Auto11/div_Payment Over Time'), 'Payment Over Time')

WebUI.click(findTestObject('Auto11/div_Payment Over Time'))

WebUI.verifyElementText(findTestObject('Auto11/text_Year'), 'Year')

WebUI.verifyElementText(findTestObject('Auto11/text_Interest'), 'Interest')

WebUI.verifyElementText(findTestObject('Auto11/text_Principal'), 'Principal')

WebUI.verifyElementText(findTestObject('Auto11/div_Amortization'), 'Amortization')

WebUI.click(findTestObject('Auto11/div_Amortization'))

WebUI.verifyElementText(findTestObject('Auto11/span_Your payments over time'), 'Your payments over time')

WebUI.click(findTestObject('Auto11/span_SEE TABLE'))

WebUI.verifyElementText(findTestObject('Auto11/h2_Your payments over time'), 'Your payments over time')

WebUI.verifyElementText(findTestObject('Auto11/span_Month'), 'Month')

WebUI.verifyElementText(findTestObject('Auto11/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto11/span_Balance'), 'Balance')

WebUI.verifyElementText(findTestObject('Auto11/span_Principal paid'), 'Principal paid')

WebUI.verifyElementText(findTestObject('Auto11/span_Interest paid'), 'Interest paid')

WebUI.verifyElementText(findTestObject('Auto11/span_Cumulative interest'), 'Cumulative interest')

WebUI.verifyElementText(findTestObject('Auto11/td_48'), '48')

WebUI.verifyElementText(findTestObject('Auto11/td_450'), '$450')

WebUI.verifyElementText(findTestObject('Auto11/td_0'), '$0')

WebUI.verifyElementText(findTestObject('Auto11/td_448'), '$448')

WebUI.verifyElementText(findTestObject('Auto11/td_2'), '$2')

WebUI.verifyElementText(findTestObject('Auto11/td_2,060'), '$2,060')

WebUI.click(findTestObject('Auto11/button_X_1'))

WebUI.verifyElementText(findTestObject('Auto11/button_Basic'), 'Basic')

WebUI.verifyElementText(findTestObject('Auto11/label_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto11/input_Must be between 0 and 100,000_Auto_Mo_a68a66'), '')

WebUI.verifyElementText(findTestObject('Auto11/label_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto11/a_i'), 'i')

WebUI.verifyElementText(findTestObject('Auto11/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto11/label_Loan term                            _ce3e10'), 'Loan term\n (months)')

WebUI.verifyElementText(findTestObject('Auto11/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto11/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto11/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto11/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.verifyElementText(findTestObject('Auto11/button_Advanced'), 'Advanced')

WebUI.click(findTestObject('Auto11/button_Advanced'))

WebUI.verifyElementText(findTestObject('Auto11/label_Cash rebate'), 'Cash rebate')

WebUI.verifyElementPresent(findTestObject('Auto11/a_i_1_2'), 0)

WebUI.verifyElementText(findTestObject('Auto11/input_Must be between 0 and 1,000,000_Auto__0bf210'), '')

WebUI.verifyElementText(findTestObject('Auto11/label_Trade-in value'), 'Trade-in value')

WebUI.verifyElementPresent(findTestObject('Auto11/a_i_1_2_3'), 0)

WebUI.mouseOver(findTestObject('Auto11/input_Must be between 0 and 200,000_Auto_Tr_2137e8'))

WebUI.click(findTestObject('Auto11/button_Basic'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto11/input_Must be between 0 and 100,000_Auto_Mo_a68a66'), '699')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto11/input_Must be between 0 and 249,999_Global__5d75c9'), '4,555')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto11/input_Must be between 1 and 360 months_Glob_4576f3'), '60')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto11/input_Must be between 0.00 and 50.00_Global_9435e8'), '4.31')

WebUI.click(findTestObject('Auto11/div_What vehicle can I afford_lf-busy-overlay'))

WebUI.click(findTestObject('Auto11/button_Advanced'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto11/input_Must be between 0 and 1,000,000_Auto__0bf210'), '2,111')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto11/input_Must be between 0 and 200,000_Auto_Tr_2137e8'), '4,500')

WebUI.sendKeys(findTestObject('Auto11/input_Trade-in value_lf_Auto_TradeInValue-i_e67fdc'), Keys.chord(
        Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto11/div_What vehicle can I afford_lf-busy-overlay'), '')

WebUI.click(findTestObject('Auto11/button_Show details'))

WebUI.verifyElementPresent(findTestObject('Auto11/td_37,668'), 0)

WebUI.verifyElementText(findTestObject('Auto11/td_4,555'), '$4,555')

WebUI.verifyElementText(findTestObject('Auto11/td_2,111'), '$2,111')

WebUI.verifyElementText(findTestObject('Auto11/td_5,000'), '$5,000')

WebUI.click(findTestObject('Auto11/button_Show details'))

WebUI.closeBrowser()

