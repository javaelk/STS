package uw.star.rts.goal;

import java.util.*;
import org.slf4j.*;

/*
 * @author Weining Liu
 * @version 2011-12-16
 * This is a domain object represent a Software Test Selection goal in the goal model tree.
 */
public class STSGoal {
	/**
	 * @uml.property  name="name"
	 */
	private String name; //name is the unique identifier
	/**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  inverse="offspring:uw.star.sts.goal.STSGoal"
	 */
	private STSGoal parent;
	/**
	 * @uml.property  name="priority"
	 */
	private int priority; //optional property
	/**
	 * @uml.property  name="offspring"
	 * @uml.associationEnd  multiplicity="(0 -1)" inverse="parent:uw.star.sts.goal.STSGoal"
	 */
	private List<STSGoal> offspring;
   	/**
	 * @uml.property  name="contributionType"
	 * @uml.associationEnd  
	 */
   	private ContributionType contributionType;
    
    static String CONTRIBUTIONTYPE="contribution_type";
    static String NAME="name";
    static Logger log= LoggerFactory.getLogger(STSGoal.class.getName());
    
    STSGoal(){
    	offspring = new ArrayList();
    }
    
    /**
	 * @return
	 * @uml.property  name="parent"
	 */
    public STSGoal getParent(){
    	return parent;
    }
    
    public List<STSGoal> getOffspings(){
    	return offspring;
    }
    
    /**
	 * @return
	 * @uml.property  name="name"
	 */
    public String getName(){
    	return name;
    }
    
    /**
	 * @return
	 * @uml.property  name="contributionType"
	 */
    public ContributionType getContributionType(){
    	return contributionType;
    }
    
    public boolean isLeafGoal(){
    	return offspring.isEmpty();
    }
    
    /**
	 * @return
	 * @uml.property  name="priority"
	 */
    public int getPriority(){
    	return priority;
    }
    
    /**
	 * @param s
	 * @uml.property  name="name"
	 */
    public void setName(String s){
    	name=s;
    }
    
    public void setContributionType(String t){
    	switch(t){
    	 case "OR" : contributionType=ContributionType.OR;break;
    	 case "AND": contributionType=ContributionType.AND;break;
    	 default: log.error("Unknown contribution type " + t);
    	}
    }
    
    /**
	 * @param p
	 * @uml.property  name="priority"
	 */
    public void setPriority(int p){
    	priority = p;
    }

    /**
	 * @param p
	 * @uml.property  name="parent"
	 */
    public void setParent(STSGoal p){
    	parent =p;
    }
    
    public void setOffsprings(List o){
    	offspring =o;
    }
    
    public boolean hasOffsprings(){
    	return !offspring.isEmpty();
    }
    
    /**
     * 
     * @return total number of goals of STSGoag tree. this goal as root. 
     */
    public int size(){
    	int count =0;
    	char[] c = this.toString().toCharArray();
    	for(int i=0;i<c.length;i++){
    		if (c[i]==',') count++;
    	}
    	return count;
    }
    @Override
    
    /**
     * Trees can be traversed in level-order, where we visit every node on a level before going to a lower level.This is called Breadth-first traversal.
     * 
     * Algorithm (informal)
     * 1. Enqueue the root node.
     * 2. Dequeue a node and print it.
     *    if node has offsprings, enqueue any successors (the direct child nodes) 
     * 3. If the queue is empty, every node on the graph has been printed 
     * 
     * @return a list of goals in level-order
     */
    public String toString(){
    	StringBuffer sbf = new StringBuffer("[");
    	Queue que = new LinkedList();
    	//this goal is the root node
    	que.add(this); //Enqueue the root node
    	while(!que.isEmpty()){
    		STSGoal current = (STSGoal)que.remove();
    		sbf.append(current.getContributionType()+": "+current.getName()+", ");
    		if(current.hasOffsprings()) que.addAll(current.getOffspings());
    	}
    	sbf.append("]");
    	return sbf.toString();
    }
    
}
