package uw.star.sts.artifact;
/**
 * this class represents one version one variant of the program code, 
 * it contains all code kinds including source files, property files , html files and compiled source files(class files)
 *
 * @author WEINING LIU
 *
 */

import java.util.*;
import org.slf4j.*;
import java.nio.file.*;

public class Code extends Artifact{
	//mandatory fields
	String variantType; // one version of the program could have 1 or many variants. 
	//internally, use a hashmap to store code for each kind, codekind as key, list of files of that kind as value
	Map<String,List<Path>> codeMap;
	
	static Logger log;
	public Code(String rep,String appName,int v,String variantType){
		super(rep,appName,v);
		setVariantType(variantType);
		codeMap = new HashMap<String,List<Path>>();
		log = LoggerFactory.getLogger(Code.class);
	}

	public String getVariantType(){
		return variantType;
	}
	
	public void setVariantType(String variantType){
		this.variantType=variantType;
		
	}
	
	/**
	 * 
	 * @param codeKind
	 * @return null if codeKind doesn't exist
	 */
	public List<Path> getCodeFiles(String codeKind){
		return codeMap.get(codeKind);
	}

	public void setCodeFiles(String codeKind,List<Path> codeFiles){
		codeMap.put(codeKind, codeFiles);
		return;
	}
	
	public void addCodeFiles(String codeKind,List<Path> codeFiles){
		if(codeMap.get(codeKind)==null){//was empty
			setCodeFiles(codeKind,codeFiles);
		}else{
			List newList = codeMap.get(codeKind);
			newList.addAll(codeFiles);
			codeMap.put(codeKind, newList);
		}
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append("variantType -" + variantType +"\n");
		for(String k: codeMap.keySet()) {
			sb.append( k + " files - [\n" );
			for(Path file: codeMap.get(k))
				sb.append(file.toString()+"\n");
			sb.append("]\n" );
		}
		return sb.toString();
		
		
	}
}
