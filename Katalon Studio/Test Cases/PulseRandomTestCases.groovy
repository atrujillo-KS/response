import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling
import pulse.TestCaseLoader

def testCaseMap = TestCaseLoader.getTestCasesByCategory()

// Adjust weights here (values = desired percentage of total)
def weights = [
        'Home': 0.60,
        'Auto': 0.25,
        'Savings': 0.15
]

// How many test cases to run in total
def totalToRun = 20

// Build the final pool based on weights
def finalList = []

weights.each { category, weight ->
    def cases = testCaseMap[category] ?: []
    def count = (totalToRun * weight).intValue()

    // Shuffle and take the required amount, wrapping if needed
    def shuffled = cases.toList().shuffled()
    if (shuffled.size() < count) {
        finalList.addAll(shuffled)
        finalList.addAll(shuffled.take(count - shuffled.size())) // Repeat if not enough cases
    } else {
        finalList.addAll(shuffled.take(count))
    }
}

// Shuffle the overall test list to simulate real user randomness
Collections.shuffle(finalList)

finalList.each { testCasePath ->
    println "Running: ${testCasePath}"
    WebUI.callTestCase(findTestCase(testCasePath), [:], FailureHandling.CONTINUE_ON_FAILURE)
}