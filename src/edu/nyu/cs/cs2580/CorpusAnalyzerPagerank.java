package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3.
 */
public class CorpusAnalyzerPagerank extends CorpusAnalyzer {
	
	
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
    
    
    
    return;
  }

  
  private Map<String, Integer> getDocIds(){
	  String corpusFile = _options._corpusPrefix + "/";
	  File folder = new File(corpusFile);
	  File[] files = folder.listFiles();
	    
	    
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
   * @throws IOException
   */
  @Override
  public void compute() throws IOException {
    System.out.println("Computing using " + this.getClass().getName());
    
    
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
    System.out.println("Loading using " + this.getClass().getName());
    return null;
  }
  
  
  public static void main(String[] args){
	  Options option;
	try {
		option = new Options("conf/engine.conf");
		CorpusAnalyzerPagerank pagerank = new CorpusAnalyzerPagerank(option);
		Map<String, Integer> maps = pagerank.getDocIds();
		Set<String> keys = new TreeSet<String>(maps.keySet());
		for(String s : keys){
			System.out.println(s);
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  
  }
}
