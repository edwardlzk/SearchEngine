package edu.nyu.cs.cs2580.hw1;

import java.util.Scanner;
import java.util.Vector;

import edu.nyu.cs.cs2580.hw1.Ranker;

public class NumViews implements Ranker {

	private Index _index;
	
	
	public NumViews(Index index){
		_index = index;
	}
	
	
	@Override
	public Vector<ScoredDocument> runquery(String query) {
		Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
	    for (int i = 0; i < _index.numDocs(); ++i){
	      retrieval_results.add(runquery(query, i));
	    }
	    return retrieval_results;
	}
	
	
	public ScoredDocument runquery(String query, int did){
		
		// Build query vector
	    Scanner s = new Scanner(query);
	    Vector < String > qv = new Vector < String > ();
	    while (s.hasNext()){
	      String term = s.next();
	      qv.add(term);
	    }
	    
	 // Get the document vector.
	    Document d = _index.getDoc(did);
	    
	    double score = qv.size() == 0? 0.0 : 1.0;
	    
	    //Iterate through the query, building smoothing score
	    for(int i = 0; i<qv.size(); i++){
	    	
	    	String currentTerm = qv.get(i);
	    	double globleLikelihood = (double)Document.termFrequency(currentTerm) / (double)Document.termFrequency();
	    	double documentLikelihood = (double)d.getLocalTermFrequency(currentTerm) / (double)d.getTotalTerms();
	    	
	    }
	    
		return new ScoredDocument(did, d.get_title_string(), score);
	}
	
	

}
