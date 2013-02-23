package edu.nyu.cs.cs2580.hw1;

public class RankerFactory {

	private static RankerFactory instance = null;
	
	private Index index;
	
	private RankerFactory(String indexPath){
		index = new Index(indexPath);
	}
	
	public static synchronized RankerFactory getInstance(String indexPath){
		
		if(instance == null){
			instance = new RankerFactory(indexPath);
		}
		
		return instance;
	}
	
	
	public Ranker getRanker(String type){
		Ranker ranker;
		if(type.equals("cosine")){
			ranker = new VectorSpace(index);
		}
		else if(type.equals("QL")){
			ranker = new QueryLikelihood(index);
		}
		else if(type.equals("phrase")){
			ranker = new Phrase(index);
		}
		else if(type.equals("view")){
			ranker = new NumViews(index);
		}
		else{
			ranker = new Ranker_Example(index);
		}
		
		return ranker;
	}
}
