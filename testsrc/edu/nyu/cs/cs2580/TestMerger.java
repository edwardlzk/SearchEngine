package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMerger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base = "temp/";
		FileOps io = new FileOps(base);
		
//		Map<String, String> mapToWrite = new HashMap<String, String>();
//		mapToWrite.put("test", "new2");
//		mapToWrite.put("new", "York2");
//		mapToWrite.put("apple", "juice2");
//		mapToWrite.put("hello", "world2");
//		mapToWrite.put("zekai", "li2");
//		mapToWrite.put("haha", "hoho");
//		
//		io.write("test2", mapToWrite);
		
		String[] files = new String[2];
		files[0] = "test1";
		files[1] = "test2";
		
		try {
			io.merge(files, "test", "|");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
