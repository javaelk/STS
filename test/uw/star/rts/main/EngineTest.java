package uw.star.rts.main;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uw.star.rts.main.Engine;

public class EngineTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInputFiles() {
		Engine.getInputFiles("config/ARTSConfiguration.property");
		assertTrue("GoalRepository file exist",Files.exists(Engine.GoalRepository));
		assertTrue("UserGoalSelection file exist",Files.exists(Engine.UserGoalSelection));
		assertTrue("CaseStudySubject file exist",Files.exists(Engine.CaseStudySubject));
		assertTrue("STStechniques file exist",Files.exists(Engine.STStechniques));
	}

	@Test
	public void testModelingGoals() {
		Engine.getInputFiles("config/ARTSConfiguration.property");
		assertEquals("test selected goals",1,Engine.modelingGoals(Engine.GoalRepository, Engine.UserGoalSelection).size());
	}

	@Test
	public void testExtractInfoFromRepository() {
		assertEquals("test extract from repository",4,Engine.extractInfoFromRepository(Paths.get("config/CaseStudySubject.xml")).size());
	}

	@Test
	public void testEvaluation(){
		Engine.getInputFiles("config/ARTSConfiguration.property");
		Engine.evaluate();
	}
}
