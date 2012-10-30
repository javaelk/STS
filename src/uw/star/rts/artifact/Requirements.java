package uw.star.rts.artifact;

import java.util.*;
import java.nio.file.*;
public class Requirements extends Specification{
  /**
 * @uml.property  name="requirments"
 */
List<Requirement> requirments;
  Requirements(String appName, int ver,Path requirementFile){
	  super(appName,ver,requirementFile);
  }
}
