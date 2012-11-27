package uw.star.rts.extraction;

import static org.junit.Assert.*;
import java.util.*;

import java.io.File;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import uw.star.rts.artifact.Application;
import uw.star.rts.main.Engine;

public class SIRJavaArtifactVerificationTest {

	static List<Application> testapps;
	static SIRJavaFactory sir;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//extract all applications 
		Engine.getInputFiles("config"+File.separator+"ARTSConfiguration.property");
		testapps = Engine.extractInfoFromRepository(Paths.get("config/CaseStudySubject.xml"));
		sir = new SIRJavaFactory();
		sir.setExperimentRoot("/home/wliu/sir");
	}

	@Test
	public void testVerifyJUnitTestCasesExists() {
		for(Application app : testapps)
			assertEquals("should be no errors", 0, new SIRJavaArtifactVerification(sir).verifyJUnitTestCasesExists(app));
	}
	
	@Test
	public void testVerifyTestCasesExistInExecutionScripts() {
		for(Application app : testapps)
			assertEquals("should be no errors", 0, new SIRJavaArtifactVerification(sir).verifyTestCasesExistInExecutionScripts(app));
	}
	

}
