package uw.star.rts.goal;

import java.nio.file.Files;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.*;

import uw.star.rts.util.XMLUtility;


/**
 * 
 * @author Weining Liu
 * @version 2011-12-16
 * a factory class to generate goal objects to represent the complete STS goal model 
 */
public class STSGoalFactory {
	
	
	
	static Logger log;
	STSGoalFactory(){
		log=LoggerFactory.getLogger(STSGoalFactory.class.getName());
	}
	
	public static STSGoalFactory newInstance(){
		return new STSGoalFactory();
	}
	
	/**
	 * @param fileName xml file with complete STS goals
	 * @return reference to root STS goal of the complete STS goal model
	 */
	public STSGoal modelingGoals(Path goalFile){
		
		if(!Files.exists(goalFile)||Files.isDirectory(goalFile)){
			log.error("goal xml file "+ goalFile.toAbsolutePath().toString()+ "does not exist!");
			System.exit(-1);                  
		}
		
		/*use XML DOM instead of XMLBEANS for easier tree traversing, some good XML DOM tutorials:
		 * http://www.w3schools.com/dom/default.asp
		 * http://tutorials.jenkov.com/java-xml/dom-document-object.html
		 */
		
		//get document object from an xml file
		Document document =XMLUtility.DocumentFactory(goalFile);
		STSGoal rootGoal = new STSGoal();
		rootGoal.setParent(null);   //root does not have parent

		if(document!=null){
			Element rootNode = document.getDocumentElement(); //get root node 
			addGoal(rootGoal,rootNode);  //recursively construct the goal tree
		}
		return rootGoal;
	}
	
	/**
	 * resursive method to construct all nodes into a STSGoal tree.
	 * @param rootGoal - root goal in STSGoal tree
	 * @param rootNode - root node in XML DOM tree
	 * @return the rootGoal linked to all offsprings
	 */
	private STSGoal addGoal(STSGoal rootGoal,Node rootNode){
		//handle nulls	
	    if(rootGoal==null || rootNode ==null) {
	    	log.error("rootGoal or rootNode is null");
	    	System.exit(-1);
	    }
	    setAttributes(rootGoal,rootNode);
		List<STSGoal> offsprings = new ArrayList<STSGoal>();
		NodeList nodes =rootNode.getChildNodes();
		for(int i=0;i<nodes.getLength();i++){
			Node node = nodes.item(i);
			//for every element child node
			if(node.getNodeType()==Node.ELEMENT_NODE){
				STSGoal nGoal = new STSGoal();
				nGoal.setParent(rootGoal);
				log.debug("make recursive call to add node "+ node + " to goal " + nGoal);
				offsprings.add(addGoal(nGoal,node));
			}
		}
		if(offsprings.size()!=0) {
			log.debug("add offsprings " + offsprings + " to root goal " + rootGoal);
			rootGoal.setOffsprings(offsprings); //add only if there is an offspring exist
		}
		log.debug(" return goal" + rootGoal);
		return rootGoal;
	}
	
	/**
	 * helper method to add name and contributes attributes of a node to a goal 
	 * @param goal
	 * @param node
	 */
	private void setAttributes(STSGoal goal, Node node){
		//get attributes of this element
		String cName = ((Element)node).getAttribute(STSGoal.NAME);
		goal.setName(cName);
		String cType= ((Element)node).getAttribute(STSGoal.CONTRIBUTIONTYPE);
		goal.setContributionType(cType);
		log.debug("add contribution type " + cType + " and name " + cName+ " to goal " + goal);
		return;
	}
}

