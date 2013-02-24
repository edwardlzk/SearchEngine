package edu.nyu.cs.cs2580.hw1;

import java.util.Scanner;
import java.util.Vector;

public class QueryLikelihood implements Ranker {

	
	private Index _index;
	private double _lambda = 0.5;
	
	public QueryLikelihood(String index_source){
		_index = new Index(index_source);
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
	    Vector < String > dv = d.get_title_vector();
	    
	    
	    
		return null;
	}

}
