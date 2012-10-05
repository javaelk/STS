package uw.star.rts.artifact;

import static org.junit.Assert.*;
import uw.star.rts.artifact.*;
import uw.star.rts.extraction.*;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SourceFileEntityTest {

	String EXPERIMENTROOT = "C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox";
	String appname = "apache-xml-security";
	Program p;
	SourceFileEntity sfe;
	StatementEntity s1,s2,s3;

	@Before
	public void setUp() throws Exception {
	SIRJavaFactory sir = new SIRJavaFactory();
	sir.setExperimentRoot(EXPERIMENTROOT);
	Application testapp = sir.extract(appname);
	p = testapp.getProgram(ProgramVariant.orig, 0);
	sfe = new SourceFileEntity(p,"default.package","test.java");
	s1 = new StatementEntity(sfe,10,"int i=0");
	s2 = new StatementEntity(sfe,20,"int j=0");
	s3 = new StatementEntity(sfe,120,"int k=0");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testGetStatementByLineNumber() {
		List<StatementEntity> stms = new ArrayList();
		stms.add(s1);
		stms.add(s2);
		stms.add(s3);
		sfe.setExecutableStatements(stms);
		List<StatementEntity> result =sfe.getExecutableStatements();
		assertTrue(result.contains(s1));
		assertTrue(result.contains(s2));
		assertTrue(result.contains(s3));
		assertEquals("s1",s1,sfe.getStatementByLineNumber(10));
		assertEquals("s2",s2,sfe.getStatementByLineNumber(20));
		assertEquals("s3",s3,sfe.getStatementByLineNumber(120));

		exception.expect(IllegalArgumentException.class);
		sfe.getStatementByLineNumber(130);
		
		assertTrue(sfe.getStatementByLineNumber(13)==null);
		assertTrue(sfe.getStatementByLineNumber(1)==null);
		assertTrue(sfe.getStatementByLineNumber(0)==null);
		assertTrue(sfe.getStatementByLineNumber(-10)==null);
	}


}
