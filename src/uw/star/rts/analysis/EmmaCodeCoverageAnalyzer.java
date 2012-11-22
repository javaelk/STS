package uw.star.rts.analysis;
import java.util.*;
import java.util.regex.*;
import org.w3c.tidy.Tidy; 
import java.io.*;
import java.lang.Integer;
import org.slf4j.*;
import org.w3c.dom.*;
import java.nio.file.*;
import uw.star.rts.artifact.*;
import uw.star.rts.artifact.Entity;
import uw.star.rts.extraction.ArtifactFactory;
import uw.star.rts.util.*;


/**
 * An EMMA code coverage analyzer can 1)extract entites from Emma reports or
 * 2)extract coverage information &create trace object between entities in a program and a test suite 
 * @author Weining Liu
 *
 */
public class EmmaCodeCoverageAnalyzer extends CodeCoverageAnalyzer {

	/**
	 * @uml.property  name="p"
	 * @uml.associationEnd  readOnly="true"
	 */
	ArtifactFactory af;
	Application testapp;
	Program program;
    TestSuite testSuite;
	/**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Logger log;
	
	//containers for each entity types, these are populated by parseXML
	Set <SourceFileEntity> srcEntities;
	Set <ClassEntity> classEntities;
	Set <MethodEntity> methodEntities;

	//values of the following should be changed for each test case, i.e. class/method/statement coverage are different for each test case
	//coverage only on class and method, not on source file, these are populated by parseXML
	Set <ClassEntity> coveredClassEntities;
	Set <MethodEntity> coveredMethodEntities;
	
	//this is populated by parseHTML
	Map<SourceFileEntity,List<StatementEntity>> coveredStatementsOfSourceFile;
	
	static int totalExecutableStms;
	static int totalCoveredStms;

	//Special char &#160 - ASCII HEX A0
	private static final Pattern nonASCII = Pattern.compile("\\xa0");
	
	/**
	 * constructor to provide all information needed
	 * @param p
	 * @param resultXMLfile
	 */
	public EmmaCodeCoverageAnalyzer(ArtifactFactory af, Application testapp, Program p,TestSuite testSuite){
		log = LoggerFactory.getLogger(EmmaCodeCoverageAnalyzer.class.getName());
		
		this.af = af;
		this.testapp = testapp;
		this.program =p;
		this.testSuite= testSuite;
		
		srcEntities = new HashSet<>();
		classEntities = new HashSet<>();
		methodEntities = new HashSet<>();
 	}
	
	/**
	 *    Entity is considered as covered if value >0%
	 *    Example of covered method in Emma xml report
	 *    <method name="&lt;static initializer&gt;">
            <coverage type="method, %" value="100% (1/1)"/>
            <coverage type="block, %" value="100% (6/6)"/>
          </method>
	 
	 * Let Ec denote the set of covered entities
	 * Ec = { e in E | (exist t in T)(t covers e)} 
	 * For every e in E, there must exist a t that executing t on P causes entity e 
	 * to be exercised at least once
	 * 
	 *  
	 * This method should be called immediately after parsexml or parsehtml , as coveredentities are different for each test case(i.e. each xml or html file)
	 * @param testcase t
	 * @return list of entities exercised by executing t on P
	 */
	List<? extends Entity> extractCoveredEntities(EntityType type){
		switch(type){
		case CLAZZ : return new ArrayList<ClassEntity>(coveredClassEntities);
	
		case METHOD : return new ArrayList<MethodEntity>(coveredMethodEntities);
	
		case STATEMENT: 
			Set<StatementEntity> result = new HashSet<>();
			for(SourceFileEntity sfe: srcEntities)
							result.addAll(coveredStatementsOfSourceFile.get(sfe));
			return new ArrayList<StatementEntity>(result);
	
		case SOURCE: 
			return new ArrayList<SourceFileEntity>(rollUpToSrcEntity(coveredClassEntities));
	
		default : 
			log.error("unknown enum value found" + type); //this should be deadcode
		}
		return null;

	}
	
	/**
	 * Parse test case name from the result file name
	 * coverage.testcasename.xml
	 * @return
	 */
	String extractTestCaseName(Path xmlfile){
		//log.debug("file name is :"+ xmlfile.getFileName().toString());
		String filename = xmlfile.getFileName().toString();
		String tcName = filename.substring(filename.indexOf(".")+1,filename.lastIndexOf("."));
		//log.debug("test case name is: "+ tcName);
		return tcName;
	}

	/**
	 * extract code entities of given type and link back to the program, src entities are set in any cases.
	 * TODO: use other ways to extract all entities. emma result xml and html files do not contain interfaces. Consider JaCoCo.
	 * Ideally, the program artifact should contain ALL classes regardless it's interface or not. 
	 * @param type
	 * @return
	 */
	public List<? extends Entity> extractEntities(EntityType type){
		List<? extends Entity> result = null;
		List<TestCase> testcases =testSuite.getTestCaseByVersion(program.getVersionNo());
		if(testcases.size()==0){
			log.error("test case set should not be zero");
		}else{
			TestCase t0 =testcases.get(0);
			log.debug("parse xml result of test case :" + t0);
			//parse any xml result file would have the same entities
			parseXML(af.getEmmaCodeCoverageResultFile(program,t0,"xml"));
			switch(type){
			case CLAZZ : 
				result= new ArrayList<ClassEntity>(classEntities);
			break;
			
			case METHOD : 
				result= new ArrayList<MethodEntity>(methodEntities);
			break;
			
			case SOURCE : 
				result =  new ArrayList<SourceFileEntity>(srcEntities);
			break;
			
			case STATEMENT: 
				//for statement , need to all parseHTML files in addition to xml, each html file only contains statements for one source
				Set<StatementEntity> stm = new HashSet<>();
				coveredStatementsOfSourceFile = new HashMap<>();
				Path htmldir =af.getEmmaCodeCoverageResultFile(program,t0,"html"); 
		        for(Path htmlfile: FileUtility.findFiles(htmldir, "*.html")){
		        	log.debug("parse html file " + htmlfile);
		        	parseHTML(htmlfile); 
		        } 
				for(SourceFileEntity sfe : srcEntities)
					stm.addAll(sfe.getExecutableStatements());
				result = new ArrayList<StatementEntity>(stm);
			break;
			
			default : 
				log.error("unknown enum value found" + type);
			}
			
			//always link source entities to program as all other types are linked to source
			if(!type.equals(EntityType.SOURCE)) 
				program.setCodeEntities(EntityType.SOURCE, new ArrayList<SourceFileEntity>(srcEntities));
			program.setCodeEntities(type,result);
		}
		return result;
	}
     
	
	/**
	 * An Emma XML report contains coverage information of all sources, classes and methods of all package
	 * One XML report for each test case
	 * From each XML file, parse all srcfile,class and methods. this method will populate all srcfile,class and methods entities and populate all
	 * covered class and methods entities. src <-> *class <->*method
	 */

	void parseXML(Path xmlfile){
		//@Refactoring: use a set instead of list in order maintain uniqueness in the list of entities 
		//(i.e. should not return duplicate class entity or duplicate method entities
		//@Refactor again: extract all entities with one pass. it's easier to link them together that way. This way would improve the performance
		//as the xml and html files are only parsed once, but it does take extra memory when method/statement coverages are not required
        log.info("Parsing xmlfile " + xmlfile);
		
        //coverage only on class and method, not on source file
		coveredClassEntities = new HashSet<>();
		coveredMethodEntities = new HashSet<>();	
		if(!Files.exists(xmlfile))
			throw new IllegalArgumentException("file does not exist" + xmlfile);
		
		Document emmaReport = XMLUtility.DocumentFactory(xmlfile);

		//iterate each package
		NodeList packageNodes = emmaReport.getElementsByTagName("package");
		for(int i=0;i<packageNodes.getLength();i++){
			Node packageNode = packageNodes.item(i); 
			if(packageNode.getNodeType() == Node.ELEMENT_NODE){
				String packageName = ((Element)packageNode).getAttribute("name");

				//get source file node first, this link source,class and method together.
				// linkages : source <--> *class <-> *method    
				for(Node srcfileNode: XMLUtility.getChildElementsByTagName(packageNode, "srcfile")){
					String srcfileName = ((Element)srcfileNode).getAttribute("name");
					//TODO: test this
					Path srcfilePath = program.getCodeFilebyName(CodeKind.SOURCE, packageName, srcfileName);
					SourceFileEntity srcEnt = new SourceFileEntity(program,packageName,srcfileName,srcfilePath);
					srcEntities.add(srcEnt); //add a source file

					List <ClassEntity> classesOfCurrentSourceFile = new ArrayList<>();  //this holds all classes of current source file to build linkage
					//get all class nodes under this srcfile node
					for(Node classNode: XMLUtility.getChildElementsByTagName(srcfileNode, "class")){
 						String className = ((Element)classNode).getAttribute("name");
						ClassEntity classEnt = new ClassEntity(program,packageName,className,program.getCodeFilebyName(CodeKind.BINARY, packageName, className));
						classEnt.setSource(srcEnt);  // class -> source
						classEntities.add(classEnt); //add a class
						classesOfCurrentSourceFile.add(classEnt);
						if(extractCoverageValue(classNode,"class, %")>0){
						    log.debug("adding class : " + className + " to covered set");
							coveredClassEntities.add(classEnt);  //add to covered set if covered
						}else{
							log.debug("class : " + className + " is NOT covered");
						}
						

						List <MethodEntity> methodsOfCurrentClass = new ArrayList<>();  //this holds all methods of current class to build linkage
						//get all methods under this class node
						for(Node methodNode: XMLUtility.getChildElementsByTagName(classNode, "method")){
							String methodName = ((Element)methodNode).getAttribute("name");
							MethodEntity methodEnt = new MethodEntity(program,packageName,className,methodName,srcfilePath);
                            methodEnt.setClassEntity(classEnt);  //method ->class 
							methodEntities.add(methodEnt); //add a method
							methodsOfCurrentClass.add(methodEnt);
							if(extractCoverageValue(methodNode,"method, %")>0)
								coveredMethodEntities.add(methodEnt);  //add to covered set if covered
						}//end of methodnode loop
						classEnt.setMethods(methodsOfCurrentClass); //class ->method
					}//end of class loop
					srcEnt.setClasses(classesOfCurrentSourceFile);  //src ->class
				}//end of source loop
			}
		}
	}
	

	/**
	 * find coverage node directly under current node and parse coverage value of type given
	 * @param current node
	 * @param type - class,method or block
	 * @return integer value of the coverage, -1 if coverage value of the type not found
	 */
	int extractCoverageValue(Node currentNode,String type){
		for(Node coverageNode: XMLUtility.getChildElementsByTagName(currentNode, "coverage")){
			if(((Element)coverageNode).getAttribute("type").equals(type)){
				String coverageValue = ((Element)coverageNode).getAttribute("value");
				int value = Integer.parseInt(coverageValue.substring(0, coverageValue.indexOf("%")));
				return value;
			}
		}
		return -1;
	}
	
	/**
	 * For each version of the program, construct a trace matrix by going through all coverage result files of all test cases.
	 * 
	 * A trace matrix of a particular type (class/method) is constructed by 
	 *   1) extract all test cases of the version(same version as p) as the row of the matrix 
	 *   2) extract all class/method entities of the version as the column
	 *   3) for each coverage file, extract Covered Entities
	 *   4) insert covered entities into the Trace matrix 
	 * @param app
	 * @return a trace between entities of specified type and all test cases in the test suite
	 */
    @Override
	public <E extends Entity> CodeCoverage<E> createCodeCoverage(EntityType type){
		List<TestCase> testcases = testSuite.getTestCaseByVersion(program.getVersionNo());
		
		List<E> entities = new ArrayList<>();
		for(Entity e:program.getCodeEntities(type)) 
		                     entities.add((E)e);
		
		Path codeCoverageResultFolder = null;
		CodeCoverage<E> coverage = new CodeCoverage<E>(testcases,entities,codeCoverageResultFolder); 
		for(TestCase tc: testcases){ //set link for every test case
			Path coverageResultFile =af.getEmmaCodeCoverageResultFile(program,tc,"xml");
			if(codeCoverageResultFolder==null) codeCoverageResultFolder=coverageResultFile.getParent();
			switch(type){
			case SOURCE:
				//code coverage of source file is computed from coverage of class
				parseXML(coverageResultFile);
				break;
			case CLAZZ:
				parseXML(coverageResultFile);
				break;
			case METHOD:
				parseXML(coverageResultFile);
				break;
			case STATEMENT:
				Path htmldir =af.getEmmaCodeCoverageResultFile(program,tc,"html");
				coveredStatementsOfSourceFile = new HashMap<>();
		        for(Path htmlfile: FileUtility.findFiles(htmldir, "*.html")){
		        	log.debug("parse html file " + htmlfile);
		        	parseHTML(htmlfile); 
		        }
		        break;
			}
			List<E> coveredEntites = new ArrayList<>();
			for(Entity e:extractCoveredEntities(type) )
				coveredEntites.add((E)e);
			coverage.setLink(tc,coveredEntites);
		}
		coverage.setArtifactFile(codeCoverageResultFolder);
		return coverage;
	}
	
	/**
	 * Roll up class coverage to source coverage. A source file is covered by a test case if the test case
	 * covers at least one class in the source
	 * @param coverage , a code coverage trace of test case -> source file
	 */
	CodeCoverage<SourceFileEntity> createSourceCodeCoverage(CodeCoverage<ClassEntity> clazzCoverage){
		//TODO: java generics problems again!
		List<SourceFileEntity> allSrc = new ArrayList<>();
		for(Entity e: program.getCodeEntities(EntityType.SOURCE))
			allSrc.add((SourceFileEntity)e);
		CodeCoverage<SourceFileEntity> srcCoverage = new CodeCoverage(testSuite.getTestCases(),allSrc,clazzCoverage.getArtifactFile());
		
		//create a new matrix based on given clazzCoverageMatrix
		int[][] clazzCoverageMatrix = clazzCoverage.getLinkMatrix();
		List<ClassEntity> allClazz = clazzCoverage.getColumns();
		List<TestCase> allTestCases = clazzCoverage.getRows();
		allSrc = srcCoverage.getColumns(); // get all src back from CodeCoverage in case order has changed
		
		/*
		 * Performance consideration: assume clazzCoverageMatrix is a sparse matrix (i.e. not many classes are covered)
		 * it's probably better to loop through the clazzmatrix instead of SourceCoverageMatrix. Either case, every elements
		 * in the matrix has to be examed ,but there is less look up time in  allSrc as only covered Src need to be looked up
		 * in the column list
		 * 
		 */
		
		for(int row=0;row<allTestCases.size();row++){
			for(int col=0;col<allClazz.size();col++){
				if(clazzCoverageMatrix[row][col]>0){//class is covered
					SourceFileEntity sfe = allClazz.get(col).getSource();
					int srcCol = allSrc.indexOf(sfe);  //lookup cost
					srcCoverage.setLink(row, srcCol);
				}
			}
		}
		return srcCoverage;
	}
	/**
	 * Helper method to roll up a set of covered class entity to a set of covered source file entity
	 */
	Set<SourceFileEntity> rollUpToSrcEntity(Set<ClassEntity> clazzSet){
		Set<SourceFileEntity> srcSet = new HashSet<>();
		for(ClassEntity clazz: clazzSet)
			srcSet.add(clazz.getSource());
		return srcSet;
	}
	
	
	
	
	/**    
	 * Extrat executable statements from one html report and build source <->* statement linkage
	 * This method returns a list of covered statments, a list of partly covered statements and a list of not covered statements
	 * In Emma HTML report, source lines containing executable code get the following color code:
	 * green for fully covered lines,
	 * yellow for partly covered lines (some instructions or branches missed) and
	 * red for lines that have not been executed at all.
	 * Lines without any executable code have no colour at all.
	 */
 
	void parseHTML(Path htmlfile){
		
		if(!Files.exists(htmlfile))
			throw new IllegalArgumentException("file does not exist" + htmlfile);
		//Use jtidy to convert a html to XML Document object
		Document htmldoc = convertHTMLtoXML(htmlfile);

		//parse xml file to get all executable statements, covered statements and partly covered statements
		//all statement should link to source , and source should link to all statement

		Set<StatementEntity> fullycoveredStm = new HashSet<>();
		Set<StatementEntity> notcoveredStm = new HashSet<>();
		Set<StatementEntity> partlycoveredStm = new HashSet<>();
		
		//specical list to keep statements as their order in the html file
		List<StatementEntity> orderedAllExecutableStm = new ArrayList<>();
		
		//some html report does not contain any source info. -i.e. package only, not useful for our purpose
		if(!containsSourceFileCoverage(htmldoc)){ 
			log.debug("skip " + htmlfile);
			return;
		}
		
		//get SourceFileEntity object
		SourceFileEntity sf = parseSourceFileEntity(htmldoc);
		
		NodeList tableNodes = htmldoc.getElementsByTagName("table");
		for(int i=0;i<tableNodes.getLength();i++){
			Node tableNode = tableNodes.item(i);
			//find table node with attribute <table class="s"
			if(tableNode.getNodeType() == Node.ELEMENT_NODE &&((Element)tableNode).getAttribute("class").equals("s") ){
				//go through all tr node
				for(Node trNode : XMLUtility.getChildElementsByTagName(tableNode, "tr")){
					if(trNode.getNodeType() == Node.ELEMENT_NODE){
						String colour = ((Element)trNode).getAttribute("class");
						switch(colour){
						// not covered - red <tr class="z">
						case "z":
							StatementEntity sm = createStm(trNode,sf);
							notcoveredStm.add(sm);
							orderedAllExecutableStm.add(sm);
						//log.debug("not covered");
						break;

						// covered  - green <tr class="c">
						case "c":
							sm = createStm(trNode,sf);
							fullycoveredStm.add(sm);
							orderedAllExecutableStm.add(sm);
						//log.debug("covered");
						break;

						// partly covered - yellow 
						case "p":
							sm = createStm(trNode,sf);
							partlycoveredStm.add(sm);
							orderedAllExecutableStm.add(sm);
						//log.debug("partly covered");
						break;

						default:
						log.debug("this trnode does not contain an executable line");
							//do nothing
						}

					}

				}
			}
		}
		//convert set to list
		ArrayList<StatementEntity> coveredStm = new ArrayList<>(fullycoveredStm);
		coveredStm.addAll(partlycoveredStm);
		//covered statements are recorded a hashmap,sourefile is the key
		coveredStatementsOfSourceFile.put(sf, coveredStm);

		//source -> statement link, update all executable statements, this should be already ordered.
		if(!orderedAllExecutableStm.isEmpty()) 
			sf.setExecutableStatements(orderedAllExecutableStm);
		
		int coveredLine = fullycoveredStm.size()+partlycoveredStm.size();
		int totalLine = coveredLine + notcoveredStm.size();
		assert orderedAllExecutableStm.size()==totalLine;
		totalExecutableStms +=totalLine;
		totalCoveredStms +=coveredLine;
		log.debug(htmlfile.getFileName().toString()+ " line coverage% " +  coveredLine+"/"+totalLine);
		log.debug("total executable statements : " + totalExecutableStms);
		log.debug("total covered statements : " + totalCoveredStms);
	}
	 
	 /**
	  * helper method to create statement from given node, positions of td tags are hardcoded.
	  * <tr class="z">
      *   <td class="l">102</td>
      *   <td>&#160;&#160;&#160;int&#160;_HMACOutputLength&#160;=&#160;0;</td>
      * </tr>
      * 
	  * 
	  */
	 StatementEntity createStm(Node trNode,SourceFileEntity sf){
		 List<Node> tdNodes = XMLUtility.getChildElementsByTagName(trNode, "td");
		 //1st td node is line number 
		 String line = tdNodes.get(0).getChildNodes().item(0).getNodeValue();
		 int lineNum=-1;

		 try{
	         if(line.isEmpty()){
/*		       <tr class="c">
		      	 <td class="l">
		      		<a name="6">466</a>
		      	</td>
		      	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;org.apache.xml.security.Init.init();</td>
		      </tr>
*/
				 Node aNode = XMLUtility.getChildElementsByTagName(tdNodes.get(0), "a").get(0);
				 line = aNode.getChildNodes().item(0).getNodeValue();
				 lineNum = Integer.parseInt(line);
	         }else{
	        	 lineNum = Integer.parseInt(line);
	         }
		 }catch(NumberFormatException e){
			log.error("error in parsing - line" + line);
		 }
         //2nd td node is statement
		 String statementStr = tdNodes.get(1).getChildNodes().item(0).getNodeValue();
		 //statementStr = statementStr.replaceAll("\\xa0", "").trim();
		 statementStr = nonASCII.matcher(statementStr).replaceAll("");
		 return new StatementEntity(sf,lineNum,statementStr);  
	 }
	 
	 
	 /**
	  * Use jtidy to convert a html to XML Document object
	  * @return xml Document object
	  */
	 Document convertHTMLtoXML(Path htmlPath){
		 //initiate jtidy
		 Tidy tidy = new Tidy();
		 tidy.setMakeClean(true);
		 tidy.setXmlOut( true); //output is XML.
		 tidy.setOnlyErrors(true);
		 tidy.setShowWarnings(false);
		 tidy.setErrout(new PrintWriter(new NullOutputStream()));
    
		 //create a XML output of the htmlfile in the same directory, same name but remove html extension add xml extension
		 String htmlname =htmlPath.getFileName().toString(); 
		 Path xmlout = Paths.get(htmlPath.getParent().toString()+File.separator+htmlname.substring(0,htmlname.lastIndexOf("."))+".xml");
		 Document htmldoc = null; 
		 //using Java7 try with resources syntax, two statements in try block. All streams are closed automatically. 
		 try(InputStream is = Files.newInputStream(htmlPath);
			 OutputStream os = Files.newOutputStream(xmlout)){
			 htmldoc = tidy.parseDOM(is , os);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 return htmldoc;
	 }
	 
	 public class NullOutputStream extends OutputStream {
		  @Override
		  public void write(int b) throws IOException {
		  }
		}
	 /**
	  * Parse pacakge name and java source file name from htmldoc and return the SourceFileEntity object 
	  * @param htmldoc
	  * @return
	  */
	 
     SourceFileEntity parseSourceFileEntity(Document htmldoc){
    	 String srcName = parseSrcFileName(htmldoc);
    	 String packageName = parsePackageName(htmldoc);
    	 for(SourceFileEntity sfe: srcEntities)
    		 if(sfe.getPackageName().equals(packageName)&&sfe.getSourceFileName().equals(srcName))
    			 return sfe;
		 log.error(packageName + "." + srcName + " not found in SourceEntity set!");
    	 return null;
		 
	 }
	 
	 /**
	  * parse htmldoc (in XML format already) to get pacakge name, the position of the element is hardcoded.
	  * @param htmldoc
	  * @return
	  */
	 String parsePackageName(Document htmldoc){
		 //get package name
		 //<table class="hdft"
		 String packageName =""; 
		 //1st <table
		 Node tableNode = htmldoc.getElementsByTagName("table").item(0);
		 //log.debug("attribute class " + ((Element)tableNode).getAttribute("class"));
		 //2nd <tr>
		 Node trNode =tableNode.getChildNodes().item(1);
		 //1st td   <td class="nv">
		 Node tdNode = trNode.getFirstChild();
		 //log.debug("td node class attribute = " + ((Element)tdNode).getAttribute("class"));
		 //2nd <a href
		Node aNode = XMLUtility.getChildElementsByTagName(tdNode, "a").get(1);
    	 //log.debug("ahref attribute =" + ((Element)aNode).getAttribute("href"));
		
    	 //package name is stored in a text node which is a child of current node 
		 NodeList nl = aNode.getChildNodes();
		 for(int i=0;i<nl.getLength();i++){
			 Node nd = nl.item(i);
			 if(nd.getNodeType()==Node.TEXT_NODE)
				 packageName = nd.getNodeValue();
		 }
    	 //log.debug("packageName is " + packageName );
		 return packageName;	
	 }
	 
	 /**
	  * parse htmldoc (in XML format already) to get source file name, the positions of the elements are hardcoded.
	  * @param htmldoc
	  * @return
	  */
	 String parseSrcFileName(Document htmldoc){
		 String srcName ="";
		 //2nd node under body <h2>
		 Node bodyNode = htmldoc.getElementsByTagName("body").item(0); // only one body
		 Node h2Node = XMLUtility.getChildElementsByTagName(bodyNode, "h2").get(0); //only one h2
		 Node spanNode = XMLUtility.getChildElementsByTagName(h2Node, "span").get(0);
		 NodeList nl = spanNode.getChildNodes();
		 for(int i=0; i<nl.getLength();i++)
			 if(nl.item(i).getNodeType()==Node.TEXT_NODE)
				 srcName = nl.item(i).getNodeValue();
		 return srcName;	
	 }
	 
	 
	 /**
	  * not all html files contain coverage for source file. Some are package level coverage information which
	  * is already parsed in XML emma output. Position of tags are hardcoded
	  * @return
	  */
	 boolean containsSourceFileCoverage(Document htmldoc){
		 String title="";
		 //2nd node under body <h2>
		 Node bodyNode = htmldoc.getElementsByTagName("body").item(0); // only one body
		 Node h2Node = XMLUtility.getChildElementsByTagName(bodyNode, "h2").get(0); //only one h2
		 NodeList nl = h2Node.getChildNodes();
		 for(int i=0; i<nl.getLength();i++)
			 if(nl.item(i).getNodeType()==Node.TEXT_NODE){
				 title  = nl.item(i).getNodeValue();
				 break; //title is 1st text node, do not continue in the loop
			 }
				
		 log.debug("this is a " + title);
		 boolean result = title.contains("SOURCE");
		 return result;	 
		 }
	 
}

        