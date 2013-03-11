package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		        	processDocument(content,name);
	      
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
		  String corpus_statistics = _options._indexPrefix+"/" + "statistics";
		  BufferedWriter outsta = new BufferedWriter(new FileWriter(corpus_statistics));
		  // the first line in the corpus_statistics is the number of docs in the corpus
		  outsta.write(_numDocs+"\n");
		  outsta.write(String.valueOf(_totalTermFrequency)+"\n");
		  outsta.close();
		  String[] files=new String[times];
		  for(int count=0;count<times;count++){
		  files[count]="temp"+count+".txt";
		  }
		  filewriter.merge(files, "index.txt", "|");
		  
		  
  }
  private void processDocument(String content,String fileName) {
	  try{
	    Scanner s;
		s = new Scanner(content).useDelimiter("\t");
		String title = s.next();
		String body = s.next();
		s.close();
		//DocumentIndexed doc = new DocumentIndexed(_documents.size());
		//doc.setTitle(title);
		//_documents.add(doc);
		  // store the document in our index	
		String filePath = _options._indexPrefix+"/"+_numDocs;     
	    BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
	    //out.write(doc._docid+"\n");
	    out.write(title+"\n");
	 
		int totalcount=generateIndex(title+body,_numDocs);
		out.write(totalcount+"\n");
		out.close();
		++_numDocs;
	  }
	  catch(Exception e){
		  System.out.println("The file that has error");
	  }
	   
}
  private int generateIndex(String content,int docid){
	  Scanner s = new Scanner(content);  // Uses white space by default.
	  int totalcount = 0;
	    while (s.hasNext()) {
	      ++_totalTermFrequency;
	      ++totalcount;
	      String token = s.next();
	      // decrement the size() by 1 as the real doc id
	      //int did=_documents.size()-1;
	     
	      Vector<Integer> plist=_index.get(token);
	      if(plist!=null){
	    	  if(plist.lastElement()!=docid){	  
	    	  plist.add(docid);
	    	  _index.put(token,plist); 
	    	  }
	      }else{
	    	  //_terms.add(token);
	    	  Vector<Integer> p=new Vector<Integer>();
	    	  p.add(docid);
	          _index.put(token, p); 
	      }
	 
	    }
	    
	    return totalcount;
  }
  
  @Override
  public void loadIndex(){
	  String indexFile = _options._indexPrefix + "/statistics";
	 
	    System.out.println("Load index from: " + indexFile);
	    try{
	    BufferedReader reader = new BufferedReader(new FileReader(indexFile));
	    String line;
	    int count=0;
	    while((line=reader.readLine())!=null){
	    	++count;
	    	if(count==1)
	    	_numDocs=Integer.parseInt(line);
	    	if(count==2)
	    	_totalTermFrequency=Integer.parseInt(line);
	  	
	    }
	    reader.close();  
	    System.out.println("Indexed " + Integer.toString(_numDocs) + " docs with " +
	            Long.toString(_totalTermFrequency) + " terms.");
	    }catch(Exception e){
	    	
	    }
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
				++count;
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
   * In HW2, you should be using {@link DocumentIndexed}
   */
  @Override
  public DocumentIndexed nextDoc(Query query, int docid) {
	  String path="tempIndex/temp0.txt";
	  Vector<Integer> ids=new Vector<Integer>();
	  //System.out.println(query._tokens.size());
	  for(int i=0;i<query._tokens.size();i++){
	  String term="";
	  try {
		BufferedReader reader=new BufferedReader(new FileReader(path));
		String line;
		while((line=reader.readLine())!=null){
			Scanner s=new Scanner(line).useDelimiter("\t");
			if(s.hasNext()){
				String doc=s.next();
				if(doc.equals(query._tokens.get(i))){   //found term
					term=s.next();
					break;
				}
			}
		}
		
		String[] pos=term.split("\\|");  //get all docids that have term
		for(String s:pos){
			if(Integer.parseInt(s)>docid){
				ids.add(Integer.parseInt(s));
				break;
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
		  DocumentIndexed doc=getDoc(ids.get(0));
		   return doc;
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

 /* private int next(String word, int docid){
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
  }*/
  
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
	  Options option = new Options("conf/engine.conf");
	  IndexerInvertedDoconly index = new IndexerInvertedDoconly(option);
   	  //index.constructIndex();
   	  index.loadIndex();
//   	  Query q=new Query("land landfall label");
//   	  q.processQuery();
//	  DocumentIndexed d=index.nextDoc(q,3);
//	  if(d!=null){
//	  System.out.println(d._docid);
//	  }
  }
  
}
  
