package uw.star.rts.util;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import java.util.*;
import java.io.*;

/*
 * Find all matching files
 */
public class FileFinder extends SimpleFileVisitor<Path> {
	
	/**
	 * @uml.property  name="matcher"
	 */
	private final PathMatcher matcher;
	/**
	 * @uml.property  name="matchedFiles"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.nio.file.Path"
	 */
	private List<Path> matchedFiles;
	
	FileFinder(String pattern){
		matcher = FileSystems.getDefault().getPathMatcher("glob:"+pattern);
		matchedFiles = new ArrayList();
	}
	
	@Override 
	public FileVisitResult visitFile(Path file,BasicFileAttributes attrs){
		Path name = file.getFileName();
		if(name != null&&matcher.matches(name))
			matchedFiles.add(file);
		return CONTINUE;
	}
	@Override
	public FileVisitResult visitFileFailed(Path file,IOException exc){
		System.err.println(exc);
		return CONTINUE;
	}
	
	public List<Path> matchedFiles(){
		return matchedFiles;
	}
}
