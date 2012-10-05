package uw.star.rts.util;

import static org.junit.Assert.*;
import java.nio.file.*;
import java.util.*;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uw.star.rts.util.FileUtility;

public class FileUtilityTest {

	static String TESTFOLDER1="c:\\cygwin\\home\\apache-xml-security\\versions.alt";
	static String TESTFOLDER2="c:\\cygwin\\home\\apache-xml-security\\versions.alt\\seeded";
	static String TESTFOLDER3="c:\\cygwin\\home\\apache-xml-security\\versions.alt\\seeded\\v1\\xml-security\\src";
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListDirectory() {
		Path t = Paths.get(TESTFOLDER1);
		List<String> res = new ArrayList();
		for(Path subdir: FileUtility.listDirectory(t)){
			res.add(subdir.getFileName().toString());
		}
		assertEquals(2,res.size());
		assertEquals(true,res.contains("seeded"));
		assertEquals(true,res.contains("orig"));
		
		t=Paths.get(TESTFOLDER2);
		res = new ArrayList();
		for(Path subdir: FileUtility.listDirectory(t,"v*"))
			res.add(subdir.getFileName().toString());
		assertEquals(3,res.size());
		assertEquals(true,res.contains("v3"));
		assertEquals(true,res.contains("v1"));
		assertEquals(true,res.contains("v2"));
				
		
	}
	
	@Test
	public void testFindFiles(){
		List<Path> res = FileUtility.findFiles(Paths.get(TESTFOLDER3), "*.java");
		/*for(Path file:res){
			System.out.println(file.toString());
		}*/
		assertEquals(179,res.size()); 
		
	}
	
	@Test
	public void testFindDir(){
		Path res = FileUtility.findDirs(Paths.get(TESTFOLDER2), "src").get(0);
		System.out.println("Match! ... "+ res);
		assertEquals(res.toString().contains("src"),true);
	}
}
