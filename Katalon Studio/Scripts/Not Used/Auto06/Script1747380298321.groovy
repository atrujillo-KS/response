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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto06?newuser')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'Which vehicle loan is better?')

WebUI.click(findTestObject('Auto01/span_Help'))

WebUI.verifyElementText(findTestObject('Auto06/p_This tool helps you determine which of tw_32dc22'), 'This tool helps you determine which of two vehicle loans is the better deal. For two loans with the same interest rate, the monthly payment is larger for the loan with the shorter term. However, you pay off the loan sooner and have a chance to deposit the savings in an interest-paying account. If your savings rate of return is much higher than the auto loan interest rate, it might be better to invest the money and finance the car.')

WebUI.click(findTestObject('Auto06/button_X'))

WebUI.verifyElementText(findTestObject('Auto06/span_Loan 2 will cost you, in todays dollar_b2c683'), 'Loan 2 will cost you, in today\'s dollars, $18,161 less than Loan 1')

WebUI.click(findTestObject('Auto06/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto06/th_Loan 1'), 'Loan 1')

WebUI.verifyElementText(findTestObject('Auto06/th_Loan 2'), 'Loan 2')

WebUI.verifyElementText(findTestObject('Auto06/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto06/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Auto06/td_570'), '$570')

WebUI.verifyElementText(findTestObject('Auto06/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto06/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto06/td_688'), '$688')

WebUI.click(findTestObject('Auto06/button_Show details'))

not_run: WebUI.verifyElementText(findTestObject('Auto06/text_Loan 1'), 'Loan 1')

not_run: WebUI.verifyElementText(findTestObject('Auto06/text_Loan 2'), 'Loan 2')

WebUI.verifyElementText(findTestObject('Auto06/div_Total Cost'), 'Total Cost')

WebUI.verifyElementText(findTestObject('Auto06/div_Payments'), 'Payments')

WebUI.verifyElementText(findTestObject('Auto06/div_Amortization'), 'Amortization')

WebUI.verifyElementPresent(findTestObject('Auto06/a_Loan 1'), 1)

WebUI.verifyElementText(findTestObject('Auto06/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto06/label_Down payment'), 'Down payment')

WebUI.verifyElementPresent(findTestObject('Auto06/div_i'), 1)

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto06/label_Loan term  (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto06/a_i'), 'i')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 1 and 360_Global_Auto_8b1711'), '')

WebUI.verifyElementText(findTestObject('Auto06/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.sendKeys(findTestObject('Auto06/a_Loan 2'), Keys.chord(Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto06/label_Purchase price_1'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0 and 250,000_Auto_Se_189a99'), '')

WebUI.verifyElementText(findTestObject('Auto06/label_Down payment_1'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto06/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0 and 249,999_Auto_Se_3b0f6d'), '')

WebUI.verifyElementText(findTestObject('Auto06/a_i_1_2'), 'i')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 1 and 360_Auto_Second_ed444e'), '')

WebUI.verifyElementText(findTestObject('Auto06/label_Interest rate_1'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0.00 and 50.00_Auto_S_bea7c8'), '')

WebUI.verifyElementText(findTestObject('Auto06/a_Tax Savings Rates'), 'TAX & SAVINGS RATES')

WebUI.sendKeys(findTestObject('Auto06/a_Tax Savings Rates'), Keys.chord(Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto06/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementText(findTestObject('Auto06/a_i_1_2_3'), 'i')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0.00 and 90.00_Global_609372'), '')

WebUI.verifyElementText(findTestObject('Auto06/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementText(findTestObject('Auto06/a_i_1_2_3_4'), 'i')

WebUI.verifyElementText(findTestObject('Auto06/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.sendKeys(findTestObject('Auto06/a_Loan 1'), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0 and 1,000,000_Globa_761fb6'), 
    '34,237')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0 and 249,999_Global__5d75c9'), 
    '1,500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 1 and 360_Global_Auto_8b1711'), 
    '60')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0.00 and 50.00_Global_9435e8'), 
    '2.56')

WebUI.sendKeys(findTestObject('Auto06/a_Loan 2'), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0 and 250,000_Auto_Se_189a99'), 
    '35,666')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0 and 249,999_Auto_Se_3b0f6d'), 
    '2,500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 1 and 360_Auto_Second_ed444e'), 
    '60')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0.00 and 50.00_Auto_S_bea7c8'), 
    '3.99')

WebUI.sendKeys(findTestObject('Auto06/a_Tax Savings Rates'), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0.00 and 90.00_Global_609372'), 
    '23.56')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto06/input_Must be between 0.00 and 100.00_Globa_c7d20a'), 
    '6.78')

WebUI.sendKeys(findTestObject('Auto06/input_Must be between 0.00 and 100.00_Globa_c7d20a'), Keys.chord(Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto06/span_Loan 1 will cost you, in todays dollar_19209a'), 'Loan 1 will cost you, in today\'s dollars, $2,050 less than Loan 2')

WebUI.click(findTestObject('Auto06/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto06/td_582'), '$582')

WebUI.verifyElementText(findTestObject('Auto06/td_611'), '$611')

WebUI.verifyElementText(findTestObject('Auto06/td_2,175'), '$2,175')

WebUI.verifyElementText(findTestObject('Auto06/td_3,473'), '$3,473')

WebUI.click(findTestObject('Auto06/button_Show details'))

WebUI.closeBrowser()

