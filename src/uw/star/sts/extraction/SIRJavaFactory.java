package uw.star.sts.extraction;
import java.nio.file.*;

import org.slf4j.*;
import java.nio.charset.*;
import java.util.*;

import uw.star.sts.artifact.*;
import uw.star.sts.util.*;

import java.io.*;
/**
 * This is to create application from SIR directory structure for a java program
 * @author wliu
 *
 */
public class SIRJavaFactory extends ArtifactFactory{
	
	//TODO: move them up to super class as all these are needed for any factories
	//SIR JAVA specific constants
	static String EXPRIMENT_ROOT_EVNIRONMENT_VARIABLE="experiment_root";
	static String CODE_ROOT_DIRECTORY="versions.alt";
	static String[] SOURCE_VARIANT_TYPE={"orig","seeded"};
	static String SOURCE_VARIANT_VERSIONS_DIRECTORY_PREFIX="v";
	static String[] CODE_DIRECTORY={"src","classes","src","src"};
	static String[] CODE_FILE_PATTERN={"*.java","*.class","*.html","{(*.txt,*.xml,*.properties,*.property}"}; //glob pattern is defined in java.nio.FileSystem
	static String TestPlan_ROOT_DIRECTORY="testplans.alt";
	static String TestPlan_VERSIONS_DIRECTORY_PREFIX="v";
	static String TestPlan_UNIVERSAL_FILE_PATTERN="v*.class.junit.universe";
	static String TestPlan_ALL_TESTCASE_FILE_PATTERN="v*.class.junit.universe.all";
	static String APPLICATION_DIR_PATTERN="application*";
	static int CODEKINDSOURCE=0;
	static int CODEKINDCLASS=1;
	static int CODEKINDHTML=2;
	static int CODEKINDPROPERTY=3;

	static String experimentRoot;
	
	static Logger log;
	
	public SIRJavaFactory(){
		log= LoggerFactory.getLogger(SIRJavaFactory.class.getName());
		//get experiment root environment variable
		try{
			Map<String,String> env = System.getenv();
			for(String envName: env.keySet())
			   if (envName.equals(EXPRIMENT_ROOT_EVNIRONMENT_VARIABLE))
				   experimentRoot=env.get(envName);
			log.debug("experiment_root is {}" ,experimentRoot );
		}catch(NullPointerException e){
			System.out.println("environment variable " +EXPRIMENT_ROOT_EVNIRONMENT_VARIABLE + " is null!"  );
		}catch(SecurityException e){
			System.out.println("Does not have access to the specified system property!");
		}
	
	}
	
	public Application extract(String applicationName){
		//TODO:extrac artifact one by one
		Application newApp = new Application();
		newApp.setName(applicationName);
		newApp.setVersions(extractCode(applicationName));
		newApp.setTestCases( extractTestCase(applicationName));
		log.debug("application details {}",newApp);
		return newApp;
		
	}
	
	
	/*
	 * Extract all code of all versions and variants, extract source,class and property files all together
	 * 
	 */
	 private List<Code> extractCode(String applicationName){   
		log.info("Extracting code files ...");
		
		List sv = new ArrayList<Code>();
		//find path to code
		Path codePath =Paths.get(experimentRoot,applicationName,CODE_ROOT_DIRECTORY); //versions.alt
	
		if(!Files.exists(codePath)||!Files.isDirectory(codePath)){
			log.error("code root directory " + codePath.toAbsolutePath().toString() + " does not exist");
			System.exit(-1);
		}

		Map<String,List<Path>> commonFiles = findCommonFilesinApplicatonDirs(codePath);
				
		//iterate each variant and each version
        for(String variant : Arrays.asList(SOURCE_VARIANT_TYPE)){
        	log.debug("variant {}",variant);
		  for(Path sourceVariant: FileUtility.findDirs(codePath, variant)){ 
			  log.debug("sourcevariant directory {}",sourceVariant);
        		//orig,seeded
        		for(Path verDir : FileUtility.listDirectory(sourceVariant, SOURCE_VARIANT_VERSIONS_DIRECTORY_PREFIX+"*")){
        			log.debug("looking into dir {}",verDir);
        			String versionNo = verDir.getFileName().toString().substring(SOURCE_VARIANT_VERSIONS_DIRECTORY_PREFIX.length());
        			//one version of code contains all source,class, html etc of that version
        			Code ver = new Code(Constant.SIR_JAVA,applicationName,Integer.parseInt(versionNo),sourceVariant.getFileName().toString());

        			/* do not search for src instead just search by file extension, some SIR subject do not have src directory 
        			for(int i=0;i<CODEKIND.length;i++){//extract each kind of code : source , class etc.
        				log.debug("find dir {} under {}", CODE_DIRECTORY[i],verDir);
        				List<Path> searchResult = FileUtility.findDirs(verDir, CODE_DIRECTORY[i]);
        				log.debug("Extracting {} files",CODEKIND[i]);	
	        			if(searchResult.size()==1){   //find one directory under verDir match CODE_DIRECTORY name
	        				log.debug("find {} files under {}", CODE_FILE_PATTERN[i],searchResult.get(0));
	        				ver.setCodeFiles(CODEKIND[i],FileUtility.findFiles(searchResult.get(0),CODE_FILE_PATTERN[i]));
	        			}else if(searchResult.size()<1){ 
	        				log.error("Couldn't find "+CODE_DIRECTORY[i]+" directory in " + verDir.toString());
	        			}else{
	        				log.error("Find more than one "+CODE_DIRECTORY[i]+" directory in " + verDir.toString());
	        			}
        			}
        			*/
        			// for each version 
        			// 1. find all codekind under component/variant directory
        			for(int i=0;i<CODEKIND.length;i++)//extract each kind of code : source , class etc.
        			ver.setCodeFiles(CODEKIND[i],FileUtility.findFiles(verDir,CODE_FILE_PATTERN[i]));
        			
        			//2. add all common files from application directories
        			for(String codeKind: commonFiles.keySet())
        				ver.addCodeFiles(codeKind,commonFiles.get(codeKind));
        			
        			//3. add all version files from application directories
        			Map<String,List<Path>> verFiles = findVersionFilesinApplicationDirs(codePath,verDir.getFileName().toString());
        			for(String codeKind: verFiles.keySet())
        				ver.addCodeFiles(codeKind,verFiles.get(codeKind));
        			
        			sv.add(ver);  //add this version of code into code array
        		}
          }
        }
		return sv;
	}
	 /**
	  * Sep 06,2011 , add logic to handle application/component folders, if there is java files directly under application folders,directly add them to all versions
	  * if the files are under vk directories, do not add  them. They will be added later in each version. this part of the code only add common java files across multiple versions
	  * @param a path to look for application folders
	  * @return a map of common files under all application dirs (excluding vk dirs), key is CODEKIND, value is a list of path
	  */
	 private Map<String,List<Path>> findCommonFilesinApplicatonDirs(Path codePath){
			
			Map<String,List<Path>> commonFiles = new HashMap<String,List<Path>>();
			
			for(int i=0;i<CODEKIND.length;i++){
			   log.debug("Extracting {} under application folders to add as common file", CODEKIND[i]);
			   //iterate all application folders
			   List<Path> applicationDirs = FileUtility.listDirectory(codePath, APPLICATION_DIR_PATTERN);
			   List<Path> codeFiles = new ArrayList<Path>();  //codeFiles will contain all code files of this kind under all application folders
			   for(Path applicationDir: applicationDirs){ //each application folder
				   codeFiles = FileUtility.listFiles(applicationDir,CODE_FILE_PATTERN[i]);  //get files directly under the folder first 
				   log.debug("added {} to codefiles",codeFiles);
				   for(Path verDir: FileUtility.listDirectory(applicationDir)) //traverse each folder 
				   {   if(verDir.getFileName().toString().substring(0, SOURCE_VARIANT_VERSIONS_DIRECTORY_PREFIX.length()).equalsIgnoreCase(SOURCE_VARIANT_VERSIONS_DIRECTORY_PREFIX)){
					   log.debug("skipped {}",verDir);	
					   continue;  //if the prefix of the verDir is "v", skip
				   		}
					   
					   codeFiles.addAll(FileUtility.findFiles(verDir,CODE_FILE_PATTERN[i]));
					   log.debug("added {} to codefiles from folder {}",codeFiles,verDir.getFileName().toString());
				   }
			   }
			   commonFiles.put(CODEKIND[i],codeFiles );
			   log.debug("added following files from application directories {}"+commonFiles.get(CODEKIND[i]));
			}
			return commonFiles;
	 }
	 
	 
	 
	 /**
	  * Go through all application dirs and find all kinds of files under the version specified
	  * @param code path - the path to look for application folders
	  * @param verDirName - name of the verion directory (e.g. v0)
	  * @return a map of codekind and list of path of that codekind
	  */
	 private Map<String,List<Path>> findVersionFilesinApplicationDirs(Path codePath, String verDirName){
			Map<String,List<Path>> verFiles = new HashMap<String,List<Path>>();
			
			for(int i=0;i<CODEKIND.length;i++){
			   log.debug("Extracting {} under application folders {} to add as verion file", CODEKIND[i],verDirName);
			   //iterate all application folders
			   List<Path> applicationDirs = FileUtility.listDirectory(codePath, APPLICATION_DIR_PATTERN);
			   for(Path applicationDir: applicationDirs) //each application folder
				   for(Path verDir: FileUtility.findDirs(applicationDir, verDirName)) //each vk folder 
				   {
					   List<Path>  codeFiles = FileUtility.findFiles(verDir,CODE_FILE_PATTERN[i]);
					   if(verFiles.containsKey(CODEKIND[i]))
						   codeFiles.addAll(verFiles.get(CODEKIND[i]));
					   verFiles.put(CODEKIND[i],codeFiles );
				   }
			   log.debug("added following files from applicaiton directories {}"+verFiles.get(CODEKIND[i]));
			}
			return verFiles;
	 }
	 
	 /*
	  * Extract all test cases for all versions, and make them into one test plan
	  */
	 private List<TestCase> extractTestCase(String applicationName){
			
		  System.out.println("Extracting test cases ...");
		 	Map<String,TestCase> tcm = new Hashtable<String,TestCase>(); //test case name as key, each test case name is unique within a application
			//find path to testplans.alt
			Path testPlanPath =Paths.get(experimentRoot,applicationName,TestPlan_ROOT_DIRECTORY); //testplans.alt
			
			if(!Files.exists(testPlanPath)||!Files.isDirectory(testPlanPath)){
				log.error("testPlan directory " + testPlanPath.toAbsolutePath().toString() + " does not exist");
				System.exit(-1);
			}
			
	        for(Path verDir : FileUtility.listDirectory(testPlanPath, TestPlan_VERSIONS_DIRECTORY_PREFIX+"*")){
	        			//v0
	        	int dirVer = Integer.parseInt(verDir.getFileName().toString().substring(TestPlan_VERSIONS_DIRECTORY_PREFIX.length()));
	        			//find universe file
	        	for(Path file: FileUtility.findFiles(verDir, TestPlan_UNIVERSAL_FILE_PATTERN)){
	        		int fileVer = parseTestCaseFileVerNo(file);
	        		if(dirVer==fileVer){
		        		//vk.class.junit.universe: common test cases between vk and vk+1. 
		        	    //applicable versions k, and k+1,exist in previous no
		        	    //these are new test cases for this version
		        		for(String n: parseTestCaseFile(file)){
                            TestCase tc = null; 
		        			if(!tcm.containsKey(n)){
			        			tc = new TestCase(Constant.SIR_JAVA,applicationName,dirVer,n);
		        			}else{
		        				tc = tcm.get(n);
		        			}
		        			tc.addApplicableVersions(dirVer);
	        				tc.addApplicableVersions(dirVer+1);
	        				tc.existInPreviousVersion(false);
	        				tcm.put(n, tc);
		        		}
	        		}else if(fileVer == dirVer-1){
	        		// vk-1.class.junit.universe: common test cases between vk-1 and vk.
	        	    // applicable versions k, and k-1,exist in previous yes
	        		//these are existing test cases for this version
		        		for(String n: parseTestCaseFile(file)){
		        			TestCase tc = null;
		        			if(!tcm.containsKey(n)){
			        			tc = new TestCase(Constant.SIR_JAVA,applicationName,dirVer,n);
		        			}else{
		        				tc = tcm.get(n);
		        			}
	        				tc.addApplicableVersions(dirVer);
	        				tc.addApplicableVersions(dirVer-1);
	        				tc.existInPreviousVersion(true);
		        		}
	        			
	        		}else{
	        			System.out.println("something is wrong, file verion number "+fileVer + " does not match with directory version number" + dirVer);
	        		}
	        		
	        	}
	        			//find universe.all, vk.class.junit.universe.all- include all test cases for vk.
        		for(Path file:FileUtility.findFiles(verDir, TestPlan_ALL_TESTCASE_FILE_PATTERN)){
        			for(String n: parseTestCaseFile(file)){
	        			TestCase tc = null;
	        			if(!tcm.containsKey(n)){
		        			tc = new TestCase(Constant.SIR_JAVA,applicationName,dirVer,n);
	        			}else{
	        				tc = tcm.get(n);
	        			}
        				tc.addApplicableVersions(dirVer);
 	        		}
        		}
        	
	        }
	        return new ArrayList<TestCase>(tcm.values());
	 }

        /**
         * Helper method to take version number from the test case file  
         * @param file - v0.class.junit.universe.all
         * @return 0
         */
		private int parseTestCaseFileVerNo(Path file){
			String fileName = file.getFileName().toString();
			//between "v" and the first .
    		String fileVer = fileName.substring(TestPlan_VERSIONS_DIRECTORY_PREFIX.length(),fileName.indexOf('.'));
    		return Integer.parseInt(fileVer);
    		
		}
		
		/**
		 * Helper method to parse a test case file and parse each line to find test cases
		 */
		private List<String> parseTestCaseFile(Path file){
			List<String> testCaseNames = new ArrayList<String>();
			Charset charset = Charset.forName("US-ASCII");
			try(BufferedReader reader=Files.newBufferedReader(file, charset)){
				 String line = null;
			    while ((line = reader.readLine()) != null) {
			    	if(line.startsWith("-P["))
					   testCaseNames.add(line.substring(line.indexOf("[")+1,line.indexOf("]")));
			    }
			}catch (IOException x) {
				System.err.format("IOException in reading " + file.getFileName().toString()+ x);
			}
			return testCaseNames; 
		}
		

}
