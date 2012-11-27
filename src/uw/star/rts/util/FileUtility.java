package uw.star.rts.util;

import java.io.IOException;
import java.util.*;
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
	  
	  
	  /**
	   * Overload method to return a sorted list of directories under current directory
	   * @param dirPath
	   * @param cp
	   * @return
	   */
	  
	  public static List<Path>  listDirectory(Path dirPath,Comparator<Path> cp){
		  return sort(listDirectory(dirPath),cp);
	  }
	  
	  
	  /**
	   *sort a list of Path with given Comparator
	   * @param dirPath - a list of path - could be either file or directory
	   * @param cp
	   * @return a sorted list of path
	   */
	  
	  static List<Path>  sort(List<Path> pathList,Comparator<Path> cp){
		Path[] pathArray = pathList.toArray(new Path[pathList.size()]);
		Arrays.sort(pathArray, cp);
		return Arrays.asList(pathArray);
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
	  
	  public static List<Path>  listFiles(Path dirPath,Comparator<Path> cp){
		  return sort(listFiles(dirPath),cp);
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
	  public static List<Path> listDirectory(Path dirPath,String pattern,Comparator<Path> cp){
		  return sort(listDirectory(dirPath,pattern),cp);
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
	  
	  public static List<Path> listFiles(Path dirPath,String pattern,Comparator<Path> cp){
		  return sort(listFiles(dirPath,pattern),cp);
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
	  
	  public static List<Path> findFiles(Path startingDir, String pattern,Comparator<Path> cp){
		  return sort(findFiles(startingDir,pattern),cp);
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
	  
	  public static List<Path> findDirs(Path startingDir,String pattern,Comparator<Path> cp){
		  return sort(findDirs(startingDir,pattern),cp);
	  }
	  /**
	   * Find a path closest to base
	   * @param base
	   * @param others
	   * @return
	   */
	  public static Path findShortestDistance(Path base, List<Path> others){
		  if(others.size()==0) return null;
		  if(others.size()==1) return others.get(0);
		  int distance[] = new int[others.size()]; //track relative distance of each path in others
		  for(int i=0;i<others.size();i++)
			  distance[i]= others.get(i).relativize(base).getNameCount();
		  //find the smallest value's index
		  int idx =0;
		  int min=distance[0];
		  for(int j=1;j<distance.length;j++){
			  if(distance[j]<min){
				  min=distance[j];
				  idx =j;
			  }
		  }
		  return others.get(idx);
	  }
  
}
