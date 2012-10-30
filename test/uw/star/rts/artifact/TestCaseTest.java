package uw.star.rts.artifact;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uw.star.rts.artifact.TestCase;

public class TestCaseTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetApplicableVersions() {
		TestCase tc = new TestCase("xml-security",0,"TESTETS",null);
		tc.addApplicableVersions(0);
		assertTrue(tc.isApplicabletoVersion(0));
		tc.addApplicableVersions(1);
		assertEquals("{0, 1}",tc.getApplicableVersions());
		assertTrue(tc.isApplicabletoVersion(1));
		assertTrue(tc.isApplicabletoVersion(0));
		tc.addApplicableVersions(4);
		assertEquals("{0, 1, 4}",tc.getApplicableVersions());
		assertTrue(tc.isApplicabletoVersion(1));
		assertTrue(tc.isApplicabletoVersion(0));
		assertTrue(tc.isApplicabletoVersion(4));
	}
}
