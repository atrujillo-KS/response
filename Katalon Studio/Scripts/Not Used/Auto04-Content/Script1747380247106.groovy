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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto04')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'Should I finance or pay cash for a vehicle?')

WebUI.click(findTestObject('Auto01/span_Help'))

WebUI.verifyElementText(findTestObject('Auto04/p_The tool helps you determine whether it i_fcc0c7'), 'The tool helps you determine whether it is cheaper to borrow money to buy a vehicle or to pay cash.')

WebUI.verifyElementText(findTestObject('Auto04/p_Generally, if the interest rate you earn _ee7113'), 'Generally, if the interest rate you earn on your savings is lower than the after-tax cost of borrowing, it is cheaper to pay cash.')

WebUI.verifyElementText(findTestObject('Auto04/p_However, you face a potential loss of fin_7eda2a'), 'However, you face a potential loss of financial flexibility if you pay cash. For example, you may have to deplete your rainy-day funds. The risk of not having an adequate emergency fund may be one you are not willing to accept in exchange for paying cash.')

WebUI.click(findTestObject('Auto04/button_X'))

WebUI.verifyElementText(findTestObject('Auto04/span_Paying cash will cost you 3,443 less t_26d5d1'), 'Paying cash will cost you $3,443 less than financing over 48 months')

WebUI.verifyElementText(findTestObject('Auto04/button_Next Steps'), 'NEXT STEPS')

WebUI.verifyElementText(findTestObject('Auto04/text_Financing'), 'Financing')

WebUI.verifyElementText(findTestObject('Auto04/text_Paying cash'), 'Paying cash')

WebUI.verifyElementText(findTestObject('Auto04/div_Total Cost'), 'Total Cost')

WebUI.verifyElementText(findTestObject('Auto04/div_Amortization'), 'Amortization')

WebUI.verifyElementText(findTestObject('Auto04/button_Basic'), 'BASIC')

WebUI.verifyElementText(findTestObject('Auto04/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto04/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto04/label_Down payment'), 'Down payment')

WebUI.verifyElementPresent(findTestObject('Auto04/a_i'), 1)

WebUI.verifyElementText(findTestObject('Auto01/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto04/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto04/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Interest rate'), 'Interest rate')

WebUI.mouseOver(findTestObject('Auto01/input_Must be between 0.00 and 50.00_Global_9435e8'))

WebUI.verifyElementText(findTestObject('Auto04/button_Advanced'), 'ADVANCED')

WebUI.click(findTestObject('Auto04/button_Advanced'))

WebUI.verifyElementText(findTestObject('Auto01/label_Years you will own vehicle'), 'Years you will own vehicle')

WebUI.verifyElementText(findTestObject('Auto01/input_Must be between 1 and 30_Auto_YearsOwned'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementPresent(findTestObject('Auto04/a_i_1_2'), 1)

WebUI.verifyElementText(findTestObject('Auto01/input_Must be between 0.00 and 90.00_Global_609372'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementText(findTestObject('Auto04/a_i_1_2_3'), 'i')

WebUI.verifyElementText(findTestObject('Auto01/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.closeBrowser()

