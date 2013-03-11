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
		        	processDocument(content);    
			  }
			  String name="temp"+i+".txt";
			  Map<String, String> content=new HashMap<String,String>();
		      for(int k=0;k<_terms.size();k++){
		    	  Set<Integer> keys=_index.get(_terms.get(k)).keySet();
		    	  StringBuilder value=new StringBuilder();
			    	for(int j:keys){
			    		Vector<Integer> pos=_index.get(_terms.get(k)).get(j);
			    		Integer docid=new Integer(j);
			    		value.append(docid.toString()).append(",");
			    		for(int p:pos){
			    		value.append(p).append(",");
			    		}
			    		value.deleteCharAt(value.length()-1);
			    		value.append("|");
			    	}
			    	value.deleteCharAt(value.length()-1);
			    	content.put(_terms.get(k),value.toString());	
		      }
		      filewriter.write(name, content);
			  _index.clear();
			  _terms.clear();
		 }
	    
  }
	  
	  private void processDocument(String content) {
		    Scanner s = new Scanner(content).useDelimiter("\t");
		    String title = s.next();
		    String body = s.next();
		    s.close();
		    DocumentIndexed doc = new DocumentIndexed(_documents.size());
		    //doc.setTitle(title);
		    _documents.add(doc);
		    ++_numDocs;
		    generateIndex(title+doc);
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
	  String docFile = _options._indexPrefix+"/";
	 
	    System.out.println("Load index from: " + indexFile);
	    
	    BufferedReader reader = new BufferedReader(new FileReader(indexFile));
	    String line;
	    while((line=reader.readLine())!=null){
	    	 int termDocFren=0;
	         String title="";
	         String data="";
	    	Scanner s=new Scanner(line).useDelimiter("\t");
	    	while(s.hasNext()){
	    		title=s.next();
	    		data=s.next();
	    	}
	    	System.out.println(data);
	    	String[] docs=data.split("\\|"); //docid and pos
	    	
	        for(String doc:docs){
	    	String[] docid= doc.split(",");  
	    	String id=docid[0];
	    	termDocFren=docid.length-1;
	        BufferedWriter addDoc=new BufferedWriter(new FileWriter(docFile+id,true));
	        addDoc.write(title+"\t"+termDocFren+"\n");
	        addDoc.close();   
	        }
	    }
	    reader.close();  
  }


  @Override
  public DocumentIndexed getDoc(int docid) {
	DocumentIndexed doc=new DocumentIndexed(docid);
	String docpath=""+docid;
	System.out.println(docpath);
    try {
		BufferedReader reader=new BufferedReader(new FileReader(docpath));
		String line;
		int count=0;
		while((line=reader.readLine())!=null){
			count++;
			if(count==1){
				doc.setTitle(line);
			}
			else if(count==2){
				doc.setTermTotal(Integer.parseInt(line));
			}				
		}
		reader.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
    return doc;
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}.
   */
  @Override
  public DocumentIndexed nextDoc(Query query, int docid) {
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
	  String indexFile = _options._indexPrefix + "/corpus.idx";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(indexFile));
	        String line;
	         while((line=reader.readLine())!=null){
	        	 int termDocFren=0;
	        	 String title="";
	        	 String data="";
	        	 Scanner s=new Scanner(line).useDelimiter("\t");
	        	 	while(s.hasNext()){
	        	 		title=s.next();
	        	 		data=s.next();
	        	 	}
	        if(title.equals(term)){
	    	String[] docs=data.split("\\|");
	    	termDocFren=docs.length;
	        }
	        reader.close();
	        return termDocFren;
	    	}
	        reader.close();   
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return 0;
  }

  @Override
  public int corpusTermFrequency(String term) {
	  String indexFile = _options._indexPrefix + "/corpus.idx";
		try {
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
	        if(title.equals(term)){
	    	String[] docs=data.split("\\|");
	    	termDocFren=docs.length;
	    	Vector<String> Appenddoc=new Vector<String>(); //docs need to update
	        for(String doc:docs){
	    	String[] docid= doc.split(",");
	    	Appenddoc.add(docid[0]);
	    	termCorpusFren +=docid.length;
	        }
	        termCorpusFren -= termDocFren;
	        reader.close();
	        return termCorpusFren;
	    	}
	        }
	        reader.close();   
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return 0;
  }

  @Override
  public int documentTermFrequency(String term, String url) {
	  String docpath=""+url;
	  int result;
	  try {
		BufferedReader reader = new BufferedReader(new FileReader(docpath));
		String line;
		int count=0;
		while((line=reader.readLine())!=null){
			count++;
			if(count>2){
				String[] terms=line.split("\t");
				if(terms[0].equals(term)){
					result=Integer.parseInt(terms[1]);
					reader.close();
					return result;
				}
			}
		}
		reader.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
    return 0;
  }
  public static void main(String[] args) throws IOException {
	  Options option = new Options("/Users/Wen/Documents/workspace2/SearchEngine/conf/engine.conf");
	  IndexerInvertedOccurrence index = new IndexerInvertedOccurrence(option);
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
