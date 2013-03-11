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
	
	public static String process(File file)
	{
		StringBuilder builder = new StringBuilder();
		String html = FileOps.readFile(file);

		String titlePattern = "<title>(.*)</title>";
		String bodyPattern = "<body.*>.+</body>";
		
		Pattern title = Pattern.compile(titlePattern);
		Pattern body = Pattern.compile(bodyPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher titleResult = title.matcher(html);
		Matcher bodyResult = body.matcher(html);
		
		titleResult.find();
		String titleString = titleResult.group(1); //process the title of the html
		String resultTitle = titleString.replaceAll("\\&.*;"," ");
		// replace all the non-word characters except ' and - to space
		 resultTitle = resultTitle.replaceAll("[^\\w]", " ");
		// replace duplicate white spaces to one space
		resultTitle = resultTitle.replaceAll("\\s+"," ");
		
		System.out.println(resultTitle);
		builder.append(resultTitle);
		// the title and body are seperated by tab
		builder.append("\t");
		
		boolean found = bodyResult.find();
		String bodyString = bodyResult.group(); //process the body of the html
		// replace all the non-word characters except ' and - to space
		String resultBody = bodyString.replaceAll("\\&.*;"," ");
		resultBody = resultBody.replaceAll("<[/]?.*?/?>", " ");
		resultBody = resultBody.replaceAll("[^\\w]", " ");
		
		// replace duplicate white spaces to one space
		resultBody = resultBody.replaceAll("\\s+"," ");
		builder.append(resultBody);
		System.out.println(resultBody);
		return builder.toString();

	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Options option = new Options("conf/engine.conf");

		File file = new File("data/hw2/wiki/'03_Bonnie_&_Clyde");	


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
