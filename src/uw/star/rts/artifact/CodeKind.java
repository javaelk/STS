package uw.star.rts.artifact;

import java.util.EnumSet;

/**
 * Identify various kind of code files
 * @author Weining Liu
 *
 */
public enum CodeKind {   
	SOURCE,BINARY,HTML,PROPERTY;

	//glob pattern is defined in java.nio.FileSystem
	public String getFilePattern(){
		String result="";
		switch(this){
		case SOURCE: result= "*.java";
		break;
		case BINARY: result="*.class";
		break;
		case HTML:   result="*.html";
		break;
		case PROPERTY:  result="{(*.txt,*.xml,*.properties,*.property}";
		break;
		default : result ="";
		} 
		return result;
	}

}
