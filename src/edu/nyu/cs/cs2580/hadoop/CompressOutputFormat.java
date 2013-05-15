package edu.nyu.cs.cs2580.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;
 
import java.io.DataOutputStream;
import java.io.IOException;


public class CompressOutputFormat extends FileOutputFormat<NullWritable, BytesWritable> {
	 
    @Override
    public RecordWriter<NullWritable, BytesWritable> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        Configuration conf = taskAttemptContext.getConfiguration();
        boolean isCompressed = getCompressOutput(taskAttemptContext);
        CompressionCodec codec = null;
        String extension = "";
        if (isCompressed) {
            Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(taskAttemptContext, GzipCodec.class);
            codec = ReflectionUtils.newInstance(codecClass, conf);
            extension = codec.getDefaultExtension();
        }
        Path file = getDefaultWorkFile(taskAttemptContext, extension);
        FileSystem fs = file.getFileSystem(conf);
        if (!isCompressed) {
            FSDataOutputStream fileOut = fs.create(file, false);
            return new ByteRecordWriter(fileOut);
        } else {
            FSDataOutputStream fileOut = fs.create(file, false);
            return new ByteRecordWriter(new DataOutputStream(codec.createOutputStream(fileOut)));
        }
    }
 
    protected static class ByteRecordWriter extends RecordWriter<NullWritable, BytesWritable> {
        private DataOutputStream out;
 
        public ByteRecordWriter(DataOutputStream out) {
            this.out = out;
        }
 
        @Override
        public void write(NullWritable key, BytesWritable value) throws IOException {
            boolean nullValue = value == null;
            if (!nullValue) {
                out.write(value.getBytes(), 0, value.getLength());
            }
        }
 
        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            out.close();
        }
    }
 
}