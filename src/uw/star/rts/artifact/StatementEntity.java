package uw.star.rts.artifact;
import java.nio.file.*;
/**
 * A statement is an executable line in a source file
 * @author wliu
 *
 */
public class StatementEntity extends Entity {
	Program program;
	String packageName;
	String srcName;
	int lineNum;
	String statement;
    //one statement belongs to source file only
	SourceFileEntity srcFile;
	
    public StatementEntity(Program p, String packageName,String srcName,int lineNum, String statement,Path sourceFile){
    	super(p.getApplicationName(),p.getVersionNo(),sourceFile);
		//lineNum should always greater than 0, starting from 1
    	if(lineNum<1)
    		throw new IllegalArgumentException("lineNum should start from 1");
		this.srcName = srcName;
		this.packageName = packageName;
		this.statement = statement;
		this.program=p;
		this.lineNum=lineNum;

		
	}
    
    
    public StatementEntity(SourceFileEntity sf, int lineNum,String statement){
    	this(sf.p,sf.packageName,sf.sourceFileName,lineNum,statement,sf.getArtifactFile());
    	srcFile = sf;  //statement --> source
    }
    
    /*
     * Link a statement to its class
     */
    public boolean setSourceFileEntity(SourceFileEntity sf){
    	srcFile = sf;
    	return true;
    }
    
    public SourceFileEntity getSourceFileEntity(){
    	return srcFile;
    }
    
    public EntityType getEntityType(){
    	return EntityType.STATEMENT;
    }
    
    public int getLineNumber(){
    	return lineNum;
    }
    
    public String getStatement(){
    	return statement;
    }
    
    @Override
    public boolean equals(Object o){
    	if(o instanceof StatementEntity){
    		StatementEntity se = (StatementEntity)o;
    		return (this.program==se.program)&&this.packageName.equals(se.packageName)&&this.srcName.equals(se.srcName)&&
    				this.statement.equals(se.statement)&&this.lineNum==se.lineNum;
    	}else{
    		return false;
    	}
    }

    
    
    public String getName(){
    	return this.toString();
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + lineNum;
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((program == null) ? 0 : program.getName().hashCode());
		result = prime * result + ((srcName == null) ? 0 : srcName.hashCode());
		result = prime * result
				+ ((statement == null) ? 0 : statement.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return packageName + "." + srcName +"." + lineNum + "."+ statement; 
	}

}
