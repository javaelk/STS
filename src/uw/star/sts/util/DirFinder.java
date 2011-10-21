package uw.star.sts.util;

import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import java.util.*;
import java.io.*;

/*
 * Find all matching directories
 * 
 */

public class DirFinder extends SimpleFileVisitor<Path>{
		
		private final PathMatcher matcher;
		private List<Path> matchedDirs;
		
		DirFinder(String pattern){
			matcher = FileSystems.getDefault().getPathMatcher("glob:"+pattern);
			matchedDirs = new ArrayList();
		}
		
		@Override 
		public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs){
			Path name = dir.getFileName();
			if(name != null&&matcher.matches(name))
				matchedDirs.add(dir);
			return CONTINUE;
		}
		
		@Override
		public FileVisitResult visitFileFailed(Path file,IOException exc){
			System.err.println(exc);
			return CONTINUE;
		}
		
		public List<Path> matchedDirs(){
			return matchedDirs;
		}
	}


