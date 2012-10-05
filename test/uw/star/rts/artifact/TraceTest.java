package uw.star.rts.artifact;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uw.star.rts.artifact.Artifact;
import uw.star.rts.artifact.TestCase;
import uw.star.rts.artifact.Trace;
import uw.star.rts.artifact.TraceType;

public class TraceTest {
	/**
	 * @uml.property  name="a"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestCase a = new TestCase("tt",0,"a");
	/**
	 * @uml.property  name="b"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestCase b = new TestCase("tt",0,"b");
	/**
	 * @uml.property  name="c"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestCase c = new TestCase("tt",0,"c");
	/**
	 * @uml.property  name="d"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestCase d = new TestCase("tt",0,"d");
	/**
	 * @uml.property  name="e"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestCase e = new TestCase("tt",0,"e");
	/**
	 * @uml.property  name="f"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestCase f = new TestCase("tt",0,"f");
	/**
	 * @uml.property  name="aa"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	Artifact[] aa= {a,b,c};
	/**
	 * @uml.property  name="ab"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	Artifact[] ab= {c,d,e,f};
	/**
	 * @uml.property  name="t1"
	 * @uml.associationEnd  
	 */
	Trace t1;
	
	@Before
	public void setUp() throws Exception {
		 t1= new Trace(TraceType.CODECOVERAGE,Arrays.asList(aa),Arrays.asList(ab));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCumulativeCoverage(){
		t1.setLink(a, c);
		t1.setLink(b, c);
		t1.setLink(1,1);
		t1.setLink(c, c);
		t1.setLink(2,2);
		System.out.println(t1);
		assertFalse("setlink out of bound test", t1.setLink(4,4));
	}
	
	@Test
	public void testTrace() {
		System.out.println(t1);
		assertEquals("t1 row size",3,t1.getRows().size());
		assertEquals("t1 column size",4,t1.getColumns().size());
	}
	
	@Test
	public void testCompressCoverageMatrix(){
		t1.setLink(a, c);
		t1.setLink(b, c);
		t1.setLink(1,1);
		t1.setLink(c, c);
		t1.setLink(2,2);
		System.out.println(t1);
		assertEquals("t1 row size",3,t1.getRows().size());
		assertEquals("t1 column size",4,t1.getColumns().size());
		Trace t2= t1.compressCoverageMatrix();
		System.out.println("t2 ====" + t2);
		assertEquals("t2 row size",3,t2.getRows().size());
		assertEquals("t2 column size",3,t2.getColumns().size());
		
	}

	@Test
	public void testGetCoveredEntities() {
		List<Artifact> lst  = new ArrayList<Artifact>();
		lst.add(c);
		t1.setLink(a, lst);
		lst.add(d);
		t1.setLink(b, lst);
		lst.remove(d);
		lst.add(e);
		t1.setLink(c, lst);
		System.out.println(t1);	
		assertTrue("a links to c", t1.getLinkedEntitiesByRow(a).contains(c));
		assertTrue("b links to c,d", t1.getLinkedEntitiesByRow(b).contains(c)&&t1.getLinkedEntitiesByRow(b).contains(d));
		assertTrue("c links to a,b,c,e", t1.getLinkedEntitiesByRow(c).contains(c)&&t1.getLinkedEntitiesByRow(c).contains(e));
	}
	
	@Test
	public void testGetLinkedEntities() {
		List<Artifact> lst  = new ArrayList<Artifact>();
		lst.add(c);
		t1.setLink(a, lst);
		lst.add(d);
		t1.setLink(b, lst);
		lst.remove(d);
		lst.add(e);
		t1.setLink(c, lst);
		System.out.println(t1);	
		assertTrue("e links to c", t1.getLinkedEntitiesByColumn(e).contains(c));
		assertTrue("d links to b", t1.getLinkedEntitiesByColumn(d).contains(b));
		assertTrue("c links to a,b,c", t1.getLinkedEntitiesByColumn(c).contains(a)&&t1.getLinkedEntitiesByColumn(c).contains(b)&&t1.getLinkedEntitiesByColumn(c).contains(c));
	}
	
	@Test
	public void testMakeRowMap(){
		t1.setLink(a, c);
		t1.setLink(b, c);
		t1.setLink(1,1);
		t1.setLink(c, c);
		t1.setLink(2,2);
		System.out.println(t1);
		Map<TestCase,int[]> rowMap = t1.makeRowMap();
		assertEquals("test row 0", 1,rowMap.get(a)[0]);
		assertEquals("test row 1", 1,rowMap.get(b)[0]);
		assertEquals("test row 1", 1,rowMap.get(b)[1]);
		assertEquals("test row 2", 1,rowMap.get(c)[0]);
		assertEquals("test row 2", 1,rowMap.get(c)[2]);
	}
	
	@Test
	public void testMakeColumnMap(){
		t1.setLink(a, c);
		t1.setLink(b, c);
		t1.setLink(1,1);
		t1.setLink(c, c);
		t1.setLink(2,2);
		System.out.println(t1);
		Map<TestCase,int[]> colMap = t1.makeColumnMap();
		assertEquals("test col 0", 1,colMap.get(c)[0]);
		assertEquals("test col 0", 1,colMap.get(c)[1]);
		assertEquals("test col 0", 1,colMap.get(c)[2]);
		assertEquals("test col 1", 1,colMap.get(d)[1]);
		assertEquals("test col 2", 1,colMap.get(e)[2]);
		assertFalse("column f is removed",colMap.containsKey(f));
	}
	
	@Test
	public void testSerializeToCSV(){
		t1.setLink(a, c);
		t1.setLink(b, c);
		t1.setLink(1,1);
		t1.setLink(c, c);
		t1.setLink(2,2);
		Path file = Paths.get("output/tracetest.csv");
		t1.serializeToCSV(file);
		Path file2 = Paths.get("output/tracetestCompressed.csv");
		t1.serializeCompressedMatrixToCSV(file2);
		assertTrue(file.toFile().exists());
		assertTrue(file2.toFile().exists());
		
		
		
	}
}
