package edu.nyu.cs.cs2580;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
public class DocumentIndexed extends Document {
  private static final long serialVersionUID = 9184892508124423115L;
  private int _termTotal; // number of terms in this doc
  private int _termDocFreq;  // the term appear in how many docs
  private int _termCorpusFreq;  // the term appear times in the whole corpus
  
  public DocumentIndexed(int docid) {
    super(docid);
  }
  public int getTermTotal(){
	  return _termTotal;
  }
  public void setTermTotal(int total){
	  _termTotal=total;
  }
  public int getTermDocFreq(){
	  return _termDocFreq;
  }
  public void setTermDocFreq(int docf){
	  _termDocFreq=docf;
  }
  public int getTermCorpusFreq(){
	  return _termCorpusFreq;
  }
  public void setTermCorpusFreq(int corf){
	  _termCorpusFreq=corf;
  }
  
}
