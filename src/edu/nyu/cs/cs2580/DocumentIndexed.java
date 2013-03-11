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
  private int getTermTotal(){
	  return _termTotal;
  }
  private void setTermTotal(int total){
	  _termTotal=total;
  }
  private int getTermDocFreq(){
	  return _termDocFreq;
  }
  private void setTermDocFreq(int docf){
	  _termDocFreq=docf;
  }
  private int getTermCorpusFreq(){
	  return _termCorpusFreq;
  }
  private void setTermCorpusFreq(int corf){
	  _termCorpusFreq=corf;
  }
  
}
