package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3.
 */
public class CorpusAnalyzerPagerank extends CorpusAnalyzer {
	
	private String graphPath = "graph";
	private String pageRankPath = "pageRank";
	private final float lamda = 0.9f;
	private final int iteration = 2;
	
	private Map<String, Integer> ids;
	
  public CorpusAnalyzerPagerank(Options options) {
    super(options);
  }

  /**
   * This function processes the corpus as specified inside {@link _options}
   * and extracts the "internal" graph structure from the pages inside the
   * corpus. Internal means we only store links between two pages that are both
   * inside the corpus.
   * 
   * Note that you will not be implementing a real crawler. Instead, the corpus
   * you are processing can be simply read from the disk. All you need to do is
   * reading the files one by one, parsing them, extracting the links for them,
   * and computing the graph composed of all and only links that connect two
   * pages that are both in the corpus.
   * 
   * Note that you will need to design the data structure for storing the
   * resulting graph, which will be used by the {@link compute} function. Since
   * the graph may be large, it may be necessary to store partial graphs to
   * disk before producing the final graph.
   *
   * @throws IOException
   */
  @Override
  public void prepare() throws IOException {
    System.out.println("Preparing " + this.getClass().getName());
    
    File graph = new File(_options._tempFolder + "/" + graphPath);
    
    //Delete last result
    if(graph.exists()){
    	graph.delete();
    }
    
    String corpusFile = _options._corpusPrefix + "/";
	  File folder = new File(corpusFile);
	  File[] files = folder.listFiles();
    //Get id definition of these documents
	  this.ids = getDocIds(files);
	  FileOps.append(graph, this.ids.size()+"");
	  //Get links from each document, construct the graph
	  for(File f : files){
//		  corpusSize++;
		  StringBuilder sb = new StringBuilder();
		  List<String> links = ProcessHtml.parseLink(f);
		  sb.append(ids.get(f.getName())).append("\t");
		  for(String l : links){
			  if(ids.containsKey(l)){
				  sb.append(ids.get(l)).append("|");
			  }
		  }
		  if(sb.charAt(sb.length()-1)=='|'){
			  sb.deleteCharAt(sb.length()-1);
		  }
		  FileOps.append(graph, sb.toString());
	  }
	  
    
    return;
  }

  
  private Map<String, Integer> getDocIds(File[] files){
	 
	  Map<String, Integer> ret = new HashMap<String, Integer>();
	  //Sort the file in proper order
	  Arrays.sort(files, new FileComparator());
	  int id = 0;
	  for(File f : files){
		  ret.put(f.getName(), id++);
	  }
	  return ret;
  }
  
  
  /**
   * This function computes the PageRank based on the internal graph generated
   * by the {@link prepare} function, and stores the PageRank to be used for
   * ranking.
   * 
   * Note that you will have to store the computed PageRank with each document
   * the same way you do the indexing for HW2. I.e., the PageRank information
   * becomes part of the index and can be used for ranking in serve mode. Thus,
   * you should store the whatever is needed inside the same directory as
   * specified by _indexPrefix inside {@link _options}.
   * 
   *
   * @throws IOException
   */
  @Override
  public void compute() throws IOException {
    System.out.println("Computing using " + this.getClass().getName());
    //Read the graph
    File graph = new File(_options._tempFolder + "/" + graphPath);
    BufferedReader input = new BufferedReader(new FileReader(graph));
    int corpusSize = Integer.parseInt(input.readLine());
    
    
    //construct matrix
    float[][] matrix = new float[corpusSize][corpusSize];
    //initiate the page rank vector as all 1
    float[] pageRank = new float[corpusSize];
    for(int i = 0; i<pageRank.length; i++)
    	pageRank[i] = 1.0f;
    
    
    String line = null; // not declared within while loop
		while ((line = input.readLine()) != null) {
			String[] lineElem = line.split("\t");
			if(lineElem.length!=2){
				//Documents with no out links.
				continue;
			}
			int currentDoc = Integer.parseInt(lineElem[0]);
			String[] connected = lineElem[1].split("\\|");
			for(String c : connected){
				int currentConnected = Integer.parseInt(c);
				//Store the value as M^T
				float value = (float)1 / (float)connected.length;
				matrix[currentConnected][currentDoc] = value;
			}
		}
		input.close();
		
    //Do Iterations
		for(int i = 0;i<iteration; i++){
			float[] newPageRank = new float[corpusSize];
			float sum = 0.0f;
			for(int j = 0; j<corpusSize ; j++){
				sum = 0.0f;
				for(int k = 0; k<corpusSize; k++){
					sum += lamda * matrix[j][k] * pageRank[k];
				}
				sum += ((float)1-lamda) * ((float)1/(float)corpusSize);
				newPageRank[j] = sum;
			}
			pageRank = newPageRank;
		}
    
		//write pr value to file
		File pr = new File(_options._indexPrefix + "/" + pageRankPath);
		for(int i = 0; i<corpusSize; i++){
			String prLine = i + "\t" + pageRank[i];
			FileOps.append(pr, prLine);
		}
		
    return;
  }

  /**
   * During indexing mode, this function loads the PageRank values computed
   * during mining mode to be used by the indexer.
   *
   * @throws IOException
   */
  @Override
  public Object load() throws IOException {
    //System.out.println("Loading using " + this.getClass().getName());
    //return null;
	  Map<Integer, Float> ret = new HashMap<Integer, Float>();
	  File pageRankFile = new File(_options._indexPrefix + "/" + pageRankPath);
	  BufferedReader input = new BufferedReader(new FileReader(pageRankFile));
	  
	  String line;
	  while ((line = input.readLine()) != null) {
			String[] prParem = line.split("\t");
			int did = Integer.parseInt(prParem[0]);
			float pr = Float.parseFloat(prParem[1]);
			ret.put(did, pr);
		}
	  
	  
	  return ret;
  }
  
  
  public static void main(String[] args){
	  Options option;
	try {
		option = new Options("conf/engine.conf");
		CorpusAnalyzerPagerank pagerank = new CorpusAnalyzerPagerank(option);
		pagerank.compute();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  
  }
}
