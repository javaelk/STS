package uw.star.rts.technique;
/*
 * Any RTS techniques implemented in ARTS framework needs to extend this class.
 * 
 */
import java.util.List;

import uw.star.rts.artifact.*;
import uw.star.rts.util.*;

public abstract class Technique {
    String id;
    String description;
    String implmentationName;
	Application testapp;
    
	public String getID(){
		return id;
	}
	
	public String getImplmentationName(){
		return this.implmentationName;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setID(String ID){
		this.id = ID;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public void setImplmentationName(String name){
		this.implmentationName = name;
	}
	
	public abstract void setApplication(Application app);
	
	//return list of test cases selected from p that is applicable in pPrime for regression testing 
	public abstract List<TestCase> selectTests(Program p,Program pPrime,StopWatch sw);
	
	public abstract double predictPrecision();
	public abstract long predictAnalysisCost();
	
}
