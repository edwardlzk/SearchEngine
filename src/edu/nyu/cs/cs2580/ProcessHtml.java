package edu.nyu.cs.cs2580;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.nyu.cs.cs2580.SearchEngine.Options;



public class ProcessHtml {
	
	public static String process(File file)
	{
		StringBuilder builder = new StringBuilder();
		try {
			Document doc = Jsoup.parse(file, "UTF-8", "");
			String title = doc.title(); //process the title of the html
			
			// replace all the non-word characters except ' and - to space
			String resultTitle = title.replaceAll("[^\\w&&[^'-]]", " ");
			// replace duplicate white spaces to one space
			resultTitle = resultTitle.replaceAll("\\s+"," ");
			System.out.println(resultTitle);
			builder.append(resultTitle);
			// the title and body are seperated by tab
			builder.append("\t");
			String body = doc.body().text(); //process the body of the html
			// replace all the non-word characters except ' and - to space
			String resultBody = body.replaceAll("[^\\w&&[^'-]]", " ");
			// replace duplicate white spaces to one space
			resultBody = resultBody.replaceAll("\\s+"," ");
			builder.append(resultBody);
			System.out.println(resultBody);
			return builder.toString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Options option = new Options("/Users/Wen/Documents/workspace2/SearchEngine/conf/engine.conf");
		File file = new File("/Users/Wen/Documents/workspace2/SearchEngine/data/wiki/1983–84_Liverpool_F.C._season.html");	
		String res = ProcessHtml.process(file);
		System.out.println(res);
		Scanner s = new Scanner(res).useDelimiter("\t");
		String title = s.next();
		String body = s.next();
		System.out.println(title);
		System.out.println(body);
		
	}

}
