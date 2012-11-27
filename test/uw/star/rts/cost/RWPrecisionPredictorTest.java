package uw.star.rts.cost;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uw.star.rts.artifact.CodeCoverageTest;

public class RWPrecisionPredictorTest extends CodeCoverageTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testGetPredicatedPercetageOfTestCaseSelected() {
		assertEquals("selection rate",0.5,RWPrecisionPredictor.getPredicatedPercetageOfTestCaseSelected(codeCoverage,tca),0.01);
	}

}
