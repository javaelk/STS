package uw.star.rts.artifact;

import java.util.*;
import org.slf4j.*;

import uw.star.rts.extraction.*;
/**
 * An application is a container of all artificats created during the application development lifecycle.
 * An application in minimum should consist of a program and a test suite
 * An applicaton object holds all versions of its artifacts
 * @author Weining Liu
 *
 */
public class Application {
	//total number of versions exist for this application. 
	/**
	 * @uml.property  name="numVersions"
	 */
	int numVersions; // All artifacts should have same number of versions.
	/**
	 * @uml.property  name="appName"
	 */
	String appName;
	/**
	 * @uml.property  name="testsuite"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	TestSuite testsuite;    //one testsuite for all versions 
	
	/**
	 * @uml.property  name="programs"
	 * @uml.associationEnd  qualifier="type:uw.star.sts.artifact.ProgramVariant java.util.List"
	 */
	Map<ProgramVariant,List<Program>> programs; //one program per variant per version
	/**
	 * @uml.property  name="traces"
	 * @uml.associationEnd  qualifier="type:uw.star.sts.artifact.TraceType java.util.List"
	 */
	Map<TraceType,List<Trace>> traces; //for each tracetype, there could be multiple traces for each version. e.g. 1 for method, 1 for class 
	//Map<SpecType,List<Specification>> specs; //one specification per type per version 
	
	//application also needs to keep a reference to the Artifact Factory object in order to do change analysis (getChangeResultFile)
	//or code coverage analysis (getEmmaCodecoverageResultFile)
	
	ArtifactFactory af;
	
	/**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Logger log;
	
	/**
	 * Mandatory fields are required at constuction time
	 * @param appName
	 * @param programs
	 * @param testsuite
	 */
	public Application(String appName, Map<ProgramVariant,List<Program>> programs,TestSuite testsuite,ArtifactFactory af){
	    log = LoggerFactory.getLogger(Application.class.getName());
		//An application in minimum should consist of a non-empty program and non-empty test suite
		if(programs.size()==0||testsuite.isEmpty()){
			log.error("Fatal - An application in minimum should consist of a non-empty program and a non-empty test suite!:" + appName);
			System.exit(-1);
		}
		this.appName = appName;
		this.programs = programs;
		this.testsuite = testsuite;
		this.af = af;
		//total number of versions equals to the largest version number of the original program + 1(assume always starts from version 0) 
		int maxVer =0;
		for(Program p : this.programs.get(ProgramVariant.orig))
			maxVer =(p.versionNo>maxVer)?p.versionNo:maxVer;
		this.numVersions = maxVer +1;	
	}
	
	public String getApplicationName(){
		return appName;
	}
	
	public TestSuite getTestSuite(){
		return testsuite;
	}
	
	public ArtifactFactory getRepository(){
		return af;
	}
	
	/*
	 * 
	 * DESIGN PATTERN:Do not expose internal data structure. Only expose various getter methods. 
	 * e.g. Internal data structure can be changed later on from Map of list to List of Maps. 
	 */
    //for each artifact, provide 3 overloaded getter methods
	/**
	 * get all versions of the program of given variant type
	 * @param type
	 * @return
	 */
	public List<Program> getProgram(ProgramVariant type){
		return programs.get(type);
	}
	
	/**
	 * get all variant of the program of given version
	 * @param versionNum
	 * @return
	 */
	public List<Program> getProgram(int versionNum){
		List<Program> results = new ArrayList<Program>();
		for(ProgramVariant type: programs.keySet())
			results.add(getProgram(type,versionNum));
		return results;
	}
	
	public int getTotalNumVersons(){
		return numVersions;
	}
	/**
	 * get particular version of the program and variant type
	 * @param type
	 * @param versionNum
	 * @return
	 */
	public Program getProgram(ProgramVariant type,int versionNum){
		return programs.get(type).get(versionNum);
	}
	
	public void setTrace(Map<TraceType,List<Trace>> tracemap){
		this.traces = tracemap;
	}
	public List<Trace> getTrace(TraceType type){
		return traces.get(type);
	}
	
	public List<Trace> getTrace(int versionNum){
		List<Trace> results = new ArrayList<Trace>();
		for(TraceType type: traces.keySet())
			results.addAll(getTrace(type,versionNum));
	    return results;	
	}
	/**
	 * There could be multiple traces for each type,each version
	 * @param type
	 * @param versionNum
	 * @return
	 */
	public List<Trace> getTrace(TraceType type,int versionNum){
		List<Trace> results = new ArrayList<Trace>();
		for(Trace t: traces.get(type))
			if(t.versionNo==versionNum)
				results.add(t);
		return results;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("---- Programs --- \n");
	    for(ProgramVariant v : programs.keySet())	
	    		buff.append(getProgram(v).toString());
		buff.append("---- TestSuite --- \n");
				buff.append(testsuite.toString());
		//optional artifacts, need to verify exist before print
 	    if(traces!=null){			
 	    	buff.append("---- Trace ----\n");
 	    	for(TraceType t: traces.keySet())
 	    		buff.append(getTrace(t).toString());
 	    }
		/*	buff.append("---- Specifications --- \n");
		if(specifications!=null)
			for(Specification s : specifications)	buff.append(s.toString());*/
		
		return buff.toString();				
	}
	
	/**
	 * print count of each artifacts
	 * @return
	 */
	public String getStats(){
		//TODO: print type of specification, size of source code
		StringBuffer buf = new StringBuffer();
		buf.append("# of Versions - " + programs.size()+"\n");
		//buf.append("# of Specifications - " + specifications.size() + "\n");
		buf.append("# of test cases - " + testsuite.getTestCases().size()+"\n");
		return buf.toString();
		
	}
	/**
	 * consider two applications are the same as long as they have the same name
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof Application){
			Application app = ((Application)o);
			return this.appName.equals(app.getApplicationName())&&this.numVersions==app.numVersions;	
		}else{
			return false;
		}
				
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numVersions;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		return result;
	}
}
