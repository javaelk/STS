package uw.star.rts.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uw.star.rts.cost.CostFactor;
import uw.star.rts.technique.Technique;

public class ResultOutput {
	public static Path outputEvalResult(String firstCellHeader,Map<Technique,List<Double>> precision,Map<Technique,List<Long>> predicatedAnalysisCost, List<String> testSubjectVersions,Map<Technique,List<StopWatch>> actualCost){
		Path outputFile = Paths.get("output"+File.separator+"evaluationResult_"+firstCellHeader+"_"+DateUtils.now("MMMdd_HHmm")+".csv");
		Charset charset = Charset.forName("UTF-8");

		try(BufferedWriter writer = Files.newBufferedWriter(outputFile,charset,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING)){
			//write column headers,1st row
			writer.write(firstCellHeader);
			List<Technique> techs = new ArrayList(precision.keySet());
			for(Technique tech: techs ){
				writer.write(","+tech.getID()+"-"+tech.getDescription()+"-"+tech.getImplmentationName()+"_precision");
				if(predicatedAnalysisCost!=null)
					writer.write(",PredicatedAnalysisCost(ms)");
				//add cost columns if exists
				if(actualCost !=null)
					for(CostFactor cf: CostFactor.values())
						if(!(firstCellHeader.equalsIgnoreCase("predicated")^cf.isPredicationCost()))
								writer.write(","+tech.getID()+"-"+cf.name()+"(ms)");
					
			}
			writer.write("\n");

			//for each row, write application-version on the 1st column, then % actual/predicated for each technique
			for(int i=0;i<testSubjectVersions.size();i++){
				writer.write(testSubjectVersions.get(i));
				for(Technique tec: techs){
					writer.write(","+precision.get(tec).get(i));
					if(predicatedAnalysisCost!=null)
						writer.write(","+predicatedAnalysisCost.get(tec).get(i));
						
					//add cost values if exists
					if(actualCost !=null)
						for(CostFactor cf: CostFactor.values())
							if(!(firstCellHeader.equalsIgnoreCase("predicated")^cf.isPredicationCost()))
							writer.write(","+actualCost.get(tec).get(i).getElapsedTime(cf));
					
				}
				writer.write("\n");
			}
		}catch(IOException ec){
			ec.printStackTrace();
		}
		return outputFile;
	}

}
