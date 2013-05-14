package edu.nyu.cs.cs2580.hadoop;

import java.io.IOException;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OccurrenceReducer extends
		Reducer<Text, SortedMapWritable, Text, Text> {
	
	public void reduce(Text key, Iterable<SortedMapWritable> values,
			Context context) throws IOException, InterruptedException {
		SortedMapWritable val = new SortedMapWritable();
		for (SortedMapWritable value : values) {
			val.putAll(value);
			value.clear();
		}
		StringBuilder builder = new StringBuilder();
		for (Object x : val.keySet()) {
			builder.append(x).append(",");
			builder.append(val.get(x));
			builder.append("|");
		}
		builder.deleteCharAt(builder.length()-1);
		Text out = new Text(builder.toString());
        context.write(key, out);
        }
}
