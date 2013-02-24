package edu.nyu.cs.cs2580.hw1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private String container = "../results/";

	private static Logger instance = new Logger();
	
	private Logger(){}
	
	private Logger(String loc){
		this.container = loc;
	}
	
	public static Logger getInstance(){
		return instance;
	}
	
	public static Logger getInstance(String loc){
		instance = new Logger(loc);
		return instance;
	}

	public void logWriter(String name, String content, boolean append) {
		String fileName = container + name + ".tsv";

		System.out.println(fileName);
		
		try {
			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			FileWriter fileWritter = new FileWriter(fileName, append);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(content);
			bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLog(String name) {
		String fileName = container + name + ".tsv";

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
}
