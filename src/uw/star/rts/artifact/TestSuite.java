package uw.star.rts.artifact;

import java.util.*;
import java.nio.file.*;
/**
 * Test suite is a container for a set of test cases. There is only one test suite object per application, test suite object holds all test cases of all versions
 * Use Test Case.getApplicableVersions to figure out which versions the test case is applicable to  
 * @author Weining Liu
 *
 */
public class TestSuite extends Artifact{
	  /**
	 * @uml.property  name="testSuiteName"
	 */
	String testSuiteName;
	  /**
	 * @uml.property  name="testcases"
	 */
	List<TestCase> testcases;
	  
	  public TestSuite(String appName,String tpName,List<TestCase> testcases,Path testSuiteRootFolder){
		  super(appName,0,testSuiteRootFolder); //always v0 as there is only one version of test suite per application
		  testSuiteName = tpName;
		  this.testcases = testcases;
	  }
	  
	  public List<TestCase> getTestCases(){
		  return testcases;
	  }
	  
	  public TestCase getTestCaseByName(String name){
		  for(TestCase t:testcases)
			  if(t.getTestCaseName().equals(name))
				  return t;

		  return null;

	  }

	  public TestCase getTestCaseByID(int id){
		  for(TestCase t: testcases)
			  if(t.getTestCaseID()==id)
				  return t;
		  return null;
	  }
	  
	  //get all test cases applicable to this version
	  public List<TestCase> getTestCaseByVersion(int ver){
		  List<TestCase> results = new ArrayList<TestCase>();
		  for(TestCase t: testcases)
			  if(t.isApplicabletoVersion(ver))
				  results.add(t);
		  return results;
	  }

	  public int size(){
		  return testcases.size();
	  }
	  
	  public int size(int ver){
		return getTestCaseByVersion(ver).size();  
	  }
	  
	  public void setTestCases(List<TestCase> tcs){
		  testcases = tcs;
	  }
	  
	  /**
	 * @param name
	 * @uml.property  name="testSuiteName"
	 */
	public void setTestSuiteName(String name){
		  testSuiteName = name;
	  }
	  
	  /**
	 * @return
	 * @uml.property  name="testSuiteName"
	 */
	public String getTestSuiteName(){
		  return testSuiteName;
	  }
	  
	
	public String getName(){
		return getTestSuiteName();
	}
	  public boolean isEmpty(){
		  return testcases.size()==0;
	  }
	  
	  @Override
	  public String toString(){
		  StringBuffer buf = new StringBuffer();
		  buf.append(super.toString());
		  buf.append("Test Suite: " + testSuiteName + "\n");
		  buf.append("Test Cases - [\n");
		  for(TestCase tc : testcases) 
			  buf.append(tc.toString()+",\n");
		  buf.append("]\n");	
		  return buf.toString();
		  
	  }
	  
}