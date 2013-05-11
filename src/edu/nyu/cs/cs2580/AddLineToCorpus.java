package edu.nyu.cs.cs2580;

import java.io.File;
import java.util.Arrays;

public class AddLineToCorpus {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String corpusFile = "data/wiki/";
		
		File folder = new File(corpusFile);
		File[] files = folder.listFiles();
		
		Arrays.sort(files, new FileComparator());
		
		int i = 0;
		
		for (File f : files){
			String content = FileOps.readFile(f);
			FileOps.write(f, i + "");
			FileOps.append(f, System.getProperty("line.separator"));
			FileOps.append(f, content);
			i++;
		}
	}

}
