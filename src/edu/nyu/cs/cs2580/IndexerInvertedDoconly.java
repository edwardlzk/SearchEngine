package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedDoconly extends Indexer {
  
  // Map each term to its associate document
  private Map<String, Vector<Integer>> _index=new HashMap<String,Vector<Integer>>();
  //all unique terms,Offsets are integer representations.
  private Vector<String> _terms = new Vector<String>();
  //Stores all Document in memory.
  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  
  private long totalTime = 0;
  private long time = 0;
  private Calendar cal = Calendar.getInstance();
  
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
		        time = new Date().getTime();
		        String content = ProcessHtml.process(file);
		        totalTime += new Date().getTime() - time;
		        if (content != null)
		        	processDocument(content);
	      
			  }
			  System.out.println("Times here : " + i);
			  System.out.println("processes:" + totalTime);
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
		 
  }
  private void processDocument(String content) {
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
		  System.out.println("The file that has error");
	  }
	   
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
	  String docFile = _options._indexPrefix+"/";
	 
	    System.out.println("Load index from: " + indexFile);
	    
	    BufferedReader reader = new BufferedReader(new FileReader(indexFile));
	    String line;
	    while((line=reader.readLine())!=null){
	    	 int termDocFren=0;
	         int termCorpusFren=0;
	         String title="";
	         String data="";
	    	Scanner s=new Scanner(line).useDelimiter("\t");
	    	while(s.hasNext()){
	    		title=s.next();
	    		data=s.next();
	    	}
	    	//System.out.println(data);
	    	String[] docs=data.split("\\|");
	    	termDocFren=docs.length;
	    	termCorpusFren=termDocFren;
	    	Vector<String> Appenddoc=new Vector<String>(); //docs need to update
	        for(String doc:docs){
	    	Appenddoc.add(doc);
	    	//termCorpusFren +=docs.length;
	        }
	        //termCorpusFren -= termDocFren;
	       // System.out.println(termDocFren+" "+termCorpusFren );
	        
	        for(String docid : Appenddoc){
	        BufferedWriter addDoc=new BufferedWriter(new FileWriter(docFile+docid,true));
	        addDoc.write(title+"\t"+termDocFren+"\t"+termCorpusFren+"\n");
	        addDoc.close();
	        }
	    }
	    reader.close(); 
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
  public DocumentIndexed nextDoc(Query query, int docid) {
	  String path="";
	  Vector<Integer> ids=new Vector<Integer>();
	  for(int i=0;i<query._tokens.size();i++){
	  String term="";
	  try {
		BufferedReader reader=new BufferedReader(new FileReader(path));
		String line;
		while((line=reader.readLine())!=null){
			Scanner s=new Scanner(line).useDelimiter("\t");
			if(s.hasNext()){
				String doc=s.next();
				if(doc==query._tokens.get(i)){   //found term
					term=s.next();
					break;
				}
			}
		}
		
		String[] pos=term.split("\\|");  //get all docids that have term
		for(String s:pos){
			if(Integer.parseInt(s)>docid){
				ids.add(Integer.parseInt(s));
			}				
		}
		reader.close();
	     } catch (Exception e) {
		 e.printStackTrace();
	    }
	  }
	  
	  if(ids.size()==0 || ids.size()!=query._tokens.size()){
		   return null;
	   }
	  
	  else if(find(ids))
	   { 
		  DocumentIndexed doc=new DocumentIndexed(ids.get(0));
		  //read doc file to get information
		   return doc;
	   }
	   else{
		  return nextDoc(query, max(ids)-1); 
	   }
	  
	
	  	  
  /* Vector<Integer> ids=new Vector<Integer>();
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
   }*/
   
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
	  Options option = new Options("./conf/engine.conf");
	  IndexerInvertedDoconly index = new IndexerInvertedDoconly(option);
   	  index.constructIndex();
   	  Query q=new Query("new");
	  DocumentIndexed d=index.nextDoc(q,0);
  
  }
  
}
  
