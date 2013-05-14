package edu.nyu.cs.cs2580;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class CorpusPreprocessing {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File file = new File("data/merge");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}

		File newFile = new File("data/merge/corpus");
		if(!newFile.exists()){
			newFile.createNewFile();
		}
		
		String corpusFile = "data/wiki/";
		
		File folder = new File(corpusFile);
		File[] files = folder.listFiles();
		
		Arrays.sort(files, new FileComparator());
		
		int i = 0;
		int corpusTotalTerms = 0;
		int corpusDocCount = 0;
		
		String idToTitle = "data/index/idToTitle";
		File idSta = new File(idToTitle);
		if(!idSta.exists()) {
			idSta.createNewFile();
		}
		BufferedWriter idout = new BufferedWriter(new FileWriter(idSta,false));
		
		String statistics = "data/index/statistics";
		File sta = new File(statistics);
		if(!sta.exists()) {
			sta.createNewFile();
		}
		BufferedWriter staout = new BufferedWriter(new FileWriter(sta,false));
		
		for(File f : files){
			int docterms = 0;
			StringBuilder sb = new StringBuilder();
			String html = FileOps.readFile(f);
			
			String[] fileContent = html.split(System.getProperty("line.separator"), 2);
			
			sb.append(fileContent[0]).append("\t");
			String content = ProcessHtmlHadoop.process(fileContent[1]);
			if(content == null){
				continue;
			}
			++corpusDocCount;
			sb.append(content);
//			System.out.println(i++);
			FileOps.append(newFile, sb.toString());
			// generate idToTitle file
			String[] contents = content.split("\t");
			String[] terms = content.split("\\s+");
			corpusTotalTerms += terms.length;
		    idout.append(fileContent[0]+"\t"+contents[0]+"\t"+f.getName()+"\t"+terms.length+"\t"+0+"\t"+0+"\n");		
		}
		idout.close();
		//generate statistic file
		staout.append(corpusDocCount+"\n");
		staout.append(corpusTotalTerms+"\n");
		staout.close();
	}

}
