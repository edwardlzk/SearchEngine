package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;


import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedOccurrence extends Indexer{
		  private HashMap<String, HashMap<Integer,Vector<Integer>>> _index=new HashMap<String,HashMap<Integer,Vector<Integer>>>();
	  //all unique terms
	  private Vector<String> _terms = new Vector<String>();
	  //Stores all Document in memory.
	  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
	
  public IndexerInvertedOccurrence(){}
  
  public IndexerInvertedOccurrence(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  
  @Override
  public void constructIndex() throws IOException {
	  String corpusFile = _options._corpusPrefix+"/";
	    System.out.println("Construct index from: " + corpusFile);
	
	    File folder = new File(corpusFile);
	    File[] listOfFiles = folder.listFiles();

	    for (File file : listOfFiles) {
	        if (file.isFile()) {
	        	String name=file.getName();
	            System.out.println(name);
	            String filepath=corpusFile+name;
	            BufferedReader reader = new BufferedReader(new FileReader(filepath));
	            StringBuffer content=new StringBuffer();
	            try{
	                String line = null;
	            	while((line = reader.readLine()) != null) {
	            		content.append(line);
	            	}
	            }finally{
	            	reader.close();
	            }
	  
	  	        processDocument(content.toString());
	        }
	    }
	    for(int i=0;i<_terms.size();i++){
	    	System.out.print(_terms.get(i)+":");
	    	Set<Integer> keys=_index.get(_terms.get(i)).keySet();
	    	for(int j:keys){
	    		System.out.println("("+j+","+_index.get(_terms.get(i)).get(j)+")");
	    	}
	    	System.out.println();
	    }
	    
	    Query q=new Query("New York");
	    q.processQuery();
	    
	    int id=nextDoc(q,0)._docid;
	    System.out.println(id);
	    
	    System.out.println(
		        "Indexed " + Integer.toString(_numDocs) + " docs with ");

		    String indexFile = _options._indexPrefix + "/corpus.idx";
		    System.out.println("Store index to: " + indexFile);
		    ObjectOutputStream writer =
		        new ObjectOutputStream(new FileOutputStream(indexFile));
		    writer.writeObject(this);
		    writer.close();
	    
  }
	  
	  private void processDocument(String content) {
		    Scanner s = new Scanner(content).useDelimiter("\t");
		    String title = s.next();
		    //String body = s.next();
		    s.close();
		    DocumentIndexed doc = new DocumentIndexed(_documents.size());
		    //doc.setTitle(title);
		    _documents.add(doc);
		    ++_numDocs;
		    generateIndex(title);
		    //generateIndex(body);
		    //System.out.println(title);
		    //System.out.println(body);
	}
	  private void generateIndex(String content){
		  Scanner s = new Scanner(content);  // Uses white space by default.
		  int pos=1;
		    while (s.hasNext()) {
		      String token = s.next();
		      int did=_documents.size()-1;
		      if (!_terms.contains(token)) {
		    	  _terms.add(token);
		    	  HashMap<Integer,Vector<Integer>> plist=new HashMap<Integer,Vector<Integer>>();
		    	  Vector<Integer> position=new Vector<Integer>();
		    	  position.add(pos);
		    	  plist.put(did, position);
		          _index.put(token, plist);
		      }else{
		    	  HashMap<Integer,Vector<Integer>> plist=_index.get(token);
		    	  if(plist.containsKey(did)){
		    		  Vector<Integer> position=plist.get(did);
			    	  position.add(pos);
			    	  plist.put(did, position);
			    	  _index.put(token,plist); 
		    	  }else{
		    	  Vector<Integer> position=new Vector<Integer>();
		    	  position.add(pos);
		    	  plist.put(did, position);
		    	  _index.put(token,plist);
		    	  }
		      }
		      pos++;
		      }
		      
		    return;
}
  
  
  
  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
	  String indexFile = _options._indexPrefix + "/corpus.idx";
	    System.out.println("Load index from: " + indexFile);

	    ObjectInputStream reader =
	        new ObjectInputStream(new FileInputStream(indexFile));
	    IndexerInvertedOccurrence loaded = (IndexerInvertedOccurrence) reader.readObject();

	    this._documents = loaded._documents;
	    // Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
	    this._numDocs = _documents.size();
	    //for (Integer freq : loaded._termCorpusFrequency.values()) {
	      //this._totalTermFrequency += freq;
	    //}
	    //this._dictionary = loaded._dictionary;
	    this._terms = loaded._terms;
	    //this._termCorpusFrequency = loaded._termCorpusFrequency;
	    //this._termDocFrequency = loaded._termDocFrequency;
	    reader.close();

	    System.out.println(Integer.toString(_numDocs) + " documents loaded ");
  }

  @Override
  public Document getDoc(int docid) {
    SearchEngine.Check(false, "Do NOT change, not used for this Indexer!");
    return null;
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}.
   */
  @Override
  public Document nextDoc(Query query, int docid) {
	  Vector<Integer> ids=new Vector<Integer>();
	   int id;
	   int result=docid;
	   for(int i=0;i<query._tokens.size();i++){
		   //System.out.println(query._tokens.get(i));
		 id=next(query._tokens.get(i),docid);
		
		 ids.add(id);  
	   }
	   if(ids.size()==1||find(ids)){
		   result=ids.get(0);
	   }else{
		  return nextDoc(query, max(ids)-1); 
	   }
	   int r;
	   r=result==Integer.MAX_VALUE? docid:result; // judge whether found
	  // if((nextPhrase(query, _documents.get(r)._docid, 0))!=Integer.MAX_VALUE)
		   return _documents.get(r);
	   
	   
	  }
	  private boolean find(Vector<Integer> ids){
		  int first=ids.get(0);
		  for(int i=1;i<ids.size();i++){
			  if(ids.get(i)!=first)
				  return false;
		  }
		  return true;
	  }
	  private int max(Vector<Integer> ids){
		  int max=0;
		  for(int i=0;i<ids.size();i++){
			  if(ids.get(i)>max)
				  max=ids.get(i);
		  }
		  return max;
	  }
	  private int next(String word, int docid){
			// Binary Search
			if(_index.size() == 0 || !_index.containsKey(word))
				return Integer.MAX_VALUE;
			HashMap<Integer,Vector<Integer>> docIDs = _index.get(word);
			Set<Integer> keys=docIDs.keySet();
			Integer[] sortedKey=new Integer[keys.size()];
			int temp=0;
			for(int k:keys){ 
				sortedKey[temp++]=k;
			}
		    Arrays.sort(sortedKey);
			if(sortedKey[sortedKey.length-1]<= docid)
				return Integer.MIN_VALUE;
			if(sortedKey[0] > docid){
				return sortedKey[0];
			}
			int high=sortedKey.length-1;
			int result=binarySearch(word,0,high,docid,sortedKey);
			return  sortedKey[result]; 	  
		}
	  private int binarySearch(String word, int low, int high, int docid, Integer[] docIDs){
		  while((high-low)>1){
			  int mid=(low+high)/2;
			  if(docIDs[mid]<=docid){
				  low=mid;
			  }else{
				  high=mid;
			  }
		  }
		  return high;
	  } 
  public int nextPhrase(Query query, int docid, int pos){
	  Document idVerify=nextDoc(query,docid-1);
	  if(!idVerify.equals(_documents.get(docid))){
		  return Integer.MAX_VALUE;
	  }
	  Vector<Integer> ids=new Vector<Integer>();
	   int id;
	   for(int i=0;i<query._tokens.size();i++){
		 id=next_pos(query._tokens.get(i),docid,pos);
		 ids.add(id);  
	   }
	   for(int k:ids){
	   if(k==Integer.MAX_VALUE)
		   return Integer.MAX_VALUE;
		}
	   int j=0;
	   for(;j<ids.size()-1;j++){
		   if((ids.get(j)+1)!=ids.get(j+1)){
			   break;
		   }
		}
	   if(j==(ids.size()-1)){
		   return ids.get(0);
	   }
	   else
		   return nextPhrase(query,docid,ids.get(ids.size()-1));
  }
  private int next_pos(String word,int docid,int pos){
	 Vector<Integer> docIDs=_index.get(word).get(docid);
	 for(int i:docIDs){
		 if(i>pos){
			 return i;
		 }
	 }
	  return Integer.MAX_VALUE;
  }
  
  @Override
  public int corpusDocFrequencyByTerm(String term) {
    return _index.containsKey(term)?
    		(_index.get(term)).size():0;
  }

  @Override
  public int corpusTermFrequency(String term) {
    return 0;
  }

  @Override
  public int documentTermFrequency(String term, String url) {
    SearchEngine.Check(false, "Not implemented!");
    return 0;
  }
}
