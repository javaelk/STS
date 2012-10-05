package uw.star.rts.cost;

import uw.star.rts.analysis.*;
import uw.star.rts.artifact.*;
import ca.uwaterloo.uw.star.sts.schema.stStechniques.TechniqueDocument.Technique;

/**
 * An improved RW predictor incorporate information about modification
 * 
 * @see Reference paper: 
 * Harrold, M.J.; Rosenblum, D.; Rothermel, G.; Weyuker, E.; , 
 * "Empirical studies of a prediction model for regression test selection," 
 * Software Engineering, IEEE Transactions on , vol.27, no.3, pp.248-263, Mar 2001 doi: 10.1109/32.910860
 * 
 * @author Weining Liu
 *
 */
public class WeightedRWPrecisionPredictor extends RWPrecisionPredictor {
	
	/**
	 * @param app
	 * @return the percentage of test case of T that the RW predictor predicts will be selected by technique teq 
	 *         when an arbitary changes is made to P 
	 */
//	@Override
/*    public static double getPredicatedPercetageOfTestCaseSelected(Program p, Technique teq){
			
		double wncm = 0;
		foreach(Entity e:p.getEntities())
		  wncm += (new CoverageAnalysis().getNumCoveredTestCases(e)) * e.getChangeFrequency();
		return wncm/(p.getTestSuite.getSize());
	}*/
	/**
	 * this predictor would need a change frequency of each entity. This would normally available from version
	 * control tool. However, when version control tool is not available(in my case , only have each versions), there
	 * is additional cost of perform change analysis. 
	 * Even though the diff tool is very effective, there is an overhead of beautify src code and convert 
	 * diff results back to modified objects arrays.
	 * 
	 */
	

}
