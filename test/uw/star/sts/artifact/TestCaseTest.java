package uw.star.sts.artifact;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCaseTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetApplicableVersions() {
		TestCase tc = new TestCase("SIR","xml-security",0,"TESTETS");
		tc.addApplicableVersions(0);
		assertEquals(tc.getApplicableVersions(),1);
		tc.addApplicableVersions(1);
		assertEquals(tc.getApplicableVersions(),3);
		tc.addApplicableVersions(2);
		assertEquals(tc.getApplicableVersions(),7);
	}
}
