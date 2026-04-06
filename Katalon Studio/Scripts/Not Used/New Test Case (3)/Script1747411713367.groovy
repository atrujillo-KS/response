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

WebUI.navigateToUrl('https://qa.awsc.leadfusion.com/response/qatest/calc/auto01')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/h1_Which is better a new or used vehicle'), 
    'Which is better: a new or used vehicle?')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/span_The used vehicle will cost you 4,542 l_e816ad'), 
    'The used vehicle will cost you $4,542 less on average each year')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_Show details'), 
    'Show details')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_Show details'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_New'), 'New')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_Used'), 'Used')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_Average cost per year'), 
    'Average cost per year')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_7,735'), '$7,735')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_3,193'), '$3,193')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_233'), '$233')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_233'), '$233')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_Total interest paid'), 
    'Total interest paid')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_1,163'), '$1,163')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/th_Total depreciation'), 
    'Total depreciation')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_20,020'), '$20,020')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/td_5,588'), '$5,588')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_Show details'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_Next Steps'), 'Next Steps')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_Next Steps'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/h2_Next Steps'), 'Next Steps')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/div_These calculators are intended to be us_42f6d3'), 
    'These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_X'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/a_New Vehicle Loan'), 'New Vehicle Loan')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/label_Purchase price'), 
    'Purchase price')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/input_Must be between 0 and 1,000,000_Globa_761fb6'), 
    '')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/label_Down payment'), 'Down payment')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/div_i'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/span_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/p_The cash you deposit towards the purchase_f6f3e8'), 
    'The cash you deposit towards the purchase of home, car, etc. The larger the down payment, the less you are required to borrow.')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_X_1'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/input_Must be between 0 and 249,999_Global__5d75c9'), 
    '')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/label_Interest rate'), 
    'Interest rate')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/input_Must be between 0.00 and 50.00_Global_9435e8'), 
    '')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/label_Loan term (months)'), 
    'Loan term (months)')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/div_i_1'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/span_Loan term'), 'Loan term')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/p_The length, or period, of a loan. It is u_528915'), 
    'The length, or period, of a loan. It is usually measured in years.')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/button_X_1'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/a_New Vehicle Cost'), 'New Vehicle Cost')

WebUI.click(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/a_New Vehicle Cost'))

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/label_Years you will own vehicle'), 
    'Years you will own vehicle')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/input_Must be between 1 and 30_Auto_YearsOwned'), 
    '')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/label_Monthly maintenance costs'), 
    'Monthly maintenance costs')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/Page_Leadfusion Auto Calculator/input_Must be between 0 and 10,000_Auto_Mon_338554'), 
    '')

