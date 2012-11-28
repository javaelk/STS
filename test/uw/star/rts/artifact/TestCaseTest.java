package uw.star.rts.artifact;

import static org.junit.Assert.*;

import java.util.BitSet;

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
		tc.addApplicableVersion(0);
		assertTrue(tc.isApplicabletoVersion(0));
		tc.addApplicableVersion(1);
		assertEquals("{0, 1}",tc.getApplicableVersions());
		assertTrue(tc.isApplicabletoVersion(1));
		assertTrue(tc.isApplicabletoVersion(0));
		tc.addApplicableVersion(4);
		assertEquals("{0, 1, 4}",tc.getApplicableVersions());
		assertTrue(tc.isApplicabletoVersion(1));
		assertTrue(tc.isApplicabletoVersion(0));
		assertTrue(tc.isApplicabletoVersion(4));
		assertFalse(tc.isApplicabletoVersion(5));
	}
	
	@Test
	public void testSetApplicableVersions2() {
		TestCase tc = new TestCase("xml-security",1,"TESTETS",null);
		tc.addApplicableVersion(1);
		assertTrue(tc.isApplicabletoVersion(1));
		assertFalse(tc.isApplicabletoVersion(0));
		
	}
	
	@Test 
	public void testBitset(){
		BitSet bits = new BitSet();
		bits.set(1);
		System.out.println(bits.get(0));
		System.out.println(bits.get(1));
		System.out.println(bits);
		bits.set(2);
		System.out.println(bits);
		System.out.println(bits.get(0));
		System.out.println(bits.get(1));
	}
}
