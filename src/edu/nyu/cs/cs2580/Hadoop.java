package edu.nyu.cs.cs2580;


import java.io.IOException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class Hadoop extends Configured implements Tool {

        
        
        
        
        public int run(String[] args) throws Exception  {
                // TODO Auto-generated method stub
                Job job = new Job(getConf());
                job.setJarByClass(Hadoop.class);
                job.setJobName("wordcount");
                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(IntWritable.class);
                
                job.setMapperClass(IndexerMapper.class);
//                job.setCombinerClass(WordCountReducer.class);
                job.setReducerClass(OccurrenceReducer.class);
                
                
                job.setInputFormatClass(TextInputFormat.class);
                job.setOutputFormatClass(TextOutputFormat.class);
                
                
                FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/user/edwardlzk/wiki/NOTICE.txt"));
                FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/user/edwardlzk/out1"));
                
                boolean success = job.waitForCompletion(true);
                return success ? 0: 1;
        }
        
        /**
         * @param args
         * @throws Exception 
         */
        public static void main(String[] args) throws Exception {
                // TODO Auto-generated method stub
                int result = ToolRunner.run(new Hadoop(), args);
                System.exit(result);
        }
}
