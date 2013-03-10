package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class IndexerInvertedDoconly extends Indexer implements Serializable{
  private static final long serialVersionUID = 1077111905740085031L;
  
  // Map each term to its associate document
  private Map<String, Vector<Integer>> _index=new HashMap<String,Vector<Integer>>();
  //all unique terms,Offsets are integer representations.
  private Vector<String> _terms = new Vector<String>();
  //Stores all Document in memory.
  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  
  
  // Provided for serialization
  public IndexerInvertedDoconly(){}
  
  // The real constructor
  public IndexerInvertedDoconly(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
	    String corpusFile = _options._corpusPrefix+"/";
	    System.out.println("Construct index from: " + corpusFile);
	
		  chooseFiles cf=new chooseFiles(_options);
		  int times = cf.writeTimes();
		  System.out.println(times);
		  FileOps filewriter = new FileOps(_options._indexPrefix+"/");
		  for(int i=0;i<times;i++){
			  Vector<String> files=cf.loadFile(i);
			  for(String name:files){
		        String filepath=corpusFile+name;
		        File file=new File(filepath);
		        String content = ProcessHtml.process(file);
		        if (content != null)
		        	processDocument(content,name);
	      
			  }
			  System.out.println("Times here : " + i);
			  String name="temp"+i+".txt";
			  Map<String, String> content = new HashMap<String,String>();
			  for(String term:_index.keySet())
			  {
				  StringBuilder builder = new StringBuilder();
				  for(Integer x:_index.get(term))
				  {
					  builder.append(x+"|");
				  }
				  builder.deleteCharAt(builder.length()-1);
				  content.put(term,builder.toString());
			  }
			  
			  filewriter.write(name, content);
			  _index.clear();
			  _terms.clear();
		 }
		
		 
/*	    for(int i=0;i<_terms.size();i++){
	    	System.out.print(_terms.get(i)+":");
	    	for(int j=0;j<_index.get(_terms.get(i)).size();j++){
	    		System.out.print(_index.get(_terms.get(i)).get(j)+" ");
	    	}
	    	System.out.println();
	    }
	  
	    System.out.println(
	        "Indexed " + Integer.toString(_numDocs) + " docs with ");

	    String indexFile = _options._indexPrefix + "/corpus.idx";
	    System.out.println("Store index to: " + indexFile);
	    ObjectOutputStream writer =
	        new ObjectOutputStream(new FileOutputStream(indexFile));
	    writer.writeObject(this);
	    writer.close();*/
		  
  }
  private void processDocument(String content, String filename) {
	  try{
	    Scanner s;
		s = new Scanner(content).useDelimiter("\t");
		String title = s.next();
		String body = s.next();
		s.close();
		DocumentIndexed doc = new DocumentIndexed(_documents.size());
		doc.setTitle(title);
		_documents.add(doc);
		++_numDocs;
		generateIndex(title);
		generateIndex(body);
	  }
	  catch(Exception e){
		  System.out.println("The file that has error: "+ filename);
	  }
		//System.out.println(title);
		//System.out.println(body);
	   
}
  private void generateIndex(String content){
	  Scanner s = new Scanner(content);  // Uses white space by default.
	    while (s.hasNext()) {
	      String token = s.next();
	      // decrement the size() by 1 as the real doc id
	      int did=_documents.size()-1;
	      if (!_terms.contains(token)) {
	    	  _terms.add(token);
	    	  Vector<Integer> plist=new Vector<Integer>();
	    	  plist.add(did);
	          _index.put(token, plist);
	      }else
	      {
	    	  Vector<Integer> plist=_index.get(token);
	    	  if(!plist.contains(did)){	  
	    	  plist.add(did);
	    	  _index.put(token,plist);
	    	  }
	      }
	      
	    }
	    return;
  }
  
  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
	    String indexFile = _options._indexPrefix + "/corpus.idx";
	    System.out.println("Load index from: " + indexFile);

	    ObjectInputStream reader =
	        new ObjectInputStream(new FileInputStream(indexFile));
	    IndexerInvertedDoconly loaded = (IndexerInvertedDoconly) reader.readObject();
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
	    
	    // ************load the index
	    this._index=loaded._index;
	    reader.close();

	    System.out.println(Integer.toString(_numDocs) + " documents loaded ");
}
  

  @Override
  public Document getDoc(int docid) {
    SearchEngine.Check(false, "Do NOT change, not used for this Indexer!");
    return null;
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}
   */
  @Override
  public Document nextDoc(Query query, int docid) {
   Vector<Integer> ids=new Vector<Integer>();
   int id;
   int result=docid;
   for(int i=0;i<query._tokens.size();i++){
	 id=next(query._tokens.get(i),docid);
	 // only add the id that exists
	 if(id != -1 )
		 ids.add(id);  
   }
   // return null if no document contains any term of the query or when couldn't find any document that contains one term
   if(ids.size()==0 || ids.size()!=query._tokens.size()){
	   return null;
   }
   else if(find(ids))
   { 
	   return _documents.get(ids.get(0));
   }
   else{
	  return nextDoc(query, max(ids)-1); 
   }
   
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
		return -1;
	Vector<Integer> docIDs = _index.get(word);
	int high=docIDs.size()-1;
	if(docIDs.lastElement() <= docid)
		return -1;
	if(docIDs.get(0) > docid){
		return docIDs.get(0);
	}
	int result=binarySearch(word,1,high,docid,docIDs);
	return  docIDs.get(result); 	  
}

  private int binarySearch(String word, int low, int high, int docid, Vector<Integer> docIDs){
   	  	while(high-low>1){
   		  int mid=(low+high) >>> 1;
   		  if(docIDs.get(mid)<=docid){
   			  low=mid+1;
   		  }else{
   			  high=mid;
   		  }
   	  }
   		  return docIDs.get(low)>docid ? low:high;
  }
  @Override
  public int corpusDocFrequencyByTerm(String term) {
    return 0;
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
 
  public static void main(String[] args) throws IOException {
	  Options option = new Options("/Users/Wen/Documents/workspace2/SearchEngine/conf/engine.conf");
	  IndexerInvertedDoconly index = new IndexerInvertedDoconly(option);
   	  index.constructIndex();
//	  Query query = new Query("the free");
//	  query.processQuery();
//	  try {
//		index.loadIndex();
//		Document nextdoc = index.nextDoc(query, 8);
//		
//		if(nextdoc!=null)
//			System.out.println(nextdoc._docid);
//		else
//			System.out.println("Null");
//		
		
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (ClassNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	  
  }
  
}
  
