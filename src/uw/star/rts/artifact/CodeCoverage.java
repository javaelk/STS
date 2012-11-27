package uw.star.rts.artifact;

import java.util.*;
import java.nio.file.*;

/**
 * Coverage represents coverage relation between test cases and an entity type E over execution of a Program p.
 * Coverage is a special type of Trace that row type is always test case and column type is always a subtype of Entity  
 * @author wliu
 *
 */
public class  CodeCoverage<E extends Entity> extends Trace<TestCase, E>{

	
	public  CodeCoverage(List<TestCase> testcases,List<E> entities,Path coverageFilesFolder){
		super(TraceType.CODECOVERAGE,testcases, entities,coverageFilesFolder);	
		//as CodeCoverage represent coverage of a list of test cases, there are multiple files, so the path is the folder contains all coverage files
	}

	
	/*
	 * DEFINITION: Coverage Matrix C(i,j) - Each row represent elements of T,
	 * each column represent elements of E(entity) element C(i,j) = 1 if Ti
	 * covers Ej, otherwise 0
	 * 
	 * @a two dimensional array, row is test case, column is entity
	 */
	public int[][] getCoverageMatrix() {
		return getLinkMatrix();
	}
	
	/**
	 * CC is the amount of cumulative coverage of achieved by T (i.e the integer
	 * sum of the entries in the coverage matrix
	 * 
	 * @return
	 */
	public int getCumulativeCoverage() {
		int sum = 0;
		for (int i = 0; i < row.size(); i++)
			for (int j = 0; j < column.size(); j++)
				sum += links[i][j];
		return sum;
	}
	
	/**
	 * Let Ec denote the set of covered entities. Ec is defined as follows:
	 * Ec =for every e in E, there exist a t in T that covers e. i.e entity e is covered by at least one t
	 * @return
	 */
	
	public List<E> getCoveredEntities(){
		Set<E> results = new HashSet<>();
		for(int j=0;j<column.size();j++){//search column by column (for each entity) 
			for(int i=0;i<row.size();i++){
				if(links[i][j]>0){  //there is a test in i covers entity at j
					results.add(column.get(j));
					break;//don't need to search rest of the rows, jump to next column
				}
			}
		}
		return new ArrayList<E>(results);
	}
}
