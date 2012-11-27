package uw.star.rts.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {

	
	/**
	 * Get a property by name
	 * @param properties
	 */
	public static String getPropertyByName(String propertyFilePath,String propertyName){
		Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(propertyFilePath));	
		}catch(IOException e){
			e.printStackTrace();
			//log.error("error in parsing configuration property file : " + propertyFilePath);
		}
		return properties.getProperty(propertyName);
	}
}
