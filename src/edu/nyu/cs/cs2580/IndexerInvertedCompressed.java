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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedCompressed extends Indexer{
  private final int vmax = 128;
  
  // the first number in the vector<Byte> is the doc id, the second number is the number of word occurrence, 
  // then follows the specific position number
  private HashMap<String, Vector<Vector<Byte>>> _index=new HashMap<String,Vector<Vector<Byte>>>();
	  //all unique terms
  private Vector<String> _terms = new Vector<String>();
	  //Stores all Document in memory.
  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  public IndexerInvertedCompressed(){}
  
  public IndexerInvertedCompressed(Options options) {
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
			  Map<String, String> content = new HashMap<String,String>();
			  
			  
		  }
//	    Vector<Integer> numlist = null;
//	    int counter=0;
//	    for(int i=0;i<_terms.size();i++){
//	    	System.out.print(_terms.get(i)+":");
//	    	Vector<Vector<Byte>> plist = _index.get(_terms.get(i));
//	    	counter=0;
//	    	for(Vector<Byte> enlist:plist){
//	    		counter++;
//	    		try {
//					numlist = extractNumbers(enlist);
//					System.out.print("(");
//		    		for(int m:numlist)
//		    		{
//		    			System.out.print(m+",");
//		    		}
//		    		System.out.print(")");    		
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	    		
//	    	}
//	    	System.out.println(counter);
//	    }
	    	
  }
  
	  private void processDocument(String content) throws IOException{
		    Scanner s = new Scanner(content).useDelimiter("\t");
		    String title = s.next();
		    String body = s.next();
		    s.close();
		    DocumentIndexed doc = new DocumentIndexed(_documents.size());
		    doc.setTitle(title);
		    _documents.add(doc);
		    ++_numDocs;
		   // generateIndex(title+body);
		    generateIndex(title);
		    generateIndex(body);
		    //System.out.println(title);
		    //System.out.println(body);
	}
	  
	  private void generateIndex(String content) throws IOException{
		  Scanner s = new Scanner(content);  // Uses white space by default.
		  int pos=1;
		  int did=_documents.size()-1;
		    while (s.hasNext()) {
		      String token = s.next();		      
		      if (!_terms.contains(token)) {
		    	  _terms.add(token);
		    	  Vector<Vector<Byte>> plist = new Vector<Vector<Byte>>();
		    	  Vector<Byte> enposition = new Vector<Byte>();
		    	  // first convert doc_id to the Vector<Byte>
		    	  Vector<Byte> v_did = vbyteConversion(did);
		    	  // add the doc_id to the enposition
		    	  enposition.addAll(v_did);
		    	  // Then convert the position to the Vector<Byte>
		    	  Vector<Byte> v_pos = vbyteConversion(pos);
		    	  // add the v_pos to the enposition
		    	  enposition.addAll(v_pos);
		    	  // add the enposition to plist
		    	  plist.add(enposition);
		          _index.put(token, plist);
		      }else{
		    	  Vector<Vector<Byte>> plist=_index.get(token);
		    	  // check if the term is the first appears in this doc, if yes, we need to encode the docid
		    	  int predid = extractDocId(plist.lastElement());
		    	  Vector<Byte> enposition = null;
		    	  // term first time occurs in this doc, encode the docid
		    	  if(predid != did)
		    	  {
		    		  enposition = new Vector<Byte>();
		    		  Vector<Byte> v_did = vbyteConversion(did);
		    		  enposition.addAll(v_did);
		    		  plist.add(enposition);
		    	  }
		    	  enposition = plist.lastElement();
		    	  Vector<Byte> v_pos = vbyteConversion(pos);
		    	  enposition.addAll(v_pos);
		      }
		      pos++;
		      }
		      
		    return;
}
 // Convert the docid and the position values
  private Vector<Integer> extractNumbers(Vector<Byte> poslist) throws IOException
  {
	  if(poslist == null || poslist.size()==0)
		  throw new IOException("List is null, no information can be extracted");
	  Vector<Integer> res = new Vector<Integer>();
	  Vector<Byte> curNum = new Vector<Byte>();
	  curNum.add(poslist.firstElement()); // the first byte of a number always starts with MSB=1
	  for(int i=1;i<poslist.size();i++)
	  {
		  if((poslist.get(i)&(1<<7))>0) // starts a new number
		  {
			  int num = convertVbyteToNum(curNum);
			  res.add(num);
			  curNum.clear();
			  curNum.add(poslist.get(i));
		  }
		  else
			  curNum.add(poslist.get(i));
	  }
	  int num = convertVbyteToNum(curNum); // add the last number in the list
	  curNum.clear();
	  res.add(num); 
	  return res;
	  
  }
 // Convert an integer to a byte vector
  private Vector<Byte> vbyteConversion(int num)
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
	  return num_to_bytes;
  }
  // the vbyte are in reverse order, so the first byte in the vbyte is the Least significant byte
	  private int convertVbyteToNum(Vector<Byte> vbyte)
	  {
		  if (vbyte == null)
			  return -1;
		  int res=0;
		  res += (int) (vbyte.get(0) & ((1<<7)-1)) ;
		  for(int i=1;i<vbyte.size();i++)
		  {
			  res+=vbyte.get(i)*((int)Math.pow(vmax, i));
		  }		  
		  return res;
	  }

//  // Need to add the vetor byte in reverse order
//  private void addNumToVector(Vector<Byte> list,Vector<Byte> toBeAdd)
//  {
//	  for(int i=toBeAdd.size()-1;i>=0;i--)
//	  {
//		  list.add(toBeAdd.get(i));
//	  }
//  }
  // extract the docId for encoded list
  private int extractDocId(Vector<Byte> enposition) throws IOException
  {
	  if(enposition==null || enposition.size()==0)
		  throw new IOException("There is no doc id inside the encoded list");
	  Vector<Byte> did = new Vector<Byte>();
	  // a number contains at least one end byte
	  did.add(enposition.firstElement());
	  for(int i=1;i<enposition.size();i++)
	  {
		  // test if the current byte is the start byte of another number
		  if((enposition.get(i)&(1<<7))>0)
			  break;
		  did.add(enposition.get(i));	  
	  }
	  return convertVbyteToNum(did) ;
  }
  
  
  // extract all the docIDs that contains the term
  private Vector<Integer> extractAlldids(String term)
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
  public void loadIndex() throws IOException, ClassNotFoundException {
	  String indexFile = _options._indexPrefix + "/corpus.idx";
	    System.out.println("Load index from: " + indexFile);

	    ObjectInputStream reader =
	        new ObjectInputStream(new FileInputStream(indexFile));
	    IndexerInvertedCompressed loaded = (IndexerInvertedCompressed) reader.readObject();

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
	   // return null if no document contains any term of the query or when couldn't find any document that contains that term
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
  
  private int next(String word, int docid){
	// Binary Search
	if(_index.size() == 0 || !_index.containsKey(word))
		return -1;
	Vector<Integer> docIDs = extractAlldids(word);
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

  
  public int nextPhrase(Query query, int docid, int pos) throws IOException{
	  Document idVerify=nextDoc(query,docid-1);
	  if(!idVerify.equals(_documents.get(docid))){
		  System.out.println("Enter here");
		  return -1;
	  }
	  Vector<Integer> poslist=new Vector<Integer>();
	   int id=0;
	   for(int i=0;i<query._tokens.size();i++){
		 id=next_pos(query._tokens.get(i),docid,pos);
		 if(id!=-1)
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
  private int next_pos(String word,int docid,int pos) throws IOException{
	  if(!_index.containsKey(word))
		  return -1;
	  Vector<Integer> docIDs=extractAlldids(word);
	  int offset=-1;
	  for(int i=0;i<docIDs.size();i++)
	  {
		 // find the offset of the docid in the list
		  if(docIDs.get(i)==docid)
		  {
			offset=i;
			break;
		  }
	  }
	  if(offset==-1)
			 throw new IOException("This docid doesn't contain this term");
	  Vector<Byte> poslist = _index.get(word).get(offset);
	  // extract the id and all the positions in that id that the term occurs
	  Vector<Integer> enlist = extractNumbers(poslist);
	  if(enlist.lastElement()<=pos)
		  return -1;
	  for(int j=1;j<enlist.size();j++)
	  {
		  if(enlist.get(j)>pos)
			  return enlist.get(j);
	  }
		 return -1;
}
  
  
  @Override
  public int corpusDocFrequencyByTerm(String term) {
    return 0;
  }

  @Override
  public int corpusTermFrequency(String term) {
    return 0;
  }

  /**
   * @CS2580: Implement this for bonus points.
   */
  @Override
  public int documentTermFrequency(String term, String url) {
    return 0;
  }
  public static void main(String[] args) throws Exception
  {
	  
	  Options option = new Options("/Users/Wen/Documents/workspace2/SearchEngine/conf/engine.conf");
	  IndexerInvertedOccurrence index = new IndexerInvertedOccurrence(option);
	  index.constructIndex();
	  Query query = new Query("Bonnie Clyde");
	  query.processQuery();
	  
	  try {
		index.loadIndex();
		Document nextdoc = index.nextDoc(query, 7);
		
		if(nextdoc!=null)
			{
				System.out.println(nextdoc._docid);
			}
		else
			System.out.println("Null");
		
		int x = index.nextPhrase(query,3,0);
		System.out.println("The next position is "+x);
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
  }
  
}
