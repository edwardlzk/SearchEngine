package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
	
	
	public static String readFile(File file){
		StringBuilder content = new StringBuilder();
		
		try {
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader input = new BufferedReader(isr);
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
	
//	public static void append(File file, String line){
//		BufferedReader input = new BufferedReader(new FileReader(file));
//	}
	
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
	
	
	
	
	/**
	 * Function to write term indexes to file
	 * @param name	Filename
	 * @param content	a Map which map from a term to its index
	 */
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
	
//	public void 
	
	public void merge(String[] tempFiles, String output, String delimiter) throws IOException{
		//Heap for keys, after getting next smallest term, get corresponding BufferedReader from map.
		PriorityQueue<String> heap = new PriorityQueue<String>();
		Map<String, LinkedList<BufferedReader>> readerMap = new HashMap<String, LinkedList<BufferedReader>>();
		Map<BufferedReader, String> valueMap = new HashMap<BufferedReader, String>();
		// Write current line
		File file = new File(base+output);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWritter = new FileWriter(file);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		
		
		//At the first iteration , construct the heap
		BufferedReader[] readers = new BufferedReader[tempFiles.length];
		for(int i = 0; i<tempFiles.length; i++){
			readers[i] = new BufferedReader(new FileReader(base + tempFiles[i]));
			String first;
			if((first = readers[i].readLine()) != null){
				String[] line = first.split("\t");
				//Update the readerMap
				if(readerMap.containsKey(line[0])){
					readerMap.get(line[0]).add(readers[i]);
				}
				else{
					LinkedList<BufferedReader> newReaderList = new LinkedList<BufferedReader>();
					newReaderList.add(readers[i]);
					readerMap.put(line[0], newReaderList);
					heap.offer(line[0]);
				}
				//update valueMap
				valueMap.put(readers[i], line[1]);
			}
		}
		//pop the smallest value from the heap until reaching end
		String current;
		StringBuilder currentLine = new StringBuilder();
		while(heap.size() > 0){
			current = heap.poll();
			currentLine.delete(0, currentLine.length());//clear the StringBuilder
			currentLine.append(current).append("\t");
			LinkedList<BufferedReader> relatedReader = readerMap.get(current);
			for(BufferedReader b : relatedReader){
				currentLine.append(valueMap.get(b)).append(delimiter);
				String newLine;
				if((newLine = b.readLine())!=null){
					String[] newLineValues = newLine.split("\t");
					if(readerMap.containsKey(newLineValues[0])){
						readerMap.get(newLineValues[0]).add(b);
					}
					else{
						LinkedList<BufferedReader> newReaderList = new LinkedList<BufferedReader>();
						newReaderList.add(b);
						readerMap.put(newLineValues[0], newReaderList);
						heap.offer(newLineValues[0]);
					}
					valueMap.put(b, newLineValues[1]);
				}
			}
			readerMap.remove(current);
			//Delete last delimiter
			currentLine.deleteCharAt(currentLine.length()-1);
			bufferWritter.write(currentLine.toString());
			bufferWritter.write(System.getProperty("line.separator"));
		}
		
		bufferWritter.close();
	}
	
}
