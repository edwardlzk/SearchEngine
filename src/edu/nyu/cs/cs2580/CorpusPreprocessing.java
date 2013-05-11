package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CorpusPreprocessing {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File newFile = new File("data/merge/corpus");
		if(!newFile.exists()){
			newFile.createNewFile();
		}
		
		String corpusFile = "data/wiki/";
		
		File folder = new File(corpusFile);
		File[] files = folder.listFiles();
		
		Arrays.sort(files, new FileComparator());
		
		int i = 0;
		
		for(File f : files){
			StringBuilder sb = new StringBuilder();
			String html = FileOps.readFile(f);
			
			String[] fileContent = html.split(System.getProperty("line.separator"), 2);
			
			sb.append(fileContent[0]).append("\t");
			String content = ProcessHtmlHadoop.process(fileContent[1]);
			if(content == null){
				continue;
			}
			sb.append(content);
			System.out.println(i++);
			FileOps.append(newFile, sb.toString());
		}
		
	}

}
