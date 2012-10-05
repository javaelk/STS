package uw.star.rts.util;

import static org.junit.Assert.*;

import org.junit.Test;

import uw.star.rts.util.JavaFileParser;

public class JavaFileParserTest {

	@Test
	public void testGetJavaPackageName(){
		String fileName = "/home/wliu/sir/jakarta-jmeter/versions.alt/orig/v0/JMeter/build/src/functions/org/apache/jmeter/functions/ThreadNumber.java";
		assertEquals("package name", "org.apache.jmeter.functions", JavaFileParser.getJavaPackageName(fileName));
	}
}
