package uw.star.rts.util;

import java.util.Comparator;
import java.nio.file.*;

public class VersionDirectoryComparator implements Comparator<Path> {
        
	private String verPrefix;
	public VersionDirectoryComparator(String versionPrefix){
		verPrefix = versionPrefix;
	}
	
	public int compare(Path o1, Path o2){
		String s1 = o1.getFileName().toString();
		String s2 = o2.getFileName().toString();
		int n1=0,n2=0;
		if(s1.startsWith(verPrefix)&&s2.startsWith(verPrefix)){
			n1 = Integer.parseInt(s1.substring(verPrefix.length()));
			n2 = Integer.parseInt(s2.substring(verPrefix.length()));
			
		}else{
			throw new IllegalArgumentException("either "+ s1 + " or " + s2 + " is not started with correct version director prefix - " + verPrefix);
		}
		
		if(n1==n2) return 0;
		return (n1>n2)?1:-1;
	}
}
