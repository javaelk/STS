package uw.star.rts.cost;

import uw.star.rts.artifact.*;

import java.util.*;

import org.slf4j.*;

import ca.uwaterloo.uw.star.sts.schema.stStechniques.TechniqueDocument.Technique;

/**
 * Implementation of Rosenblum-Weyuker Cost Predictor
 * Reference Paper: 
 * D.S. Rosenblum and E.J. Weyuker, "Using Coverage Information
 * to Predict the Cost-Effectiveness of Regression Testing Strategies",
 * IEEE Trans. Software Eng., vol. 23, no. 3, pp. 146-156, Mar. 1997.
 * 
 * @author Weining Liu
 * @version 2012-01-06
 *
 */

public class RWPrecisionPredictor {
	static Logger log =LoggerFactory.getLogger(RWPrecisionPredictor.class.getName());
	/**
	 * PIm = CC/ |Ec||T|
	 * The fraction of the test suite that needs to be rerun is called PIm
	 * PIm equals cumulative coverage / (size of covered entities * size of test suite)
	 * 
	 * this method should definitely NOT apply the technique getPredicatedPercetageOfTestCaseSelectedto select test case! 
	 * this is assume there is only single entity change,if technique is not effective with single entity change, 
	 * it won't be effective for multiple entity changes (as there will be more test cases selected)
	 * @param 
	 * @return the percentage of test case of T that the RW predictor predicts will be selected by technique teq 
	 *         when an arbitrary changes is made to P 
	 */
//	public static double getPredicatedPercetageOfTestCaseSelected(Application app,Program p, Technique teq){
	public static double getPredicatedPercetageOfTestCaseSelected(CodeCoverage cover,List<TestCase> testcases){
		double cc = cover.getCumulativeCoverage();
		log.debug("Cumlative Coverage is " + cc);
		int ec = cover.getCoveredEntities().size();
		log.debug("Total num of covered entities is " + ec);
		int t = testcases.size();
		log.debug("Test case size is " + t);
		double pim = cc/(ec*t);
		return pim;
	}
	
	/**
	 * Prediction Procedure
	 *  M is cost effective if Sm|Tm| < r(|T| - |Tm|)
	 */
	
	public static boolean  isCostEffective(TestSuite t, TestSuite tm, int versionNum, Technique m){
		//TODO:implement me! 
		return true;
	}
}
