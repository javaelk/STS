package uw.star.sts.artifact;

import java.util.*;

public class TestPlan extends Artifact{
	  String testPlanName;
	  List<TestCase> testcases;
	  
	  TestPlan(String rep,String appName,int v,String tpName){
		  super(rep,appName,v);
		  testPlanName = tpName;
	  }
	  
	  public List<TestCase> getTestCases(){
		  return testcases;
	  }
	  
	  public void setTestCases(List<TestCase> tcs){
		  testcases = tcs;
	  }
	  
	  public void setTestPlanName(String name){
		  testPlanName = name;
	  }
	  
	  public String getTestPlanName(){
		  return testPlanName;
	  }
	  
	  @Override
	  public String toString(){
		  StringBuffer buf = new StringBuffer();
		  buf.append(super.toString());
		  buf.append("Test Plan: " + testPlanName + "\n");
		  buf.append("Test Cases - [\n");
		  for(TestCase tc : testcases) 
			  buf.append(tc.toString()+",\n");
		  buf.append("]\n");	
		  return buf.toString();
		  
	  }
	  
}