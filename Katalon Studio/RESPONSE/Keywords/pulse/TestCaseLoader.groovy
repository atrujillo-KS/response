package pulse

import java.nio.file.*
import groovy.io.FileType
import com.kms.katalon.core.configuration.RunConfiguration
import java.util.regex.Pattern
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testcase.TestCase

class TestCaseLoader {

	static Map<String, List<String>> getTestCasesByCategory() {
		def rootDir = Paths.get(RunConfiguration.getProjectDir(), "Test Cases")
		def testCaseMap = [:].withDefault { [] }

		Files.walk(rootDir).filter { Files.isRegularFile(it) && it.toString().endsWith('.tc') }.each { path ->
			def relativePath = rootDir.relativize(path).toString().replace(".tc", "")
			def category = relativePath.split(Pattern.quote(File.separator))[0]
			def testCasePath = relativePath.replace(File.separator, "/")
			testCaseMap[category] << testCasePath
		}

		return testCaseMap
	}
}
