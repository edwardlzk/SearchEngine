package edu.nyu.cs.cs2580;


import java.io.IOException;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

	public class OccurrenceCombiner extends
			Reducer<Text, SortedMapWritable, Text, SortedMapWritable> {
		public void reduce(Text key, Iterable<SortedMapWritable> values,
				Context context) throws IOException, InterruptedException {
			SortedMapWritable val = new SortedMapWritable();
			for (SortedMapWritable value : values) {
				val.putAll(value);
				value.clear();
			}
	            context.write(key, val);
	        }
}

	
	//	StringBuilder builder = new StringBuilder();
//	for (Object x : val.keySet()) {
//		builder.append(x).append(",");
//		builder.append(val.get(x));
//		builder.append("|");
//	}

			//builder.deleteCharAt(builder.length()-1);

			//Text out = new Text(builder.toString());