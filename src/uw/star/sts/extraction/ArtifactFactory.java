package uw.star.sts.extraction;
import uw.star.sts.artifact.*;

public abstract class ArtifactFactory {
	
	static String[] CODEKIND={"source","class","html","property"};
	
	/**
	 * each factory class implement how to extract all artifacts of an application from a given repository
	 * @return
	 */
	public abstract Application extract(String applicationName);
}
