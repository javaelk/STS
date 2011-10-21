package uw.star.sts.artifact;
import java.util.*;

public class MethodEntity extends CodeEntity {
    List<String> methods;

    public MethodEntity(List<String> methods){
		methods = this.methods;
	}
	@Override
	public int count() {
	  return methods.size();
	}

	@Override
	public String toString() {
		return methods.toString();
	}

}
