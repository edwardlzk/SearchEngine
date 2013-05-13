package edu.nyu.cs.cs2580;



import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;


<<<<<<< HEAD
=======

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
>>>>>>> 5b38a10ba7af32befa572d30630ab7dc974e5ad3
import org.apache.hadoop.io.SortedMapWritable;
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

                job.setJobName("Inverted Index");

                

                job.setMapOutputKeyClass(Text.class);
                job.setMapOutputValueClass(SortedMapWritable.class);
                

                
<<<<<<< HEAD
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
               
                
                job.setMapperClass(IndexerMapper.class);
                job.setCombinerClass(OccurrenceCombiner.class);
                job.setReducerClass(OccurrenceReducer.class);
=======
                
                
                
                job.setMapperClass(IndexerMapper.class);
//                job.setCombinerClass(WordCountReducer.class);
                
>>>>>>> 5b38a10ba7af32befa572d30630ab7dc974e5ad3
                
                
//                job.setInputFormatClass(CorpusInputFormat.class);
                job.setInputFormatClass(TextInputFormat.class);
                


                FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/user/banduo/input"));
                FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/user/banduo/indexer"));
                
                if(args[0].equals("occurrence")){
                job.setReducerClass(OccurrenceReducer.class);
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                job.setOutputFormatClass(TextOutputFormat.class);
                }
                else if (args[0].equals("compress")){
                	job.setReducerClass(CompressReducer.class);
                	job.setOutputKeyClass(NullWritable.class);
                	job.setOutputValueClass(BytesWritable.class);
                  job.setOutputFormatClass(CompressOutputFormat.class);
                }
                


                FileInputFormat.setInputPaths(job, new Path(args[1]));
                FileOutputFormat.setOutputPath(job, new Path(args[2]));




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
