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
		
		if(!titleResult.find() || !bodyResult.find()){
			System.err.println(file.getName());
		}

		String titleString = titleResult.group(1); //process the title of the html
		String resultTitle = titleString.replaceAll("\\&.*;"," ");
		// replace all the non-word characters except ' and - to space
		 resultTitle = resultTitle.replaceAll("[^\\w]", " ");
		// replace duplicate white spaces to one space
		resultTitle = resultTitle.replaceAll("\\s+"," ");
		
//		System.out.println(resultTitle);
		builder.append(resultTitle);
		// the title and body are seperated by tab
		builder.append("\t");

		String bodyString = bodyResult.group(); //process the body of the html
		// replace all the non-word characters except ' and - to space
		String resultBody = bodyString.replaceAll("\\&.*?;"," ");
		//replace all scripts
		resultBody = resultBody.replaceAll("<script.*?>[\\d\\D]*?</script>"," ");
		//replace all labels
		resultBody = resultBody.replaceAll("</?.*?/?>", " ");
		resultBody = resultBody.replaceAll("[^\\w]", " ");
		
		// replace duplicate white spaces to one space
		resultBody = resultBody.replaceAll("\\s+"," ");
		builder.append(resultBody);
		String output = builder.toString();
		
		System.out.println("before stem:"+output);
		
		Stemmer stemmer = new Stemmer();
		stemmer.add(output);
		stemmer.stem();
		
		System.out.println("after stem:"+stemmer.toString());
		return stemmer.toString();

	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Options option = new Options("conf/engine.conf");
		File file = new File("data/hw2/wiki/'03_Bonnie_&_Clyde");	
		ProcessHtml.process(file);
//		File folder = new File(option._corpusPrefix+"/");
//		
//		File[] listOfFiles = folder.listFiles();
//		FileOps fileOps = new FileOps("testdata/parse/");
//		
//		for(File f : listOfFiles){
//			String res = ProcessHtml.process(f);
//			fileOps.write(f.getName(), res);
//			
//		}
		
		

		
	}

}
