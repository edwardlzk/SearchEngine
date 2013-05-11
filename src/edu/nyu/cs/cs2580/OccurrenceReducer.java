package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OccurrenceReducer extends Reducer<Text, IntWritable, Text, IntWritable>
{
        public void reduce(Text key, Iterable<Map.Entry<K, V>> values, Context context) throws IOException, InterruptedException
        {
                int sum = 0;
                Iterator<Map> it=values.iterator();
                while(it.hasNext())
                {
                        sum = it.next().size();
                }
                context.write(key, new IntWritable(sum));
        }
        
}
