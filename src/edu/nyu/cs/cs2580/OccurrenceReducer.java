package edu.nyu.cs.cs2580;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OccurrenceReducer extends Reducer<Text, SortedMapWritable, Text, Text>
{
        public void reduce(Text key, Iterable<SortedMapWritable> values, Context context) throws IOException, InterruptedException
        {
        		SortedMapWritable val = new SortedMapWritable();
                for(SortedMapWritable value: values)
                {
                        val.putAll(value);
                }
                StringBuilder builder = new StringBuilder();
                for(Object x:val.keySet()) {
                	builder.append(x);
                	builder.append(val.get(x));
                }
                Text out = new Text(builder.toString());
                context.write(key, out);
        }
        
}
