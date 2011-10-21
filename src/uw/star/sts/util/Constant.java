package uw.star.sts.util;

/**
 * This class holds all the constant in artifact package
 * @author WEINING LIU
 *
 */
public class Constant {
	public static int VARIANT_ORIG=0;
	public static int VARIANT_SEEDED=1;
	public static int VARIANT_ALTERNATIVE=2; //this is semantically equivalent to original source code. This variant is created to accommodate some prototype analysis tools that is not robust enough to process a particular syntactic construct
	public static String SIR_JAVA="SIR_JAVA";
}
