package edu.nyu.cs.cs2580.hw1;

import java.util.Scanner;
import java.util.Vector;

public class QueryLikelihood implements Ranker {

	
	private Index _index;
	private double _lambda = 0.5;
	
	public QueryLikelihood(Index index){
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
	    
	    double score = 0.0;
	    
	    //Iterate through the query, building smoothing score
	    for(int i = 0; i<qv.size(); i++){
	    	
	    	String currentTerm = qv.get(i);
	    	double globleLikelihood = (double)Document.termFrequency(currentTerm) / (double)Document.termFrequency();
	    	double documentLikelihood = (double)d.getLocalTermFrequency(currentTerm) / (double)d.getTotalTerms();
	    	
//	    	System.out.println("INFO: "+d.getLocalTermFrequency(currentTerm) + " "+d.getTotalTerms()+" "+Document.termFrequency(currentTerm) + " "+Document.termFrequency());
	    	
	    	score += Math.log((1-_lambda)*documentLikelihood + _lambda*globleLikelihood);
	    }
	    
	    
//	    System.out.println(did + " "+score);
		return new ScoredDocument(did, d.get_title_string(), score);
	}

}
