package edu.nyu.cs.cs2580.hw1;

import java.util.Vector;

public interface Ranker {

	 public Vector < ScoredDocument > runquery(String query);
	 
	 public String getLogName();
	 
	 public ScoredDocument runquery(String query, int did);
}
