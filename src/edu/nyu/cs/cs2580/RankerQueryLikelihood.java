package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;
import edu.nyu.cs.cs2580.DocumentIndexed;
import edu.nyu.cs.cs2580.ScoredDocument;

/**
 * @CS2580: Implement this class for HW2 based on a refactoring of your favorite
 * Ranker (except RankerPhrase) from HW1. The new Ranker should no longer rely
 * on the instructors' {@link IndexerFullScan}, instead it should use one of
 * your more efficient implementations.
 */
public class RankerQueryLikelihood extends Ranker {
	
	private double _lambda = 0.5;
	Map<String, int[]> phraseCount = new HashMap<String, int[]>();
	

  public RankerQueryLikelihood(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
  
	  Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
	  
	  System.out.println("numResults is"+numResults);
	  
	  
	  //get all the phrase information from indexer;
	  for(String q : query._tokens){
		  String[] queriesInTerm = q.split(" ");
		  if(queriesInTerm.length > 1){
			  //If it is a phrase
			  int[] phraseOccurrence = getPhrase(q);
			  
			  phraseCount.put(q, phraseOccurrence);
		  }
		  System.out.println(q);
	  }
	  
	    for (int i = 0; i < _indexer.numDocs(); ++i){
	      retrieval_results.add(runquery(query._tokens, i));
//	      System.out.println(i);
	    }
	    Collections.sort(retrieval_results);
	    Collections.reverse(retrieval_results);
	    
	    Vector < ScoredDocument > ret = new Vector < ScoredDocument > ();
	    
	    for(int i = 0; i<numResults; i++){
	    	ret.add(retrieval_results.get(i));
	    }
	    
//	    return (Vector<ScoredDocument>) retrieval_results.subList(0, numResults);
	    return ret;
  }
  
  public ScoredDocument runquery(Vector<String> qv, int did){
		
	    
	 // Get the document vector.
	    DocumentIndexed d = (DocumentIndexed) _indexer.getDoc(did);
	    
	    double score = 0.0;
	    
	    //Iterate through the query, building smoothing score
	    for(int i = 0; i<qv.size(); i++){
	    	
	    	String currentTerm = qv.get(i);
	    	
	    	
	    	double globleLikelihood, documentLikelihood;
	    	
	    	//Determine if currentTerm is phrase or term
	    	if(currentTerm.split(" ").length > 1){
	    		//It is a phrase
	    		int[] count = phraseCount.get(currentTerm);
	    		
	    		globleLikelihood = (double)count[count.length-1] / (double)_indexer.totalTermFrequency();
		    	documentLikelihood = (double)count[did] / (double)d.getTermTotal();
	    	}
	    	else{
	    		//It is a term
	    		double corpusTermFreq = (double)_indexer.corpusTermFrequency(currentTerm);
//	    		System.out.println(corpusTermFreq);
	    		double totalTermFreq = (double) _indexer.totalTermFrequency();
//	    		System.out.println(totalTermFreq);
	    		double docTermFreq =  (double)_indexer.documentTermFrequency(currentTerm,String.valueOf(did));
//	    		System.out.println(docTermFreq);
	    		double docTotal = (double)d.getTermTotal();
//	    		System.out.println(i);
		    	globleLikelihood = corpusTermFreq / totalTermFreq;
//		    	System.out.println("term "+currentTerm+" global frequency is "+globleLikelihood);
		    	documentLikelihood =  docTermFreq/ docTotal;
//		    	System.out.println("term "+currentTerm+" local frequency is "+documentLikelihood);
	    	}
	    	
	    	score += Math.log((1-_lambda)*documentLikelihood + _lambda*globleLikelihood);
	    	
	    	
	    }
	    
	    
//	    System.out.println(did + " "+score);
		return new ScoredDocument(d, Math.pow(Math.E, score));
	}
  
  
  
  /**
   * An internal function that gives occurrence of phrases through out the corpus
   * @return	a double array that indexed by doc id, where corpus.size() -> total phrase number.
   */
  private int[] getPhrase(String phrase){
	  int n = _indexer.numDocs();
	  int[] ret = new int[n+1];
	  int total = 0;
	  Query query = new Query(phrase);
	  query.processQuery();
	  for(int i = 0; i<n; i++){
		  ret[i] = getPhraseByDoc(query, i);
		  total += ret[i];
//		  System.out.println("getPhrase:"+i);
	  }
	  ret[n] = total;
	  return ret;
	  
  }
  
  /**
   * Get the phrase occurrence with in a given document
   * @param phrase	phrase to be searched
   * @param doc	target doc id
   * @return	the occurrence
   */
  private int getPhraseByDoc(Query phrase, int doc){
	  int pos = 0;
	  int sum = 0;
	  
	  
	  
	  int currentPos = 0;
	  while((currentPos = _indexer.nextPhrase(phrase, doc, pos))!=-1){
		  sum++;
		  pos = currentPos;
	  }
	  
	  return sum;
	  
  }
  
  public static void main(String[] args) throws IOException, ClassNotFoundException{
	  Options options = new Options("conf/engine.conf");
	  CgiArguments cgi = new CgiArguments("query=web&ranker=QL");
	  Indexer indexer = Indexer.Factory.getIndexerByOption(options);
	  
	  indexer.loadIndex();
	  
	  Ranker ranker = new RankerQueryLikelihood(options, cgi, indexer);
	  Query q = new QueryPhrase("\"web\"");
	  q.processQuery();
	  
	  Vector<ScoredDocument> result = ranker.runQuery(q, 10);
	  for (ScoredDocument s : result){
		  System.out.println(s.asTextResult());
	  }
  }
  
  
  
}
