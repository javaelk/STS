package uw.star.rts.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.*;

import uw.star.rts.extraction.SIRJavaFactory;

public class JavaFileParser {

	static Logger log = LoggerFactory.getLogger(SIRJavaFactory.class.getName());

	/**
	 * parse java file and get package name 
	 * @param fileName - absolute path to the java file
	 * @return package name of the given java file
	 */
	public static String getJavaPackageName(String fileName){
		Path javafile = Paths.get(fileName);
		//read file 
		if(!Files.exists(javafile)){
			log.error(fileName + " does not exist" );
			throw new IllegalArgumentException();
		}
		if(Files.isDirectory(javafile)){
			log.error(fileName + " is a directory" );
			throw new IllegalArgumentException();
		}


		Charset cs = Charset.forName("latin1");
		try(BufferedReader reader = Files.newBufferedReader(javafile, cs)){
			String line =null;
			Pattern pattern1 = Pattern.compile("^package(.*);.*");
			Pattern pattern2 = Pattern.compile("^*/package(.*);.*");
			while((line = reader.readLine())!=null){
				Matcher m1 = pattern1.matcher(line);
				if(m1.find())
					return m1.group(1).trim();
				Matcher m2 = pattern2.matcher(line);
				if(m2.find())
					return m2.group(1).trim();
				
			}
			log.error("package name not found in file " + fileName);
		}catch(IOException e){
			log.error("IO exception in reading file " + fileName);
			e.printStackTrace();
		}
		return null;
	}
}
