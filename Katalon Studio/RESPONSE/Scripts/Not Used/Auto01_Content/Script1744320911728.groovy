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

WebUI.navigateToUrl(GlobalVariable.URL + '/response/qatest/calc/auto01')

WebUI.verifyElementPresent(findTestObject('Auto01/h1_Title'), 1)

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'Which is better: a new or used vehicle?')

WebUI.verifyElementText(findTestObject('Auto01/span_Help'), 'Help')

WebUI.click(findTestObject('Auto01/span_Help'))

WebUI.verifyElementText(findTestObject('Auto01/h2_Help'), 'Help')

WebUI.verifyElementText(findTestObject('Auto01/p_The decision to buy a new vehicle instead_8fb65b'), 'The decision to buy a new vehicle instead of a used one requires that you consider the following:')

WebUI.verifyElementText(findTestObject('Auto01/p_Purchase price. A new vehicle will clearl_f88dae'), 'Purchase price. A new vehicle will clearly cost you more than a used vehicle of the same make and model. The extent to which the new or used car will drop in value is based on the depreciation rate chosen - average, high or low.')

WebUI.verifyElementText(findTestObject('Auto01/p_Depreciation. Some vehicles depreciate fa_243f55'), 'Depreciation. Some vehicles depreciate faster than others do. The slower the depreciation rate of the vehicle, the higher its resale value.')

WebUI.verifyElementText(findTestObject('Auto01/p_Operating costs. A used vehicle is likely_fe087c'), 'Operating costs. A used vehicle is likely to require more maintenance and repair than a new one. Also, fuel consumption may be less efficient.')

WebUI.verifyElementText(findTestObject('Auto01/p_Years of ownership. The longer you own th_aa892f'), 'Years of ownership. The longer you own the vehicle, the lower the average annual cost, as acquisition costs are spread out over more years.')

WebUI.verifyElementText(findTestObject('Auto01/p_Financing cost. Interest rates for new ve_90a184'), 'Financing cost. Interest rates for new vehicle loans are generally lower than for used vehicle loans.')

WebUI.click(findTestObject('Auto01/span_X'))

WebUI.verifyElementText(findTestObject('Auto01/span_answer'), 'The used vehicle will cost you $4,542 less on average each year')

WebUI.verifyElementText(findTestObject('Auto01/button_Show details'), 'Show details')

WebUI.click(findTestObject('Auto01/button_Show details'))

WebUI.mouseOver(findTestObject('Auto01/th_New'))

WebUI.verifyElementText(findTestObject('Auto01/th_Used'), 'Used')

WebUI.verifyElementText(findTestObject('Auto01/th_Average cost per year'), 'Average cost per year')

WebUI.verifyElementText(findTestObject('Auto01/span_7,735'), '$7,735')

WebUI.verifyElementText(findTestObject('Auto01/td_7,735'), '$7,735')

WebUI.mouseOver(findTestObject('Auto01/td_3,193'))

WebUI.verifyElementText(findTestObject('Auto01/th_Monthly payment'), 'Monthly payment')

WebUI.verifyElementText(findTestObject('Auto01/td_875'), '$875')

WebUI.verifyElementText(findTestObject('Auto01/td_233'), '$233')

WebUI.verifyElementText(findTestObject('Auto01/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto01/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto01/td_1,163'), '$1,163')

WebUI.verifyElementText(findTestObject('Auto01/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto01/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto01/td_1,163'), '$1,163')

WebUI.verifyElementText(findTestObject('Auto01/th_Total depreciation'), 'Total depreciation')

WebUI.verifyElementText(findTestObject('Auto01/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto01/td_1,163'), '$1,163')

WebUI.verifyElementText(findTestObject('Auto01/th_Total depreciation'), 'Total depreciation')

WebUI.verifyElementText(findTestObject('Auto01/td_TotDepAuto01_result_value'), '$20,020')

WebUI.verifyElementText(findTestObject('Auto01/td_5,588'), '$5,588')

WebUI.click(findTestObject('Auto01/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto01/button_Next Steps'), 'NEXT STEPS')

WebUI.click(findTestObject('Auto01/button_Next Steps'))

WebUI.waitForElementVisible(findTestObject('Auto01/h2_Next Steps'), 100)

WebUI.verifyElementText(findTestObject('Auto01/h2_Next Steps'), 'Next Steps')

WebUI.verifyElementText(findTestObject('Auto01/span_These calculators are intended to be u_5fea1a'), 'These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.')

WebUI.click(findTestObject('Auto01/span_X_1'))

WebUI.verifyElementVisible(findTestObject('Auto01/input_Next Steps_patterns_enabled'))

WebUI.verifyElementText(findTestObject('Auto01/label_Enable color patterns'), 'Enable color patterns')

WebUI.verifyElementVisible(findTestObject('button_View_Data_Table'))

WebUI.click(findTestObject('button_View_Data_Table'))

WebUI.verifyElementText(findTestObject('Auto01/caption_Average cost per year of the new ve_56ea73'), 'Average cost per year of the new vehicle and the used vehicle')

WebUI.verifyElementText(findTestObject('Auto01/th_Category'), 'Category')

WebUI.verifyElementText(findTestObject('Auto01/th_New_1'), 'New')

WebUI.verifyElementText(findTestObject('Auto01/th_Used_1'), 'Used')

WebUI.verifyElementText(findTestObject('Auto01/th_Average Cost Per Year (1)'), 'Average Cost Per Year')

WebUI.verifyElementText(findTestObject('Auto01/td_7,735_1'), '$7,735')

WebUI.verifyElementText(findTestObject('Auto01/td_3,193_1'), '$3,193')

WebUI.click(findTestObject('button_View_Data_Table'))

String ariaLabel = WebUI.getAttribute(findTestObject('graph_1_description'), 'aria-label')

WebUI.verifyMatch(ariaLabel, 'Column chart comparing the average cost per year of the new vehicle and the used vehicle.', 
    false)

WebUI.verifyElementText(findTestObject('Auto01/legend_item1'), 'New')

WebUI.verifyElementText(findTestObject('Auto01/legend_item2'), 'Used')

WebUI.verifyElementText(findTestObject('Auto01/div_Average Cost'), 'Average Cost')

WebUI.verifyElementText(findTestObject('Auto01/div_Payments'), 'Payments')

WebUI.click(findTestObject('Auto01/div_Payments'))

String ariaLabelTwo = WebUI.getAttribute(findTestObject('graph_2_description'), 'aria-label')

WebUI.verifyMatch(ariaLabelTwo, 'Column chart comparing the monthly payment for the new vehicle and the used vehicle.', 
    false)

WebUI.verifyElementText(findTestObject('Auto01/span_These calculators are intended to be u_5fea1a_1'), 'These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.')

WebUI.verifyElementText(findTestObject('Auto01/a_New Vehicle Loan'), 'NEW VEHICLE LOAN')

WebUI.verifyElementText(findTestObject('Auto01/label_Purchase price'), 'Purchase price')

WebUI.verifyElementAttributeValue(findTestObject('input_lf_Global_AutoPurchasePrice'), 'value', '40,000', 1)

not_run: WebUI.verifyElementAttributeValue(findTestObject('Auto01/input_Purchase_Price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto01/input_Purchase_Price'), '', FailureHandling.CONTINUE_ON_FAILURE)

WebUI.verifyElementText(findTestObject('Auto01/label_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto01/div_i'), 'i')

WebUI.click(findTestObject('Auto01/div_i'))

WebUI.verifyElementText(findTestObject('Auto01/span_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto01/p_The cash you deposit towards the purchase_f6f3e8'), 'The cash you deposit towards the purchase of home, car, etc. The larger the down payment, the less you are required to borrow.')

WebUI.click(findTestObject('Auto01/span_X_1_2'))

WebUI.verifyElementText(findTestObject('Auto01/input_Global_AutoDownPayment_Must be between 0 and 249,999'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto01/input_Global_AutoInterestRate_Must be between 0.00 and 50.00'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto01/div_i_1'), 'i')

WebUI.click(findTestObject('Auto01/div_i_1'))

WebUI.verifyElementText(findTestObject('Auto01/span_Loan term'), 'Loan term')

WebUI.verifyElementText(findTestObject('Auto01/p_The length, or period, of a loan. It is u_528915'), 'The length, or period, of a loan. It is usually measured in years.')

WebUI.click(findTestObject('Auto01/span_X_1_2'))

WebUI.verifyElementText(findTestObject('Auto01/input_Global_AutoLoanTerm_Must be between 1 and 360'), '')

WebUI.verifyElementText(findTestObject('Auto01/a_New Vehicle Cost'), 'NEW VEHICLE COST')

WebUI.click(findTestObject('Auto01/a_New Vehicle Cost'))

WebUI.verifyElementText(findTestObject('Auto01/label_Years you will own vehicle'), 'Years you will own vehicle')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_YearsOwned_Must be between 1 and 30'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Monthly maintenance costs'), 'Monthly maintenance costs')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_MonthlyMaintenance_Must be between 0 and 10,000'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Monthly insurance costs'), 'Monthly insurance costs')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_MonthlyInsurance_Must be between 0 and 10,000'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Registration costs'), 'Registration costs')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_Registration_Must be between 0 and 100,000'), '')

WebUI.verifyElementText(findTestObject('Auto01/a_Used Vehicle Loan'), 'USED VEHICLE LOAN')

WebUI.click(findTestObject('Auto01/a_Used Vehicle Loan'))

WebUI.verifyElementText(findTestObject('Auto01/label_Purchase price_1'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehiclePurchasePrice_Must be between 0 and 250,000'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Down payment_1'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto01/div_i_1_2'), 'i')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleDownPaymen_Must be between 0 and 249,999'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Interest rate_1'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleInterestRate_Must be between 0.00 and 50.00'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term  (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto01/div_i_1_2_3'), 'i')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleLoanTerm_ Must be between 1 and 360'), '')

WebUI.verifyElementText(findTestObject('Auto01/a_Used Vehicle Cost'), 'USED VEHICLE COST')

WebUI.click(findTestObject('Auto01/a_Used Vehicle Cost'))

WebUI.verifyElementText(findTestObject('Auto01/label_Vehicle age                          _16c007'), 'Vehicle age (years)')

WebUI.verifyElementText(findTestObject('Auto01/span_(years)'), '(years)')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleAgeInYears_Must be between 0 and 30'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Years you will own vehicle_1'), 'Years you will own vehicle')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_YearsUsedVehicleOwned_Must be between 1 and 30'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Monthly maintenance costs_1'), 'Monthly maintenance costs')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleMonthlyMaintenance_Must be between 0 and 10,000'), 
    '')

WebUI.verifyElementText(findTestObject('Auto01/label_Monthly insurance costs_1'), 'Monthly insurance costs')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleMonthlyInsurance_Must be between 0 and 10,000'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Registration costs_1'), 'Registration costs')

WebUI.verifyElementText(findTestObject('Auto01/input_Auto_UsedVehicleRegistration_Must be between 0 and 100,000'), '')

WebUI.verifyElementText(findTestObject('Auto01/a_Taxes  Depreciation'), 'TAXES & DEPRECIATION')

WebUI.click(findTestObject('Auto01/a_Taxes  Depreciation'))

WebUI.verifyElementText(findTestObject('Auto01/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementText(findTestObject('Auto01/div_i_1_2_3_4'), 'i')

WebUI.click(findTestObject('Auto01/div_i_1_2_3_4'))

WebUI.verifyElementText(findTestObject('Auto01/span_Tax rates'), 'Tax rates')

WebUI.verifyElementText(findTestObject('Auto01/p_The percentage of your taxable income tha_1cb3d7'), 'The percentage of your taxable income that is owed to the state and federal governments. The tax rate increases as the taxable base amount increases.')

WebUI.click(findTestObject('Auto01/span_X_1_2'))

WebUI.verifyElementText(findTestObject('Auto01/input_Global_FederalAndStateTaxRate_Must be between 0.00 and 90.00'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Future depreciation'), 'Future depreciation')

WebUI.verifyElementText(findTestObject('Auto01/div_i_1_2_3_4_5'), 'i')

WebUI.click(findTestObject('Auto01/div_i_1_2_3_4_5'))

WebUI.verifyElementText(findTestObject('Auto01/span_Depreciation'), 'Depreciation')

WebUI.verifyElementText(findTestObject('Auto01/p_The decline in an assets value over the c_40befa'), 'The decline in an asset\'s value over the course of its useful life due to wear and tear and obsolescence.')

WebUI.click(findTestObject('Auto01/span_X_1_2'))

not_run: WebUI.verifyElementText(findTestObject('Auto01/select_Low                                A_f43cd9'), 'Average')

WebUI.verifyElementText(findTestObject('Auto01/label_Your savings interest rate'), 'Your savings interest rate')

WebUI.verifyElementText(findTestObject('Auto01/input_Global_SavingsInterestRate_Must be between 0.00 and 100.00'), '')

WebUI.closeBrowser()

