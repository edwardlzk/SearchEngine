package edu.nyu.cs.cs2580.hw1;

public class RankerFactory {

	private static RankerFactory instance = null;
	
	private String indexPath;
	
	private RankerFactory(String indexPath){
		this.indexPath = indexPath;
	}
	
	public static synchronized RankerFactory getInstance(String indexPath){
		
		if(instance == null){
			instance = new RankerFactory(indexPath);
		}
		
		return instance;
	}
	
	
	public Ranker getRanker(String type){
		Ranker ranker;
		if(type.equals("QL")){
			ranker = new QueryLikelihood(indexPath);
		}
		else{
			ranker = new Ranker_Example(indexPath);
		}
		
		return ranker;
	}
}
