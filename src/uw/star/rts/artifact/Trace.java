package uw.star.rts.artifact;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.*;
import java.util.*;
import org.slf4j.*;

/**
 * Trace is a matrix that links a set of artifacts to another set of artifacts
 * e.g. test-code trace is a matrix links test cases to some code entities.
 *      or bug-bug trace is a matrix link bugs to their related bugs.
 * 
 * @author Weining Liu
 * 
 */

// @Refactoring: use generics, trace is no longer a link between two artifacts,
// it's a link between two subtypes of artifact
/*
 * TODO: more refactoring possibilities - use different data structure to
 * improve space/performance
 * http://stackoverflow.com/questions/3040864/java-sparse-bit-vector list of
 * bitset, map of bitset if column is at statement/block level, matrix could be
 * really huge.
 */
public class Trace<T extends Artifact, U extends Artifact> extends Artifact {

	/**
	 * @uml.property name="traceType"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	TraceType traceType;
	/**
	 * @uml.property name="row"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="uw.star.sts.artifact.Artifact"
	 */
	List<T> row;
	/**
	 * @uml.property name="column"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="uw.star.sts.artifact.Artifact"
	 */
	List<U> column;
	/**
	 * @uml.property name="links" multiplicity="(0 -1)" dimension="2"
	 */
	int[][] links;
	/**
	 * @uml.property name="log"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	Logger log;
	static String DELIMITER="~";

	/**
	 * Trace is constructed as long as two sets of artifacts are provided. Link
	 * matrix are built later.
	 * 
	 * @param a
	 *            - rows
	 * @param b
	 *            - columns
	 */
	public Trace(TraceType type, List<T> a, List<U> b, Path traceFile) {
		// assume all artifacts are in the same version and all belong to one
		// application!
		super(a.get(0).getApplicationName(), a.get(0).getVersionNo(),traceFile);
		log = LoggerFactory.getLogger(Trace.class.getName());
		if (a.size() == 0 || b.size() == 0)
			log.error("row/column artifact sets is empty");
		row = a;
		column = b;
		traceType = type;
		links = new int[a.size()][b.size()];
	}

	public List<T> getRows() {
		return row;
	}

	public List<U> getColumns() {
		return column;
	}

	/**
	 * this should insert one row into the matrix
	 * 
	 * @param a
	 *            , an artifact in the row
	 * @param linkedArtifacts
	 *            , list of artifacts in column that need to be linked to a
	 */
	public boolean setLink(T a, List<U> linkedArtifacts) {
		// find a from row, a and row should be of same type and have equals
		// method implemented
		int i = row.indexOf(a);
		// find all b from column
		if (i < 0) { // a not found
			log.error("insertLink " + a + " does not exist in row array");
			return false;
		}
		for (U b : linkedArtifacts)
			links[i][column.indexOf(b)] = 1;
		return true;
	}

	/**
	 * 
	 * @param a
	 *            , an artifact in the row
	 * @param b
	 *            , an artifact in the column
	 */
	public boolean setLink(T a, U b) {
		if ((!row.contains(a)) || (!column.contains(b))) {
			log.error("can not set link, both " + a + " and " + b
					+ "must exist in the row and column arrays!");
			return false;
		}
		links[row.indexOf(a)][column.indexOf(b)] = 1;
		return true;
	}

	/**
	 * 
	 * @param rowIdx
	 *            - row index
	 * @param columnIdx
	 *            - column index
	 */
	public boolean setLink(int rowIdx, int columnIdx) {
		if (rowIdx > row.size() || columnIdx > column.size()) {
			log.error("row or column idx out of bound");
			return false;
		}
		links[rowIdx][columnIdx] = 1;
		return true;
	}

	/**
	 * Retrieve all artifacts linked to a,search a in row only.
	 * 
	 * @param a
	 * @return all Artifiacts covered by a (or by executing a if a is a test
	 *         case)
	 */
	public List<U> getLinkedEntitiesByRow(T a) {
		// a could be of both row type and column type, e.g.in a test case -
		// test case matrix, but this method only searches row
		// this is to assume "covers" property is not a symmetric relation. i.e.
		// a covers b does not imply b covers a.
		List<U> linkedArtifacts = new ArrayList<>();
		// search rows
		int i = row.indexOf(a);
		if (i >= 0) { // a found in row
			for (int k = 0; k < column.size(); k++)
				if (links[i][k] == 1)
					linkedArtifacts.add(column.get(k));
		} else {
			log.error("Artifact " + a
					+ " does not exist in row array ");
		}
		return linkedArtifacts;
	}

	
	/**
	 * Retrieve all artifacts linked to a,search a in column only.
	 * 
	 * @param a
	 * @return all Artifiacts covers a (or all test cases covers this entity)
	 */
	public List<T> getLinkedEntitiesByColumn(U a) {
		List<T> linkedArtifacts = new ArrayList<>();
		// search columns
		int i = column.indexOf(a);
		if (i >= 0) { // a found in column
			for (int k = 0; k < row.size(); k++)
				if (links[k][i] == 1)
					linkedArtifacts.add(row.get(k));
		} else {
			log.error("Artifact " + a
					+ " does not exist in column array");
		}
		return linkedArtifacts;
	}
	
	/**
	 * Get the two dimensional matrix represents the linkage
	 * @return a two dimensioal matrix C ,whose rows represent elements of T and whose columns represent elements of U
	 * C(i,j) = 1 if element of Ti is linked to Uj
	 */
	
	public int[][] getLinkMatrix() {
		return links;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("|  ," + column);
		for (int i = 0; i < row.size(); i++) {
			buf.append(row.get(i));
			for (int j = 0; j < column.size(); j++) {
				buf.append(links[i][j] + ",");
			}
			buf.append("\n");
		}
		return buf.toString();

	}

	public String getName() {
		return this.traceType + "." + row.get(0) + "-" + column.get(0);
	}

	/**
	 * Export the Trace object as a CSV file for manual analysis in Excel
	 * Output format: entity name as column, test case name as row.
	 */
	public void serializeToCSV(Path file){
		if(Files.isDirectory(file))
			throw new IllegalArgumentException(file + "is a directory");
		Charset charset = Charset.forName("UTF-8");
		try(BufferedWriter writer = Files.newBufferedWriter(file,charset,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING)){
			//write 1st write column headers
			StringBuilder buf = new StringBuilder();
			buf.append(DELIMITER);
			for(U col: column)
			      buf.append(col.getName()+DELIMITER);
			buf.append("\n");
			writer.write(buf.toString(),0,buf.length());
			
			//write each row
			for(int i=0;i<row.size();i++){
				buf.setLength(0); //reuse the same buf
				buf.append(row.get(i).getName()+DELIMITER);
				for(int j=0;j<column.size();j++)
					buf.append(links[i][j] +DELIMITER);
				buf.append("\n");
				writer.write(buf.toString(),0,buf.length());
			}
		}catch(IOException e){
			log.error("error in writing to file " + file.getFileName());
			e.printStackTrace();
		}
	}
	/**
	 * Export the Trace object as a CSV file for manual analysis in Excel
	 * Output format: entity name as column, test case name as row.
	 */
	public void serializeToCSVReversedRowCol(Path file){
		if(Files.isDirectory(file))
			throw new IllegalArgumentException(file + "is a directory");
		Charset charset = Charset.forName("UTF-8");
		try(BufferedWriter writer = Files.newBufferedWriter(file,charset,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING)){
			//1st write column headers
			StringBuilder buf = new StringBuilder();
			buf.append(DELIMITER);
			for(T r: row)
			      buf.append(r.getName()+DELIMITER);
			buf.append("\n");
			writer.write(buf.toString(),0,buf.length());
			
			//write each row
			for(int i=0;i<column.size();i++){
				buf.setLength(0); //reuse the same buf
				buf.append(column.get(i).getName()+DELIMITER);
				for(int j=0;j<row.size();j++)
					buf.append(links[j][i] +DELIMITER);
				buf.append("\n");
				writer.write(buf.toString(),0,buf.length());
			}
		}catch(IOException e){
			log.error("error in writing to file " + file.getFileName());
			e.printStackTrace();
		}
	}
	
	/**
	 * Export the Trace object as a CSV file for manual analysis in
	 * Excel. The export will collapse the Coverage Matrix so that only
	 * rows/columns contain as at least one non zero value will be exported.
	 * i.e. row/column with all zero will NOT be exported 
	 * output format: entity name as column, test case name as row.
	 */
	public void serializeCompressedMatrixToCSV(Path file){
		this.compressCoverageMatrix().serializeToCSV(file);
	}

	/**
	 * Compress the Trace matrix to remove all zero rows/columns algorithm:
	 * 1)Transform a Trace to a row map or a column map. In a row map, each
	 * element in row list is the key, and value is an int array holds all the
	 * values of that row. Empty rows are not copied into the map. in a column
	 * map, each element in column list is the key, and value is an int array
	 * holds all the values of that column. Empty columns are not copied into
	 * the map. 2)Transform back to Trace structure - 2 list and one two
	 * dimensional array
	 * 
	 * @return a compressed Trace matrix
	 */
	public Trace<T, U> compressCoverageMatrix() {
		Trace<T, U> nt = convertRowMapToTrace(this.column, this.makeRowMap());
		return convertColumnMapToTrace(nt.getRows(), nt.makeColumnMap());
	}

	// TODO:following methods maybe better implemented with java.util.Bitset
	/**
	 * O(n*m)
	 * 
	 * @return
	 */
	Map<T, int[]> makeRowMap() {
		Map<T, int[]> rowMap = new HashMap<>();
		for (int i = 0; i < row.size(); i++)
			for (int j = 0; j < column.size(); j++) {
				if (links[i][j] != 0) {
					rowMap.put(row.get(i), links[i]);
					break;
				}
			}
		return rowMap;
	}

	/**
	 * O(n*m)
	 * 
	 * @return
	 */
	Map<U, int[]> makeColumnMap() {
		Map<U, int[]> colMap = new HashMap<>();
		for (int j = 0; j < column.size(); j++) {
			int col[] = new int[row.size()];
			boolean emptyRow = true;
			for (int i = 0; i < row.size(); i++) {
				if (emptyRow && links[i][j] != 0)
					emptyRow = false; // any cell not zero, whole column is not
										// empty
				col[i] = links[i][j];
			}
			if (!emptyRow)
				colMap.put(column.get(j), col);
		}
		return colMap;

	}

	Trace convertRowMapToTrace(List<U> col, Map<T, int[]> rowMap) {
		List<T> r = new ArrayList<T>(rowMap.keySet());
		Trace<T, U> nt = new Trace<T, U>(this.traceType, r, col,this.getArtifactFile());
		for (int i = 0; i < r.size(); i++)
			for (int j = 0; j < col.size(); j++)
				if (rowMap.get(r.get(i))[j] == 1)
					nt.setLink(i, j);
		return nt;
	}

	Trace convertColumnMapToTrace(List<T> roo, Map<U, int[]> colMap) {
		List<U> col = new ArrayList<U>(colMap.keySet());
		Trace<T, U> nt = new Trace<T, U>(this.traceType, roo, col,this.getArtifactFile());
		for (int j = 0; j < col.size(); j++)
			for (int i = 0; i < roo.size(); i++)
				if (colMap.get(col.get(j))[i] == 1)
					nt.setLink(i, j);
		return nt;
	}

}
