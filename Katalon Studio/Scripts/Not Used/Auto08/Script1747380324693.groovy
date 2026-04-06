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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto08?newuser')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'Should I use a home equity loan instead of a vehicle loan?')

WebUI.click(findTestObject('Auto08/span_Help'))

WebUI.verifyElementText(findTestObject('Auto08/p_This tool helps you determine which loan _de4773'), 'This tool helps you determine which loan saves you more by comparing the terms of a home equity and auto loan. The interest on most home equity loans can be deducted from your federal income taxes. This results in a lower effective interest rate. However, you still need to compare closing costs and the after-tax interest rates of the two loans to decide which loan is cheaper.')

WebUI.click(findTestObject('Auto08/button_X'))

WebUI.verifyElementText(findTestObject('Auto08/span_The Home Equity loan will cost you, in_cd9714'), 'The Home Equity loan will cost you, in today\'s dollars, $1,259 less than the Vehicle loan')

WebUI.click(findTestObject('Auto08/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto08/th_Home Equity Loan'), 'Home Equity Loan')

WebUI.verifyElementText(findTestObject('Auto08/th_Vehicle Loan'), 'Vehicle Loan')

WebUI.verifyElementText(findTestObject('Auto08/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto08/td_858'), '$858')

WebUI.verifyElementText(findTestObject('Auto08/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Auto08/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto08/td_3,552'), '$3,552')

WebUI.verifyElementText(findTestObject('Auto08/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto08/th_Total tax savings'), 'Total tax savings')

WebUI.verifyElementText(findTestObject('Auto08/td_1,200'), '$1,200')

WebUI.verifyElementText(findTestObject('Auto08/td_NA'), 'N/A')

WebUI.click(findTestObject('Auto08/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto08/div_Cost Difference'), 'Cost Difference')

WebUI.verifyElementText(findTestObject('Auto08/div_Payments'), 'Payments')

WebUI.verifyElementText(findTestObject('Auto08/div_Amortization'), 'Amortization')

WebUI.click(findTestObject('Auto08/div_Payments'))

WebUI.verifyElementText(findTestObject('Auto08/text_Home Equity'), 'Home Equity')

WebUI.rightClick(findTestObject('Auto08/svg_Chart context menuMonthly payment of th_c8abfb'))

WebUI.verifyElementText(findTestObject('Auto08/text_Vehicle'), 'Vehicle')

WebUI.click(findTestObject('Auto08/div_Amortization'))

WebUI.verifyElementText(findTestObject('Auto08/span_Payment Schedule - Home Equity Loan'), 'Payment Schedule - Home Equity Loan')

WebUI.verifyElementText(findTestObject('Auto08/span_SEE TABLE'), 'SEE TABLE')

WebUI.click(findTestObject('Auto08/span_SEE TABLE'))

WebUI.waitForElementPresent(findTestObject('Auto08/h2_Payment Schedule - Home Equity Loan'), 1)

WebUI.verifyElementText(findTestObject('Auto08/span_Month'), 'Month')

WebUI.verifyElementText(findTestObject('Auto08/span_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto08/span_Balance'), 'Balance')

WebUI.verifyElementText(findTestObject('Auto08/span_Principal paid'), 'Principal paid')

WebUI.verifyElementText(findTestObject('Auto08/span_Interest paid'), 'Interest paid')

WebUI.verifyElementText(findTestObject('Auto08/span_Cumulative interest'), 'Cumulative interest')

WebUI.verifyElementText(findTestObject('Auto08/td_48'), '48')

WebUI.verifyElementText(findTestObject('Auto08/td_875_1'), '$875')

WebUI.verifyElementText(findTestObject('Auto08/td_0'), '$0')

WebUI.verifyElementText(findTestObject('Auto08/td_871'), '$871')

WebUI.verifyElementText(findTestObject('Auto08/td_4'), '$4')

WebUI.verifyElementText(findTestObject('Auto08/td_4,005_1'), '$4,005')

WebUI.click(findTestObject('Auto08/button_X_1'))

WebUI.verifyElementText(findTestObject('Auto08/a_Shared Loan Info'), 'SHARED LOAN INFO')

WebUI.verifyElementText(findTestObject('Auto08/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto08/label_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto08/a_i'), 'i')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto08/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto08/a_Home Equity Loan'), 'HOME EQUITY LOAN')

WebUI.click(findTestObject('Auto08/a_Home Equity Loan'))

WebUI.verifyElementText(findTestObject('Auto08/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto08/a_i_1_2'), 'i')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0.00 and 50.00_Global_61bf90'), '')

WebUI.verifyElementText(findTestObject('Auto08/label_Upfront costs'), 'Upfront costs')

WebUI.verifyElementText(findTestObject('Auto08/a_i_1_2_3'), 'i')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0 and 10,000_Global_H_712942'), '')

WebUI.verifyElementText(findTestObject('Auto08/a_Vehicle Loan'), 'VEHICLE LOAN')

WebUI.click(findTestObject('Auto08/a_Vehicle Loan'))

WebUI.verifyElementText(findTestObject('Auto08/label_Interest rate_1'), 'Interest rate')

WebUI.verifyElementVisible(findTestObject('Auto08/a_i_1_2_3_4'), 'i')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.verifyElementText(findTestObject('Auto08/a_Tax  Savings Rates'), 'TAX & SAVINGS RATES')

WebUI.click(findTestObject('Auto08/a_Tax  Savings Rates'))

WebUI.verifyElementText(findTestObject('Auto08/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementText(findTestObject('Auto08/a_i_1_2_3_4_5'), 'i')

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0.00 and 90.00_Global_609372'), '')

WebUI.verifyElementText(findTestObject('Auto08/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementPresent(findTestObject('Auto08/a_i_1_2_3_4_5_6'), 1)

WebUI.verifyElementText(findTestObject('Auto08/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.click(findTestObject('Auto08/a_Shared Loan Info'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0 and 1,000,000_Globa_761fb6'), '27,111')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0 and 249,999_Global__5d75c9'), '999')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 1 and 360 months_Glob_4576f3'), '60')

WebUI.click(findTestObject('Auto08/a_Home Equity Loan'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0.00 and 50.00_Global_61bf90'), '5.09')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0 and 10,000_Global_H_712942'), '2,334')

WebUI.click(findTestObject('Auto08/a_Vehicle Loan'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0.00 and 50.00_Global_9435e8'), '3.89')

WebUI.click(findTestObject('Auto08/a_Tax  Savings Rates'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0.00 and 90.00_Global_609372'), '30.01')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto08/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '5.21')

WebUI.sendKeys(findTestObject('Auto08/input_Must be between 0.00 and 100.00_Globa_c7d20a'), Keys.chord(
        Keys.ENTER))

WebUI.mouseOver(findTestObject('Auto08/span_The Vehicle loan will cost you, in tod_090075'))

WebUI.click(findTestObject('Auto08/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto08/td_494'), '$494')

WebUI.verifyElementText(findTestObject('Auto08/td_480'), '$480')

WebUI.verifyElementText(findTestObject('Auto08/td_3,964'), '$3,964')

WebUI.verifyElementText(findTestObject('Auto08/td_2,664'), '$2,664')

WebUI.verifyElementText(findTestObject('Auto08/td_1,190'), '$1,190')

WebUI.verifyElementText(findTestObject('Auto08/td_NA'), 'N/A')

WebUI.click(findTestObject('Auto08/button_Show details'))

