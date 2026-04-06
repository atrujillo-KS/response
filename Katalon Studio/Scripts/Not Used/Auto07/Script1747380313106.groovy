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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto07?newuser')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'What term of vehicle loan should I choose?')

WebUI.click(findTestObject('Auto01/span_Help'))

WebUI.verifyElementText(findTestObject('Auto07/p_This tool helps you determine which of tw_c00dcd'), 'This tool helps you determine which of two vehicle loans with different terms is the better deal. For two loans with the same interest rate, the monthly payment is larger for the loan with the shorter term. However, you pay off the loan sooner and have a chance to deposit the savings in an interest-paying account. If your savings rate of return is much higher than the auto loan interest rate, it might be better to invest the money and finance the car.')

WebUI.click(findTestObject('Auto07/button_X'))

WebUI.verifyElementText(findTestObject('Auto07/span_Loan2 will cost you, in todays dollars_971acd'), 'Loan 2 will cost you, in today\'s dollars, $8,302 less than Loan 1')

WebUI.click(findTestObject('Auto07/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto07/th_Loan 1'), 'Loan 1')

WebUI.verifyElementText(findTestObject('Auto07/th_Loan 2'), 'Loan 2')

WebUI.verifyElementText(findTestObject('Auto07/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto07/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Auto07/td_1,667'), '$1,667')

WebUI.verifyElementText(findTestObject('Auto07/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto07/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto07/td_2,011'), '$2,011')

WebUI.click(findTestObject('Auto07/button_Show details'))

not_run: WebUI.verifyElementText(findTestObject('Auto07/text_Loan 1'), 'Loan 1')

not_run: WebUI.verifyElementText(findTestObject('Auto07/text_Loan 2'), 'Loan 2')

WebUI.verifyElementText(findTestObject('Auto07/div_Total Cost'), 'Total Cost')

WebUI.verifyElementText(findTestObject('Auto07/div_Payments'), 'Payments')

WebUI.verifyElementText(findTestObject('Auto07/div_Amortization'), 'Amortization')

WebUI.verifyElementText(findTestObject('Auto07/a_Shared Loan Info'), 'SHARED LOAN INFO')

WebUI.verifyElementText(findTestObject('Auto07/div_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto07/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementPresent(findTestObject('Auto07/a_i'), 1)

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0.00 and 90.00_Global_609372'), '')

WebUI.verifyElementText(findTestObject('Auto07/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementText(findTestObject('Auto07/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.verifyElementText(findTestObject('Auto07/a_Loan 1'), 'LOAN 1')

WebUI.sendKeys(findTestObject('Auto07/a_Loan 1'), Keys.chord(Keys.ENTER))

WebUI.verifyElementPresent(findTestObject('Auto07/div_Loan term                              _b73c6a'), 1)

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 1 and 360_Global_Auto_8b1711'), '')

WebUI.verifyElementText(findTestObject('Auto07/label_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto07/a_i_1_2'), 'i')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto07/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.verifyElementText(findTestObject('Auto07/a_Loan 2'), 'LOAN 2')

WebUI.sendKeys(findTestObject('Auto07/a_Loan 2'), Keys.chord(Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto07/label_Loan term                            _b7f117'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 1 and 360_Auto_Second_ed444e'), '')

WebUI.verifyElementText(findTestObject('Auto07/label_Down payment_1'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto07/a_i_1_2_3'), 'i')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0 and 249,999_Auto_Se_3b0f6d'), '')

WebUI.verifyElementText(findTestObject('Auto07/label_Interest rate_1'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto07/input_Must be between 0.00 and 50.00_Auto_S_bea7c8'), '')

WebUI.click(findTestObject('Auto07/a_Shared Loan Info'))

WebUI.sendKeys(findTestObject(''), Keys.chord(Keys.ENTER))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0 and 1,000,000_Globa_761fb6'), 
    '16,543')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0.00 and 90.00_Global_609372'), 
    '18.54')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0.00 and 100.00_Globa_c7d20a'), 
    '4.87')

WebUI.sendKeys(findTestObject('Auto07/a_Loan 1'), Keys.chord(Keys.ENTER))

WebUI.delay(1)

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 1 and 360_Global_Auto_8b1711'), 
    '24')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0 and 249,999_Global__5d75c9'), 
    '1,500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0.00 and 50.00_Global_9435e8'), 
    '6.91')

WebUI.sendKeys(findTestObject('Auto07/a_Loan 2'), Keys.chord(Keys.ENTER))

WebUI.delay(1)

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 1 and 360_Auto_Second_ed444e'), 
    '36')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0 and 249,999_Auto_Se_3b0f6d'), 
    '4,500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto07/input_Must be between 0.00 and 50.00_Auto_S_bea7c8'), 
    '4.22')

WebUI.sendKeys(findTestObject('Auto07/input_Must be between 0.00 and 50.00_Auto_S_bea7c8'), Keys.chord(Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto07/span_Loan1 will cost you, in todays dollars_bedcd6'), 'Loan 1 will cost you, in today\'s dollars, $1,143 less than Loan 2')

WebUI.click(findTestObject('Auto07/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto07/td_673'), '$673')

WebUI.verifyElementText(findTestObject('Auto07/td_357'), '$357')

WebUI.verifyElementText(findTestObject('Auto07/td_1,107'), '$1,107')

WebUI.verifyElementText(findTestObject('Auto07/td_800'), '$800')

WebUI.click(findTestObject('Auto07/button_Show details'))

WebUI.closeBrowser()

