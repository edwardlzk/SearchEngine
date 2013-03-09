package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author edwardlzk
 *
 */
public class FileOps {
	
	String base;
	
	public FileOps(String base){
		this.base = base;
	}
	
	public String read(String name) {
		
		String fileName = base + name;

		StringBuilder content = new StringBuilder();

		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					content.append(line);
					content.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return content.toString();
	}
	
	public void write(String name, Map<String, String> content) {
		String fileName = base + name;

		//Use TreeSet to get sorted keys
		Set<String> keySet = new TreeSet<String>(content.keySet());
		//Iterate through the TreeSet
		Iterator<String> it = keySet.iterator();
		
		try {
			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fileWritter = new FileWriter(fileName);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			while(it.hasNext()){
				String currentKey = it.next();
				String currentValue = content.get(currentKey);
				bufferWritter.write(currentKey + "\t" + currentValue);
				bufferWritter.write(System.getProperty("line.separator"));
			}
			
			
			bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void merge(String[] tempFiles, String output, String delimiter) throws IOException{
		//Heap for keys, after getting next smallest term, get corresponding BufferedReader from map.
		PriorityQueue<String> heap = new PriorityQueue<String>();
		Map<String, ArrayList<BufferedReader>> readerMap = new HashMap<String, ArrayList<BufferedReader>>();
		Map<BufferedReader, String> ValueMap = new HashMap<BufferedReader, String>();
		
		//Put BufferedReader to reader
		BufferedReader[] readers = new BufferedReader[tempFiles.length];
		for(int i = 0; i<tempFiles.length; i++){
			readers[i] = new BufferedReader(new FileReader(tempFiles[i]));
			//At the first iteration , construct the heap
			String first;
			if((first = readers[i].readLine()) != null){
				
			}
		}
	}
	
}
