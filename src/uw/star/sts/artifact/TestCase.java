package uw.star.sts.artifact;
import java.util.*;

public class TestCase extends Artifact{
	
	BitSet applicableVersions;
	boolean existInPreviousVersion;
	String testSteps;
	
	//mandatory fields
	private String testCaseName;
	private int testID;
	
	//test id is globally unique
	private static int globalTestID;
	
	
	public TestCase(String rep,String appName,int v,String tcName){
		super(rep,appName,v);
		testCaseName = tcName;
		testID = globalTestID++;
		applicableVersions = new BitSet();
	}
	
	public String getTestCaseName(){
		return testCaseName;
	}
	
	public int getTestCaseID(){
		return testID;
	}
	
	public void addApplicableVersions(int ver){
		applicableVersions.set(ver); 
	}
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
