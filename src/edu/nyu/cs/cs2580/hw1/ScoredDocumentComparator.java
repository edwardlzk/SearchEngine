package edu.nyu.cs.cs2580.hw1;

import java.util.Comparator;

public class ScoredDocumentComparator implements Comparator<ScoredDocument> {

	@Override
	public int compare(ScoredDocument arg0, ScoredDocument arg1) {
		return (arg0._score < arg1._score)?1:-1;
	}

}
