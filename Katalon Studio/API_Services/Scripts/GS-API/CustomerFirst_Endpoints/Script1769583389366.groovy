import com.utils.MainValidator as MV
CustomKeywords.'com.utils.MainValidator.runRestJson'('CustomerFirst_Endpoints')

// Force a RED step in the Test Case Log if the keyword recorded any failure
assert MV.lastRunFailed() == false