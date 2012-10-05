package uw.star.rts.extraction;
import java.nio.file.*;

import org.slf4j.*;
import java.nio.charset.*;
import java.util.*;

import uw.star.rts.analysis.*;
import uw.star.rts.artifact.*;
import uw.star.rts.util.*;

import java.io.*;
/**
 * This is to create application from SIR directory structure for a java program
 * @author wliu
 *
 */
public class SIRJavaFactory extends ArtifactFactory{
	//SIR JAVA specific constants
	static String EXPRIMENT_ROOT_EVNIRONMENT_VARIABLE="experiment_root";
	static String DEFAULT_EXPERIMENT_ROOT="C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox";
	static String VERSIONS_DIRECTORY_PREFIX="v";
	
	//code 
	static String CODE_ROOT_DIRECTORY="versions.alt"; 
	static String APPLICATION_DIR_PATTERN="application*";

	//test
	static String TestSuite_ROOT_DIRECTORY="testplans.alt";
	static String TestSuite_UNIVERSAL_FILE_PATTERN="v*.class.junit.universe";
	static String TestSuite_ALL_TESTCASE_FILE_PATTERN="v*.class.junit.universe.all";
	
    //trace 
	static String TRACE_ROOT_DIRECTORY="traces.alt";
	static String CODECOVRAGE_DIRECTORY="CODECOVERAGE";
	
	
	//changes
	static String CHANGES_ROOT_DIRECTORY="changes";
	
	static String experimentRoot;
	static Logger log;
	
	public SIRJavaFactory(){
		log= LoggerFactory.getLogger(SIRJavaFactory.class.getName());
	}
	
	/**
	 * this should extract all artifacts except changes(change analysis) and traces (coverage analysis)
	 */
	public Application extract(String applicationName){
		Application newApp = new Application(applicationName,extractProgram(applicationName),extractTestSuite(applicationName),this);
		return newApp;
	}
	

	public void setExperimentRoot(String expRoot){
		experimentRoot = expRoot;
		log.debug("experiment root is set as " + experimentRoot);
	}

	/**
	 * Extract all code of all versions and variants, extract source,class and property files all together
	 * This method will link program -> codeFiles, but will not link program to codeEntities yet. 
	 * @param applicationName
	 * @return
	 */
	 Map<ProgramVariant,List<Program>> extractProgram(String applicationName){   
		log.info("Extracting code files ...");
		Map<ProgramVariant,List<Program>> results = new HashMap<ProgramVariant,List<Program>>(); 
		//find path to code
		Path codePath =Paths.get(experimentRoot,applicationName,CODE_ROOT_DIRECTORY); //versions.alt
		if(!Files.exists(codePath)||!Files.isDirectory(codePath)){
			log.error("code root directory " + codePath.toAbsolutePath().toString() + " does not exist");
			System.exit(-1);	
		}

		Map<CodeKind,List<Path>> commonFiles = findCommonFilesinApplicatonDirs(codePath);
				
		//iterate each variant - orig,seeded 
        for(ProgramVariant variant : ProgramVariant.values()){
        	List sv = new ArrayList<Program>();
        	log.debug("variant {}",variant);
          //iterate each directories name = variant, should be just one - orig,seeded
		  for(Path sourceVariant: FileUtility.findDirs(codePath, variant.toString())){ 
			  log.debug("sourcevariant directory {}",sourceVariant);
        		//iterate each version directory - v0, v1...
        		for(Path verDir : FileUtility.listDirectory(sourceVariant, VERSIONS_DIRECTORY_PREFIX+"*",new VersionDirectoryComparator(VERSIONS_DIRECTORY_PREFIX))){
        			log.debug("looking into dir {}",verDir);
        			String versionNo = verDir.getFileName().toString().substring(VERSIONS_DIRECTORY_PREFIX.length());
        			//one version of code contains all source,class, html etc of that version
        			Program ver = new Program(applicationName,Integer.parseInt(versionNo),variant);

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
        			/* Feb 13,2012 BUG fixing - src_sameples,src_unitTests are also added as sourcefiles
        			 * this is due to search of all *.java files under v0 dir. Need to search in build directory only.
        			 * the src and classes dir under build are created duing build process and the build dir is copied back to version.alt manually
        			 * there doesn't seem to be any consistence between different SIR test subjects! 
        			 */
        			Path buildDir = FileUtility.findDirs(verDir, "build").get(0);
        			// for each version of the program
        			// 1. find all codekind under component/variant directory
        			
        			for(CodeKind ck : CodeKind.values()){//extract each kind of code : source , class etc. and set them to the program object
        				log.debug("search code kind " + ck + " in directory " + buildDir + " looking for pattern " + ck.getFilePattern());
        				ver.setCodeFiles(ck,FileUtility.findFiles(buildDir,ck.getFilePattern()));
        				log.debug("#files added " + ver.getCodeFiles(ck).size());
        			}
        			
        			
        			//2. add all common files from application directories
        			for(CodeKind codeKind: commonFiles.keySet())
        				ver.addCodeFiles(codeKind,commonFiles.get(codeKind));
        			
        			//3. add all version files from application directories
        			Map<CodeKind,List<Path>> verFiles = findVersionFilesinApplicationDirs(codePath,verDir.getFileName().toString());
        			for(CodeKind codeKind: verFiles.keySet())
        				ver.addCodeFiles(codeKind,verFiles.get(codeKind));
        			
        			sv.add(ver);  //add this version of the program into program array
        		}
     
          }
		  results.put(variant, sv);
        }
		return results;
	}
	 /**
	  * Sep 06,2011 , add logic to handle application/component folders, if there is java files directly under application folders,directly add them to all versions
	  * if the files are under vk directories, do not add  them. They will be added later in each version. this part of the code only add common java files across multiple versions
	  * @param a path to look for application folders
	  * @return a map of common files under all application dirs (excluding vk dirs), key is CODEKIND, value is a list of path
	  */
	 Map<CodeKind,List<Path>> findCommonFilesinApplicatonDirs(Path codePath){
			
			Map<CodeKind,List<Path>> commonFiles = new HashMap<CodeKind,List<Path>>();
			
			for(CodeKind ck: CodeKind.values()){
			   log.debug("Extracting {} under application folders to add as common file", ck);
			   //iterate all application folders
			   List<Path> applicationDirs = FileUtility.listDirectory(codePath, APPLICATION_DIR_PATTERN);
			   List<Path> codeFiles = new ArrayList<Path>();  //codeFiles will contain all code files of this kind under all application folders
			   for(Path applicationDir: applicationDirs){ //each application folder
				   codeFiles = FileUtility.listFiles(applicationDir,ck.getFilePattern());  //get files directly under the folder first 
				   log.debug("added {} to codefiles",codeFiles);
				   for(Path verDir: FileUtility.listDirectory(applicationDir)) //traverse each folder 
				   {   if(verDir.getFileName().toString().substring(0, VERSIONS_DIRECTORY_PREFIX.length()).equalsIgnoreCase(VERSIONS_DIRECTORY_PREFIX)){
					   log.debug("skipped {}",verDir);	
					   continue;  //if the prefix of the verDir is "v", skip
				   		}
					   
					   codeFiles.addAll(FileUtility.findFiles(verDir,ck.getFilePattern()));
					   log.debug("added {} to codefiles from folder {}",codeFiles,verDir.getFileName().toString());
				   }
			   }
			   commonFiles.put(ck,codeFiles );
			   log.debug("added following files from application directories {}"+commonFiles.get(ck));
			}
			return commonFiles;
	 }
	 
	 
	 
	 /**
	  * Go through all application dirs and find all kinds of files under the version specified
	  * @param code path - the path to look for application folders
	  * @param verDirName - name of the verion directory (e.g. v0)
	  * @return a map of codekind and list of path of that codekind
	  */
	 Map<CodeKind,List<Path>> findVersionFilesinApplicationDirs(Path codePath, String verDirName){
			Map<CodeKind,List<Path>> verFiles = new HashMap<CodeKind,List<Path>>();
			
			for(CodeKind ck: CodeKind.values()){
			   log.debug("Extracting {} under application folders {} to add as verion file", ck,verDirName);
			   //iterate all application folders
			   List<Path> applicationDirs = FileUtility.listDirectory(codePath, APPLICATION_DIR_PATTERN);
			   for(Path applicationDir: applicationDirs) //each application folder
				   for(Path verDir: FileUtility.findDirs(applicationDir, verDirName)) //each vk folder 
				   {
					   List<Path>  codeFiles = FileUtility.findFiles(verDir,ck.getFilePattern());
					   if(verFiles.containsKey(ck))
						   codeFiles.addAll(verFiles.get(ck));
					   verFiles.put(ck,codeFiles );
				   }
			   log.debug("added following files from applicaiton directories {}"+verFiles.get(ck));
			}
			return verFiles;
	 }
	 
	 /*
	  * Extract all test cases for all versions, and make them into one test suite
	  * TODO: this one extracts Junit test suites only, need a different method for TSL type of tests, get type information from xml configuration file
	  */
	 TestSuite extractTestSuite(String applicationName){
			
		  log.info("Extracting test cases ...");
		 	Map<String,TestCase> tcm = new Hashtable<String,TestCase>(); //test case name as key, each test case name is unique within an application
			//find path to testplans.alt
			Path testPlanPath =Paths.get(experimentRoot,applicationName,TestSuite_ROOT_DIRECTORY); //testplans.alt
			
			if(!Files.exists(testPlanPath)||!Files.isDirectory(testPlanPath)){
				log.error("testPlan directory " + testPlanPath.toAbsolutePath().toString() + " does not exist");
				System.exit(-1);
			}
			
	        for(Path verDir : FileUtility.listDirectory(testPlanPath, VERSIONS_DIRECTORY_PREFIX+"*",new VersionDirectoryComparator(VERSIONS_DIRECTORY_PREFIX))){
	        			//v0
	        	int dirVer = Integer.parseInt(verDir.getFileName().toString().substring(VERSIONS_DIRECTORY_PREFIX.length()));
	        			//find universe file
	        	for(Path file: FileUtility.findFiles(verDir, TestSuite_UNIVERSAL_FILE_PATTERN)){
	        		int fileVer = parseTestCaseFileVerNo(file);
	        		if(dirVer==fileVer){
		        		//vk.class.junit.universe: common test cases between vk and vk+1. 
		        	    //applicable versions k, and k+1,exist in previous no
		        	    //these are new test cases for this version
		        		for(String n: parseTestCaseFile(file)){
                            TestCase tc = null; 
		        			if(!tcm.containsKey(n)){
			        			tc = new TestCase(applicationName,dirVer,n);
			        			log.debug("creating new test case: " + applicationName + dirVer + n);
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
			        			tc = new TestCase(applicationName,dirVer,n);
			        			log.debug("creating new test case: " + applicationName + dirVer + n);
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
        		for(Path file:FileUtility.findFiles(verDir, TestSuite_ALL_TESTCASE_FILE_PATTERN)){
        			for(String n: parseTestCaseFile(file)){
	        			TestCase tc = null;
	        			if(!tcm.containsKey(n)){
		        			tc = new TestCase(applicationName,dirVer,n);
	        			}else{
	        				tc = tcm.get(n);
	        			}
        				tc.addApplicableVersions(dirVer);
 	        		}
        		}
        	
	        }
	        //create a new test suite
	        TestSuite ts = new TestSuite(applicationName,applicationName+"_testSuite",new ArrayList<TestCase>(tcm.values()));
	        return ts;
	 }

        /**
         * Helper method to take version number from the test case file  
         * @param file - v0.class.junit.universe.all
         * @return 0
         */
		int parseTestCaseFileVerNo(Path file){
			String fileName = file.getFileName().toString();
			//between "v" and the first .
    		String fileVer = fileName.substring(VERSIONS_DIRECTORY_PREFIX.length(),fileName.indexOf('.'));
    		return Integer.parseInt(fileVer);
    		
		}
		
		/**
		 * Helper method to parse a test case file and parse each line to find test cases
		 */
		List<String> parseTestCaseFile(Path file){
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
		
		
		
		/**
		 * find diff result file based on textual differencing
		 * This is the output file of ChangeAnalysis.sh script
		 * file name is in the format of pVersionpVariantpPrimeVersionpPrimeVariant
		 * e.g. changesv0origv1orig.txt
		 * @param p
		 * @param pPrime
		 * @return
		 */
		public Path getChangeResultFile(Program p, Program pPrime){
			String fileName="changes"+p.getVariantType()+"v"+p.getVersionNo()+pPrime.getVariantType()+ "v"+pPrime.getVersionNo()+".txt";
			Path result = Paths.get(experimentRoot,p.getApplicationName(),CHANGES_ROOT_DIRECTORY,fileName);
			if(!Files.exists(result))
				log.error("change result file" + result + " does not exist");
			return result;
		}
		
		/**
		 * Provide an abstraction of folder structures of output files from Emma code coverage tool
		 * This can be used by EmmaCodeAnalyzer to further analyze the files. (parse the file line by line)
		 * @param p
		 * @param tc
		 * @param fileType, emma has two types of output files, html or xml
		 * @return Emma output file by executing TestCase tc on Program p, xml -> a file, html-> a directory contains all htmls files.
		 */
		public Path getEmmaCodeCoverageResultFile(Program p, TestCase tc, String fileType){
			Path verDir =Paths.get(experimentRoot,p.getApplicationName(),TRACE_ROOT_DIRECTORY,CODECOVRAGE_DIRECTORY,p.getVariantType().toString(),VERSIONS_DIRECTORY_PREFIX+p.getVersionNo()); 
			if(fileType.equalsIgnoreCase("xml")){
				return Paths.get(verDir.toString(),"coverage."+tc.getTestCaseName()+".xml");
			}else if(fileType.equalsIgnoreCase("html")){
				return Paths.get(verDir.toString(),"coverage."+tc.getTestCaseName(),"_files");
			}else{
				log.error("unknown emma result file type " + fileType);
				return null;
			}
		}
}