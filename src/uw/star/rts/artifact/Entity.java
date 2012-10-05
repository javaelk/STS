package uw.star.rts.artifact;

/**
 * represent an entity in program P, entity might include statement,branches,functions of definition-use assocations
 * @author Weining Liu
 *
 */
public abstract class Entity extends Artifact {
	
	
	Entity(String appName,int ver){
		super(appName,ver);
	}
	//subclasses must implement equals explicitly
	public abstract boolean equals(Object o);
	
	public abstract EntityType getEntityType();
	
}
