package uw.star.rts.cost;

import uw.star.rts.artifact.*;
/**
 * General cost related functions
 * @author Weining Liu
 *
 */

public class Cost {

	/**
	 * average cost r of running a test case in T on P
	 * a naive way is to call the execution script of T (assume all t in T are automated)
	 * track the start and end time stamp 
	 * cost in dollars = time * a factor (human cost factor, or machine/lab cost factor)
	 * this would require two additional input for each case study subject
	 * 1. path to test automation script
	 * 2. a cost factor to convert execution time to dollar amount
	 * To make this extensible for manual tests as well, r is supplied in case study subject configuration file so that 
	 * for manual tests, testers/PMs can estimate r based on histocial data/project committment. 
	 * It is a common practice for project managers to ask test team to provide when all tests will be exuected for one pass, and 
	 * how many tester resources are committed to this project and in what capacity. (e.g. 50% on this project)
	 * cost r = (#of resouces committed to execute test cases * capacity * commited working hours to complete 1st pass of execution)/ size of T
	 * 
	 *     
	 */
//	public static double getAverageTestExecutionCost(TestSuite t,Program P,int version){
//		
//	}
	
	
	/**
	 * set this to zero if analysis cost can be amotized in the preliminary phase of the regression testing.
	 * @return
	 */
/*	public static double getCostofAnalysis(){
		
	}*/
}
