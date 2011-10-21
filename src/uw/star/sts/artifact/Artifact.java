package uw.star.sts.artifact;


public abstract class Artifact {
	String repository;
	int versionNo;
	String applicationName;
	
	Artifact(String rep, String appName,int v){
		repository = rep;
		applicationName = appName;
		versionNo = v;
	}
	
	public String getApplicationName(){
		return applicationName;
	}
	
	public int getVersionNo(){
		return versionNo;
	}
	
	
	public String getRepository(){
		return repository;
	}
	
	public String toString(){
	 StringBuffer buf = new StringBuffer();
	 buf.append("Repository: " + repository + "\n");
	 buf.append("applicationName " + applicationName + "\n");
	 buf.append("Version NO: " + versionNo + "\n");
	 return buf.toString();
	}
}
