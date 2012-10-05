package uw.star.rts.cost;
import java.util.*;
/**
 * Each element in the CostFactor is a factor/element in cost model that impact the total cost
 * @author wliu
 *
 */
public enum CostFactor {
  CoverageAnalysisCost,
  ChangeAnalysisCost,
  ApplyTechniqueCost,  //cost of applying selection technique
  TestExecutionCost, //cost of test execution
  PrecisionPredictionCost, //cost to predict technique precision
  AnalysisCostPredictionCost; //cost to predict technique analysis cost
  
  public boolean isPredicationCost(){
	  boolean result = false;
	  switch(this){
	  case CoverageAnalysisCost:
		  break;
	  case ChangeAnalysisCost:
		  break;
	  case ApplyTechniqueCost:
		  break;
	  case TestExecutionCost:
		  break;
	  case PrecisionPredictionCost: result = true;
	  	break;
	  case AnalysisCostPredictionCost: result = true;
	  	 break;
	  default :	 
	  }
	  return result;
	  

  }
  
}
