import com.utils.MainValidator as MV

// Run the test for this calc ID
CustomKeywords.'com.utils.MainValidator.runFromJson'('home09')

// Force a RED step in the Test Case Log if the keyword recorded any failure
assert MV.lastRunFailed() == false