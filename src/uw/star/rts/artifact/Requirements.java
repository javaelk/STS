package uw.star.rts.artifact;

import java.util.*;

public class Requirements extends Specification{
  /**
 * @uml.property  name="requirments"
 */
List<Requirement> requirments;
  Requirements(String appName, int ver){
	  super(appName,ver);
  }
}
