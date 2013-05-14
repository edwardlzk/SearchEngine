package edu.nyu.cs.cs2580.hadoop;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CompressReducer extends
		Reducer<Text, SortedMapWritable, NullWritable, BytesWritable> {
	
	private static final int con = Integer.MAX_VALUE;
	private static final int vmax = 128;
	
	public void reduce(Text key, Iterable<SortedMapWritable> values,
			Context context) throws IOException, InterruptedException {
		SortedMapWritable val = new SortedMapWritable();
		for (SortedMapWritable value : values) {
			val.putAll(value);
			value.clear();
		}
		Vector<Byte> outBytes = new Vector<Byte>();
		
		
		 Vector<Byte> bytesAllDoc = new Vector<Byte>();
		//Get all the positions
		for (Object x : val.keySet()) {
			Vector<Byte> bytesInDoc = new Vector<Byte>();
			IntWritable id = (IntWritable) x;
			//1.add the id(hashed term)
			bytesInDoc.addAll(vbyteConversion(id.get()));
			//2.get all the positions within this doc id.
			Text positionList = (Text)val.get(x);
			//Mapper gives comma separated position String
			String[] positions = positionList.toString().split(",");
			//3.Add the positions one by one
			for(String s : positions){
				bytesInDoc.addAll(vbyteConversion(Integer.parseInt(s)));
			}
			
			//4.Add current Doc bytes to the all docs collection
			//first add size
			bytesAllDoc.addAll(vbyteConversion(bytesInDoc.size()));
			bytesAllDoc.addAll(bytesInDoc);
		}
		
		//Build Bytes
		//Hash term
		long termhash = (long)key.toString().hashCode()+(long)con;
		outBytes.addAll(vbyteConversion(termhash));
		outBytes.addAll(vbyteConversion(bytesAllDoc.size()));
		outBytes.addAll(bytesAllDoc);
		
		//convert to byte array
		byte[] outArray = new byte[outBytes.size()];
		for(int i = 0; i<outBytes.size(); i++){
			outArray[i] = outBytes.get(i);
		}
		
		BytesWritable out = new BytesWritable(outArray);
		NullWritable nullKey = NullWritable.get();
		context.write(nullKey, out);
	}
	
	
	 private Vector<Byte> vbyteConversion(long num)
	 {
		  Vector<Byte> num_to_bytes = new Vector<Byte>();
		  boolean firstByte = true;
		  if(num == 0)
			  num_to_bytes.add((byte)(1<<7));
		  while(num>0)
		  {
			  byte bytenum = (byte)(num % vmax);
			  num = num >> 7;
			  if (firstByte)
			  {
				  // indicate the end of a byte, set the hightest bit to 1
				  bytenum |= 1 << 7;
				  firstByte = false;
			  }
			  num_to_bytes.add(bytenum);	  
		  }
		  Collections.reverse(num_to_bytes);
		  return num_to_bytes;
	 }
	 
	// @Tested Convert an integer to a byte vector
	  private Vector<Byte> vbyteConversion(int num)
	  {
		  
		  Vector<Byte> num_to_bytes = new Vector<Byte>();
		  boolean firstByte = true;
		  if(num == 0)
			  num_to_bytes.add((byte)(1<<7));
		  while(num>0)
		  {
			  byte bytenum = (byte)(num % vmax);
			  num = num >> 7;
			  if (firstByte)
			  {
				  // indicate the end of a byte, set the hightest bit to 1
				  bytenum |= 1 << 7;
				  firstByte = false;
			  }
			  num_to_bytes.add(bytenum);	  
		  }
		  Collections.reverse(num_to_bytes);
		  return num_to_bytes;
	  }
}
