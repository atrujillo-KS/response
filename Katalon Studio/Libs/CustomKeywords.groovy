
/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */

import java.lang.String

import com.kms.katalon.core.testobject.TestObject



def static "com.utils.MainValidator.runFromJson"(
    	String calcIdParam	) {
    (new com.utils.MainValidator()).runFromJson(
        	calcIdParam)
}

 /**
	 * Refresh browser
	 */ 
def static "com.utils.InputHelper.refreshBrowser"() {
    (new com.utils.InputHelper()).refreshBrowser()
}

 /**
	 * Click element
	 * @param to Katalon test object
	 */ 
def static "com.utils.InputHelper.clickElement"(
    	TestObject to	) {
    (new com.utils.InputHelper()).clickElement(
        	to)
}

 /**
	 * Get all rows of HTML table
	 * @param table Katalon test object represent for HTML table
	 * @param outerTagName outer tag name of TR tag, usually is TBODY
	 * @return All rows inside HTML table
	 */ 
def static "com.utils.InputHelper.getHtmlTableRows"(
    	TestObject table	
     , 	String outerTagName	) {
    (new com.utils.InputHelper()).getHtmlTableRows(
        	table
         , 	outerTagName)
}

 /**
	 * Safely clears and sets text in an input field
	 * Works around issues where WebUI.setText appends instead of replacing
	 */ 
def static "com.utils.InputHelper.clearAndSetText"(
    	TestObject testObject	
     , 	String value	) {
    (new com.utils.InputHelper()).clearAndSetText(
        	testObject
         , 	value)
}

 /**
	 * Refresh browser
	 */ 
def static "helpers.InputUtils.refreshBrowser"() {
    (new helpers.InputUtils()).refreshBrowser()
}

 /**
	 * Click element
	 * @param to Katalon test object
	 */ 
def static "helpers.InputUtils.clickElement"(
    	TestObject to	) {
    (new helpers.InputUtils()).clickElement(
        	to)
}

 /**
	 * Get all rows of HTML table
	 * @param table Katalon test object represent for HTML table
	 * @param outerTagName outer tag name of TR tag, usually is TBODY
	 * @return All rows inside HTML table
	 */ 
def static "helpers.InputUtils.getHtmlTableRows"(
    	TestObject table	
     , 	String outerTagName	) {
    (new helpers.InputUtils()).getHtmlTableRows(
        	table
         , 	outerTagName)
}

 /**
	 * Safely clears and sets text in an input field
	 * Works around issues where WebUI.setText appends instead of replacing
	 */ 
def static "helpers.InputUtils.clearAndSetText"(
    	TestObject inputField	
     , 	String text	) {
    (new helpers.InputUtils()).clearAndSetText(
        	inputField
         , 	text)
}


def static "com.utils.MainValidatorBasic.runFromJson"(
    	String calcId	) {
    (new com.utils.MainValidatorBasic()).runFromJson(
        	calcId)
}
