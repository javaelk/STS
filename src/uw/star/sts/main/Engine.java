package uw.star.sts.main;

import uw.star.sts.artifact.*;
import uw.star.sts.extraction.*;
import uw.star.sts.goal.*;
import uw.star.sts.util.*;
import org.apache.xmlbeans.*;
import ca.uwaterloo.uw.star.sts.schema.caseStudySubject.*;
import ca.uwaterloo.uw.star.sts.schema.caseStudySubject.CsSubjectDocument.CsSubject;

import java.io.*;
import java.nio.file.Paths;
import org.slf4j.*;

public class Engine {
	public static void main(String[] args){
		
		Logger log = LoggerFactory.getLogger(Engine.class);
		//get user selected goals
		//extract test object , list of test objects provided as xml file , arg0 is file name
		
		
		
		//arg0 is experiment object xml file name
		if(args.length!=1){
			System.out.println("Please provide experiment object xml file name");
			System.exit(-1);
		}
		
		CasestudySubjectDocument eo = null;
		try{
			eo=CasestudySubjectDocument.Factory.parse(Paths.get(args[0]).toFile());
		}catch(XmlException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
        if(eo!=null){
        	for(CsSubject e : eo.getCasestudySubject().getCsSubjectArray()){
        		if(e.getRepository().equals(Constant.SIR_JAVA)){
        			Application newApp = new SIRJavaFactory().extract(e.getName());
        			System.out.println(newApp);
        			System.out.println(newApp.getStats());
        		}else{
        			log.error("Error, repository factory "+e.getRepository()+ " is not implemented!");
        		}
   
        	}
        }
	}
}
