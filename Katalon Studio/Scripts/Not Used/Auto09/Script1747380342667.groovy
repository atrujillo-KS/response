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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto09?newuser')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'Which is better: a rebate or special dealer financing?')

WebUI.verifyElementText(findTestObject('Auto09/span_The low rate is the better deal.  In t_ab9f04'), 'The low rate is the better deal. In today\'s dollars, you\'ll save $2,479.')

WebUI.verifyElementText(findTestObject('Auto09/button_Next Steps'), 'Next Steps')

WebUI.verifyElementText(findTestObject('Auto09/label_Enable color patterns'), 'Enable color patterns')

WebUI.verifyElementText(findTestObject('Auto09/text_Cash Rebate'), 'Cash Rebate')

WebUI.verifyElementText(findTestObject('Auto09/text_Special Dealer Financing'), 'Special Dealer Financing')

WebUI.verifyElementText(findTestObject('Auto09/a_Loan Info'), 'Loan Info')

WebUI.verifyElementText(findTestObject('Auto09/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto09/label_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto09/a_i'), 'i')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto09/div_Loan term (months)                     _57d7a2'), 'Loan term (months)\n i')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto09/label_Regular interest rate'), 'Regular interest rate')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 0.00 and 50.00_Auto_R_cb7905'), '')

WebUI.verifyElementText(findTestObject('Auto09/a_Rebate'), 'Rebate')

WebUI.click(findTestObject('Auto09/div_Rebate'))

WebUI.verifyElementText(findTestObject('Auto09/label_Cash rebate'), 'Cash rebate')

WebUI.verifyElementText(findTestObject('Auto09/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 0 and 1,000,000_Auto__0bf210'), '')

WebUI.verifyElementText(findTestObject('Auto09/label_Rebate is put toward'), 'Rebate is put toward')

WebUI.verifyElementText(findTestObject('Auto09/select_Savings                             _015cfd'), 'Savings\n\n Down Payment')

WebUI.verifyElementText(findTestObject('Auto09/a_Special Dealer Financing'), 'Special Dealer Financing')

WebUI.click(findTestObject('Auto09/a_Special Dealer Financing'))

WebUI.verifyElementText(findTestObject('Auto09/label_Low interest rate'), 'Low interest rate')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 0.00 and 50.00_Auto_L_151d70'), '')

WebUI.verifyElementText(findTestObject('Auto09/a_Tax  Savings Rates'), 'Tax & Savings Rates')

WebUI.click(findTestObject('Auto09/a_Tax  Savings Rates'))

WebUI.verifyElementText(findTestObject('Auto09/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementText(findTestObject('Auto09/a_i_1_2'), 'i')

WebUI.verifyElementText(findTestObject('Auto09/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementText(findTestObject('Auto09/a_i_1_2_3'), 'i')

WebUI.verifyElementText(findTestObject('Auto09/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.click(findTestObject('Auto09/a_Loan Info'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0 and 1,000,000_Globa_761fb6'), '26,444')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0 and 249,999_Global__5d75c9'), '3,200')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 1 and 360 months_Glob_4576f3'), '36')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0.00 and 50.00_Auto_R_cb7905'), '9.11')

WebUI.click(findTestObject('Auto09/a_Rebate'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0 and 1,000,000_Auto__0bf210'), '4,000')

WebUI.selectOptionByValue(findTestObject('Auto09/select_Savings                             _015cfd'), 
    '1', true)

WebUI.click(findTestObject('Auto09/a_Special Dealer Financing'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0.00 and 50.00_Auto_L_151d70'), '3.04')

WebUI.click(findTestObject('Auto09/a_Tax  Savings Rates'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0.00 and 90.00_Global_609372'), '17.88')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto09/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '5.99')

WebUI.sendKeys(findTestObject('Auto09/input_Must be between 0.00 and 100.00_Globa_c7d20a'), Keys.chord(
        Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto09/span_The rebate is the better deal.  In tod_5cc545'), 'The rebate is the better deal. In today\'s dollars, you\'ll save $2,119.')

WebUI.closeBrowser()

