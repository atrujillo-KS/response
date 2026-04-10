import com.utils.MainValidator as MV
CustomKeywords.'com.utils.MainValidator.runFromJson'('lifeins07', true)

// Force a RED step in the Test Case Log if the keyword recorded any failure
assert MV.lastRunFailed() == false