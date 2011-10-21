package uw.star.sts.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {
	
	/*
	   * Helper method to find all directories under current directory. Files in the current directory will not be returned.
	   * e.g list all directories under version.alt
	   * @param dirPath directory to list
	   * @return a list of path 
	   */
	  
	  public static List<Path>  listDirectory(Path dirPath){
	  	    List<Path> result = new ArrayList<Path>(); 
	        if (!Files.isDirectory(dirPath)) return result;  //return right away if dirPath is not a directory
	  		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)){
				for(Path file: stream)
					if(Files.isDirectory(file)) result.add(file);

			} catch(IOException x){
				System.err.println(x);
			}
			return result;
	  }
	  
	  
		/*
	   * Helper method to find all files under current directory. Directories in the current directory will not be returned.
	   * e.g list all files under application
	   * @param dirPath directory to list
	   * @return a list of path 
	   */
	  
	  public static List<Path>  listFiles(Path dirPath){
	  	    List<Path> result = new ArrayList<Path>(); 
	        if (!Files.isDirectory(dirPath)) return result;  //return right away if dirPath is not a directory
	  		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)){
				for(Path file: stream)
					if(!Files.isDirectory(file)) result.add(file);

			} catch(IOException x){
				System.err.println(x);
			}
			return result;
	  }
	  /*
	   * Helper method to list all directories in specific directory that matches a pattern
	   */
	  
	  public static List<Path> listDirectory(Path dirPath,String pattern){
		  List<Path> res = new ArrayList();
		  if(!Files.isDirectory(dirPath)) return res;

		  try(DirectoryStream<Path> stream=Files.newDirectoryStream(dirPath, pattern)){
			for(Path file:stream)
				if(Files.isDirectory(file)) res.add(file);
		  }catch(IOException x){
				System.err.println(x);
		  }
		  return res;
		  
	  }
	  
	  
	  /*
	   * Helper method to list all files in specific directory that matches a pattern
	   */
	  
	  public static List<Path> listFiles(Path dirPath,String pattern){
		  List<Path> res = new ArrayList();
		  if(!Files.isDirectory(dirPath)) return res;

		  try(DirectoryStream<Path> stream=Files.newDirectoryStream(dirPath, pattern)){
			for(Path file:stream)
				if(!Files.isDirectory(file)) res.add(file);
		  }catch(IOException x){
				System.err.println(x);
		  }
		  return res;
		  
	  }
	  
	  /**
	   * Traverse starting directory and all subdirectories to find all files that matches the pattern
	   * This method will only return files, will not return directories
	   * @param startingDir
	   * @param pattern
	   * @return
	   */
	  public static List<Path> findFiles(Path startingDir, String pattern){
		  FileFinder finder = new FileFinder(pattern);
		  try{
		  Files.walkFileTree(startingDir, finder);
		  }catch(IOException e){
			  System.err.println(e);
		  }
		  return finder.matchedFiles();
	  }
	  
	  /**
	   * Traverse starting directory and all subdirectories to find all directories that matches the pattern
	   * This method will only return directory, will not return files
	   * @param startingDir
	   * @param pattern
	   * @return
	   */
	  public static List<Path> findDirs(Path startingDir,String pattern){
		  DirFinder finder = new DirFinder(pattern);
		  try{
			  Files.walkFileTree(startingDir, finder);
		  }catch(IOException e){
			  System.err.println(e);
		  }
		  return finder.matchedDirs();
	  }
}
