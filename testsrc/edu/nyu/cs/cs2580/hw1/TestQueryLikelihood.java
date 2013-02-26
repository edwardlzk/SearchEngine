package edu.nyu.cs.cs2580.hw1;

import java.util.Vector;


public class TestQueryLikelihood {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String indexPath = "/home/edwardlzk/Dropbox/workspace/SearchEngine/data/hw1/corpus.tsv";
		String testQuery = "data mining";
		
		
		Index index = new Index(indexPath);
		Ranker ranker = new Phrase(index);
		
		
		Vector<ScoredDocument> result = ranker.runquery(testQuery);
		for(ScoredDocument s : result){
			System.out.println(s.asString());
		}
		
		
		
	}

}
