package uw.star.rts.TCselection;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uw.star.rts.TCselection.TestCaseSelection;
import uw.star.rts.artifact.*;
import uw.star.rts.extraction.ArtifactFactory;
import uw.star.rts.extraction.SIRJavaFactory;
import uw.star.rts.technique.*;

public class TestCaseSelectionTest {
	Application app;
	Program v1,v2;
	List<Technique> techs;
	@Before
	public void setUp() throws Exception {
		ArtifactFactory af =new SIRJavaFactory(); 
		af.setExperimentRoot("/home/wliu/sir");
		app = af.extract("apache-xml-security");
		v1=app.getProgram(ProgramVariant.orig, 1);
		v2=app.getProgram(ProgramVariant.orig, 2);
		TechniqueFactory tf=new TechniqueFactory(Paths.get("config/STStechniques.xml"));
		techs = tf.techniquesModeling();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApplyTechnique() {
		String tech1="uw.star.sts.technique.TextualDifference_Source";
		String tech2="uw.star.sts.technique.TextualDifference_Statement";
		for(Technique tec: techs){
			if(tec.getImplmentationName().equalsIgnoreCase(tech1)){
				Map<Program,List<TestCase>> selectedTC = TestCaseSelection.applyTechnique(tec, app,null);
				assertEquals("tc selected for all versions", 4,selectedTC.keySet().size());
				assertTrue("tc selected for ver1",selectedTC.keySet().contains(v1));
				assertEquals("size of selected test cases for v1",13,selectedTC.get(v1).size());
				assertEquals("size of selected test cases for v2",15,selectedTC.get(v2).size());
			}else if(tec.getImplmentationName().equalsIgnoreCase(tech2)){
				Map<Program,List<TestCase>> selectedTC = TestCaseSelection.applyTechnique(tec, app,null);
				assertEquals("tc selected for all versions", 4,selectedTC.keySet().size());
				assertTrue("tc selected for ver1",selectedTC.keySet().contains(v1));
				assertEquals("size of selected test cases for v1",13,selectedTC.get(v1).size());
				assertEquals("size of selected test cases for v2",14,selectedTC.get(v2).size());
			}

		}

	}

}
