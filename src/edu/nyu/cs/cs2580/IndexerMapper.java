package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class IndexerMapper extends Mapper<LongWritable, Text, Text, SortedMapWritable>
{
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private int id;
        private Map<Integer,String> out;
        private StringBuilder builder;
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);
                int pos = 1;
                Map<String,HashMap<Integer,StringBuilder>> terms = new HashMap<String,HashMap<Integer,StringBuilder>>();
                while(tokenizer.hasMoreTokens())
                {
                		String word = tokenizer.nextToken();
                		if(word.startsWith("###")) {
                			id = Integer.parseInt(word.substring(3));
                			pos = 1;
                		}
                		else {
                			if(!terms.containsKey(word)) {
                				HashMap map = new HashMap<Integer,StringBuilder>();
                				terms.put(word, map);
                			}
                			HashMap<Integer,StringBuilder> list = terms.get(word);
                			if(!list.containsKey(id)) {
                				StringBuilder builder = new StringBuilder();
                				list.put(id, builder);
                			}
                			StringBuilder t = list.get(id);
                			t.append(" "+pos);
                			pos ++;
                		}
                }
                for(String x:terms.keySet()) {
                	SortedMapWritable sortout = new SortedMapWritable();
                	for(int n:terms.get(x).keySet()) {
                		sortout.put(new IntWritable(n),new Text(terms.get(x).get(n).toString()));
                	}
                	context.write(new Text(x), sortout);
                }
        }
}
