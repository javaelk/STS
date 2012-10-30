package uw.star.rts.artifact;
import java.util.*;
import org.slf4j.*;
import java.nio.file.*;
/**
 * a SourceFileEntity represents a uncompiled source file 
 * TODO: source file should be beautified first @see http://stackoverflow.com/questions/996646/stand-alone-java-code-formatter-beautifier-pretty-printer
 * @author wliu
 *
 */
public class SourceFileEntity extends Entity {
	
	String packageName;
	String sourceFileName; //with extension (e.g HexDump.java)
	Program p; //every source file belongs to a program
	//TODO: do I really need to link class and its source both ways? remove one if not needed
	// source <--> class <->method
	// source <-->statement      
	List<ClassEntity> clazz; //a source could be compiled into multiple classes 
	
	List<StatementEntity> executableStm; //all executable statment ordered by line number
	int[] stmIndex; //a  map table: index:line number, value: index in executableStm.This is for O(1) access of getStatementByLineNumber()
	Logger log;
	
	public SourceFileEntity(Program p, String packageName, String srcfileName, Path sourceFile){
		super(p.getApplicationName(),p.getVersionNo(),sourceFile);
		this.packageName =packageName;
		this.sourceFileName = srcfileName;
		this.p =p;
		//Bug fix:Feb26,2012: remove coverage information from source file. Coverage information should be in trace.
		// A source file should contain an array of executable statements but not the coverage informaiton of them as they are dependant on test cases.
        executableStm = new ArrayList<>();
        //still don't know stmIndex size at this time.
        log = LoggerFactory.getLogger(SourceFileEntity.class.getName());
	}
	
	
	
	public boolean setClasses(List<ClassEntity> ce){
		this.clazz = ce;
		return true;
	}
	
	public List<ClassEntity> getClazzes(){
		return this.clazz;
	}
	

	//fully covered + partly covered + not covered, need to make sure there is no duplicates
	public List<StatementEntity> getExecutableStatements(){
		return executableStm;
	}
    
	/**
	 * 
	 * @param allExecutableStms, a set of all executable statements, statements are ordered and unique
	 * @return
	 */
	public boolean setExecutableStatements(List<StatementEntity> allExecutableStms){
		if(allExecutableStms==null||allExecutableStms.isEmpty())
			return false; //nothing to set
		if(executableStm.isEmpty()){
			executableStm = allExecutableStms;
		}else{
			log.error("should not insert all executable statement again");
			return false;
		}	

		//build index array O(n),assume allExecutableStms is already ordered by statement line number
		//line number of last element is the size of index array
		if(executableStm.size()>0){
			stmIndex = new int[executableStm.get(executableStm.size()-1).getLineNumber()];
			//initalize to -1, otherwise default is zero which will point to the 1st element of executableStm
			for(int i=0;i<stmIndex.length;i++)
				stmIndex[i] = -1;

			for(int i=0;i<executableStm.size();i++)
				stmIndex[executableStm.get(i).getLineNumber()-1] = i; //line number starts from 1, array index starts from 0
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * this garantees O(1) operation
	 * @param lineNum, lineNum should start from 1. 
	 * @return @nullable Statement objective with given lineNum or null if given line is not an executable statement
	 */
	public StatementEntity getStatementByLineNumber(int lineNum){
		//be careful of array index out of bound.
		if(lineNum <=0) 
			throw new IllegalArgumentException("line number " + lineNum + " is out of bound");
		if(lineNum >stmIndex.length)
			return null;
		
		int idx = stmIndex[lineNum-1];
		if(idx<0 || idx >= executableStm.size()) return null;
		return executableStm.get(idx);
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public String getSourceFileName(){
		return sourceFileName;
	}
	


	@Override
	public EntityType getEntityType() {
		return EntityType.SOURCE;
	}

	@Override
	public String getName() {
		return this.toString();
	}
	@Override
	public String toString(){
		return packageName+"."+sourceFileName;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result
				+ ((sourceFileName == null) ? 0 : sourceFileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof SourceFileEntity){
			SourceFileEntity so = (SourceFileEntity) o;
			return this.packageName.equals(so.packageName)&&this.p==so.p&&this.sourceFileName.equals(so.sourceFileName);
		}else{
			return false;
		}

	}
	
	

}
