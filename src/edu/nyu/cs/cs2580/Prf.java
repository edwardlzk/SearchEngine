package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import edu.nyu.cs.cs2580.ScoredDocument;
import edu.nyu.cs.cs2580.SearchEngine.Options;

public class Prf {
	private int numterms;
	private Vector<ScoredDocument> scoredDocs = null;
	private Map<String,Integer> term_count;
	private Options op = null;
	private String corpus_prefix = null;
	private String index_prefix = null;
	private int totaltermscount;
	
	public Prf(Vector<ScoredDocument> scoredDocs,int numterms,Options op) {
		// TODO Auto-generated constructor stub
		this.scoredDocs = scoredDocs;
		this.numterms = numterms;
		this.op = op;
		corpus_prefix = op._corpusPrefix+"/";
		index_prefix = op._indexPrefix+"/";
	}
	public void processDocs(StringBuilder builder) throws IOException{
		
		term_count = new HashMap<String,Integer>();
		BufferedReader reader = null;
		Map<String,String> fileNames = new HashMap<String,String>();
		for(ScoredDocument doc:scoredDocs){
			System.out.println(doc.asTextResult());
			String[] docLine = doc.asTextResult().split("\t");
			fileNames.put(docLine[0], null);
		}
		String line = null;
		reader = new BufferedReader(new FileReader(this.index_prefix+"/idToTitle"));
		// search the idToTitle file to find the corresponding file names for those ids
		while((line=reader.readLine())!=null){
			String[] linecontents = line.split("\t");
			if(fileNames.containsKey(linecontents[0])){
				fileNames.put(linecontents[0], linecontents[2]);
			}
		}
		for(String filename:fileNames.values()){
			// read the original file
			String content = ProcessHtml.process(new File(this.corpus_prefix+filename));
			content = ProcessHtml.removeStopword(op, content);
			Scanner s;
			s = new Scanner(content).useDelimiter("\t");
			String title = s.next();
			String body = s.next();
			s.close();
			Scanner s2 = new Scanner(title);
			// process the title
			while (s2.hasNext()) {
				String term = s2.next();
				this.totaltermscount++;
				if(!term_count.containsKey(term)){
					term_count.put(term, 0);
				}
				term_count.put(term, term_count.get(term)+1);
			}
			 s2.close();
		   // process the body
			s2 = new Scanner(body);
			while(s2.hasNext()){
				String term = s2.next();
				this.totaltermscount++;
				if(!term_count.containsKey(term)){
					term_count.put(term, 0);
				}
				term_count.put(term, term_count.get(term)+1);
			}
			s2.close();
		}
		term_count = sortMap();
//		Iterator<String> x = term_count.keySet().iterator();
//		for(int i=0;i<10;i++){
//			String term = x.next();
//			System.out.println(term+"\t"+term_count.get(term));
//		}
		printResult(term_count,this.totaltermscount,builder);
		
	}
	private LinkedHashMap<String,Integer> sortMap(){
			List<String> mapKeys = new ArrayList(term_count.keySet());
			List<Integer> mapValues = new ArrayList(term_count.values());
			Collections.sort(mapValues,Collections.reverseOrder());
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
		            sortedMap.put((String)key, (Integer)val);
		            break;
		        }
		    }

		}
//			System.out.println("Sorted Map size is: "+sortedMap.size());
		return sortedMap;	
	}
	private void printResult(Map<String,Integer> percentage,int totaltermscount,StringBuilder builder){
		Iterator<String> key = percentage.keySet().iterator();
		double normal = 0.0;
		for(int i=0;i<this.numterms;i++){
			if(!key.hasNext())
			{
				break;
			}
			double freq = percentage.get(key.next()) / (double)totaltermscount;
			normal = normal + freq;
		}
		key = percentage.keySet().iterator();
		for(int i=0;i<this.numterms;++i){
			if(!key.hasNext())
				break;
			String term = key.next();
			System.out.println(term+":\t"+ percentage.get(term));
			double freq = percentage.get(term) / (double)totaltermscount;
			builder.append(term+"\t"+freq/normal+"\n");
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Vector<ScoredDocument> scoredDocs = null;
//		int numterms = 0;
//		Options op = null;
//		Prf p = new Prf(scoredDocs,numterms,op);
		
	}

}
