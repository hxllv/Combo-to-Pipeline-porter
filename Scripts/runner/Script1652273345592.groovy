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

WebUI.callTestCase(findTestCase('main'), [('combourl') : 'http://kekec:8080/job/COMBO%20-%20TS%20Portal%20-%20Builder/configure'
        , ('pipelineurl') : 'http://kekec:8080/view/TS%20-%20Portal%20/job/.COMBO%20-%20Pipeline%20-%20TS%20Portal%20-%20Builder/configure'], FailureHandling.STOP_ON_FAILURE)

WebUI.comment('PLEASE READ!')

WebUI.comment('How url\'s should look:')

WebUI.comment('combourl = http://kekec:8080/job/{{comboName}}/configure')

WebUI.comment('pipelineurl = http://kekec:8080/job/{{pipelineName}}/configure')

WebUI.comment('After runner is done, double check script and delete trailing curly braces.')

WebUI.comment('If any editing of the "main" test case is needed, only edit it in script view, as editing in manual view messes up the script')

