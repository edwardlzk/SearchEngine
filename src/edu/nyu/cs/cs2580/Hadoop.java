package edu.nyu.cs.cs2580;



import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;


import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class Hadoop extends Configured implements Tool {

        
        
        public int run(String[] args) throws Exception  {
                // TODO Auto-generated method stub
                Job job = new Job(getConf());
                job.setJarByClass(Hadoop.class);
                job.setJobName("indexing");
                

                job.setMapOutputKeyClass(Text.class);
                job.setMapOutputValueClass(SortedMapWritable.class);
                

                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
               
                
                job.setMapperClass(IndexerMapper.class);
                job.setCombinerClass(OccurrenceCombiner.class);
                job.setReducerClass(OccurrenceReducer.class);
                
                
                job.setInputFormatClass(CorpusInputFormat.class);
                job.setOutputFormatClass(TextOutputFormat.class);
                
                
<<<<<<< HEAD


                FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/user/banduo/input"));
                FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/user/banduo/indexer"));
                
=======
<<<<<<< HEAD
                FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/user/Wen/input"));
                FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/user/Wen/out2"));
=======
                FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/user/edwardlzk/wiki"));
                FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/user/edwardlzk/out5"));
>>>>>>> c44a40b7edcb9ab0fa05b127e210c8937700fc39

>>>>>>> 64ac004d20f97e64eeae2202513ee5eca8b1eb5b
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
