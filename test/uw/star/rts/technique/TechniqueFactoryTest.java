package uw.star.rts.technique;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.*;
import java.util.*;

import uw.star.rts.technique.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TechniqueFactoryTest {

	TechniqueFactory tf;
	
	@Before
	public void setUp() throws Exception {
		tf=new TechniqueFactory(Paths.get("test"+File.separator+"testfiles"+File.separator+"STStechniques_all.xml"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllTechniques() {
		assertEquals("get all techniques from document", 2,tf.getAllTechniques().size());
	}

	@Test
	public void testTechniquesModeling() {
		List<Technique> techs = tf.techniquesModeling();
		assertEquals("get all techniques from document", 5,techs.size());
		List<String> techNames = new ArrayList<>();
		for(Technique tec: techs)
			techNames.add(tec.getImplmentationName());
		assertTrue(techNames.contains("uw.star.rts.technique.TextualDifference_Source"));
		assertTrue(techNames.contains("uw.star.rts.technique.TextualDifference_Statement"));
	}


	public void testFilterOnDataAvailability() {
		fail("Not yet implemented");
	}

}
