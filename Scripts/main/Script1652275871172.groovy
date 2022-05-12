import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.util.KeywordUtil

WebUI.openBrowser(combourl)

WebUI.delay(5)

int i = 1

def phaseNames = []
def jobNames = []
def conditions = []

while (true) {
    if (!WebUI.verifyElementPresent(findTestObject('phase', [('i') : i]), 1, FailureHandling.OPTIONAL)) {
        break
    }
	
	if (!WebUI.verifyElementPresent(findTestObject('phase name', [('i') : i]), 1, FailureHandling.OPTIONAL)) {
		KeywordUtil.markFailed("phase name not present, step might be different than expected")
		i++
		phaseNames.add(null)
		jobNames.add(null)
		conditions.add(null)
		continue
	}
	
	int j = 1;
	def tempJobs = []
	while (true) {
		if (!WebUI.verifyElementPresent(findTestObject('job name', [('i') : i, ('j') : j]), 1, FailureHandling.OPTIONAL)) {
			break
		}
		
		tempJobs.add(WebUI.getAttribute(findTestObject('job name', [('i') : i, ('j') : j]), "value"))
		j++
	}
	
	if (j == 1) {
		KeywordUtil.markFailed("job name not present, step might be different than expected")
		i++
		phaseNames.add(null)
		jobNames.add(null)
		conditions.add(null)
		continue
	}
	
	jobNames.add(tempJobs)
	phaseNames.add(WebUI.getAttribute(findTestObject('phase name', [('i') : i]), "value"))
	conditions.add(WebUI.getAttribute(findTestObject('continuation condition', [('i') : i]), "value"))
	
	println(i)
	i++
}

println phaseNames
println jobNames
println conditions

String label = ""
String scriptStart = """pipeline {
agent any
stages {""" 

if (WebUI.verifyElementPresent(findTestObject('label'), 1, FailureHandling.OPTIONAL)) {
	label = WebUI.getAttribute(findTestObject('label'), "value")
	
	scriptStart = """pipeline {
agent { label '${label}' }
stages {""" 
}

String scriptEnd = 
"""
}
post {
success {
script {
def previousResult = currentBuild.previousBuild?.result
if (previousResult && previousResult != currentBuild.result) {
slackSend channel: "#jenkinz",
color: "good",
message: "\${env.JOB_NAME} - #\${env.BUILD_NUMBER} Back to normal (<\${env.BUILD_URL}|Open>)"
}
}
}
}
} // delete all after here""" 

String script = """"""

for (int j = 0; j < i - 1; j++) {
	String tempScript = """"""
	
	if (phaseNames[j] == null) {
		script += """
// something should be here, please check the combo"""
		continue
	}
	
	if (conditions[j] == "ALWAYS" || conditions[j] == "COMPLETED") {
		for (int k = 0; k < jobNames[j].size(); k++) {
			if (jobNames[j][k].contains("COMBO")) {
				jobNames[j][k] = jobNames[j][k].replace("COMBO", ".COMBO - Pipeline")
			}
			
			tempScript += """
catchError { build '${jobNames[j][k]}' }"""
		}
	}
	else if (conditions[j] == "SUCCESSFUL") {
		for (int k = 0; k < jobNames[j].size(); k++) {
			if (jobNames[j][k].contains("COMBO")) {
				jobNames[j][k] = jobNames[j][k].replace("COMBO", ".COMBO - Pipeline")
			}
			
			tempScript += """
build '${jobNames[j][k]}'"""
		}
	}
	else {
		KeywordUtil.markFailed("Don't know what to do with this condition :/")
		continue
	}
	
	if (phaseNames[j].contains("COMBO")) {
		phaseNames[j] = phaseNames[j].replace("COMBO", "Combo - Pipeline")
	}
	
	script += """
stage('${phaseNames[j]}') {
steps {""" + tempScript + """
}
}"""
}

WebUI.navigateToUrl(pipelineurl)

WebUI.delay(5)

WebUI.setText(findTestObject("script area"), scriptStart + script + scriptEnd)


