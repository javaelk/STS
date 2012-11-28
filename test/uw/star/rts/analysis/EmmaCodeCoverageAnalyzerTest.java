package uw.star.rts.analysis;

import static org.junit.Assert.*;
import java.nio.file.*;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import uw.star.rts.analysis.EmmaCodeCoverageAnalyzer;
import uw.star.rts.artifact.*;
import uw.star.rts.extraction.ArtifactFactory;
import uw.star.rts.extraction.SIRJavaFactory;

public class EmmaCodeCoverageAnalyzerTest {
    
	/**
	 * @uml.property  name="file"
	 */
	static Path xmlfile = Paths.get("/home/wliu/sir/apache-xml-security/traces.alt/CODECOVERAGE/orig/v0/coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest.xml");
	static Path htmlfile = Paths.get("/home/wliu/sir/apache-xml-security/traces.alt/CODECOVERAGE/orig/v0/coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest/_files/71.html");
	static Path htmldir = Paths.get("/home/wliu/sir/apache-xml-security/traces.alt/CODECOVERAGE/orig/v0/coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest/_files"); 
	static Path tidyhtmlfile = Paths.get("/home/wliu/sir/apache-xml-security/traces.alt/CODECOVERAGE/orig/v0/coverage.org.apache.xml.security.test.c14n.helper.C14nHelperTest/_files/71.xml");
	static String packageName = "org.apache.xml.security.algorithms.implementations";
	/**
	 * @uml.property  name="app"
	 * @uml.associationEnd  
	 */
	static Application app;
	static ArtifactFactory af;
	/**
	 * @uml.property  name="p"
	 * @uml.associationEnd  
	 */
	static Program p;
	static EmmaCodeCoverageAnalyzer analyzer;
	
	@BeforeClass
	public static void setUp() throws Exception {
		af =new SIRJavaFactory();
		af.setExperimentRoot("/home/wliu/sir");
		app = af.extract("apache-xml-security");
		p=app.getProgram(ProgramVariant.orig, 0);
		analyzer = new EmmaCodeCoverageAnalyzer(af,app,p,app.getTestSuite());
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTestCaseName() {
		assertEquals("test get test case name from file name","org.apache.xml.security.test.c14n.helper.C14nHelperTest",analyzer.extractTestCaseName(xmlfile));
	}

	@Test
	public void testGetAllEntitiesandCoveredEntities() {
		
 		List<? extends Entity> allClassEntities =  analyzer.extractEntities(EntityType.CLAZZ);
 		List<? extends Entity> allSrc = analyzer.extractEntities(EntityType.SOURCE); 
 		List<? extends Entity> allMethodEntities = analyzer.extractEntities(EntityType.METHOD);
 		List<? extends Entity> allStatementEntities = analyzer.extractEntities(EntityType.STATEMENT);
 
		assertEquals("test number of srcfiles",162,allSrc.size());
		assertEquals("test number of classes ",181,allClassEntities.size() );
		assertEquals("test number of methods",1627,allMethodEntities.size() );
//test contains IntegrityHmac.java
		//test entities are linked together src<->class<->method
		for(Entity se: allSrc)
			if(se.getName().equals("org.apache.xml.security.algorithms.implementations.IntegrityHmac.java")){
				assertEquals("test IntegrityHmac.java has 7 classes",7,((SourceFileEntity)se).getClazzes().size());
				for(Entity ce: ((SourceFileEntity)se).getClazzes())
					if(ce.getName().equals(se.getName()+".IntegrityHmac")){
						assertTrue("class linked to src", ((ClassEntity)ce).getSource()==se);
						List<MethodEntity> methods = ((ClassEntity)ce).getMethods();
						assertEquals("test number of method contains",20,methods.size());
						for(MethodEntity me: methods)
							if(me.getName().equals(ce.getName()+".engineAddContextToElement (Element): void"))
								assertTrue("method links to class",me.getClassEntity()==ce);
						
					}
				
			}
		boolean res = true;
		for(Entity src: allSrc)
			if (((SourceFileEntity)src).getExecutableStatements().size()==0){
				res = false;
				System.out.println("there is no executable statement in " + src);
			}		
		assertTrue("verify there is at least one executable statement in each src file",res );
	}
	@Test 
	public void testGetEntititesP1(){
		Program p1=app.getProgram(ProgramVariant.orig, 1);
		EmmaCodeCoverageAnalyzer analyzer1 = new EmmaCodeCoverageAnalyzer(af,app,p1,app.getTestSuite());
		List<? extends Entity> allSrc = analyzer1.extractEntities(EntityType.SOURCE); 
		assertEquals("test number of srcfiles",173,allSrc.size());
	}
	@Test
	public void testCreateCodeCoverageTrace(){
		
		TestCase t0 = app.getTestSuite().getTestCaseByName(analyzer.extractTestCaseName(xmlfile));
		analyzer.extractEntities(EntityType.CLAZZ);
		Trace clazztrace = analyzer.createCodeCoverage(EntityType.CLAZZ);
		clazztrace.serializeCompressedMatrixToCSV(Paths.get("output","classtrace.txt"));
		assertEquals("test # of covered clazz by t0",63,clazztrace.getLinkedEntitiesByRow(t0).size());
		
		analyzer.extractEntities(EntityType.SOURCE);
		Trace srctrace = analyzer.createCodeCoverage(EntityType.SOURCE);
		srctrace.serializeCompressedMatrixToCSV(Paths.get("output","srctrace.txt"));
		assertEquals("test # of covered source by t0",50,srctrace.getLinkedEntitiesByRow(t0).size());
		
		analyzer.extractEntities(EntityType.METHOD);
		Trace methodTrace = analyzer.createCodeCoverage(EntityType.METHOD);
		assertEquals("test # of methods covered by t0",91,methodTrace.getLinkedEntitiesByRow(t0).size());
		methodTrace.serializeCompressedMatrixToCSV(Paths.get("output","methodtrace.txt"));
		
		analyzer.extractEntities(EntityType.STATEMENT);
		Trace stmTrace = analyzer.createCodeCoverage(EntityType.STATEMENT);
		stmTrace.serializeToCSVReversedRowCol(Paths.get("output","stmtrace.txt"));
		assertEquals("total # of statements in array",8630,stmTrace.getColumns().size());
		assertEquals("total # of test cases in array",13,stmTrace.getRows().size());
		assertEquals("test # of statements covered by t0",408,stmTrace.getLinkedEntitiesByRow(t0).size());
		Trace stmCompressed = stmTrace.compressCoverageMatrix();
		assertEquals("test # of statements covered by t0",408,stmCompressed.getLinkedEntitiesByRow(t0).size());
		assertEquals("total # of statements in array",2816,stmCompressed.getColumns().size());
		assertEquals("total # of test cases in array",13,stmCompressed.getRows().size());
		stmCompressed.serializeToCSVReversedRowCol(Paths.get("output","stmCompressedtrace.txt"));
	}
	
	@Test
	public void testConvertHTMLtoXML(){
		Document htmldoc = analyzer.convertHTMLtoXML(htmlfile);
		assertTrue("verfiy XML document is created",tidyhtmlfile.toFile().exists());
		assertEquals("test document contains table nodes", 5, htmldoc.getElementsByTagName("table").getLength());
	}
	
	@Test 
	public void testParsePackageName(){
		Document htmldoc = analyzer.convertHTMLtoXML(htmlfile);
		assertEquals("test packagename",packageName,analyzer.parsePackageName(htmldoc));
	}
	
	@Test 
	public void testParseSrcName(){
		Document htmldoc = analyzer.convertHTMLtoXML(htmlfile);
		assertEquals("test srcname","IntegrityHmac.java",analyzer.parseSrcFileName(htmldoc));
	}
	@Test 
	public void testParseSourceFileEntity(){
		analyzer.parseXML(xmlfile);
		Document htmldoc = analyzer.convertHTMLtoXML(htmlfile);
		SourceFileEntity sfe = analyzer.parseSourceFileEntity(htmldoc);
		assertTrue("source file entity found!" , sfe!=null);
		assertEquals("source file object contains classes",7,sfe.getClazzes().size() );
	}
	
	@Test 
	public void testCreateStm(){
		analyzer.parseXML(xmlfile);
		Document htmldoc = analyzer.convertHTMLtoXML(htmlfile);
		Node tableNode = htmldoc.getElementsByTagName("table").item(3);
		Node trNode = tableNode.getChildNodes().item(98);
		StatementEntity stm = analyzer.createStm(trNode, analyzer.parseSourceFileEntity(htmldoc));
		assertEquals("test statement line number",99,stm.getLineNumber());
	}
	@Test
	public void testParseHtml(){
		analyzer.extractEntities(EntityType.STATEMENT);
		analyzer.parseXML(xmlfile);
		analyzer.parseHTML(htmlfile);
		
		Document htmldoc = analyzer.convertHTMLtoXML(htmlfile);
		SourceFileEntity sfe = analyzer.parseSourceFileEntity(htmldoc);
		List<StatementEntity> coveredStms = analyzer.coveredStatementsOfSourceFile.get(sfe);
		assertEquals("test number of covered statements", 3, coveredStms.size());
	}
}
