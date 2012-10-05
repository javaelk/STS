package uw.star.rts.analysis;

import org.apache.bcel.classfile.*;

import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.ba.*;
import edu.umd.cs.findbugs.classfile.*;
import edu.umd.cs.findbugs.classfile.impl.ClassFactory;
import edu.umd.cs.findbugs.*;
import uw.star.rts.artifact.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.slf4j.*;

/**
 * Utilities create Basic Blocks from Class file. This utilize BCEL and FindBugs.
 * TODO: this may be implemented with WALA instead 
 * @author wliu
 *
 */
public class BasicBlockFactory {
	
	Program program;
	Logger log;
	public BasicBlockFactory(Program p){
		this.program =p;
		log = LoggerFactory.getLogger(BasicBlockFactory.class);
		
	}
	// reference - http://tech.joshuacummings.com/2010/05/testing-custom-findbugs-detectors-in.html
	public List<BasicBlock> parse() {
		
		List<BasicBlock> results = new ArrayList();
		// project represents a program
		try {
			//all the path setting here are useless, actual class file are provided later from the program.getCodeFiles()
			Project proj = new Project();
			//TODO: this filepath should not be hardcoded. should come from program
			String filepath = "C:\\Documents and Settings\\wliu\\My Documents\\personal\\Dropbox\\apache-xml-security\\versions.alt\\orig\\v0\\xml-security\\build\\classes";
/*			String fileToAnalyze = filepath + File.separator
					+ "org\\apache\\xml\\security\\algorithms\\encryption\\helper\\AESWrapper.class";
			proj.addFile(fileToAnalyze);
			proj.setConfiguration(UserPreferences.createDefaultUserPreferences());
			proj.setCurrentWorkingDirectory(new File(filepath));*/
			BugReporter bugReporter = new XMLBugReporter(proj);

			// a great deal of code to say 'analyze the files in this directory'
			IClassFactory classFactory = ClassFactory.instance();
			IClassPath classPath = classFactory.createClassPath();
			IAnalysisCache analysisCache = classFactory.createAnalysisCache(classPath, bugReporter);
			Global.setAnalysisCacheForCurrentThread(analysisCache);
			FindBugs2.registerBuiltInAnalysisEngines(analysisCache);
			IClassPathBuilder builder = classFactory.createClassPathBuilder(bugReporter);

			//List classesToAnalyze = builder.getAppClassList();
			AnalysisCacheToAnalysisContextAdapter analysisContext = new AnalysisCacheToAnalysisContextAdapter();
			AnalysisContext.setCurrentAnalysisContext(analysisContext);

			//CFG
		      for(java.nio.file.Path classFile: program.getCodeFiles(CodeKind.BINARY)){
		    	  try{
		    		 // log.debug("filepath " + classFile.toFile().getParentFile().getCanonicalPath());
	//TODO: filepath?	  filepath = classFile.toFile().getParentFile().getCanonicalPath();
		  		ICodeBaseLocator locator = classFactory.createFilesystemCodeBaseLocator(filepath);
					builder.addCodeBase(locator, true);
					builder.build(classPath, new NoOpFindBugsProgress()); 
		    	
					JavaClass theClass = new ClassParser(Files.newInputStream(classFile),classFile.getFileName().toString()).parse();
		    	  ClassContext classContext = analysisContext.getClassContext(theClass);
		    	  log.debug("Start analyzing Class : " + theClass.getClassName());
		    	  	for(Method theMethod: Arrays.asList(theClass.getMethods())){
		    	  		log.debug("Start analyzing Method : " + theMethod.getSignature() + "   "+ theMethod.getName());
		    	  		CFG cfg = classContext.getCFG(theMethod);
		    	  		for(Iterator i =cfg.blockIterator();i.hasNext();){
		    	  			BasicBlock bb = (BasicBlock) i.next();
		    	  			log.debug("Add basic block [" + bb.getFirstInstruction()+","+bb.getLastInstruction()+ "] for Class:" + theClass.getClassName() + "."+ theMethod.getName());
		    	  			results.add(bb);
		    	  		}
		    	  	}
		    	  
		    	  }catch(ClassFormatException e){
		    		  log.error("Class format exception");
		    	  }catch(IOException e){
		    		  log.error("Error reading file stream");
		    	  }catch(edu.umd.cs.findbugs.ba.MethodUnprofitableException e){
		    		log.error("Appears unprofitable to analyze the method");
		    	  }catch(CFGBuilderException e){
		    		  log.error("Failed to build CFG for method ");
		    	  }
		    	  
		      }

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	      return results;
	}

}
