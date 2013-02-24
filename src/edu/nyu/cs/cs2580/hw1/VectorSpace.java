package edu.nyu.cs.cs2580.hw1;

import java.util.*;

public class VectorSpace implements Ranker{

	private Index _index;
	private String logName = "hw1.1-vsm";
	
	public VectorSpace(Index index){
		this._index = index;
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
	public ScoredDocument runquery(String query, int did){
		
		// Build query vector
				Scanner s = new Scanner(query);
				Set<String> qv = new HashSet<String>();
				Map<String, Integer> queryTerms = new HashMap<String, Integer>();
				while (s.hasNext()) {
					String term = s.next();
					//Count terms that appears at the doc
					if(_index.termFrequency(term) > 0){
						qv.add(term);
						if(!queryTerms.containsKey(term)){
							queryTerms.put(term, 1);
						}
						else{
							queryTerms.put(term, queryTerms.get(term)+1);
						}
					}
					
				}
	    
				
				// Get the document vector.
			    Document d = _index.getDoc(did);
				
				if(queryTerms.isEmpty()){
					return new ScoredDocument(did, d.get_title_string(), 0);
				}
				
		 
		    
		    //Calculate Document TF-IDF
		    Vector<String> body = d.get_body_vector();
		    
		    double sumMatrix = 0;//Dividend
		    double sumSquareQuery = 0;
		    double sumSquaryDoc = 0;
		    
		    for(String content : body){
		    	double tf = d.getLocalTermFrequency(content);
		    	double idf = _index.IDF(content);
		    	double tfidf = tf * idf;
		    	
		    	sumSquaryDoc += Math.pow(tfidf, 2);
		    	
		    	if(queryTerms.containsKey(content)){
		    		//content is also a term in query
		    		double frequency = queryTerms.get(content) * _index.IDF(content);
		    		sumMatrix += frequency * tfidf;
		    		sumSquareQuery += Math.pow(frequency, 2);
		    	}
		    }
		    
		    double score = 0;
		    
		    if(sumSquareQuery > 0 && sumSquaryDoc > 0){
		    	score = sumMatrix / (Math.sqrt(sumSquareQuery)*Math.sqrt(sumSquaryDoc));
		    } 
		    
		    

			return new ScoredDocument(did, d.get_title_string(), score);
	}

	@Override
	public String getLogName() {
		return logName;
	}
	

}
