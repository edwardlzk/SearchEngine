package edu.nyu.cs.cs2580;

import java.util.Comparator;

public class ScoredDocumentComparator implements Comparator<ScoredDocument> {

	@Override
	public int compare(ScoredDocument arg0, ScoredDocument arg1) {
		Double s1 = new Double(arg0._score);
		Double s2 = new Double(arg1._score);
		return s2.compareTo(s1);
	}

}
