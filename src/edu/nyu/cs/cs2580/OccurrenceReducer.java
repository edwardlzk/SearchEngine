package edu.nyu.cs.cs2580;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OccurrenceReducer extends Reducer<Text, SortedMapWritable, Text, Text> {
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		// StringBuilder sum = new StringBuilder();


		Text one = new Text("1");
		
		context.write(key, one);
	}

}
