package uw.star.sts.artifact;

import java.util.*;

public class Application {
	List<Code> versions;
	List<Specification> specifications;
	List<TestCase> testcases;
	String name;
	
	public Application(){
		versions = new ArrayList();
		specifications = new ArrayList();
		testcases = new ArrayList();
	}
	public void setVersions(List<Code> svs){
		versions =svs;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setTestCases(List<TestCase> tcs){
		testcases = tcs;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("---- SourceVersions --- \n");
	    if(versions!=null)
	    	for(Code v : versions)	buff.append(v.toString());
		buff.append("---- Specifications --- \n");
		if(specifications!=null)
			for(Specification s : specifications)	buff.append(s.toString());
		buff.append("---- TestPlans --- \n");
		if(testcases!=null)
			for(TestCase t : testcases)	buff.append(t.toString());
		
		return buff.toString();				
	}
	
	/**
	 * print count of each artifacts
	 * @return
	 */
	public String getStats(){
		//TODO: print type of specification, size of source code
		StringBuffer buf = new StringBuffer();
		buf.append("# of Versions - " + versions.size()+"\n");
		buf.append("# of Specifications - " + specifications.size() + "\n");
		buf.append("# of test cases - " + testcases.size()+"\n");
		return buf.toString();
		
	}
	
	
}
