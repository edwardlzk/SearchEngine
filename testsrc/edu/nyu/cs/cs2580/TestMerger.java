package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.nyu.cs.cs2580.hw3.FileOps;

public class TestMerger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base = "temp/";
		FileOps io = new FileOps(base);
		
//		Map<String, String> mapToWrite = new HashMap<String, String>();
//		mapToWrite.put("test", "new");
//		mapToWrite.put("new", "York");
//		mapToWrite.put("apple", "juice");
//		mapToWrite.put("hello", "world");
//		mapToWrite.put("zekai", "li");
//		mapToWrite.put("haha", "hoho");
//		
//		io.write("test2", mapToWrite);
		
		String[] files = new String[2];
		files[0] = "test";
		files[1] = "test2";
		
		try {
			io.merge(files, "test1", "|");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
