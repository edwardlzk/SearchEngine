package edu.nyu.cs.cs2580.hw3;

import java.io.*;
import java.util.*;

public class Bhattacharyya {
	Vector<String> queries=new Vector<String>();// all queries
	
	//query term probability
	HashMap<String,HashMap<String,Double>> extendedQ=new HashMap<String,HashMap<String,Double>>();
	
	
	private void get(String path) throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(path));
		String line;
		while((line=reader.readLine())!=null){
			String query=line.split(":")[0];
			String file=line.split(":")[1];
			try{
			BufferedReader br=new BufferedReader(new FileReader(file));
			String s;
			while((s=br.readLine())!=null){	
				if(extendedQ.containsKey(query)){
					HashMap<String,Double> hs=extendedQ.get(query);	
					hs.put(s.split("\t")[0], Double.parseDouble(s.split("\t")[1]));
					extendedQ.put(query, hs);	
				}
				else{
				queries.add(query);
				HashMap<String,Double> hs=new HashMap<String,Double>();
				hs.put(s.split("\t")[0], Double.parseDouble(s.split("\t")[1]));
				extendedQ.put(query, hs);		
				}		
				
			}
			br.close();
			}catch(Exception e){
				
			}
				
		}
		reader.close();
	}
	
	private Vector<Double> calculate(){
		Vector<Double> results=new Vector<Double>();
		for(int i=0;i<queries.size()-1;i++){
			HashMap<String,Double> h1=extendedQ.get(queries.get(i));
			Set<String> term=h1.keySet();
			for(int j=i+1;j<queries.size();j++){
				HashMap<String,Double> h2=extendedQ.get(queries.get(j));
				double re=0.0;
				for(String s:term){
					if(h2.containsKey(s)){
						re+=Math.sqrt(h1.get(s)*h2.get(s));
					}
				}
				results.add(re);
			}
		}
		return results;
	}
	private void write(String path) throws IOException{
		Vector<Double> re=calculate();
		BufferedWriter writer=new BufferedWriter(new FileWriter(path));
		int k=0;
		for(int i=0;i<queries.size()-1;i++){
			for(int j=i+1;j<queries.size();j++){
				String s=queries.get(i)+"\t"+queries.get(j)+"\t"+re.get(k)+"\n";
				writer.write(s);
				k++;
				}
			}
		writer.close();
	}
	
	
	
	public static void main(String[] args) throws IOException{
	if(args.length<2){
		System.out.println("There should be 2 parameters");
	}
	else if(args.length>2){
		System.out.println("Too many parameters.");
	}
	else{
		Bhattacharyya b=new Bhattacharyya();
		b.get(args[0]);
		b.write(args[1]);
	}

}
}
