package uw.star.rts.artifact;

/**
 * This is equivalent to emma report.metrics property as all coverage report are collected by Emma, an entity type not supported by
 * Emma won't be useful to perform coverage analysis.   
 * @author Weining Liu
 *
 */
public enum EntityType {
	SOURCE,CLAZZ,METHOD,STATEMENT
	//,BLOCK,LINE
}
