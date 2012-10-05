package uw.star.rts.goal;

import static org.junit.Assert.*;

import java.nio.file.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uw.star.rts.goal.STSGoal;
import uw.star.rts.goal.STSGoalFactory;

public class STSGoalFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testParseSTSgoalsXML() {
		Path fileName = Paths.get("config","STSgoals.xml");
		STSGoal rootGoal = STSGoalFactory.newInstance().modelingGoals(fileName);
		assertEquals("STSGoalXML parsing result: #of goals",17,rootGoal.size());
	}

}
