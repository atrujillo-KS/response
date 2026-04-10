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

//WebUI.navigateToUrl('https://qa.awsc.leadfusion.com/response/qatest/calc/auto03')
WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto03')

WebUI.mouseOver(findTestObject('Auto03/h1_Should I lease or purchase a vehicle'))

WebUI.click(findTestObject('Auto03/span_Help'))

WebUI.verifyElementText(findTestObject('Auto03/p_This tool helps you to decide if leasing _7188ff'), 'This tool helps you to decide if leasing is preferable to borrowing to acquire a vehicle.')

WebUI.verifyElementText(findTestObject('Auto03/p_It is assumed that you do not exercise th_efca92'), 'It is assumed that you do not exercise the purchase option at the end of the lease term. With a vehicle loan, you will own the vehicle when the loan is paid off.')

WebUI.verifyElementText(findTestObject('Auto03/p_Your average yearly cost of owning a vehi_3a3bce'), 'Your average yearly cost of owning a vehicle declines sharply after the first few years. This is due to smaller depreciation and financing expenses.')

WebUI.verifyElementText(findTestObject('Auto03/p_Your first year of a lease term is the mo_56003a'), 'Your first year of a lease term is the most expensive since you pay extra lease-financing charges. As a lessee, you do not incur depreciation expenses.')

WebUI.click(findTestObject('Auto03/button_X'))

WebUI.verifyElementText(findTestObject('Auto03/span_The Lease option will cost you 2,816 l_3e03f2'), 'The Lease option will cost you $2,816 less on average each year.')

WebUI.click(findTestObject('Auto03/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto03/th_Purchase'), 'Purchase')

WebUI.verifyElementText(findTestObject('Auto03/th_Lease'), 'Lease')

WebUI.verifyElementText(findTestObject('Auto03/th_Average cost per year'), 'Average cost per year')

WebUI.verifyElementText(findTestObject('Auto03/td_6,796'), '$6,796')

WebUI.verifyElementText(findTestObject('Auto03/td_3,980'), '$3,980')

WebUI.verifyElementText(findTestObject('Auto03/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto03/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Auto03/td_250'), '$250')

WebUI.verifyElementText(findTestObject('Auto03/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto03/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto03/td_NA'), 'N/A')

WebUI.verifyElementText(findTestObject('Auto03/th_Total depreciation'), 'Total depreciation')

WebUI.verifyElementText(findTestObject('Auto03/td_20,020'), '$20,020')

WebUI.verifyElementText(findTestObject('Auto03/td_NA_1'), 'N/A')

WebUI.click(findTestObject('Auto03/button_Show details'))

WebUI.verifyElementPresent(findTestObject('Auto03/button_Next Steps'), 1)

WebUI.verifyElementText(findTestObject('Auto03/div_Average Cost'), 'Average Cost')

WebUI.verifyElementText(findTestObject('Auto03/div_Payments'), 'Payments')

WebUI.verifyElementText(findTestObject('Auto03/text_Purchase'), 'Purchase')

WebUI.verifyElementText(findTestObject('Auto03/text_Lease'), 'Lease')

WebUI.verifyElementText(findTestObject('Auto03/a_Purchase'), 'PURCHASE')

WebUI.verifyElementText(findTestObject('Auto03/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Down payment'), 'Down payment')

WebUI.verifyElementPresent(findTestObject('Auto03/div_i'), 1)

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Loan term (months)'), 'Loan term (months)')
//WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementPresent(findTestObject('Auto03/a_i'), 1)

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.verifyElementText(findTestObject('Auto03/a_Purchase Assumptions'), 'PURCHASE ASSUMPTIONS')

WebUI.click(findTestObject('Auto03/a_Purchase Assumptions'))

WebUI.verifyElementText(findTestObject('Auto03/label_Sales tax rate'), 'Sales tax rate')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0.00 and 99.00_Global_4e88e3'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Vehicle age                          _16c007'), 'Vehicle age (years)')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 30 years_Auto_V_3f7ed3'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Cash rebate'), 'Cash rebate')

WebUI.verifyElementText(findTestObject('Auto03/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 1,000,000_Auto__0bf210'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Other upfront costs'), 'Other upfront costs')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 100,000_Auto_Ot_4fdd98'), '')

WebUI.verifyElementText(findTestObject('Auto03/a_Lease'), 'LEASE')

WebUI.click(findTestObject('Auto03/div_Lease'))

WebUI.verifyElementText(findTestObject('Auto03/label_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 100,000_Auto_Le_0deb64'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Down payment_1'), 'Down payment')

WebUI.verifyElementPresent(findTestObject('Auto03/a_i_1_2'), 1)

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 100,000_Auto_Le_beb52f'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Lease term (months)'), 'Lease term (months)')

WebUI.verifyElementPresent(findTestObject('Auto03/a_i_1_2_3'), 1)

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 1 and 360 months_Auto_1a347a'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Security deposit'), 'Security deposit')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 10,000_Auto_Lea_137eaa'), '')

WebUI.verifyElementText(findTestObject('Auto03/label_Cash rebate_1'), 'Cash rebate')

WebUI.verifyElementPresent(findTestObject('Auto03/a_i_1_2_3_4'), 1)

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0 and 100,000_Auto_Le_2b96d9'), '')

WebUI.verifyElementText(findTestObject('Auto03/a_Taxes  Depreciation'), 'TAXES & DEPRECIATION')

WebUI.click(findTestObject('Auto03/a_Taxes  Depreciation'))

WebUI.verifyElementText(findTestObject('Auto03/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementPresent(findTestObject('Auto03/a_i_1_2_3_4_5'), 1)

WebUI.verifyElementText(findTestObject('Auto03/label_Future depreciation'), 'Future depreciation')

WebUI.verifyElementPresent(findTestObject('Auto03/a_i_1_2_3_4_5_6'), 1)

WebUI.verifyOptionsPresent(findTestObject('Auto03/select_Low                                A_f43cd9'), ['Low', 'Average'
        , 'High'])

WebUI.verifyOptionSelectedByLabel(findTestObject('Auto03/select_Low                                A_f43cd9'), 'Average', 
    false, 10)

WebUI.verifyElementText(findTestObject('Auto03/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementText(findTestObject('Auto03/a_i_1_2_3_4_5_6_7'), 'i')

WebUI.verifyElementText(findTestObject('Auto03/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.closeBrowser()

