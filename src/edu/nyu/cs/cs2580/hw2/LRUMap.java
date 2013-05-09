package edu.nyu.cs.cs2580.hw2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author edwardlzk
 *
 */
public class LRUMap<K,V> extends LinkedHashMap<K,V> {

	private static final long serialVersionUID = -3167375197348567693L;
	private int max_cap;
    

    public LRUMap(int initial_cap, int max_cap) {
        super(initial_cap, 0.75f, true);
        this.max_cap = max_cap;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > this.max_cap;
    }
}
