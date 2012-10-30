package uw.star.rts.artifact;
/**
 * 
 * A program contains all source code, binary code and any necessary information to run the program (e.g. libary, configuration files, xml etc.)
 * A program may contain many code kinds including source files, property files , html files and compiled source files(class files)
 * A program represents one version the program code of ALL code kind and of one particular program variant.
 * Note internal structure of a program is a list of Path to disk files, which means for programs stored in ALM tools(e.g. Perforce), the artifacts
 * extractor would need to extract code from tools and store in a disk structure. 
 *
 * @author WEINING LIU
 *
 */

import java.util.*;
import org.slf4j.*;

import java.io.File;
import java.nio.file.*;

public class Program extends Artifact{
	//mandatory fields
	/**
	 * @uml.property  name="variantType"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ProgramVariant variantType; // one version of the program could have 1 or many variants. 
	//internally, use a hashmap to store code for each kind, codekind as key, list of files of that kind as value
	/**
	 * @uml.property  name="codeMap"
	 * @uml.associationEnd  qualifier="codeKind:java.lang.String java.util.List"
	 */
	Map<CodeKind,List<Path>> codeMap;
	
	//
	/**
	 * @uml.property  name="codeEntities"
	 * @uml.associationEnd  qualifier="type:uw.star.sts.artifact.EntityType java.util.List"
	 */
	Map<EntityType,List<? extends Entity>> codeEntities;
	
	Path codeFilesRoot;
	
	static Logger log;
	public Program(String appName,int v,ProgramVariant variantType,Path programRootFolder){
		super(appName,v,programRootFolder);
		setVariantType(variantType);
		codeMap = new HashMap<CodeKind,List<Path>>();
		log = LoggerFactory.getLogger(Program.class);
		codeEntities = new HashMap<>();
	}

	/**
	 * @return
	 * @uml.property  name="variantType"
	 */
	public ProgramVariant getVariantType(){
		return variantType;
	}
	
	/**
	 * @param variantType
	 * @uml.property  name="variantType"
	 */
	public void setVariantType(ProgramVariant variantType){
		this.variantType=variantType;
		
	}
	
	/**
	 * 
	 * @param codeKind
	 * @return null if codeKind doesn't exist
	 */
	public List<Path> getCodeFiles(CodeKind codeKind){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
		return codeMap.get(codeKind);
	}

	public void setCodeFiles(CodeKind codeKind,List<Path> codeFiles){
		codeMap.put(codeKind, codeFiles);
		return;
	}
	
	public void setCodeFilesRoot(Path root){
		codeFilesRoot = root;
	}
	
	public Path getCodeFilesRoot(){
		return codeFilesRoot;
	}
	
	public Path getCodeFilebyName(CodeKind codeKind,String packageName, String name){
		Path resultPath = null;
		//unique constraints
		//by package name and class name
		switch(codeKind){
		case BINARY :
			resultPath = findFilebyName(getCodeFiles(codeKind),packageName,name,".class");
			break;
		case SOURCE:
			resultPath = findFilebyName(getCodeFiles(codeKind),packageName,name,"");
			break;
		case HTML:
			resultPath = findFilebyName(getCodeFiles(codeKind),name);
			break;
		case PROPERTY:
			resultPath = findFilebyName(getCodeFiles(codeKind),name);
			break;
		default: 
			log.error("Unknown codeKind " + codeKind);
			
		}
		return resultPath;
	}
	/**
	 * Helper method to find the actual path to a given class/source file
	 * this method basically reconstruct the file path based on the package name and class name
	 * this method returns the 1st occurrence of the given name   
	 * TODO: performance is really bad as this iterates every code files in the program.
	 * @param codeFiles - a given list of class files 
	 * @param packageName - class file's package name
	 * @param className - class name
	 * @return 
	 */
	private Path findFilebyName(List<Path> codeFiles,String packageName,String className,String extenstion){
		if(packageName!=null){
			if(packageName.contains(".")){
			    className = packageName.replace('.', File.separatorChar)+File.separator+className + extenstion;
			}else{
				className = packageName+File.separator+className + extenstion;
			}
		}
				
	    for(Path code: codeFiles)
	        if(code.endsWith(className))
	        	return code;
	    log.error("Package " + packageName + " class " + className + extenstion + " not found in given code list ");
	    return null;
	}
	
	/**
	 * Helper method to find a file find the given list
	 * this method returns the 1st occurrence of the given name   
	 * @param fullname - file's full name with extension
	 * @param className - class name
	 * @return 
	 */
	private Path findFilebyName(List<Path> codeFiles,String fullName){
	    for(Path code: codeFiles)
	        if(code.endsWith(fullName))
	        	return code;
	    log.error(fullName + " not found in given code list ");
	    return null;
	}
	
	public void addCodeFiles(CodeKind codeKind,List<Path> codeFiles){
		if(codeMap.get(codeKind)==null){//was empty
			setCodeFiles(codeKind,codeFiles);
		}else{
			List newList = codeMap.get(codeKind);
			newList.addAll(codeFiles);
			codeMap.put(codeKind, newList);
		}
	}
	
	/**
	 * set, replace if entity type already exist
	 * @param type
	 * @param entities
	 * @return
	 */
	public boolean setCodeEntities(EntityType type,List<? extends Entity> entities){
		if(codeEntities.containsKey(type)){
			log.error("Entity type " + type + " already exist, replace by new values");
		}
		codeEntities.put(type, entities);
		return true;
	}
	/**
	 * add, merge with existing entity type if exist
	 * @param type
	 * @param entities
	 * @return
	 */
	public boolean addCodeEntities(EntityType type,List<Entity> entities){
		if(codeEntities.containsKey(type)){
			log.debug("Entity type" + type + " already exist, replace by new values");
			Set<Entity> current = new HashSet(codeEntities.get(type));
			//convert to set in case there exist an entity in current and entities
			current.addAll(entities);
			codeEntities.put(type, new ArrayList<Entity>(current));
		}else{
			codeEntities.put(type,entities);
		}
		return true;
	}
	
	public List<? extends Entity> getCodeEntities(EntityType type){
		return codeEntities.get(type);
	}
	
	public Entity getEntityByName(EntityType type, String name){
		for(Entity e: codeEntities.get(type))
			if(name.equals(e.getName()))
					return e;
		log.error("Entity " + type + name + " not found ");
		return null;
	}
	
	
	public <E extends Entity> boolean contains(E entity){
		for(EntityType type: EntityType.values()){
			if(codeEntities.get(type).contains(entity)) return true;
		}
		return false;
	}
	
		
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append("variantType -" + variantType +"\n");
		for(CodeKind k: codeMap.keySet()) {
			sb.append( k + " files - [\n" );
			for(Path file: codeMap.get(k))
				sb.append(file.toString()+"\n");
			sb.append("]\n" );
		}
		return sb.toString();
		
		
	}
	

	public String getName(){
		return this.applicationName+"."+this.variantType+".v"+this.versionNo;
	}
}
