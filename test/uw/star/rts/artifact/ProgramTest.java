package uw.star.rts.artifact;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import uw.star.rts.analysis.EmmaCodeCoverageAnalyzer;
import uw.star.rts.extraction.ArtifactFactory;
import uw.star.rts.extraction.SIRJavaFactory;


public class ProgramTest {
	
	static Program p;
	
	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ArtifactFactory af =new SIRJavaFactory();
		af.setExperimentRoot("/home/wliu/sir");
		Application app = af.extract("apache-xml-security");
		p=app.getProgram(ProgramVariant.orig, 0);
		EmmaCodeCoverageAnalyzer analyzer = new EmmaCodeCoverageAnalyzer(af,app,p,app.getTestSuite());
		analyzer.extractEntities(EntityType.CLAZZ);
	}

	@Test
	public void testGetCodeFilebyName() {
		System.out.println(p.getCodeFiles(CodeKind.BINARY));
		System.out.println(p.getCodeFiles(CodeKind.SOURCE));
		//test public class
		Path jcemapper = Paths.get("/home/wliu/sir/apache-xml-security/versions.alt/orig/v0/xml-security/build/classes/org/apache/xml/security/algorithms/JCEMapper.class");
		assertEquals("test public class",0,jcemapper.compareTo(p.getCodeFilebyName(CodeKind.BINARY, "org.apache.xml.security.algorithms", "JCEMapper")));
		
		//test public class
		Path hexDump = Paths.get("/home/wliu/sir/apache-xml-security/versions.alt/orig/v0/xml-security/build/classes/ant/HexDump.class");
		assertTrue(Files.exists(hexDump));
		assertEquals("test public class",0,hexDump.compareTo(p.getCodeFilebyName(CodeKind.BINARY, "ant", "HexDump")));
				
		//test inner class
		Path jcemapperInner = Paths.get("/home/wliu/sir/apache-xml-security/versions.alt/orig/v0/xml-security/build/classes/org/apache/xml/security/algorithms/JCEMapper$ProviderIdClass.class");
		Path codeFile = p.getCodeFilebyName(CodeKind.BINARY, "org.apache.xml.security.algorithms", "JCEMapper$ProviderIdClass");
		assertEquals("test inner class",0,jcemapperInner.compareTo(codeFile));
		
		//test java
		Path aJavaFile = Paths.get("/home/wliu/sir/apache-xml-security/versions.alt/orig/v0/xml-security/build/src/org/apache/xml/security/algorithms/JCEMapper.java");
		codeFile = p.getCodeFilebyName(CodeKind.SOURCE, "org.apache.xml.security.algorithms", "JCEMapper.java");
		System.out.println("Comparing " + aJavaFile.toString() +" \n to " + codeFile.toString());
		assertEquals("test java source file",0,aJavaFile.compareTo(codeFile));
		
		//test html
		
		//test property file

	}

	@Test
	public void testGetEntityByName() {

		Entity e = p.getEntityByName(EntityType.SOURCE, "org.apache.xml.security.algorithms.JCEMapper.java");
		Path aJavaFile = Paths.get("/home/wliu/sir/apache-xml-security/versions.alt/orig/v0/xml-security/build/src/org/apache/xml/security/algorithms/JCEMapper.java");
		assertEquals("test get source entity",0,aJavaFile.compareTo(e.getArtifactFile()));

		Entity c = p.getEntityByName(EntityType.CLAZZ, "org.apache.xml.security.algorithms.JCEMapper$ProviderIdClass");
		Path jcemapperInner = Paths.get("/home/wliu/sir/apache-xml-security/versions.alt/orig/v0/xml-security/build/classes/org/apache/xml/security/algorithms/JCEMapper$ProviderIdClass.class");
		assertEquals("test get source entity",0,jcemapperInner.compareTo(c.getArtifactFile()));

	}
	
	@Test
	public void testGetArtifactFile(){
		//test every entity has a Path associated
		boolean result = true;
		for(Entity e: p.getCodeEntities(EntityType.CLAZZ)){
			if(e.getArtifactFile()==null) result = false;
			System.out.println("File for Entity " + e.toString() + " is " + e.getArtifactFile());
		}
		assertTrue("test every class entity has a Path associated",result);
	}


}
