package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class IndexerMapper extends Mapper<LongWritable, Text, Text, SortedMapWritable>
{
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        SortedMapWritable m=new SortedMapWritable();
        
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);
                while(tokenizer.hasMoreTokens())
                {
                        word.set(tokenizer.nextToken());
                        
                        context.write(word,m);
                }
        }
}
