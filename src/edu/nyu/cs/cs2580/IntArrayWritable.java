package edu.nyu.cs.cs2580;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;

public class IntArrayWritable extends ArrayWritable { 
	public IntArrayWritable() { 
		super(IntWritable.class); 
		} 
	}