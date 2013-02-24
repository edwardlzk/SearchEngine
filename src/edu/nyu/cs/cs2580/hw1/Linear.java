package edu.nyu.cs.cs2580.hw1;

import java.util.Collections;
import java.util.Vector;

public class Linear implements Ranker {

	private String logName = "hw1.2-linear";
	private Index _index;
	private Ranker[] rankers;
	private double[] prams;
	
	public Linear(Index index){
		this._index = index;
		RankerFactory rf = RankerFactory.getInstance();
		this.rankers = new Ranker[4];
		this.prams = new double[4];
		
		rankers[0] = rf.getRanker("cosine");
		prams[0] = 0.5;
		
		rankers[1] = rf.getRanker("QL");
		prams[1] = 0.45;
		
		rankers[2] = rf.getRanker("phrase");
		prams[2] = 0.04995;
		
		rankers[3] = rf.getRanker("view");
		prams[3] = 0.000005;
	}
	
	@Override
	public Vector<ScoredDocument> runquery(String query) {
		// TODO Auto-generated method stub
		Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
	    for (int i = 0; i < _index.numDocs(); ++i){
	      retrieval_results.add(runquery(query, i));
	    }
	    
	    Collections.sort(retrieval_results, new ScoredDocumentComparator());
	    return retrieval_results;
	}
	
	@Override
	public ScoredDocument runquery(String query, int did) {
		double score = 0;
		
		Document d = _index.getDoc(did);
		
		for(int i = 0; i<rankers.length; i++){
			score += rankers[i].runquery(query, did)._score * prams[i];
		}
		return new ScoredDocument(did, d.get_title_string(), score);
	}

	@Override
	public String getLogName() {
		// TODO Auto-generated method stub
		return logName;
	}

}
