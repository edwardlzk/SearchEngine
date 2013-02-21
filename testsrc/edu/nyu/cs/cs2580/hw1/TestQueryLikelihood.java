package edu.nyu.cs.cs2580.hw1;


public class TestQueryLikelihood {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String indexPath = "/home/edwardlzk/Dropbox/workspace/SearchEngine/data/hw1/corpus.tsv";
		String testQuery = "the";
		
		
		Index index = new Index(indexPath);
		Ranker ranker = new NumViews(index);
		
		System.out.println(ranker.runquery(testQuery));
		
	}

}
