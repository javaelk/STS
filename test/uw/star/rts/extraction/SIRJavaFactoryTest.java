package uw.star.rts.extraction;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import uw.star.rts.artifact.*;
import uw.star.rts.extraction.SIRJavaFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SIRJavaFactoryTest {
    
	/**
	 * @uml.property  name="testapp"
	 * @uml.associationEnd  
	 */
	Application testapp;
	/**
	 * @uml.property  name="sir"
	 * @uml.associationEnd  
	 */
	SIRJavaFactory sir;
	/**
	 * @uml.property  name="appname"
	 */
	String appname = "apache-xml-security";
    
	@Before
	public void setUp() throws Exception {
	  sir = new SIRJavaFactory();
	  sir.setExperimentRoot("C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox");
	  testapp = sir.extract(appname);
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
		assertEquals("orig has 4 version",4,testapp.getProgram(ProgramVariant.orig).size());
		//assertEquals("seeded has 3 version",3,testapp.getProgram(ProgramVariant.seeded).size());
		//TODO:java.lang.AssertionError: #of .java files in orig v0 expected:<228> but was:<395>
        assertEquals("#of .java files in orig v0", 167,testapp.getProgram(ProgramVariant.orig, 0).getCodeFiles(CodeKind.SOURCE).size());
        assertEquals("#of .class files in orig v3", 209,testapp.getProgram(ProgramVariant.orig, 3).getCodeFiles(CodeKind.BINARY).size());
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
	
		//assertEquals("verify nanoxml has 6 versions of test suites",6, nanoxmlTest.size());
		//assertEquals("verfiy v0 has x test cases", 10,nanoxmlTest.get(0).getTestCases().size());
		
	
		//fail("Not yet implemented");
	}

	@Test
	public void testParseTestCaseFileVerNo() {
		//fail("Not yet implemented");
	}

	@Test
	public void testParseTestCaseFile() {
		//fail("Not yet implemented");
	}
	
	
	@Test
	public void testGetEmmaCodeCoverageResultFile(){
		Path xmlfile = Paths.get("C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox\\apache-xml-security\\traces.alt\\CodeCoverage\\orig\\v0\\coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest.xml");
		Path htmldir = Paths.get("C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox\\apache-xml-security\\traces.alt\\CodeCoverage\\orig\\v0\\coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest\\_files"); 
		Program p0 = testapp.getProgram(ProgramVariant.orig, 0);
		TestCase t0 = testapp.getTestSuite().getTestCaseByName("org.apache.xml.security.test.c14n.helper.C14nHelperTest");
		assertEquals("test xml", xmlfile,sir.getEmmaCodeCoverageResultFile(p0, t0, "xml"));
		assertEquals("test html", htmldir,sir.getEmmaCodeCoverageResultFile(p0, t0, "html"));
		assertEquals("test null",null,sir.getEmmaCodeCoverageResultFile(p0, t0, "something"));
	}
}
