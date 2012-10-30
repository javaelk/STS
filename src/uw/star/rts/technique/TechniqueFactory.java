package uw.star.rts.technique;

import ca.uwaterloo.uw.star.sts.schema.stStechniques.STStechniquesDocument;
import java.lang.reflect.*;
import ca.uwaterloo.uw.star.sts.schema.stStechniques.STStechniquesDocument.STStechniques;
import ca.uwaterloo.uw.star.sts.schema.stStechniques.TechniqueDocument.Technique;
import ca.uwaterloo.uw.star.sts.schema.stStechniques.ImplementationDocument.Implementation;

import java.io.IOException;
import java.nio.file.*;

import java.util.*;

import org.apache.xmlbeans.XmlException;
import org.slf4j.*;


/**
 * Factory class to create techniques objects 
 * @author Weining Liu
 *
 */
public class TechniqueFactory {
	
	/**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Logger log;
	/**
	 * @uml.property  name="techniques"
	 */
	private List<Technique> techniques; 
	/**
	 * constructor
	 * extract all techniques from xml file
	 */
	public TechniqueFactory(Path techniqueFile){
		log = LoggerFactory.getLogger(TechniqueFactory.class);
		
        STStechniquesDocument sd = null;
        try{
        	sd = STStechniquesDocument.Factory.parse(techniqueFile.toFile());
        }catch(XmlException e){
        	e.printStackTrace();
        }catch(IOException e){
        	e.printStackTrace();
        }
        
        if(sd!=null)
        	techniques = Arrays.asList(sd.getSTStechniques().getTechniqueArray());
	}
	
	
	List<ca.uwaterloo.uw.star.sts.schema.stStechniques.TechniqueDocument.Technique> getAllTechniques(){
		return techniques;
	}
	/**
	 * Model techniques by construct technique objects for each technique implementation listed in the xml file
	 * @return a list of technique objects
	 */
	public List<uw.star.rts.technique.Technique> techniquesModeling(){
		List<uw.star.rts.technique.Technique> techniqueImplmentations = new ArrayList<>();
		for(ca.uwaterloo.uw.star.sts.schema.stStechniques.TechniqueDocument.Technique techSpec: getAllTechniques()){
			Implementation[] impl = techSpec.getImplementationArray();
			for(Implementation im: impl){
				String className = im.getClassName();
				uw.star.rts.technique.Technique tech = getTechniqueInstancebyName(className); 
				tech.setID(techSpec.getID());
				tech.setDescription(techSpec.getDescription());
				techniqueImplmentations.add(tech);
				}
		}
		return techniqueImplmentations;
	}
	

	 /**
	  * Create an instance of the technique based on the provided fully qualified class name
	  * @param className - fully qualified class name of technique implementation.
	  * @return a technique instance
	  * This method uses Java reflection API
	  * @see http://docs.oracle.com/javase/tutorial/reflect/member/ctorInstance.html
	  */
	private uw.star.rts.technique.Technique getTechniqueInstancebyName(String className){
		uw.star.rts.technique.Technique tech = null;
		//use java reflection @see http://docs.oracle.com/javase/tutorial/reflect/class/classNew.html
		try {
			Class<?> c = Class.forName(className);  //don't know the type of class yet (without casting), hence the <?>
			//find the zero-argument constructor
			Constructor[] ctors = c.getDeclaredConstructors();
			Constructor ctor = null;
			for (int i = 0; i < ctors.length; i++) {
			    ctor = ctors[i];
			    if (ctor.getGenericParameterTypes().length == 0)
				break;
			}
		    ctor.setAccessible(true);
		    tech = (uw.star.rts.technique.Technique)ctor.newInstance();
		     // production code should handle these exceptions more gracefully
		} catch (InstantiationException x) {
		    x.printStackTrace();
	 	} catch (InvocationTargetException x) {
	 	    x.printStackTrace();
		} catch (IllegalAccessException x) {
		    x.printStackTrace();
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	    return tech;
	    
		/* technique name is hardcoded here, only work for two techniques for testing purpose.
		ClassLoader classLoader = TechniqueFactory.class.getClassLoader();	
		 switch(className){
		case "":
			uw.star.rts.technique.Technique tech = new TextualDifference_Source();
			tech.setID(techSpec.getID());
			tech.setDescription(techSpec.getDescription());
			techniqueImplmentations.add(tech);
			break;
		case "uw.star.sts.technique.TextualDifference_Statement":
			tech = new TextualDifference_Statement();
			tech.setID(techSpec.getID());
			tech.setDescription(techSpec.getDescription());
			techniqueImplmentations.add(tech);
			break;
		default:
		}*/
		
		
		/* use classLoader, didn't really work
		log.debug("try to load class by name "+ className );
	    try {
	        Object aClass = classLoader.loadClass(im.getClassName());
		    if(aClass instanceof uw.star.sts.technique.Technique){
		    	techniqueImplmentations.add((uw.star.sts.technique.Technique)aClass);
		    }else{
		    	log.error(className + " is not an instance of Technique");
		    }
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	        log.error("failed to load class " + className);
	    }*/

	}
	/**
	 * 
	 * @return
	 */
	public List<uw.star.rts.technique.Technique> filterOnDataAvailability(List<uw.star.rts.technique.Technique> techs){
		//TODO: Implement me! 
		return techs;
	}
}
