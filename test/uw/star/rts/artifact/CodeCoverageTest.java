package uw.star.rts.artifact;

import static org.junit.Assert.*;
import uw.star.rts.artifact.Application;
import uw.star.rts.artifact.CodeCoverage;
import uw.star.rts.artifact.Entity;
import uw.star.rts.artifact.Program;
import uw.star.rts.artifact.ProgramVariant;
import uw.star.rts.artifact.SourceFileEntity;
import uw.star.rts.artifact.StatementEntity;
import uw.star.rts.artifact.TestCase;
import uw.star.rts.extraction.*;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeCoverageTest extends TraceTest{

	
	String EXPERIMENTROOT = "C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox";
	String appname = "apache-xml-security";
	Program p;
	SourceFileEntity sfe;
	StatementEntity s1,s2,s3;

	CodeCoverage t2;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		SIRJavaFactory sir = new SIRJavaFactory();
		sir.setExperimentRoot(EXPERIMENTROOT);
		Application testapp = sir.extract(appname);
		p = testapp.getProgram(ProgramVariant.orig, 0);
		sfe = new SourceFileEntity(p,"default.package","test.java");
		s1 = new StatementEntity(sfe,10,"int i=0");
		s2 = new StatementEntity(sfe,20,"int j=0");
		s3 = new StatementEntity(sfe,120,"int k=0");
		
		List<TestCase> tca = new ArrayList<>();
		tca.add(a);tca.add(b);tca.add(c);tca.add(d);tca.add(e);tca.add(f);
		List<StatementEntity> sea = new ArrayList<>();
		sea.add(s1);sea.add(s2);sea.add(s3);
		t2 =new CodeCoverage<StatementEntity>(tca,sea);
		t2.setLink(a, s1);
		t2.setLink(a, s3);
		t2.setLink(c, s1);
		t2.setLink(c, s3);
		t2.setLink(e, s3);
		t2.setLink(f,s1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCumulativeCoverage() {
		assertEquals("cumlative coverage ",6,t2.getCumulativeCoverage() );
	}
	@Test
	public void testGetCoveredEntites(){
		List<Entity> covered = t2.getCoveredEntities();
		assertEquals("# of  entites are covered",2,covered.size());
		assertTrue("s1 is covered",covered.contains(s1));
		assertFalse("s2 is not covered",covered.contains(s2));
		assertTrue("s3 is covered",covered.contains(s3));
	}

}
