package edu.nyu.cs.cs2580;

import java.util.HashMap;
import java.util.Map;

public class SparseVector {
	private final int N;             // length
    private Map<Integer, Float> values;  // the vector, represented by index-value pairs

    // initialize the all 0s vector of length N
    public SparseVector(int N) {
        this.N  = N;
        this.values = new HashMap<Integer, Float>();
    }

    // put st[i] = value
    public void put(int i, float value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (value == 0.0) values.remove(i);
        else              values.put(i, value);
    }

    // return st[i]
    public float get(int i) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (values.containsKey(i)) return values.get(i);
        else                return 0.0f;
    }



}
