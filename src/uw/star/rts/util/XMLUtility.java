package uw.star.rts.util;

import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.nio.file.*;
import java.util.*;

public class XMLUtility {
	
	static Logger log;
	public XMLUtility(){
		log = LoggerFactory.getLogger(XMLUtility.class.getName());
		
	}
	
	public static Document DocumentFactory(Path xmlfile){
		//get document object of the xml file
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		Document document =null;
		try{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			document = builder.parse(xmlfile.toFile());	
		}catch(ParserConfigurationException e){
			log.error("DOM parser configuration exception");
			e.printStackTrace();
		}catch(SAXException e){
			log.error("DOM parse error");
			e.printStackTrace();
		}catch(IOException e){
			log.error("IO exception");
			e.printStackTrace();
		}
		return document;
	}
	
	/**
	 * Find all child node(immediate children only) under current node that is an element node and node name is tagName
	 * This is  a combination of getChildNodes() and getElementByTagName().
	 * Also this helper method returns an iterable node list for convinient use in the foreach statement.
	 * Only element node directly under currentNode are returned.(i.e no grandchildren).   
	 * The order of the children are maintained (removing non-element node) 
	 * @param currentNode
	 * @param tagName - case sensitive
	 * @return list of element nodes equals tagname (case sensitive) 
	 */
	public static List<Node>  getChildElementsByTagName(Node currentNode,String tagName){
		List<Node> results = new ArrayList<Node>();
		NodeList childNodes = currentNode.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++){
			Node child = childNodes.item(i);
			if((child.getNodeType() == Node.ELEMENT_NODE)&& child.getNodeName().equals(tagName))
				results.add(child);
		}
		return results;
	}

}
