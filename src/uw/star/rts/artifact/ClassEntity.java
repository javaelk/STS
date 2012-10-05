package uw.star.rts.artifact;
import java.util.*;

/**
 * this class represents compiled java class 
 * @author wliu
 *
 */
public class ClassEntity extends Entity{
	/**
	 * @uml.property  name="packageName"
	 */
	String packageName;
	/**
	 * @uml.property  name="className"
	 */
	String className;
	
	Program p;
	SourceFileEntity source;
	List<MethodEntity> methods;
	
	//class entity represent a class file(complied from source file)
	public ClassEntity(Program p, String packageName, String className){
		super(p.getApplicationName(),p.getVersionNo());
		this.packageName =packageName;
		this.className = className;
		this.p =p;
	}
    
	public boolean setSource(SourceFileEntity se){
		source = se;
		return true;
	}
	
	public SourceFileEntity getSource(){
		return source;
		
	}
	public boolean setMethods(List<MethodEntity> methods){
		this.methods = methods;
		return true;
	}
	
	public List<MethodEntity> getMethods(){
		return this.methods;
	}

	
	@Override
	public String toString(){
		return packageName+ "."+ className;
	}
	
	public String getName(){
		return this.toString();
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public String getClassName(){
		return className;
	}
	
	public Program getProgram(){
		return p;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((p == null) ? 0 : p.getName().hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override 
    public boolean equals(Object o){
		if(this==o) return true;
		if(o instanceof ClassEntity){//if o is null, this would be false 
			ClassEntity ce = (ClassEntity)o;
			return (this.packageName.equals(ce.getPackageName())&&this.className.equals(ce.getClassName())&&this.p==ce.getProgram());
		}else{
			return false;
		}
	}
	
	public EntityType getEntityType(){
		return EntityType.CLAZZ;
	}
}
