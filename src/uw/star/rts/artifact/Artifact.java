package uw.star.rts.artifact;


public abstract class Artifact {
	

	/**
	 * @uml.property  name="versionNo"
	 */
	int versionNo;
	/**
	 * @uml.property  name="applicationName"
	 */
	String applicationName;
	
	Artifact(String appName,int v){
		applicationName = appName;
		versionNo = v;
	}
	
	/**
	 * @return
	 * @uml.property  name="applicationName"
	 */
	public String getApplicationName(){
		return applicationName;
	}
	
	/**
	 * @return
	 * @uml.property  name="versionNo"
	 */
	public int getVersionNo(){
		return versionNo;
	}
	
	public abstract String getName();
	
	public String toString(){
	 StringBuffer buf = new StringBuffer();
	 buf.append("applicationName " + applicationName + "\n");
	 buf.append("Version NO: " + versionNo + "\n");
	 return buf.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((applicationName == null) ? 0 : applicationName.hashCode());
		result = prime * result + versionNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artifact other = (Artifact) obj;
		if (applicationName == null) {
			if (other.applicationName != null)
				return false;
		} else if (!applicationName.equals(other.applicationName))
			return false;
		if (versionNo != other.versionNo)
			return false;
		return true;
	}

}
