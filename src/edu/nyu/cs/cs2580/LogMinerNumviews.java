package edu.nyu.cs.cs2580;

import java.io.*;
import java.util.*;


import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3.
 */
public class LogMinerNumviews extends LogMiner {
  
  protected HashMap<String, Integer> numviews=new HashMap<String, Integer>();
  // #views and its position according to decreasing order
  protected HashMap<Integer, Integer> rank=new HashMap<Integer, Integer>();
  
  protected Map<Integer,Integer> docNumviews=new HashMap<Integer,Integer>();
	
  public LogMinerNumviews(){
	  super();
  }
  public LogMinerNumviews(Options options) {
    super(options);
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
   * This function processes the logs within the log directory as specified by
   * the {@link _options}. The logs are obtained from Wikipedia dumps and have
   * the following format per line: [language]<space>[article]<space>[#views].
   * Those view information are to be extracted for documents in our corpus and
   * stored somewhere to be used during indexing.
   *
   * Note that the log contains view information for all articles in Wikipedia
   * and it is necessary to locate the information about articles within our
   * corpus.
   *
   * @throws IOException
   */
  @Override
  public void compute() throws IOException {
	  
	  String dirpath="/Users/banduo/Documents/workspace/HW3/wiki/";
	  File folder = new File(dirpath);
	  File[] listOfFiles = folder.listFiles();
	 
	  Map<String,Integer> map=getDocIds(listOfFiles);
	  
	  for (File file : listOfFiles) {
	        if (file.isFile()) {
	        	String name=file.getName();
	        	numviews.put(name,0);
	        }
	  }   
	  
	  String path="/Users/banduo/Documents/workspace/HW3/data/log/20130301-160000.log";
		BufferedReader reader=new BufferedReader(new FileReader(path));
		String line;
		while(((line=reader.readLine())!= null)){
			Scanner s=new Scanner(line);
			while(s.hasNext()){
				if(s.next().equals("en")){
					String doc=s.next();
					if(numviews.containsKey(doc)){
						try{
						int num=Integer.parseInt(s.next());
						numviews.put(doc,num);
						rank.put(num,0);
						}catch(Exception e){
						}
					}
					
				}
			}
		}
		reader.close();
		//docid --> #views
		for(String s:map.keySet()){
			docNumviews.put(map.get(s), numviews.get(s));
		}
		  
		 // sort #views 
		 Set<Integer> rkeys=rank.keySet();
		  ArrayList<Integer> srKey=new ArrayList<Integer>(rkeys);
		  Collections.sort(srKey);
	      int r=1;
		  for(int k=srKey.size()-1;k>=0;k--){
			  rank.put(srKey.get(k),r++);  
		  }
		 rank.put(0,r);	
		 for(int i:rank.keySet())
		 System.out.println(i+":"+rank.get(i));
		 
	// write to output file
	String wpath="/Users/banduo/Documents/workspace/HW3/data/temp/numviews.txt";
	BufferedWriter writer=new BufferedWriter(new FileWriter(wpath));
	
	 Set<String> keys=numviews.keySet();
	  ArrayList<String> sortedKey=new ArrayList<String>(keys);
	  Collections.sort(sortedKey);

	  for(String k:sortedKey){
		  String s=k+":"+rank.get(numviews.get(k))+"\n";
		  writer.write(s);
	  }
		
	  
	 writer.close();
     return;
  }

  /**
   * During indexing mode, this function loads the NumViews values computed
   * during mining mode to be used by the indexer.
   * 
   * @throws IOException
   */
  @Override
  public Object load() throws IOException {
    System.out.println("Loading using " + this.getClass().getName());
    return docNumviews;
  }
  
  
  
  public static void main(String[] args) throws IOException{
	  LogMinerNumviews ln=new LogMinerNumviews();
	  ln.compute();
	  
  }
}
