package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;

import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class IndexerMapper extends
		Mapper<LongWritable, Text, Text, SortedMapWritable> {

	private Text term = new Text();
	private SortedMapWritable smw = new SortedMapWritable();
	
	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String delimiter = ",";
		
		Map<String, ArrayList<String>> positions = new HashMap<String, ArrayList<String>>();
		int currentPos = 0;
		
		String[] fileContent = value.toString().split("\t", 2);
		
		int id = Integer.parseInt(fileContent[0]);
		String content = fileContent[1];
		

		StringTokenizer tokenizer = new StringTokenizer(content);
		
		while(tokenizer.hasMoreTokens()){
			String currentTerm = tokenizer.nextToken();
			if(positions.containsKey(currentTerm)){
				positions.get(currentTerm).add(currentPos+"");
			}
			else{
				ArrayList<String> newList = new ArrayList<String>();
				newList.add(currentPos+"");
				positions.put(currentTerm, newList);
			}
			currentPos++;
		}
		
		//output the map to SortMapWritable

		Iterator<Map.Entry<String, ArrayList<String>>> it = positions.entrySet().iterator();
		SortedMapWritable smw = new SortedMapWritable();

		while(it.hasNext()){
			Map.Entry<String, ArrayList<String>> entry = it.next();
			String currentTerm = entry.getKey();
			ArrayList<String> termPos = entry.getValue();
			//to String
			StringBuilder sb = new StringBuilder();
			for(String s : termPos){
				sb.append(s)
				.append(delimiter);
			}
			sb.deleteCharAt(sb.length()-1);
			
			smw.clear();
			smw.put(new IntWritable(id), new Text(sb.toString()));
			
			term.set(currentTerm);
			
			context.write(term, smw);
		}
		
	}

}
