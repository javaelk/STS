package uw.star.rts.extraction;

import java.io.File;
import java.nio.file.Path;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uw.star.rts.artifact.*;
import uw.star.rts.util.FileUtility;

public class SIRJavaArtifactVerification extends ArtifactVerification {

	SIRJavaFactory sir;
	Logger log;
	public SIRJavaArtifactVerification(SIRJavaFactory sirJava){
		sir = sirJava;
		log= LoggerFactory.getLogger(SIRJavaArtifactVerification.class.getName());
	}

	/**
	 * Every test case in the version should exist in the execution script of the same version
	 * @param app
	 * @return number of errors 
	 */
	public int verifyTestCasesExistInExecutionScripts(Application app){
        int numVersions = app.getTotalNumVersons();
        int errorCounter=0;
        List<Path> testScripts = sir.extractTestExecutionScripts(app.getApplicationName());
        for(int i=0;i<numVersions;i++){
        	List<TestCase> testcases = app.getTestSuite().getTestCaseByVersion(i);
        	Path testScriptFile = getTestScriptByVersion(testScripts,i);
        	List<String> testsInExecutionScript = sir.parseTestScriptFile(testScriptFile);
        	for(TestCase t: testcases){
        		boolean exist = false;
        		for(String testInScript: testsInExecutionScript)
        		  if(testInScript.equals(t.getTestCaseName())){
        			  exist = true;
        			  log.debug(t.getApplicationName() + "-" + "test case " + t.getTestCaseName() + " exist in " + testScriptFile);
        			  break;
        		  }
        		if(!exist){
        			log.error(t.getApplicationName() + "-" + "test case " + t.getTestCaseName()  + " does not exist in test script" + testScriptFile);
        			errorCounter++;
        		}
        	}
        }
        if(errorCounter==0)
        	log.debug("Application " + app.getApplicationName() + " contains no errors - all junit test cases found in test scripts! ");
        return errorCounter;
		
	}
	
	
	Path getTestScriptByVersion(List<Path> scriptFiles,int ver){
		for(Path f:scriptFiles){
			String expectedFileName = "scriptR"+ver+"coverage.cls";
			if(f.getFileName().toString().equals(expectedFileName))
				return f;
		}
		log.error("Error finding script file for version" + ver + " in given list " + scriptFiles);
		return null;
	}
	
	
	
	/**
	 * Based on the JUnit test case names in test suite, confirm they all exist in class files
	 * @return number of errors
	 * @param app
	 */
	public int verifyJUnitTestCasesExists(Application app){
        int numVersions = app.getTotalNumVersons();
        int errorCounter=0;
        for(int i=0;i<numVersions;i++){
        	List<TestCase> testcases = app.getTestSuite().getTestCaseByVersion(i);
        	Program p = app.getProgram(ProgramVariant.orig, i);
        	List<Path> classFiles = p.getCodeFiles(CodeKind.BINARY);
        	for(TestCase t: testcases){
        		boolean exist = false;
        		String testcaseNametoPath = t.getTestCaseName().replace('.', File.separatorChar)+".class";
        		for(Path classFile: classFiles)
        		  if(classFile.endsWith(testcaseNametoPath)){
        			  exist = true;
        			  log.debug(t.getApplicationName() + "-" + "test case " + t.getTestCaseName() + " exist in " + classFile);
        			  break;
        		  }
        		if(!exist){
        			log.error(t.getApplicationName() + "-" + "test case " + t.getTestCaseName()  + " does not have a junit class exist in version" + i);
        			errorCounter++;
        		}
        	}
        }
        if(errorCounter==0)
        	log.debug("Application " + app.getApplicationName() + " contains no errors - all junit test cases found! ");
        return errorCounter;
	}
	
	
	 //nothing need to be added or created here as all test cases should have been added in the universe.all file already. This step is for verification only.
	 void parseUniverseFiles(Path verDir,Map<String,TestCase> testcaseMap,int totalNumOfVersions){
    	 
		 int dirVer = Integer.parseInt(verDir.getFileName().toString().substring(SIRJavaFactory.VERSIONS_DIRECTORY_PREFIX.length()));
    	
		 //find universe file
		 for(Path file: FileUtility.findFiles(verDir,SIRJavaFactory.TestSuite_UNIVERSAL_FILE_PATTERN)){
			 int fileVer = sir.parseTestCaseFileVerNo(file);
			 if(dirVer==fileVer){
				 //vk.class.junit.universe: common test cases between vk and vk+1. 
				 //applicable versions k, and k+1,exist in previous no
				 for(String n: sir.parseTestCaseFile(file)){
					 if(!testcaseMap.containsKey(n)){
						 log.error("Test case " + n + " in " + file.toString() + " does not exist!");
					 }else{
						 if(!(testcaseMap.get(n).isApplicabletoVersion(dirVer)))
							 log.error("Test case " + n + " in " + file.toString() + " should be applicable to version" + dirVer);
						 if(dirVer<totalNumOfVersions-1&&!(testcaseMap.get(n).isApplicabletoVersion(dirVer+1)))  //don't need to check last version
							 log.error("Test case " + n + " in " + file.toString() + " should be applicable to version" + (dirVer+1));
					 }
				 }
			 }else if(fileVer == dirVer-1){
				 // vk-1.class.junit.universe: common test cases between vk-1 and vk.
				 // applicable versions k, and k-1,exist in previous yes
				 //these are existing test cases for this version
				 for(String n: sir.parseTestCaseFile(file)){
					 if(!testcaseMap.containsKey(n)){
						 log.error("Test case " + n + " in " + file.toString() + " does not exist!");
					 }else{
						 if(!(testcaseMap.get(n).isApplicabletoVersion(dirVer)))
							 log.error("Test case " + n + " in " + file.toString() + " should be applicable to version" + dirVer);
						 if(!(testcaseMap.get(n).isApplicabletoVersion(dirVer-1)))
							 log.error("Test case " + n + " in " + file.toString() + " should be applicable to version" + (dirVer-1));
					 }
				 }

			 }else{
				 System.out.println("something is wrong, file verion number "+fileVer + " does not match with directory version number" + dirVer);
			 }

		 }
	 }
}
