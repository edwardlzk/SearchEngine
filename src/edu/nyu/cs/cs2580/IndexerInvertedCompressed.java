package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 * 
 * TODO compress method
 */
public class IndexerInvertedCompressed extends Indexer{
  private static final int vmax = 128;
  private static final int con = Integer.MAX_VALUE;
  private static final String mergefile = "index.idx";
  private static String baseName;
  private Map<Integer,Float> pageranks = null;
  private Map<Integer,Integer> numviews = null;
  private Map<Long,Map<Integer,Vector<Integer>>> termtemp = new LRUMap<Long,Map<Integer,Vector<Integer>>>(1000,1000);
//  private final byte[] newline = "\n".getBytes("UTF-8");
  
  // the first number in the vector<Byte> is the doc id, the second number is the number of word occurrence, 
  // then follows the specific position number
  private HashMap<String, Vector<Vector<Byte>>> _index=new HashMap<String,Vector<Vector<Byte>>>();
	  //Stores all Document in memory.
  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  public IndexerInvertedCompressed(){}
  
  public IndexerInvertedCompressed(Options options) {
    super(options);
    baseName = _options._indexPrefix+"/";
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
	  	// load the page Rank value
	  	CorpusAnalyzer ca = new CorpusAnalyzerPagerank(_options);
	  	pageranks = (HashMap<Integer,Float>)ca.load();
	  	LogMinerNumviews log = new LogMinerNumviews(_options);
	  	numviews = (HashMap<Integer,Integer>)log.load();
	    String corpusFile = _options._corpusPrefix+"/";
	    System.out.println("Construct index from: " + corpusFile);
	    String path = _options._indexPrefix+"/"+"idToTitle";
		  File idfile = new File(path);
		  if(idfile.exists())
			  idfile.delete();
		  chooseFiles cf=new chooseFiles(_options);
		  int times = cf.writeTimes();
		  System.out.println(times);
		  FileOutputStream fos = null;
		  for(int i=0;i<times;i++){
			  Vector<String> files=cf.loadFile(i);		 
			  for(String name:files){
		        String filepath=corpusFile+name;
		        File file=new File(filepath);
		        String content = ProcessHtml.process(file);
		        if (content != null)
		        	processDocument(content,name);  
			  }		  
			  String name="temp"+i+".txt";
			  Map<Long,Vector<Byte>> map = new HashMap<Long,Vector<Byte>>();
			  for(String term:_index.keySet())
			  {
				  // convert the term to its hashcode and convert it to vector<byte>
				  long termhash = (long)term.hashCode()+(long)con;
				  Vector<Byte> finalbytes = new Vector<Byte>();
				  for(Vector<Byte> bytes:_index.get(term))
				  {
					  int count = bytes.size();
					  finalbytes.addAll(vbyteConversion(count));
					  finalbytes.addAll(bytes);
				  }
				  
					  map.put(termhash, finalbytes);
			  }
			  write(name,baseName, map);
			  map.clear();
			  _index.clear();  
			  System.out.println(i);
		  }
		 
		  String corpus_statistics = _options._indexPrefix+"/" + "statistics";
		  FileOutputStream fos_corpus = new FileOutputStream(corpus_statistics);
		  // first write the num of Docs to the file
		  byte[] v_num_ar = vbyteConversionToArray(this._numDocs);
		  fos_corpus.write(v_num_ar);
		  // write the total term frequency to the file
		  Vector<Byte> tot_arr = this.vbyteConversion(this._totalTermFrequency);
		  byte[] v_tot_arr = new byte[tot_arr.size()];
		  for(int j=0;j<tot_arr.size();++j)
			  v_tot_arr[j] = tot_arr.get(j);
		  fos_corpus.write(v_tot_arr);
		  fos_corpus.close();
		  String[] files=new String[times];
		  for(int count=0;count<times;count++){
		  files[count]="temp"+count+".txt";
		  System.out.println(files[count]);
		  }
		  merge(files, mergefile, baseName);
		  
  }
  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
	  String indexFile = _options._indexPrefix+"/" + "statistics";
	    System.out.println("Load index from: " + indexFile);
	    FileInputStream s = new FileInputStream(indexFile);
	    Vector<Byte> sta = new Vector<Byte>();
	    int cur = 0;
	    while((cur=s.read())!=-1)
	    	{	
	    		 sta.add((byte)cur);   	 
	    	}
	    s.close();
	    Vector<Integer> sta_num = extractNumbers(sta);
	    this._numDocs = sta_num.get(0);
	    this._totalTermFrequency = sta_num.get(1);
	    System.out.println("Number of docs: "+this._numDocs);
	    System.out.println("TotalTermFrequency: "+this._totalTermFrequency);
	    System.out.println(Integer.toString(_numDocs) + " documents loaded ");
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
    		}
    		reader.close();
    }
  	private void processDocument(String content,String docname) throws IOException{
		    Scanner s = new Scanner(content).useDelimiter("\t");
		    String title = s.next();
		    String body = s.next();
		    s.close();
		    DocumentIndexed doc = new DocumentIndexed(_documents.size());
		    doc.setTitle(title);
		    _documents.add(doc);
		    ++_numDocs;
		    generateIndex(title+body,title,docname);
	}
/*
 *  <In each doc we generated, the first term is the doc title in hashcode>
 *  the second term is the total term counts
 *  then term, count .....	  
 */
	 private void generateIndex(String content,String title,String docname) throws IOException{
		  Scanner s = new Scanner(content);  // Uses white space by default.
		  int pos=1;
		  int totalcount = 0;
		  int did=_documents.size()-1;
		  HashMap<String,Vector<Byte>> plist = new HashMap<String,Vector<Byte>>();
		  // first convert doc_id to the Vector<Byte>
		  Vector<Byte> v_did = vbyteConversion(did);
		  Vector<Byte> enposition = null;
		    while (s.hasNext()) {
		     // the total terms in the this doc
		      ++totalcount;
		      ++_totalTermFrequency;
		      String token = s.next();	
		      if (!plist.containsKey(token)) {
		    	  enposition = new Vector<Byte>();
		    	  // the term first time occurs, add the doc_id to the enposition
		    	  enposition.addAll(v_did);
		    	  // Then convert the position to the Vector<Byte>
		    	  Vector<Byte> v_pos = vbyteConversion(pos);
		    	  // add the v_pos to the enposition
		    	  enposition.addAll(v_pos);
		    	  // add put the token and enposition to plist
		    	  plist.put(token,enposition);
		      }else{
		    	  enposition = plist.get(token);
		    	  // add the position to the plist
		    	  Vector<Byte> v_pos = vbyteConversion(pos);
		    	  enposition.addAll(v_pos);
		      }
		      ++pos;
		    }
		   // add the plist to the index
		    for(String term:plist.keySet())
		    {
		    	if(!_index.containsKey(term)){
		    		Vector<Vector<Byte>> nlist = new Vector<Vector<Byte>>();
		    		nlist.add(plist.get(term));
		    		_index.put(term, nlist);
		    	}
		    	else{
		        	Vector<Vector<Byte>> nlist = _index.get(term);
		        	nlist.add(plist.get(term));
		    	}	    		
		    }
		    try{
			    // store the document in our index	        
			    String filePath = _options._indexPrefix+"/"+did;
			    FileOutputStream fos = new FileOutputStream(filePath);
//			    int t_title = title.hashCode();
//			    byte[] v_t_title = vbyteConversionToArray(t_title);
//			    fos.write(v_t_title);
			    // then write the total term in this document
			    byte[] v_totcount = vbyteConversionToArray(totalcount);
			    fos.write(v_totcount);
			    for(String term:plist.keySet())
			    {
			    	// first write the term in hashcode to the file
			    	long hash_term = (long)term.hashCode()+(long)con;
			    	byte[] v_hash_term = vbyteConversionToArray(hash_term);
			    	fos.write(v_hash_term);
			    	// count how many times the term occurs in this document and write to the file
			    	int termcounts = countVectorListNumber(plist.get(term))-1;
			    	byte[] v_counts = vbyteConversionToArray(termcounts);
			    	fos.write(v_counts);	    	
			    }
			    fos.close();
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
 // @tested Convert the docid and the position values
  private Vector<Integer> extractNumbers(Vector<Byte> poslist)
  {
	  if(poslist == null || poslist.size()==0)
		  throw new IllegalArgumentException("List is null, no information can be extracted");
	  Vector<Integer> res = new Vector<Integer>();
	  Vector<Byte> curNum = new Vector<Byte>();
	  for(int i=0;i<poslist.size();i++)
	  {
		  curNum.add(poslist.get(i));
		  if((poslist.get(i)&(1<<7))>0) // ends of the current number
		  {
			  int num = convertVbyteToNum(curNum);
			  res.add(num);
			  curNum.clear();
		  }
	  }
	  return res;	  
  }
  // calculate how many numbers in the list, 
  // eg after decoding the values in the list is  1 3 15 4 6
  // we should return 5
  //@tested
  public int countVectorListNumber(Vector<Byte> list)
  {
	  int res =0;
	  for(Byte x: list)
	  {
		  if((x&(1<<7))>0)
			  ++res;
	  }
	  return res;
  }
  // @tested calculate the number of bytes for the title
   static int bytesInTitle(Vector<Byte> list)
  {
	  int i = 0;
	  for(int j=0;j<list.size();j++)
	  {
		  ++i;
		  if((list.get(j)&(1<<7))>0)
			  break;
	  }
	  return i;
  }
  
 // @Tested Convert an integer to a byte vector
  public Vector<Byte> vbyteConversion(int num)
  {
	  
	  Vector<Byte> num_to_bytes = new Vector<Byte>();
	  boolean firstByte = true;
	  if(num == 0)
		  num_to_bytes.add((byte)(1<<7));
	  while(num>0)
	  {
		  byte bytenum = (byte)(num % vmax);
		  num = num >> 7;
		  if (firstByte)
		  {
			  // indicate the end of a byte, set the hightest bit to 1
			  bytenum |= 1 << 7;
			  firstByte = false;
		  }
		  num_to_bytes.add(bytenum);	  
	  }
	  Collections.reverse(num_to_bytes);
	  return num_to_bytes;
  }
  //@tested
  public byte[] vbyteConversionToArray(int num)
  {
	  if(num == 0){
		  byte[] res = new byte[1];
		  res[0] = (byte)(1<<7);
		  return res;
	  }
	  int count = 0;
	  int temp = num;
	  boolean firstByte = true;
	  while(temp>0){
		  ++ count;
		  temp = temp >> 7;
	  }
	  byte[] res = new byte[count];
	  int i =0;
	  while(num > 0){
		  byte bytenum = (byte)(num % vmax);
		  num = num >> 7;
		  if (firstByte)
		  {
			  // indicate the end of a byte, set the hightest bit to 1
			  bytenum |= 1 << 7;
			  firstByte = false;
		  }
		  res[count-1-i] = bytenum;
		  i++;
	  }
	  return res;
  }
  //@tested
  public byte[] vbyteConversionToArray(long num)
  {
	  if(num == 0){
		  byte[] res = new byte[1];
		  res[0] = (byte)(1<<7);
		  return res;
	  }
	  int count = 0;
	  long temp = num;
	  boolean firstByte = true;
	  while(temp>0){
		  ++ count;
		  temp = temp >> 7;
	  }
	  byte[] res = new byte[count];
	  int i =0;
	  while(num > 0){
		  byte bytenum = (byte)(num % vmax);
		  num = num >> 7;
		  if (firstByte)
		  {
			  // indicate the end of a byte, set the hightest bit to 1
			  bytenum |= 1 << 7;
			  firstByte = false;
		  }
		  res[count-1-i] = bytenum;
		  i++;
	  }
	  return res;
  }
//@tested Convert an long to a byte vector
 public Vector<Byte> vbyteConversion(long num)
 {
	  Vector<Byte> num_to_bytes = new Vector<Byte>();
	  boolean firstByte = true;
	  if(num == 0)
		  num_to_bytes.add((byte)(1<<7));
	  while(num>0)
	  {
		  byte bytenum = (byte)(num % vmax);
		  num = num >> 7;
		  if (firstByte)
		  {
			  // indicate the end of a byte, set the hightest bit to 1
			  bytenum |= 1 << 7;
			  firstByte = false;
		  }
		  num_to_bytes.add(bytenum);	  
	  }
	  Collections.reverse(num_to_bytes);
	  return num_to_bytes;
 }
  // @tested
	  public int convertVbyteToNum(Vector<Byte> vbyte)
	  {
		  Collections.reverse(vbyte);
		  if (vbyte == null || vbyte.size() == 0)
			  return -1;
		  int res=0;
		  res += (int) (vbyte.get(0) & ((1<<7)-1)) ;
		  for(int i=1;i<vbyte.size();i++)
		  {
			  res+=vbyte.get(i)*((int)Math.pow(vmax, i));
		  }		  
		  return res;
	  }
	  public long convertVbyteToNumLong(Vector<Byte> vbyte)
	  {
		  
		  Collections.reverse(vbyte);
		  if (vbyte == null || vbyte.size() == 0)
			  return -1;
		  long res=0;
		  res += (long) (vbyte.get(0) & ((1<<7)-1)) ;
		  for(int i=1;i<vbyte.size();i++)
		  {
			  res+=vbyte.get(i)*((long)Math.pow(vmax, i));
		  }		  
		  return res;
	  }
 // @tested decode the byte array to a number 
	  public int convertVbyteToNum(byte[] vbyte)
	  {
		  if(vbyte.length == 0)
			  return -1;
		  int res = 0;
		  res += (long)(vbyte[vbyte.length-1] & ((1<<7)-1));
		  for(int i=vbyte.length-2;i>=0;--i)
			  res+=vbyte[i]*((int)Math.pow(vmax, (vbyte.length-1-i)));
		  return res;
	  }
	  public long convertVbyteToNumLong(byte[] vbyte)
	  {
		  if(vbyte.length == 0)
			  return -1;
		  long res = 0;
		  res += (long)(vbyte[vbyte.length-1] & ((1<<7)-1));
		  for(int i=vbyte.length-2;i>=0;--i)
			  res+=vbyte[i]*((long)Math.pow(vmax, (vbyte.length-1-i)));
		  return res;
	  }
  // @tested extract the docId for encoded list
  private int extractDocId(Vector<Byte> enposition) throws IOException
  {
	  if(enposition==null || enposition.size()==0)
		  throw new IOException("There is no doc id inside the encoded list");
	  Vector<Byte> did = new Vector<Byte>();
	  for(int i=0;i<enposition.size();i++)
	  {
		  did.add(enposition.get(i));	
		  // test if the current byte is the end byte of the number
		  if((enposition.get(i)&(1<<7))>0)
			  break;
		    
	  }
	  return convertVbyteToNum(did) ;
  }
  
  
  // extract all the docIDs that contains the term
  public Vector<Integer> extractAlldids(String term)
  {
	  if (!_index.containsKey(term))
		  return null;
	  Vector<Vector<Byte>> plist = _index.get(term);
		Vector<Integer> docIDs = new Vector<Integer>();
		for(Vector<Byte> enlist: plist)
		{
			// extract the doc id
			int did;
			try {
				did = extractDocId(enlist);
				docIDs.add(did);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return docIDs;
  }
 
  @Override
  public Document getDoc(int docid) {
	if (docid<0 || docid > _documents.size()-1)
		return null;
    return _documents.get(docid);
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}
   */
  @Override
  public Document nextDoc(Query query, int docid) {
	  Vector<Integer> ids=new Vector<Integer>();
	   int id;
	   for(int i=0;i<query._tokens.size();i++){
		 id=next(query._tokens.get(i),docid);
		 // only add the id that exists
		 if(id != -1 )
			 ids.add(id);  
	   }
	   // return null if no document contains any term of the query or when couldn't find any document that contains that term
	   if(ids.size()==0 || ids.size()!=query._tokens.size()){
		   return null;
	   }
	   else if(find(ids))
	   { 
		   return getDoc(ids.get(0));
	   }
	   else{
		  return nextDoc(query, max(ids)-1); 
	   }
  }
  
  private int next(String word, int docid){
	// Binary Search
	//Map<Integer,Vector<Integer>> res = getTerm((long)word.hashCode()+(long)con);
	 long hashword = (long)word.hashCode()+(long)con;
	 Map<Integer,Vector<Integer>> res = null;
	if(termtemp.containsKey(hashword)){
		 res = termtemp.get(hashword);
	}
	else{
		res = getTerm(hashword);
		termtemp.put(hashword, res);
	}
	if(res.size()==0)
		return -1;
	TreeSet<Integer> keySet = new TreeSet<Integer>(res.keySet());
	Integer nextdoc = keySet.higher(docid);
    return nextdoc==null? -1:nextdoc;
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

  public int nextPhrase(Query query, int docid, int pos){
	  Document idVerify=nextDoc(query,docid-1);
	  if(!idVerify.equals(getDoc(docid))){
		  System.out.println("Enter here");
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
		   return nextPhrase(query,docid,poslist.get(poslist.size()-1));
  }
  
  private boolean checkContinuous(Vector<Integer> poslist)
  {
	 if(poslist == null)
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
	  long hashword = (long)word.hashCode()+(long)con;
	  if(termtemp.containsKey(hashword)){
		  res = termtemp.get(hashword);
	  }
	  else{
		  res = getTerm(hashword);
		  termtemp.put(hashword, res);
	  }
	  if(res.size()==0|| !res.containsKey(docid) || res.get(docid).size()==0)
		  return -1; 
	  Vector<Integer> poslist = res.get(docid);
	  if(poslist.get(0)>pos)
		  return poslist.get(0);
	  if(poslist.get(poslist.size()-1)<pos)
		  return -1;
//	  TreeSet<Integer> posSet = new TreeSet<Integer>(res.get(docid));
//	  Integer nextpos = posSet.higher(pos);
	  int nextpos = binarySearch(res.get(docid),pos);
	  return nextpos;
}
  public static int binarySearch(Vector<Integer> ls, int pos){
	  int low = 0;
	  int high = ls.size()-1;
		while(high-low>1){
	   		  int mid=(low+high) >>> 1;
	   		  if(ls.get(mid)<=pos){
	   			  low=mid+1;
	   		  }else{
	   			  high=mid;
	   		  }
	   	  }
	   		  return ls.get(low)>pos ? ls.get(low):ls.get(high);
  }
  
  @Override
  public int corpusDocFrequencyByTerm(String term) {
	  long hashterm = (long)term.hashCode()+(long)con;
	  Map<Integer,Vector<Integer>> res = null;
	  if(termtemp.containsKey(hashterm)){
		  res = termtemp.get(hashterm);
	  }
	  else{
		  res = getTerm(hashterm);
		  termtemp.put(hashterm, res);
	  }
	  return res.size();
  }

  @Override
  public int corpusTermFrequency(String term) {
	  if (queryCache.containsKey(term)) {
			return queryCache.get(term);
		}
	  long hashterm = (long)term.hashCode()+(long)con;
	  Map<Integer,Vector<Integer>> res = null;
	  //Map<Integer,Vector<Integer>> res = getTerm((long)term.hashCode()+(long)con);
	  if(termtemp.containsKey(hashterm)){
		  res = termtemp.get(hashterm);
	  }else{
		  res = getTerm(hashterm);
		  termtemp.put(hashterm, res);
	  }
	  int total=0;
	  for(int did:res.keySet())
	  {
		  total += res.get(did).size();
	  }
	  
	  queryCache.put(term, total);
	  return total;
  }
  
  private void write(String output, String base, Map<Long, Vector<Byte>> content){
	  try{
	  
	  
	  File file = new File(base+output);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileOutputStream fos = new FileOutputStream(base + output);
		Set<Long> keySet = new TreeSet<Long>(content.keySet());
		for(long i : keySet){
			//First write the term hash
			fos.write(vbyteConversionToArray(i));
			Vector<Byte> termPos = content.get(i);
			//Write size of positions
			fos.write(vbyteConversionToArray(termPos.size()));
			byte[] termPosContent = new byte[termPos.size()];
			for(int j = 0; j<termPos.size(); j++){
				termPosContent[j] = termPos.get(j);
			}
			//write position content
			fos.write(termPosContent);
		}
		
	  }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  
  
  private void merge(String[] tempFiles, String output, String base) throws IOException{
	  FileInputStream[] fis = new FileInputStream[tempFiles.length];
	  PriorityQueue<Long> heap = new PriorityQueue<Long>();
	  Map<Long, LinkedList<FileInputStream>> inputMap = new HashMap<Long, LinkedList<FileInputStream>>();
	  Map<FileInputStream, Integer> inputSeq = new HashMap<FileInputStream, Integer>();
	  Map<FileInputStream, byte[]> valueMap = new HashMap<FileInputStream, byte[]>();
	  
	  File file = new File(base+output);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileOutputStream fos = new FileOutputStream(base + output);
	  
	//construct the heap
	  for(int i = 0; i<tempFiles.length; i++){
		  fis[i] = new FileInputStream(base + tempFiles[i]);
		  inputSeq.put(fis[i], i);
		  //get term hash
		  long termHash = convertVbyteToNumLong(getNextChunk(fis[i]));
		  if(termHash == -1){
			  continue;
		  }
		  //update the inputMap
		  if(inputMap.containsKey(termHash)){
			  inputMap.get(termHash).add(fis[i]);
		  }
		  else{
			  LinkedList<FileInputStream> newInputList = new LinkedList<FileInputStream>();
			  newInputList.add(fis[i]);
			  inputMap.put(termHash, newInputList);
			  heap.offer(termHash);
		  }
		  //store the compressed value of this term.
		  int byteLength = convertVbyteToNum(getNextChunk(fis[i]));
		  byte[] currentValue = new byte[byteLength];
		  fis[i].read(currentValue);
		  valueMap.put(fis[i], currentValue);
	  }
	  
	  //Pop the smallest
	  long current;
	  
	  while(heap.size() > 0){
		  current = heap.poll();
		  //write the term hash first
		  fos.write(vbyteConversionToArray(current));
		  //get related reader
		  LinkedList<FileInputStream> relatedInput = inputMap.get(current);
		  TreeSet<Integer> inputNum = new TreeSet<Integer>();
		  for(FileInputStream f : relatedInput){
			  inputNum.add(inputSeq.get(f));
		  }
		  List<Byte> termRelatedPos = new ArrayList<Byte>();
		  for(int i : inputNum){
			  FileInputStream f = fis[i];

			  byte[] value = valueMap.get(f);
			  //Add the bytes to list
			  for(byte b : value)
				  termRelatedPos.add(b);
			  
			  //Now get next term and value
			  long nextTerm = convertVbyteToNumLong(getNextChunk(f));
			  if(nextTerm != -1){
				  if(inputMap.containsKey(nextTerm)){
					  inputMap.get(nextTerm).add(f);
				  }
				  else{
					  LinkedList<FileInputStream> newInputList = new LinkedList<FileInputStream>();
					  newInputList.add(f);
					  inputMap.put(nextTerm, newInputList);
					  heap.offer(nextTerm);
				  }
				  //store the compressed value of this term.
				  int byteLength = convertVbyteToNum(getNextChunk(f));
				  byte[] nextValue = new byte[byteLength];
				  f.read(nextValue);
				  valueMap.put(f, nextValue);
			  }
		  }
		  inputMap.remove(current);
		  //write the total number
		  fos.write(vbyteConversionToArray(termRelatedPos.size()));
		  //Write the following bytes
		  byte[] result = new byte[termRelatedPos.size()];
		  for(int i = 0; i<termRelatedPos.size(); i++){
			  result[i] = termRelatedPos.get(i);
		  }
		  fos.write(result);
	  }
	  fos.close();
	  //Delete all temporary files
	  for(String tFile : tempFiles){
			File currentFile = new File(base + tFile);
			currentFile.delete();
		}
  }
	  
	  private Vector<Byte> getNextChunk(FileInputStream fis){
		  Vector<Byte> currentChunk = new Vector<Byte>();
		  int current;
		  try {
			while((current = fis.read())!=-1){
				  currentChunk.add((byte)current);
				  if((current & (1<<7)) > 0){
					  //the current byte is the ending byte
					  break;
				  }
			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentChunk;
	  }
	  
	  private Vector<Vector<Byte>> getChunks(byte[] input){
		  Vector<Byte> currentChunk = new Vector<Byte>();
		  Vector<Vector<Byte>> ret = new Vector<Vector<Byte>>();
		  byte current;
		  int index = 0;
			while(index < input.length){
				current = input[index++];
				currentChunk.add((byte)current);
				if((current & (1<<7)) > 0){
					//the current byte is the ending byte
					ret.add(currentChunk);
					currentChunk = new Vector<Byte>();
				}
			}
		return ret;
	  }
	  
	  
	  /**
	   * Get the term information from compressed file
	   * @param termHash
	   * @return
	   */
	  private Map<Integer, Vector<Integer>> getTerm(long termHash){
			
		  
		  Map<Integer, Vector<Integer>> ret = new HashMap<Integer, Vector<Integer>>();
		  
			try {
				FileInputStream fis = new FileInputStream(baseName + mergefile);
				
				long currentTerm;
				while((currentTerm = convertVbyteToNumLong(getNextChunk(fis)))!= -1){
					//Loop when we have following terms
					int length = convertVbyteToNum(getNextChunk(fis));
					if(currentTerm != termHash){
						//skip this term
						fis.skip((long)length);
					}
					else{
						while(length > 0){
							Vector<Byte> lengthByte = getNextChunk(fis);
							int docLength = convertVbyteToNum(lengthByte);
							byte[] doc = new byte[docLength];
							fis.read(doc);
							Vector<Vector<Byte>> chunks = getChunks(doc);
							int docId = convertVbyteToNum(chunks.get(0));
							Vector<Integer> positions = new Vector<Integer>();
							for(int i = 1; i<chunks.size(); i++){
								positions.add(convertVbyteToNum(chunks.get(i)));
							}
							//Add the doc and positions to the map
							ret.put(docId, positions);
							length-=(docLength + lengthByte.size());
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ret;
	  }

	  

  
  /**
   * @CS2580: Implement this for bonus points.
   */
  @Override
  public int documentTermFrequency(String term, String url) {
	  int did = Integer.parseInt(url);
	  if(did<0 || did > this._documents.size()-1){
		  return -1;
	  }
	  Map<Integer,Vector<Integer>> res = null;
	  int freq = 0;
	  long hashterm = (long)term.hashCode()+(long)con;
	  if(termtemp.containsKey(hashterm)){
		  res = termtemp.get(hashterm);
	  }else{
		  res = getTerm(hashterm);
		  termtemp.put(hashterm, res);
	  }
	  if(res.containsKey(did))
		  freq = res.get(did).size();
	  return freq;
  }
  public static void main(String[] args) throws Exception
  {
//	  Options option = new Options("conf/engine.conf");
//	  IndexerInvertedCompressed index = new IndexerInvertedCompressed(option);
//	  index.constructIndex();
//	  index.loadIndex();
//	  System.out.println(index.documentTermFrequency("another","0"));
	  Vector<Integer> test= new Vector<Integer>();
	  test.add(1);
	  test.add(3);
	  test.add(5);
	  test.add(7);
	  int res =IndexerInvertedCompressed.binarySearch(test, 5);
	  System.out.println(res);
	  
//	  String[] corpus = {"test", "1","this","is","another","2","real","3"};
//	  String tempFile = option._indexPrefix+"/"+"idToTitle";
//	    BufferedReader reader = new BufferedReader(new FileReader(tempFile));
//	    String line =null;
//	    while((line = reader.readLine())!=null)
//	    {
//	    	Scanner sca = new Scanner(line).useDelimiter("\t");
//	    	if(sca.hasNext())
//	    		System.out.print("Did is: "+sca.next()+"  ");
//	    	if(sca.hasNext())
//	    		System.out.print("Title is "+sca.next());
//	    	System.out.println();
//	    }
//	    for(String str:corpus)
//	    {
//	    	System.out.println("The hash code for term "+str+" is "+((long)(str.hashCode()+(long)con)));
//	    }
//	  FileInputStream s = new FileInputStream(option._indexPrefix+"/"+"index.txt");
//	    Vector<Byte> sta = new Vector<Byte>();
//	    HashMap<Long,Vector<Vector<Integer>>> res = new HashMap<Long,Vector<Vector<Integer>>>();
//	    int cur = 0;
//	   
//	    boolean flag = true;
//	    while((cur=s.read())!=-1)
//	    	{	
//	    		byte curbyte = (byte) cur;
//	    		// extract the term
//	    		while((curbyte & (1 << 7))==0) // stop when reach the end of the current byte;
//	    		{
//	    			sta.add(curbyte);
//	    			curbyte = (byte)s.read();
//	    		}
//	    		sta.add(curbyte); // add the end byte of the number
//	    		long hashterm = index.convertVbyteToNumLong(sta);
//	    		System.out.println("Recovered: hashterm   "+hashterm);
//	    	//	System.out.println("Term hashcode is: "+hashterm);
//	    		sta.clear();
//	    		// extract the total bytes count
//	    		while((cur = s.read())!=1)
//	    		{
//	    		   curbyte = (byte) cur;
//	    		   sta.add(curbyte);
//	    		   if((curbyte & (1<<7))>0)
//	    			   break;		
//	    		}
//	    		int totalbytes =  index.convertVbyteToNum(sta);
//	    		System.out.println("Here: totalbytes"+totalbytes);
//	    		sta.clear();
//	    		// extract all the docs that contains this term
//	    		int i=0;
//	    		while(i<totalbytes)
//	    		{
//	    			// the first number is the bytes counts for this 
//	    			while((cur = s.read())!=1)
//		    		{
//	    			   ++i;
//		    		   curbyte = (byte) cur;
//		    		   sta.add(curbyte);
//		    		   if((curbyte & (1<<7))>0) 
//		    				   break;
//		    		}
//	    			int didcounts = index.convertVbyteToNum(sta);
//	    			System.out.println("Here: didcounts"+didcounts);
//	    			sta.clear();
//	    	        int j=0;
//	    			while(j<didcounts&&(cur = s.read())!=1)
//		    		{
//	    			   ++i;
//	    			   ++j;
//		    		   curbyte = (byte) cur;
//	    			   sta.add(curbyte);
//		    		}	    			
//	    			Vector<Integer> list= index.extractNumbers(sta);
//	    			// the first time we are meet this term, means we are processing the first doc that has the term
//	    			if(!res.containsKey(hashterm))
//	    			{
//	    				Vector<Vector<Integer>> first = new Vector<Vector<Integer>>();
//	    				res.put(hashterm, first);
//	    			}
//	    			Vector<Vector<Integer>> second = res.get(hashterm);
//	    			second.add(list);
//	    			sta.clear();
//	    		}
//	    	}
//	    s.close();
//	   
//	    for(long hterm:res.keySet())
//	    {
//	    	System.out.print(hterm+": ");
//	    	for(Vector<Integer> li:res.get(hterm))
//	    	{
//	    		System.out.print("[");
//	    		for(Integer t:li)
//	    		{
//	    			System.out.print(t+",");
//	    		}
//	    		System.out.print("]");
//	    	}
//	    	System.out.println();
//	    }
	 
			  
  }
  
}
