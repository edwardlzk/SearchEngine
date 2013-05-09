package edu.nyu.cs.cs2580.hw2;

import java.util.Vector;

import edu.nyu.cs.cs2580.hw2.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.hw2.SearchEngine.Options;

public class RankerPhrase extends Ranker{

	protected RankerPhrase(Options options, CgiArguments arguments,
			Indexer indexer) {
		super(options, arguments, indexer);
		System.out.println("Using Ranker: " + this.getClass().getSimpleName());
	}

	@Override
	public Vector<ScoredDocument> runQuery(Query query, int numResults) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

	

}
