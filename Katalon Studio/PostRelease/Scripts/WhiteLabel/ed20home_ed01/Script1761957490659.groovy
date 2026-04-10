import com.utils.MainValidatorEDU as MV

// Run the test for this calc ID
CustomKeywords.'com.utils.MainValidatorEDU.runFromJson'('home_ed01')

// Force a RED step in the Test Case Log if the keyword recorded any failure
assert MV.lastRunFailed() == false