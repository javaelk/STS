package uw.star.rts.artifact;


public class MethodEntity extends Entity {
    
	/**
	 * @uml.property  name="className"
	 */
	String className;
	/**
	 * @uml.property  name="packageName"
	 */
	String packageName;
	ClassEntity clazz;
	
	/**
	 * @uml.property  name="methodSignature"
	 */
	String methodSignature;
	


	Program program;
	
    public MethodEntity(Program p, String packageName,String className,String methodSignature){
    	super(p.getApplicationName(),p.getVersionNo());
		this.className = className;
		this.packageName = packageName;
		this.methodSignature = methodSignature;
		this.program=p;
	}
    /*
     * Link a method to its class
     */
    public boolean setClassEntity(ClassEntity ce){
    	clazz = ce;
    	return true;
    }
    
    public ClassEntity getClassEntity(){
    	return clazz;
    }
    
    public EntityType getEntityType(){
    	return EntityType.METHOD;
    }
    
    @Override
    public boolean equals(Object o){
    	if(o instanceof MethodEntity){
    		MethodEntity me = (MethodEntity)o;
    		return (this.program==me.program)&&this.packageName.equals(me.packageName)&&this.className.equals(me.className)&&
    				this.methodSignature.equals(me.methodSignature);
    	}else{
    		return false;
    	}
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((methodSignature == null) ? 0 : methodSignature.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((program == null) ? 0 : program.getName().hashCode());
		return result;
	}

    public String getName(){
    	return this.toString();
    }
    
	@Override
	public String toString() {
		return packageName + "." + className +"." + methodSignature; 
	}

}
