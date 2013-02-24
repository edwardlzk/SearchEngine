package edu.nyu.cs.cs2580.hw1;

import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

import edu.nyu.cs.cs2580.hw1.Ranker;

public class NumViews implements Ranker {

	private Index _index;
	private String logName = "hw1.1-numviews";
	
	public NumViews(Index index){
		_index = index;
	}
	
	
	@Override
	public Vector<ScoredDocument> runquery(String query) {
		Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
	    for (int i = 0; i < _index.numDocs(); ++i){
	      retrieval_results.add(runquery(query, i));
	    }
	    
	    Collections.sort(retrieval_results, new ScoredDocumentComparator());
	    return retrieval_results;
	}
	
	
	public ScoredDocument runquery(String query, int did){
		
	    
	 // Get the document vector.
	    Document d = _index.getDoc(did);

		return new ScoredDocument(did, d.get_title_string(), d.get_numviews());
	}


	@Override
	public String getLogName() {
		// TODO Auto-generated method stub
		return logName;
	}
	
	

}
