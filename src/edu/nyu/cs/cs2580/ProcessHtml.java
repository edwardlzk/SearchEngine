package edu.nyu.cs.cs2580;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.nyu.cs.cs2580.SearchEngine.Options;



public class ProcessHtml {
	
	public static String process(File file) throws IOException
	{
//		StringBuilder builder = new StringBuilder();
//		String html = FileOps.readFile(file);

//		String titlePattern = "<title>(.*)</title>";
//		String bodyPattern = "[.\\s]*<body[.\\s]*>([.\\s]*)</body>";
//		
//		Pattern title = Pattern.compile(titlePattern);
//		Pattern body = Pattern.compile(bodyPattern);
//		
//		Matcher titleResult = title.matcher(html);
//		Matcher bodyResult = body.matcher(html);
//		
//		titleResult.find();
//		String titleString = titleResult.group(1); //process the title of the html
		Document doc = Jsoup.parse(file,"UTF-8","");
		if(doc.title()==null || doc.title()=="" || doc.body()==null || doc.body().text()==null || doc.body().text()=="")
			return null;
		StringBuilder builder = new StringBuilder();
		
		String titleString = doc.title();
		titleString = titleString.replaceAll("[^\\w]", " ");
		titleString = titleString.replaceAll("\\s+"," ");
		builder.append(titleString);
//		String resultTitle = titleString.replaceAll("\\&.*;"," ");
		// replace all the non-word characters except ' and - to space
//		 resultTitle = resultTitle.replaceAll("[^\\w]", " ");
		// replace duplicate white spaces to one space
//		resultTitle = resultTitle.replaceAll("\\s+"," ");
		
//		System.out.println(resultTitle);
//		builder.append(resultTitle);
		// the title and body are seperated by tab
		builder.append("\t");
		String resultBody = doc.body().text();
		resultBody = resultBody.replaceAll("[^\\w]", " ");
		resultBody = resultBody.replaceAll("\\s+"," ");
		builder.append(resultBody);
		
//		boolean found = bodyResult.find();
//		String bodyString = bodyResult.group(1); //process the body of the html
		// replace all the non-word characters except ' and - to space
//		String resultBody = bodyString.replaceAll("[^\\w]", " ");
		// replace duplicate white spaces to one space
//		resultBody = resultBody.replaceAll("\\s+"," ");
//		builder.append(resultBody);
//		System.out.println(resultBody);
		return builder.toString();

	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Options option = new Options("conf/engine.conf");
		File file = new File("/Users/Wen/Documents/workspace2/SearchEngine/testdata/'03_Bonnie_&_Clyde");	

		String res = ProcessHtml.process(file);
		//System.out.println(res);
		if(res != null)
		{
			Scanner s = new Scanner(res).useDelimiter("\t");
			String title = s.next();
			String body = s.next();
			System.out.println(title);
			System.out.println(body);
		}
		else
			System.out.println(res);
		
	}

}
