package uw.star.rts.extraction;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import uw.star.rts.artifact.*;
import uw.star.rts.extraction.SIRJavaFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SIRJavaFactoryTest {
    
	/**
	 * @uml.property  name="testapp"
	 * @uml.associationEnd  
	 */
	static Application testapp;
	/**
	 * @uml.property  name="sir"
	 * @uml.associationEnd  
	 */
	static SIRJavaFactory sir;
	/**
	 * @uml.property  name="appname"
	 */
	static String appname = "apache-xml-security";
    
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	  sir = new SIRJavaFactory();
	  sir.setExperimentRoot("/home/wliu/sir");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExtract() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSIRJavaFactory() {
		//fail("Not yet implemented");
	}

	@Test
	public void testExtractProgram() {
		 testapp = sir.extract(appname);
		assertEquals("orig has 4 version",4,testapp.getProgram(ProgramVariant.orig).size());
		//assertEquals("seeded has 3 version",3,testapp.getProgram(ProgramVariant.seeded).size());
		//TODO:java.lang.AssertionError: #of .java files in orig v0 expected:<228> but was:<395>
        assertEquals("#of .java files in orig v0", 167,testapp.getProgram(ProgramVariant.orig, 0).getCodeFiles(CodeKind.SOURCE).size());
        assertEquals("#of .class files in orig v3", 209,testapp.getProgram(ProgramVariant.orig, 3).getCodeFiles(CodeKind.BINARY).size());
	}
	
	@Test
	public void testExtractAntProgram() {
		  testapp = sir.extract("apache-ant");
        assertEquals("#of .class file in apache-ant orig v1",316,testapp.getProgram(ProgramVariant.orig, 1).getCodeFiles(CodeKind.BINARY).size());
	}
	

	@Test
	public void testFindCommonFilesinApplicatonDirs() {
		//fail("Not yet implemented");
	}

	@Test
	public void testFindVersionFilesinApplicationDirs() {
		//fail("Not yet implemented");
	}

	@Test
	public void testExtractTestSuites() {

		Path file = Paths.get("output"+File.separator+"testSuiteAnalysis_"+testapp.getApplicationName()+".csv");
		Charset charset = Charset.forName("UTF-8");
		TestSuite ts = testapp.getTestSuite();
		try(BufferedWriter writer = Files.newBufferedWriter(file,charset,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING)){
			//write column headers
			writer.write("version,test case applicable to current ver,#of test case applicable to current ver,test cases also applicable to next ver, #test case also applicable to next ver\n");
			for(int i=0;i<testapp.getTotalNumVersons();i++){
				writer.write("v"+i+",");
				List<TestCase> tc = ts.getTestCaseByVersion(i); 
				for(TestCase test:tc)
					writer.write(test.getTestCaseName()+" ");
				writer.write(",");
				writer.write(tc.size()+",");
				if(i<testapp.getTotalNumVersons()-1){
					int counter=0;
					for(TestCase test:tc){//for every test case in current version
						if(test.isApplicabletoVersion(i+1)){ //verify if it's also applicable to next version
							writer.write(test.getTestCaseName()+" ");
							counter++;
						} 
					}
					writer.write(",");
					writer.write(counter+"");
					writer.write("\n");
				}
			}
		}catch(IOException ec){
			ec.printStackTrace();
		}
	}

	@Test
	public void testParseTestCaseFileVerNo() {
	}

	@Test
	public void testParseTestCaseFile() {
		Path univserFile = Paths.get("test"+File.separator+"testfiles"+File.separator+"v0.class.junit.universe");
		List<String> testcaseNames = sir.parseTestCaseFile(univserFile);
		assertTrue("there should be total of 13 test cases", testcaseNames.size()==13);
		assertTrue("should contain test case org.apache.xml.security.test.interop.IAIKTest",testcaseNames.contains("org.apache.xml.security.test.interop.IAIKTest"));
		assertTrue("should contain test case org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test",testcaseNames.contains("org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test"));
	}
	
	
	@Test
	public void testGetEmmaCodeCoverageResultFile(){
		testapp = sir.extract(appname);
		Path xmlfile = Paths.get("/home/wliu/sir/apache-xml-security/traces.alt/CODECOVERAGE/orig/v0/coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest.xml");
		Path htmldir = Paths.get("/home/wliu/sir/apache-xml-security/traces.alt/CODECOVERAGE/orig/v0/coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest/_files"); 
		Program p0 = testapp.getProgram(ProgramVariant.orig, 0);
		TestCase t0 = testapp.getTestSuite().getTestCaseByName("org.apache.xml.security.test.c14n.helper.C14nHelperTest");
		assertEquals("test xml", xmlfile,sir.getEmmaCodeCoverageResultFile(p0, t0, "xml"));
		assertEquals("test html", htmldir,sir.getEmmaCodeCoverageResultFile(p0, t0, "html"));
		assertNull("test null",sir.getEmmaCodeCoverageResultFile(p0, t0, "something"));
	}
	
	@Test
	public void testGetTestScripts(){
		List<Path> scripts = sir.extractTestExecutionScripts(appname);
		assertEquals("apache-xml-security has 4 test scripts in total",4,scripts.size());
		Path s0 = Paths.get("/home/wliu/sir/apache-xml-security/scripts/TestScripts/scriptR0coverage.cls");
		assertTrue("apache-xml-security contains script 0",scripts.contains(s0));
	}
	
	@Test 
	public void testParseTestScriptFile(){
	    Path sampleTestScript = Paths.get("test"+File.separator+"testfiles"+File.separator+"scriptR3coverage.cls");
	    List<String> testcases = sir.parseTestScriptFile(sampleTestScript);
	    assertEquals("contains 13 test cases", 13, testcases.size());
	    System.out.println(testcases);
	    assertTrue("contains test case org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test",testcases.contains("org.apache.xml.security.test.c14n.implementations.Canonicalizer20010315Test"));
	    
	}
}
