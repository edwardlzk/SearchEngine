package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * 
 * TODO bytes[] for line
 * 
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedOccurrence extends Indexer {
	private HashMap<String, HashMap<Integer, Vector<Integer>>> _index = new HashMap<String, HashMap<Integer, Vector<Integer>>>();
	private Map<Integer,Float> pageranks = null;
	private Map<Integer,Integer> numviews = null;
	// map the doc id to the index of the _documents for fast documents retrieval
	private Map<Integer,Integer> idToIndex = new HashMap<Integer,Integer>();
	// Stores all Document in memory.
	private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
	private Map<String,Map<Integer,Vector<Integer>>> termtemp = new LRUMap<String,Map<Integer,Vector<Integer>>>(1000,1000);

	

	public IndexerInvertedOccurrence() {
	}

	public IndexerInvertedOccurrence(Options options) {
		super(options);
		System.out.println("Using Indexer: " + this.getClass().getSimpleName());
	}

	@Override
	public void constructIndex() throws IOException {
	  	// load the page Rank value
	  	CorpusAnalyzer ca = new CorpusAnalyzerPagerank(_options);
	  	pageranks = (HashMap<Integer,Float>)ca.load();
	  	LogMinerNumviews log = new LogMinerNumviews(_options);
	  	numviews = (HashMap<Integer,Integer>)log.load();
		String corpusFile = _options._corpusPrefix + "/";
		System.out.println("Construct index from: " + corpusFile);

	    String path = _options._indexPrefix+"/"+"idToTitle";
		File idfile = new File(path);
		if(idfile.exists())
			  idfile.delete();
		chooseFiles cf = new chooseFiles(_options);
		int times = cf.writeTimes();
		System.out.println(times);
		FileOps filewriter = new FileOps(_options._indexPrefix + "/");
		String[] tempFiles = new String[times];
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			Vector<String> files = cf.loadFile(i);
			for (String name : files) {
				String filepath = corpusFile + name;
				File file = new File(filepath);
				// the content returned by processing html is a String array, the first element is the id
				// the second element is the real content after processing
				String[] content = ProcessHtml.process(file, _options);
				if (content != null)
					processDocument(content,name);
			}
			String name = "temp" + i + ".txt";
			tempFiles[i] = name;
			Map<String, String> content = new HashMap<String, String>();
			for (String term : _index.keySet()) {
				StringBuilder value = new StringBuilder();

				Set<Integer> keys = _index.get(term).keySet();
				Integer[] did = new Integer[keys.size()];
				int count = 0;
				for (Integer key : keys) {
					did[count++] = key;
				}
				Arrays.sort(did);
				for (count = 0; count < did.length; count++) {
					value.append(did[count]).append(",");
					for (int p : _index.get(term).get(did[count])) {
						value.append(p).append(",");
					}
					value.deleteCharAt(value.length() - 1);
					value.append("|");
				}
				value.deleteCharAt(value.length() - 1);
				content.put(term, value.toString());
			}
			filewriter.write(name, content);
			_index.clear();
		}
		String corpus_statistics = _options._indexPrefix + "/" + "statistics";
		BufferedWriter outsta = new BufferedWriter(new FileWriter(
				corpus_statistics));
		// the first line in the corpus_statistics is the number of docs in the
		// corpus
		outsta.write(_numDocs + "\n");
		outsta.write(_totalTermFrequency + "\n");
		outsta.close();
//***		filewriter.merge(tempFiles, "merge.txt", "|");
		filewriter.merge(tempFiles, "index.idx", "|");


	}

	private void processDocument(String[] content,String docname) {
		try {
			Scanner s = new Scanner(content[1]).useDelimiter("\t");
			String title = s.next();
			String body = s.next();
			s.close();
			++_numDocs;
			int did = Integer.parseInt(content[0]);
			generateIndex(did,title + body, title,docname);
		} catch (Exception e) {
			System.out.println("The file that has error");
		}
	}

	private void generateIndex(int did,String content, String title,String docname) {
		Scanner s = new Scanner(content); // Uses white space by default.
		int pos = 1;
		int totalcount = 0;
		HashMap<String, Vector<Integer>> t_plist = new HashMap<String, Vector<Integer>>();
		Vector<Integer> t_poslist = null;
		while (s.hasNext()) {
			++_totalTermFrequency;
			++totalcount;
			String token = s.next();
			if (!t_plist.containsKey(token)) {
				t_poslist = new Vector<Integer>();
				t_plist.put(token, t_poslist);
			}
			t_poslist = t_plist.get(token);
			t_poslist.add(pos);
			++pos;
		}
		for (String term : t_plist.keySet()) {
			if (!_index.containsKey(term)) {
				HashMap<Integer, Vector<Integer>> n_list = new HashMap<Integer, Vector<Integer>>();
				n_list.put(did, t_plist.get(term));
				_index.put(term, n_list);
			} else {
				HashMap<Integer, Vector<Integer>> plist = _index.get(term);
				plist.put(did, t_plist.get(term));
				_index.put(term, plist);
			}

		}
		try {
		    // idToTitle is actually id to original FileName
		    String path = _options._indexPrefix+"/"+"idToTitle";
		    File file = new File(path);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
		    BufferedWriter out = new BufferedWriter(new FileWriter(path,true));
		    float pagerank = 0;
		    if(this.pageranks.containsKey(did))
		    	pagerank = this.pageranks.get(did);
		    int views = 0;
		    if(this.numviews.containsKey(did))
		    	views = this.numviews.get(did);
		    out.append(did+"\t"+title+"\t"+docname+"\t"+totalcount+"\t"+pagerank+"\t"+views+"\n");
		    out.close();    
		    }catch(IOException e){
		    	e.printStackTrace();	    	
		    } 
		return;
	}

	@Override
	public void loadIndex() throws IOException, ClassNotFoundException {
		String indexFile = _options._indexPrefix + "/statistics";
		System.out.println("Load index from: " + indexFile);
		BufferedReader reader = new BufferedReader(new FileReader(indexFile));
		String line = null;
		if ((line = reader.readLine()) != null)
			this._numDocs = Integer.parseInt(line);
		if ((line = reader.readLine()) != null)
			this._totalTermFrequency = Integer.parseInt(line);
		reader.close();
		System.out.println("Number of docs: " + this._numDocs);
		System.out.println("TotalTermFrequency: " + this._totalTermFrequency);
		System.out.println(_numDocs + " documents loaded ");
	    reconstructDocs();

	}
	private void reconstructDocs() throws NumberFormatException, IOException{
   		
   		BufferedReader reader = new BufferedReader(new FileReader(_options._indexPrefix+"/"+"idToTitle"));
   		String line = null;
   		while((line = reader.readLine())!=null){
   			String[] linecontents = line.split("\t");
   	    	int id	= Integer.valueOf(linecontents[0]);
   	    	DocumentIndexed doc = new DocumentIndexed(id);
   	    	doc.setTitle(linecontents[1]);
   	    	doc.setUrl(linecontents[2]);
   	    	doc.setTermTotal(Integer.parseInt(linecontents[3]));
   	    	doc.setPageRank(Float.parseFloat(linecontents[4]));
   	    	doc.setNumViews(Integer.parseInt(linecontents[5]));
   	    	this._documents.add(doc);
   	    	this.idToIndex.put(id, this._documents.size()-1);
   		}
   		reader.close();
   }
	  @Override
	  public DocumentIndexed getDoc(int docid) {
		if(!this.idToIndex.containsKey(docid))
			return null;
		int index = this.idToIndex.get(docid);
	    return _documents.get(index);
	  }
	  // get the doc id list and position list from the index
	  private Map<Integer, Vector<Integer>> getTerm(String term){
		  if(termtemp.containsKey(term))
			  return termtemp.get(term);
		  Map<Integer,Vector<Integer>> res = new HashMap<Integer,Vector<Integer>>();// each doc and its positions
//***		  String indexFile = _options._indexPrefix + "/merge.txt";
			  String indexFile = _options._indexPrefix + "/index.idx";
			try {
				BufferedReader reader = new BufferedReader(
						new FileReader(indexFile));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] data = line.split("\t");
					if (data[0].equals(term)) { // find the term
						String[] docs = data[1].split("\\|");
						for(String doc:docs){
							String[] pos = doc.split(",");
							Vector<Integer> poslist = new Vector<Integer>();
							int did = Integer.parseInt(pos[0]);
							for(int i=1;i<pos.length;i++){
								poslist.add(Integer.parseInt(pos[i]));
							}
							res.put(did, poslist);
						}
						termtemp.put(term, res);
						return res;
					}
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		  return res;
	  }
	/**
	 * In HW2, you should be using {@link DocumentIndexed}.
	 */
	 @Override
//	  public DocumentIndexed nextDoc(Query query, int docid) {
//		  Vector<Integer> ids=new Vector<Integer>();
//		   int id;
//		   for(int i=0;i<query._tokens.size();i++){
//			   System.out.println(query._tokens.size());
//			 id=next(query._tokens.get(i),docid);
//			 // only add the id that exists
//			 if(id != -1 )
//				 ids.add(id);  
//		   }
//		   // return null if no document contains any term of the query or when couldn't find any document that contains that term
//		   if(ids.size()==0 || ids.size()!=query._tokens.size()){
//			   return null;
//		   }
//		   else if(find(ids))
//		   { 
//			   return getDoc(ids.get(0));
//		   }
//		   else{
//			  return nextDoc(query, max(ids)-1); 
//		   }
//	  }
	 
	  public DocumentIndexed nextDoc(Query query, int docid) {
		  Vector<Integer> ids=new Vector<Integer>();
		   int id=-1;
		   int add=0;
		   Query qtemp = null;
		   for(int i=0;i<query._tokens.size();i++){
			   //System.out.println(query._tokens.size());
			   if(query._tokens.get(i).contains(" ")){
				   String[] tokens=query._tokens.get(i).split(" ");
				   add+=tokens.length-1;
				   StringBuilder qt=new StringBuilder("");
				   for(int c=0;c<tokens.length;c++){
					   qt.append(tokens[c]+" ");
					   id=next(tokens[c],docid);
					   if(id != -1 )
							 ids.add(id); 
				   }
				   qtemp=new Query(qt.toString());
				   qtemp.processQuery();
				   //System.out.println(qtemp._tokens.size());
			   }
			   else{ 
			       id=next(query._tokens.get(i),docid);
			       if(id != -1 )
						 ids.add(id);
			       }
			 // only add the id that exists
			   
		   }
		   // return null if no document contains any term of the query or when couldn't find any document that contains that term
		   if(ids.size()==0 || ids.size()!=(query._tokens.size()+add)){
			   return null;
		   }
		   // find a document inlcudes all the terms
		   else if(find(ids))
		   { 
			   //not phrase
			   if(add==0){
			   return getDoc(ids.get(0));
			   }
			   //phrase
			   else{
				   int pos=nextPhrase(qtemp,ids.get(0),0);
				   // find position
				   if(pos!=-1){
					   return getDoc(ids.get(0));
				   } 
				   //not find, continue with next Document
				   else
					   return nextDoc(query, ids.get(0));
			   }
		   }
		   else{
			  return nextDoc(query, max(ids)-1); 
		   }
		
	  }
	 
	  private int next(String word, int docid){
			 Map<Integer,Vector<Integer>> res = null;
			if(termtemp.containsKey(word)){
				 res = termtemp.get(word);
			}
			else{
				res = getTerm(word);
				termtemp.put(word, res);
				System.out.print(word+":");
				System.out.println(res.size());
			}
			if(res.size()==0)
				return -1;
			TreeSet<Integer> keySet = new TreeSet<Integer>(res.keySet());
			Integer nextdoc = keySet.higher(docid);
		    return nextdoc==null? -1:nextdoc;
		} 
	  
	private boolean find(Vector<Integer> ids) {
		int first = ids.get(0);
		for (int i = 1; i < ids.size(); i++) {
			if (ids.get(i) != first)
				return false;
		}
		return true;
	}

	private int max(Vector<Integer> ids) {
		int max = 0;
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) > max)
				max = ids.get(i);
		}
		return max;
	}
	  public int nextPhrase(Query query, int docid, int pos){
		  Document idVerify=nextDoc(query,docid-1);
		  if(idVerify==null || !idVerify.equals(getDoc(docid))){
			  //System.out.println("Enter here");
			  return -1;
		  }
		  Vector<Integer> poslist=new Vector<Integer>();
		   int id=0;
		   for(int i=0;i<query._tokens.size();i++){
			 id=next_pos(query._tokens.get(i),docid,pos);
			 if(id==-1)
				 return -1;
			 poslist.add(id);
		   }
		   if (checkContinuous(poslist))
			   return poslist.get(0);
		   else
			   return nextPhrase(query,docid,max(poslist)-1);
	  }
	  private boolean checkContinuous(Vector<Integer> poslist)
	  {
		 if(poslist == null || poslist.size()==0)
			 return false;
		 for(int i=1;i<poslist.size();i++)
		 {
			 if(poslist.get(i) != poslist.get(i-1)+1)
				 return false;			 
		 }
		 return true;
	  }
	  // the next occurrence of the term in docid after pos 
	  private int next_pos(String word,int docid,int pos){
		  //Map<Integer,Vector<Integer>> res = getTerm((long)word.hashCode()+(long)con);
		  Map<Integer,Vector<Integer>> res = null;
		  if(termtemp.containsKey(word)){
			  res = termtemp.get(word);
		  }
		  else{
			  res = getTerm(word);
			  termtemp.put(word, res);
		  }
		  if(res.size()==0|| !res.containsKey(docid) || res.get(docid).size()==0)
			  return -1; 
		  Vector<Integer> poslist = res.get(docid);
		  if(poslist.get(0)>pos)
			  return poslist.get(0);
		  if(poslist.get(poslist.size()-1)<=pos)
			  return -1;
		  TreeSet<Integer> posSet = new TreeSet<Integer>(res.get(docid));
		  Integer nextpos = posSet.higher(pos);
		  return nextpos==null? -1:nextpos;
	}
	  @Override
	  public int corpusDocFrequencyByTerm(String term) {
		  Map<Integer,Vector<Integer>> res = null;
		  if(termtemp.containsKey(term)){
			  res = termtemp.get(term);
		  }
		  else{
			  res = getTerm(term);
			  termtemp.put(term, res);
		  }
		  return res.size();
	  }

	  @Override
	  public int corpusTermFrequency(String term) {
		  if (queryCache.containsKey(term)) {
				return queryCache.get(term);
			}
		  Map<Integer,Vector<Integer>> res = null;
		  //Map<Integer,Vector<Integer>> res = getTerm((long)term.hashCode()+(long)con);
		  if(termtemp.containsKey(term)){
			  res = termtemp.get(term);
		  }else{
			  res = getTerm(term);
			  termtemp.put(term, res);
		  }
		  int total=0;
		  for(int did:res.keySet())
		  {
			  total += res.get(did).size();
		  }
		  
		  queryCache.put(term, total);
		  return total;
	  }

	  @Override
	  public int documentTermFrequency(String term, String url) {
		  int did = Integer.parseInt(url);
		  Map<Integer,Vector<Integer>> res = null;
		  int freq = 0;
		  if(termtemp.containsKey(term)){
			  res = termtemp.get(term);
		  }else{
			  res = getTerm(term);
			  termtemp.put(term, res);
		  }
		  if(res.containsKey(did))
			  freq = res.get(did).size();
		  return freq;
	  }
	
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		Options option = new Options("conf/engine.conf");
		IndexerInvertedOccurrence index = new IndexerInvertedOccurrence(option);

		//index.constructIndex();
		index.loadIndex();
		//
		Query query = new QueryPhrase("\"web searching\" google");
		query.processQuery();
		Document nextdoc=index.nextDoc(query, 0);;
		int id=nextdoc._docid;
		int count=1;
	
    while(nextdoc!=null){
		 nextdoc= index.nextDoc(query, id);
		 if(nextdoc!=null){
		 id=nextdoc._docid;
		 count++;
		 }
    }
    System.out.print(count);


	}
}
