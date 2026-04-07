package internal

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.main.TestCaseMain


/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */
public class GlobalVariable {
     
    /**
     * <p></p>
     */
    public static Object URL
     
    /**
     * <p></p>
     */
    public static Object category
     
    /**
     * <p></p>
     */
    public static Object calcId
     
    /**
     * <p></p>
     */
    public static Object isHeadless
     
    /**
     * <p></p>
     */
    public static Object clientId
     
    /**
     * <p></p>
     */
    public static Object debugging
     
    /**
     * <p></p>
     */
    public static Object browser
     
    /**
     * <p></p>
     */
    public static Object pauseOnStep
     

    static {
        try {
            def selectedVariables = TestCaseMain.getGlobalVariables("default")
			selectedVariables += TestCaseMain.getGlobalVariables(RunConfiguration.getExecutionProfile())
    
            URL = selectedVariables['URL']
            category = selectedVariables['category']
            calcId = selectedVariables['calcId']
            isHeadless = selectedVariables['isHeadless']
            clientId = selectedVariables['clientId']
            debugging = selectedVariables['debugging']
            browser = selectedVariables['browser']
            pauseOnStep = selectedVariables['pauseOnStep']
            
        } catch (Exception e) {
            TestCaseMain.logGlobalVariableError(e)
        }
    }
}
