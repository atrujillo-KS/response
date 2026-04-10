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

WebUI.navigateToUrl(GlobalVariable.URL + 'm/response/qatest/calc/auto10?newuser')

WebUI.verifyElementText(findTestObject('Auto01/h1_Title'), 'How long should I keep a vehicle?')

WebUI.click(findTestObject('Auto10/span_Help'))

WebUI.verifyElementText(findTestObject('Auto10/p_This tool helps you determine the cheaper_423fe6'), 'This tool helps you determine the cheaper option: buying a vehicle and owning it for a long period, or selling and buying a new vehicle more often. Most likely, keeping a car for a long time is cheaper than buying and selling every couple of years, especially if the user buys and sells a new car every couple of years.')

WebUI.verifyElementText(findTestObject('Auto10/p_The following variables are the primary r_fb7db7'), 'The following variables are the primary reasons why it is usually more expensive to buy and sell a car often:')

WebUI.verifyElementText(findTestObject('Auto10/p_Each time a new car is purchased, the use_e9a40d'), 'Each time a new car is purchased, the user needs to take out a new loan and pay interest on the loan.')

WebUI.verifyElementText(findTestObject('Auto10/p_If new cars are bought, the biggest depre_174e02'), 'If new cars are bought, the biggest depreciation occurs in the first year of ownership.')

WebUI.verifyElementText(findTestObject('Auto10/p_Each time the user buys a car, sales tax _029792'), 'Each time the user buys a car, sales tax needs to be paid.')

WebUI.verifyElementText(findTestObject('Auto10/p_Car insurance is higher for newer cars'), 'Car insurance is higher for newer cars.')

WebUI.verifyElementText(findTestObject('Auto10/p_Vehicle licensingregistration fees are al_74032c'), 'Vehicle licensing/registration fees are also higher for newer cars.')

WebUI.verifyElementText(findTestObject('Auto10/p_For an owner that buys more than one vehi_256add'), 'For an owner that buys more than one vehicle in the period, we assume that vehicle sale price, down payment, depreciation rate, and loan terms are the same for each new vehicle.')

WebUI.click(findTestObject('Auto10/button_X'))

WebUI.verifyElementText(findTestObject('Auto10/span_Keeping a vehicle longer will save you_6d9a6c'), 'Keeping a vehicle longer will save you $16,697 in today\'s dollars')

WebUI.click(findTestObject('Auto10/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto10/th_Keep It'), 'Keep It')

WebUI.verifyElementText(findTestObject('Auto10/th_Sell Often'), 'Sell Often')

WebUI.verifyElementText(findTestObject('Auto10/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto10/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto10/td_10,961'), '$10,961')

WebUI.verifyElementText(findTestObject('Auto10/th_Total interest paid'), 'Total interest paid')

WebUI.verifyElementText(findTestObject('Auto10/td_4,005'), '$4,005')

WebUI.verifyElementText(findTestObject('Auto10/td_10,961'), '$10,961')

WebUI.verifyElementText(findTestObject('Auto10/th_Total depreciation'), 'Total depreciation')

WebUI.verifyElementText(findTestObject('Auto10/td_31,731'), '$31,731')

WebUI.verifyElementText(findTestObject('Auto10/td_53,641'), '$53,641')

WebUI.verifyElementText(findTestObject('Auto10/th_Total repairs'), 'Total repairs')

WebUI.verifyElementText(findTestObject('Auto10/td_6,000'), '$6,000')

WebUI.verifyElementText(findTestObject('Auto10/td_4,000'), '$4,000')

WebUI.verifyElementText(findTestObject('Auto10/th_Total insurance and registration'), 'Total insurance and registration')

WebUI.verifyElementText(findTestObject('Auto10/td_8,000'), '$8,000')

WebUI.verifyElementText(findTestObject('Auto10/td_12,000'), '$12,000')

WebUI.verifyElementText(findTestObject('Auto10/th_Taxes at purchase'), 'Taxes at purchase')

WebUI.verifyElementText(findTestObject('Auto10/td_3,200'), '$3,200')

WebUI.verifyElementText(findTestObject('Auto10/td_9,600'), '$9,600')

WebUI.click(findTestObject('Auto10/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto10/a_General Info'), 'General Info')

WebUI.verifyElementText(findTestObject('Auto10/label_Purchase price'), 'Purchase price')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 1,000,000_Globa_761fb6'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Down payment'), 'Down payment')

WebUI.verifyElementText(findTestObject('Auto10/a_i'), 'i')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 249,999_Global__5d75c9'), '')

WebUI.verifyElementText(findTestObject('Auto01/label_Loan term (months)'), 'Loan term (months)')

WebUI.verifyElementText(findTestObject('Auto10/a_i_1'), 'i')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 1 and 360 months_Glob_4576f3'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Interest rate'), 'Interest rate')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0.00 and 50.00_Global_9435e8'), '')

WebUI.verifyElementText(findTestObject('Auto10/a_Keep It'), 'Keep It')

WebUI.click(findTestObject('Auto10/a_Keep It'))

WebUI.verifyElementText(findTestObject('Auto10/label_Years before selling'), 'Years before selling')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 1 and 30_Auto_YearsBe_2e1f49'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Yearly repairs'), 'Yearly repairs')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_5d1724'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Yearly insurance'), 'Yearly insurance')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_41b782'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Yearly registration'), 'Yearly registration')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_fe1cab'), '')

WebUI.verifyElementText(findTestObject('Auto10/a_Sell Often'), 'Sell Often')

WebUI.click(findTestObject('Auto10/a_Sell Often'))

WebUI.verifyElementText(findTestObject('Auto10/label_Years before selling_1'), 'Years before selling')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 1 and 30_Auto_YearsBe_a86674'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Yearly repairs_1'), 'Yearly repairs')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_d3b30b'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Yearly insurance_1'), 'Yearly insurance')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_9abb1e'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Yearly registration_1'), 'Yearly registration')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_c0e4ae'), '')

WebUI.verifyElementText(findTestObject('Auto10/a_Taxes  Depreciation'), 'Taxes & Depreciation')

WebUI.click(findTestObject('Auto10/a_Taxes  Depreciation'))

WebUI.verifyElementText(findTestObject('Auto10/label_Vehicle age                          _16c007'), 'Vehicle age\n (years)')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0 and 30 years_Auto_V_3f7ed3'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Future depreciation'), 'Future depreciation')

WebUI.verifyElementText(findTestObject('Auto10/a_i_1_2'), 'i')

WebUI.verifyElementText(findTestObject('Auto10/select_Low                                A_f43cd9'), 'Low\n\n Average\n\n High')

WebUI.verifyElementText(findTestObject('Auto10/label_Sales tax rate'), 'Sales tax rate')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0.00 and 99.00_Global_4e88e3'), '')

WebUI.verifyElementText(findTestObject('Auto10/label_Your state  federal tax rate'), 'Your state & federal tax rate')

WebUI.verifyElementPresent(findTestObject('Auto10/a_i_1_2_3'), 0)

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0.00 and 90.00_Global_609372'), '')

WebUI.verifyElementText(findTestObject('Auto10/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '')

WebUI.click(findTestObject('Auto10/a_General Info'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 1,000,000_Globa_761fb6'), '31,822')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 249,999_Global__5d75c9'), '500')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 1 and 360 months_Glob_4576f3'), '72')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0.00 and 50.00_Global_9435e8'), '6.33')

WebUI.click(findTestObject('Auto10/a_Keep It'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 1 and 30_Auto_YearsBe_2e1f49'), '8')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_5d1724'), '344')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_41b782'), '799')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_fe1cab'), '555')

WebUI.click(findTestObject('Auto10/a_Sell Often'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 1 and 30_Auto_YearsBe_a86674'), '5')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_d3b30b'), '555')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_9abb1e'), '999')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 10,000_Auto_Yea_c0e4ae'), '544')

WebUI.click(findTestObject('Auto10/a_Taxes  Depreciation'))

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0 and 30 years_Auto_V_3f7ed3'), '3')

WebUI.selectOptionByValue(findTestObject('Auto10/select_Low                                A_f43cd9'), 
    '2', true)

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0.00 and 99.00_Global_4e88e3'), '5.99')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0.00 and 90.00_Global_609372'), '19.77')

CustomKeywords.'com.utils.InputHelper.clearAndSetText'(findTestObject('Auto10/input_Must be between 0.00 and 100.00_Globa_c7d20a'), '5.22')

WebUI.sendKeys(findTestObject('Auto10/input_Must be between 0.00 and 100.00_Globa_c7d20a'), Keys.chord(
        Keys.ENTER))

WebUI.verifyElementText(findTestObject('Auto10/span_Keeping a vehicle longer will save you_37d04f'), 'Keeping a vehicle longer will save you $1,274 in today\'s dollars')

WebUI.click(findTestObject('Auto10/button_Show details'))

WebUI.verifyElementText(findTestObject('Auto10/td_6,405'), '$6,405')

WebUI.verifyElementText(findTestObject('Auto10/td_10,908'), '$10,908')

WebUI.verifyElementText(findTestObject('Auto10/td_24,147'), '$24,147')

WebUI.verifyElementText(findTestObject('Auto10/td_34,592'), '$34,592')

WebUI.verifyElementText(findTestObject('Auto10/td_2,752'), '$2,752')

WebUI.verifyElementText(findTestObject('Auto10/td_4,440'), '$4,440')

WebUI.verifyElementText(findTestObject('Auto10/td_10,832'), '$10,832')

WebUI.verifyElementText(findTestObject('Auto10/td_12,344'), '$12,344')

WebUI.verifyElementText(findTestObject('Auto10/td_1,906'), '$1,906')

WebUI.verifyElementText(findTestObject('Auto10/td_3,812'), '$3,812')

WebUI.click(findTestObject('Auto10/button_Show details'))

WebUI.closeBrowser()