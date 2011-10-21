package uw.star.sts.artifact;

import java.util.*;
public class ClassEntity extends CodeEntity {
	List<String> classes;
	
	public ClassEntity(List<String> classes){
		classes = this.classes;
	}
	@Override
	public int count(){
		return classes.size();
	}
	@Override
	public String toString(){
		return classes.toString();
	}
}
