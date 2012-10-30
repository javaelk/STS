package uw.star.rts.artifact;
import java.util.*;
import java.nio.file.*;
/**
 * represents one test case in the test suite
 * @author Weining Liu
 *
 */
public class TestCase extends Artifact{
	
	//Use one TestCase object represents all applicableVersions to save memory footprint. this is necessary for large test suite size (e.g. >3000 test cases over 20 versions) 
	/**
	 * @uml.property  name="applicableVersions"
	 */
	BitSet applicableVersions;
	/**
	 * @uml.property  name="existInPreviousVersion"
	 */
	boolean existInPreviousVersion;
	/**
	 * @uml.property  name="testSteps"
	 */
	String testSteps;
	
	//mandatory fields
	/**
	 * @uml.property  name="testCaseName"
	 */
	private String testCaseName;
	/**
	 * @uml.property  name="testID"
	 */
	private int testID;
	
	//test id is globally unique
	private static int globalTestID;
	
	
	public TestCase(String appName,int v,String tcName,Path testcaseFile){
		super(appName,v,testcaseFile);
		testCaseName = tcName;
		testID = globalTestID++;
		applicableVersions = new BitSet();
	}
	
	/**
	 * @return
	 * @uml.property  name="testCaseName"
	 */
	public String getTestCaseName(){
		return testCaseName;
	}
	
	public String getName(){
		return getTestCaseName();
	}
	
	public int getTestCaseID(){
		return testID;
	}
	
	public void addApplicableVersions(int ver){
		applicableVersions.set(ver); 
	}
	/**
	 * this return a string of applicable versions {1, 3, 5}. 
	 * @see BITSET
	 * @param ver
	 */
	public String getApplicableVersions(){
		return applicableVersions.toString(); 
	}
	
	public boolean isApplicabletoVersion(int ver){
		return applicableVersions.get(ver);
	}
	
	public void setApplicableVersions(int[] vers){
		for(int i:vers) applicableVersions.set(i);
	}
	
	public void existInPreviousVersion(boolean isExist){
		
		existInPreviousVersion = isExist;
	}
	
	/**
	 * @param steps
	 * @uml.property  name="testSteps"
	 */
	public void setTestSteps(String steps){
		testSteps = steps;
	}
	
	@Override
	public String toString(){
		return testID+ ","+ testCaseName + ",applicable to " + applicableVersions + ",exist in previous version" + existInPreviousVersion + ", Steps :" + testSteps +"\n"; 
	}
	
	@Override
	public boolean equals(Object e){
		if(!(e instanceof TestCase)) return false;
	    TestCase tc = (TestCase)e;
	    return tc.getTestCaseID()==this.testID;
	}
}
