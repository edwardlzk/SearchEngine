package edu.nyu.cs.cs2580.hw1;

import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

public class Phrase implements Ranker {

	Index _index;
	private String logName = "hw1.1-phrase";

	public Phrase(Index index) {
		this._index = index;
	}

	@Override
	public Vector<ScoredDocument> runquery(String query) {
		// TODO Auto-generated method stub
		Vector<ScoredDocument> retrieval_results = new Vector<ScoredDocument>();
		for (int i = 0; i < _index.numDocs(); ++i) {
			retrieval_results.add(runquery(query, i));
		}
		
		Collections.sort(retrieval_results, new ScoredDocumentComparator());
		return retrieval_results;
	}

	@Override
	public ScoredDocument runquery(String query, int did) {

		// Build query vector
		Scanner s = new Scanner(query);
		Vector<String> qv = new Vector<String>();
		while (s.hasNext()) {
			String term = s.next();
			qv.add(term);
		}

		// Get the document vector.
		Document d = _index.getDoc(did);
		double score = 0;
		
		if(qv.size() == 1){
			score = d.getLocalTermFrequency(qv.get(0));
		}
		else{
			Vector<String> dv = d.get_body_vector();
			double count = 0;
			for(int i = 0; i<qv.size()-1; i++){
				for(int j = 0; j<dv.size()-1; j++){
					if(dv.get(j).equals(qv.get(i)) && 
							dv.get(j+1).equals(qv.get(i+1))){
						count ++;
					}
				}
			}
			score = count;
		}
		

		return new ScoredDocument(did, d.get_title_string(), score);
	}

	@Override
	public String getLogName() {
		// TODO Auto-generated method stub
		return logName;
	}

}
