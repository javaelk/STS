package uw.star.rts.extraction;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import uw.star.rts.artifact.*;

public abstract class ArtifactFactory {
	

	/**
	 * each factory class implements artifacts extraction of an application from a given repository
	 * This is a abstraction layer to disk IO/file structures as each repository will likely have different 
	 * folder structures ,file names etc to store various artifacts.
	 * @return
	 */
	public abstract Application extract(String applicationName);
	public abstract Path getChangeResultFile(Program p, Program pPrime);
	public abstract Path getEmmaCodeCoverageResultFile(Program p, TestCase tc, String fileType);
	
	public abstract void setExperimentRoot(String expRoot);
	abstract Map<ProgramVariant,List<Program>>  extractProgram(String applicationName);
	abstract TestSuite extractTestSuite(String applicationName);
}
