package uw.star.rts.util;

import static org.junit.Assert.*;
import java.nio.file.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uw.star.rts.util.VersionDirectoryComparator;

import java.nio.file.Paths;
import java.util.*;

public class VersionDirectoryComparatorTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testCompare() {
		Comparator<Path> cp = new VersionDirectoryComparator("v");
		assertEquals("same dir",0,cp.compare(Paths.get("v2"),Paths.get("v2")) );
		assertEquals("great than",1,cp.compare(Paths.get("v22"),Paths.get("v2")) );
		assertEquals("equal",0,cp.compare(Paths.get("v02"),Paths.get("v2")) );
		assertEquals("less than",-1,cp.compare(Paths.get("v1"),Paths.get("v12")) );
		assertEquals("transitive",-1,cp.compare(Paths.get("v12"),Paths.get("v20")) );
		assertEquals("transitive",-1,cp.compare(Paths.get("v1"),Paths.get("v20")) );
		
		exception.expect(IllegalArgumentException.class);
		assertEquals(567,cp.compare(Paths.get("anything"),Paths.get("v12")));
		
		exception.expect(NumberFormatException.class);
		assertEquals(567,cp.compare(Paths.get("vanything"),Paths.get("v12")));
	}

}
