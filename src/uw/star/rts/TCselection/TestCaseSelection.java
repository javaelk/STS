package uw.star.rts.TCselection;

import uw.star.rts.artifact.*;
import uw.star.rts.cost.*;
import uw.star.rts.extraction.*;
import uw.star.rts.technique.*;
import uw.star.rts.util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import org.slf4j.*;

public class TestCaseSelection {
	static Logger log = LoggerFactory.getLogger(TestCaseSelection.class.getName());   

	/**
	 * Apply one STS technique to a application
	 * @param techniqueClassName
	 * @param testsubject
	 * @return List of test cases selected for each version of the program in Application test subject
	 */
	public static Map<Program,List<TestCase>> applyTechnique(Technique technique, Application testsubject,List<StopWatch> cost){

		Map<Program,List<TestCase>> results = new HashMap<>();
		int totalNumVersions = testsubject.getTotalNumVersons();
		if(totalNumVersions<1){
			log.error("this doesn't make sense, should be at least one version");
			return results;
		}

		//for version 0, put all test cases applicable to that version
		results.put(testsubject.getProgram(ProgramVariant.orig, 0), testsubject.getTestSuite().getTestCaseByVersion(0));

		//apply selection technique only where there is more than one version
		if(totalNumVersions ==1)
			return results;  

		technique.setApplication(testsubject);
		for(int i=0;i<totalNumVersions-1;i++){
			Program p = testsubject.getProgram(ProgramVariant.orig, i);
			Program pPrime = testsubject.getProgram(ProgramVariant.orig, i+1);
			results.put(pPrime, technique.selectTests(p, pPrime,cost.get(i+1)));
		}
		outputToCSV(technique,testsubject,results);
		return results;
	}

	static void outputToCSV(Technique tech,Application testsubject, Map<Program,List<TestCase>> selectedTC){
		String appName = testsubject.getApplicationName();
		Path file = Paths.get("output"+File.separator+"runResult_"+appName+"_"+tech.getImplmentationName()+".csv");
		Charset charset = Charset.forName("UTF-8");
		try(BufferedWriter writer = Files.newBufferedWriter(file,charset,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING)){
			//write column headers
			writer.write(",Total# of Applicable Test Cases,");
			writer.write(tech.getID()+"-"+tech.getDescription()+"-"+tech.getImplmentationName());
			writer.write("\n");

			int totalNumVersions = testsubject.getTotalNumVersons();
			for(int i=0;i<totalNumVersions;i++){
				writer.write(appName+"-v"+i+",");
				writer.write(testsubject.getTestSuite().getTestCaseByVersion(i).size()+",");
				Program p = testsubject.getProgram(ProgramVariant.orig,i);
				writer.write(selectedTC.get(p).size()+"\n");
			}
		}catch(IOException ec){
			ec.printStackTrace();
		}

	}

	/**
	 * Apply a list of Techniques to a list of application.
	 * This method is  for evaluation purpose only and will only output 2 csv files. This method will not return actual test cases selected.
	 * @param techniqueClassNames
	 * @param testsubject
	 * @return
	 */
	public static void applyTechniques(List<Technique> techniqueClassNames, List<Application> testsubjects){
		log.info("Apply all techniques to all test subjects");
		
		List<String> testSubjectVersions = new ArrayList<>();
		for(Application app: testsubjects){
			String appName = app.getApplicationName();
			for(int i=0;i<app.getTotalNumVersons();i++)
				testSubjectVersions.add(appName+"-v"+i);
		}
		
		//actual %
		Map<Technique,List<Double>> actualPrecision = new HashMap<>();
		//for each technique, there is a list of stop watches
		Map<Technique,List<StopWatch>> actualCost = new HashMap<>();
		
		for(Technique tec: techniqueClassNames){
			List<Double> actualPrecisionArray = new ArrayList<>();
			List<StopWatch> costArrayPerTec = new ArrayList<>(); //this is the array to  track all cost of all test subject for one technique
			for(Application app: testsubjects){
				List<StopWatch> costArrayPerApp = new ArrayList<>(); //list of all cost for one application,re-initialize for each app
				int totalNumVersions = app.getTotalNumVersons();
				//Initialize all stop watches
				for(int i=0;i<totalNumVersions;i++)
					costArrayPerApp.add(new StopWatch());

				//only need to call this once for each application, result contains selected TC for all versions      
				Map<Program,List<TestCase>> selectedTCforApp = TestCaseSelection.applyTechnique(tec, app,costArrayPerApp);
				for(int i=0;i<totalNumVersions;i++)
					actualPrecisionArray.add(calculatePrecision(app,selectedTCforApp,i));
				costArrayPerTec.addAll(costArrayPerApp);
			}
			actualPrecision.put(tec, actualPrecisionArray);
			actualCost.put(tec, costArrayPerTec);
			log.info("Completed RTS technqiue " + tec.getImplmentationName());
		}
		Path resultFile = ResultOutput.outputEvalResult("actual",actualPrecision,null,testSubjectVersions,actualCost);
		log.info("All techniques are applied successfully, results have been written to "+ resultFile.getFileName());
	}
    
	/**
	 * Calculate precision of a version. Precision is the percentages of test cases selected from previous version for regression. 
	 * Lower precision number indicates greater savings which is better. 
	 * Since test cases new to this version are always need to be executed anyways,so they are not counted in the calculation
	 * Formally, we define precision p as (|T'|/|T|)*100
	 *    T' - test cases selected from previous version (should not include new test cases in this version)
	 *    T - test cases in previous version
	 * @param app
	 * @param selectedTCforApp
	 * @param i
	 * @return
	 */
	static double calculatePrecision(Application app,Map<Program,List<TestCase>> selectedTCforApp,int currentVersion){
		if(currentVersion==0) return 1.0; //1st version always selects everything	
		
		int sizeOfT = app.getTestSuite().getTestCaseByVersion(currentVersion-1).size(); // version should be equal to or great than 1 now
		Program p = app.getProgram(ProgramVariant.orig, currentVersion);
		int sizeOfTPrime =selectedTCforApp.get(p).size();
		double percentageSelected = (sizeOfTPrime*1.0)/sizeOfT;
		return percentageSelected;
	}
	
	
	
}
