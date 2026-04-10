import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// === START TEST ===
WebUI.openBrowser('')
WebUI.navigateToUrl('https://qa.awsc.leadfusion.com/response/qatest/calc/auto01')

// === VERIFY HEADER AND HELP LINK ===
WebUI.verifyElementPresent(findTestObject('Object Repository/Auto01/div_Which is better a new or used vehicle_l_2ad435'), 0)
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/h1_Title'), 'Which is better: a new or used vehicle?')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/span_Help'), 'Help')

// === CLICK AND VERIFY HELP TEXT ===
WebUI.click(findTestObject('Object Repository/Auto01/span_Help'))
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/h2_Help'), 'Help')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/p_The decision to buy a new vehicle instead_8fb65b'), 
    'The decision to buy a new vehicle instead of a used one requires that you consider the following:')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/p_Purchase price. A new vehicle will clearl_f88dae'), 
    'Purchase price. A new vehicle will clearly cost you more than a used vehicle of the same make and model. The extent to which the new or used car will drop in value is based on the depreciation rate chosen - average, high or low.')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/p_Depreciation. Some vehicles depreciate fa_243f55'), 
    'Depreciation. Some vehicles depreciate faster than others do. The slower the depreciation rate of the vehicle, the higher its resale value.')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/p_Operating costs. A used vehicle is likely_fe087c'), 
    'Operating costs. A used vehicle is likely to require more maintenance and repair than a new one. Also, fuel consumption may be less efficient.')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/p_Years of ownership. The longer you own th_aa892f'), 
    'Years of ownership. The longer you own the vehicle, the lower the average annual cost, as acquisition costs are spread out over more years.')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/p_Financing cost. Interest rates for new ve_90a184'), 
    'Financing cost. Interest rates for new vehicle loans are generally lower than for used vehicle loans.')
WebUI.click(findTestObject('Object Repository/Auto01/span_X'))

// === VERIFY INITIAL RESULT ===
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/span_answer'), 
    'The used vehicle will cost you $4,542 less on average each year')

// === SHOW DETAILS (EXPAND CATEGORIES) ===
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/button_Show details'), 'Show details')
WebUI.click(findTestObject('Object Repository/Auto01/button_Show details'))

// === VERIFY TABLE HEADERS & VALUES ===
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/th_Used'), 'Used')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/th_Average cost per year'), 'Average cost per year')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_7,735'), '$7,735')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_3,193'), '$3,193')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/th_Monthly payment'), 'Monthly payment')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_875'), '$875')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_233'), '$233')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/th_Total interest paid'), 'Total interest paid')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_4,005'), '$4,005')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_1,163'), '$1,163')

WebUI.verifyElementText(findTestObject('Object Repository/Auto01/th_Total depreciation'), 'Total depreciation')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_TotDepAuto01_result_value'), '$20,020')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/td_5,588'), '$5,588')

// === HIDE DETAILS (COLLAPSE CATEGORIES) ===
WebUI.click(findTestObject('Object Repository/Auto01/button_Show details'))

// === NEXT STEPS SECTION ===
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/button_Next Steps'), 'NEXT STEPS')
WebUI.click(findTestObject('Object Repository/Auto01/button_Next Steps'))
WebUI.waitForElementVisible(findTestObject('Object Repository/Auto01/h2_Next Steps'), 100)
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/h2_Next Steps'), 'Next Steps')

// === FOOTER DISCLAIMER ===
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/span_These calculators are intended to be u_5fea1a'), 
    'These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.')

// === RETURN TO MAIN VIEW ===
WebUI.click(findTestObject('Object Repository/Auto01/span_X_1'))
WebUI.verifyElementVisible(findTestObject('Object Repository/Auto01/input_Next Steps_patterns_enabled'))
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/label_Enable color patterns'), 'Enable color patterns')

// === GRAPHICAL SECTIONS ===
// These steps are currently disabled but can be activated when graph validation is needed
// not_run: WebUI.verifyElementVisible(findTestObject('Object Repository/Auto01/div_Chartbar-auto01chartdiv'))
// not_run: WebUI.verifyElementVisible(findTestObject('Object Repository/Auto01/svg_Chartbar'))

// === FINAL SECTION HEADERS ===
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/div_Average Cost'), 'Average Cost')
WebUI.verifyElementText(findTestObject('Object Repository/Auto01/div_Payments'), 'Payments')

// === CLOSE BROWSER ===
WebUI.closeBrowser()
