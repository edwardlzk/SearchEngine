package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;
import edu.nyu.cs.cs2580.hw1.ScoredDocumentComparator;
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

  public RankerQueryLikelihood(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
	  Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
	    for (int i = 0; i < _indexer.numDocs(); ++i){
	      retrieval_results.add(runquery(query._tokens, i));
	    }
	    Collections.sort(retrieval_results);
	    return retrieval_results;
  }
  
  public ScoredDocument runquery(Vector<String> qv, int did){
		
	    
	 // Get the document vector.
	    DocumentIndexed d = (DocumentIndexed) _indexer.getDoc(did);
	    
	    double score = 0.0;
	    
	    //Iterate through the query, building smoothing score
	    for(int i = 0; i<qv.size(); i++){
	    	
	    	String currentTerm = qv.get(i);
	    	Integer id=new Integer(d._docid);
	    	double globleLikelihood = (double)_indexer.corpusTermFrequency(currentTerm) / (double)_indexer.totalTermFrequency();
	    	double documentLikelihood = (double)_indexer.documentTermFrequency(currentTerm,id.toString()) / (double)d.getTermTotal();
	    	
	    	
	    	score += Math.log((1-_lambda)*documentLikelihood + _lambda*globleLikelihood);
	    }
	    
	    
//	    System.out.println(did + " "+score);
		return new ScoredDocument(d, Math.pow(Math.E, score));
	}
  
}
