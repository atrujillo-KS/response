import com.utils.MainValidatorGS as MV

// Run the test for this calc ID
CustomKeywords.'com.utils.MainValidatorGS.runFromJson'('pnc_single')

// Force a RED step in the Test Case Log if the keyword recorded any failure
assert MV.lastRunFailed() == false