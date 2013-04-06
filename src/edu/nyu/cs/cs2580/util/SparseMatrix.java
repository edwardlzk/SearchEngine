package edu.nyu.cs.cs2580.util;

public class SparseMatrix {

	 private final int N;           // N-by-N matrix
	    private SparseVector[] rows;   // the rows, each row is a sparse vector

	    // initialize an N-by-N matrix of all 0s
	    public SparseMatrix(int N) {
	        this.N  = N;
	        rows = new SparseVector[N];
	        for (int i = 0; i < N; i++) rows[i] = new SparseVector(N);
	    }

	    // put A[i][j] = value
	    public void put(int i, int j, float value) {
	        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
	        if (j < 0 || j >= N) throw new RuntimeException("Illegal index");
	        rows[i].put(j, value);
	    }

	    // return A[i][j]
	    public float get(int i, int j) {
	        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
	        if (j < 0 || j >= N) throw new RuntimeException("Illegal index");
	        return rows[i].get(j);
	    }

	    
	
	
}
