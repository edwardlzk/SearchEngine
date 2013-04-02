package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import edu.nyu.cs.cs2580.ScoredDocument;
import edu.nyu.cs.cs2580.SearchEngine.Options;

public class Prf {
	private int numterms;
	private Vector<ScoredDocument> scoredDocs;
	private Map<String,Integer> term_count;
	private Options op;
	private String prefix = op._indexPrefix+"/";
	
	public Prf(Vector<ScoredDocument> scoredDocs,int numterms,Options op) {
		// TODO Auto-generated constructor stub
		this.scoredDocs = scoredDocs;
		this.numterms = numterms;
		this.op = op;
	}
	private void processDocs() throws IOException{
		term_count = new HashMap<String,Integer>();
		BufferedReader reader = null;
		// tot is is the count for all the words
		int tot = 0;
		for(ScoredDocument doc:scoredDocs){
			// scan each document and find it's terms and the term frequency and store it in map
			String[] docLine = doc.asTextResult().split("\t");
			// docLine[0] is the docid
			reader = new BufferedReader(new FileReader(this.prefix+docLine[0]));
			String line;
			reader.readLine(); // skip the first line which contains the title
			tot = tot + Integer.parseInt(reader.readLine()); // sum up all the term counts in all docs
			while ((line = reader.readLine()) != null) {		
				String[] contents = line.split("\t");
				// contents[0] is the term, contents[1] is the frequency
				if(!term_count.containsKey(contents[0])){
					term_count.put(contents[0], 0);
				}
				term_count.put(contents[0], Integer.parseInt(contents[1])+term_count.get(contents[0]));
			}
			reader.close();
		}
		term_count = sortMap(tot);
		
	}
	private LinkedHashMap<String,Integer> sortMap(int totaltermscount){
			List<String> mapKeys = new ArrayList(term_count.keySet());
			List<Integer> mapValues = new ArrayList(term_count.values());
			Collections.sort(mapValues);
			Collections.sort(mapKeys);
			
			LinkedHashMap sortedMap = new LinkedHashMap();

			Iterator<Integer> valueIt = mapValues.iterator();
			while (valueIt.hasNext()) {
		       Object val = valueIt.next();
		       Iterator<String> keyIt = mapKeys.iterator();
		       while (keyIt.hasNext()) {
		    	   Object key = keyIt.next();
		    	   String comp1 = term_count.get(key).toString();
		    	   String comp2 = val.toString();
		        if (comp1.equals(comp2)){
		            term_count.remove(key);
		            mapKeys.remove(key);
		            //double freq = (double(val)) / (double(totaltermscount);
		            sortedMap.put((String)key, (Double)val);
		            break;
		        }
		    }

		}
		return sortedMap;	
	}
	private void printResult(){
		Iterator<String> key = term_count.keySet().iterator();
		for(int i=0;i<this.numterms;++i){
			if(!key.hasNext())
				break;
			String term = key.next();
			System.out.println(term+"\t"+term_count.get(term));
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a  = 1;
		int b = 2;
		System.out.println(a/(double)b);
	}

}
