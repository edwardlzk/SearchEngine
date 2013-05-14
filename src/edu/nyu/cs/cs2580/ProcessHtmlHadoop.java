package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class ProcessHtmlHadoop {
	
	static List<String> stopwords;
	
	static String stopwordPath = "data/stopword/stopword";
	
	public static String process(File file) throws IOException{
		String html = FileOps.readFile(file);
		
		return process(html);
	}
	
	public static String process(String html) 
	{
		
		StringBuilder builder = new StringBuilder();

		String titlePattern = "<title>(.*)</title>";
		String bodyPattern = "<body.*>.+</body>";
		
		Pattern title = Pattern.compile(titlePattern);
		Pattern body = Pattern.compile(bodyPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher titleResult = title.matcher(html);
		Matcher bodyResult = body.matcher(html);
		
		if(!titleResult.find() || !bodyResult.find()){
			return null;
		}

		String titleString = titleResult.group(1); //process the title of the html
		
		if(titleString.equals("")){
			return null;
		}
		String resultTitle = titleString.replaceAll("\\&.*;"," ");
		
		// replace all the non-word characters except ' and - to space
		 resultTitle = resultTitle.replaceAll("[^\\w]", " ");
		// replace duplicate white spaces to one space
		resultTitle = resultTitle.replaceAll("\\s+"," ");
		
		resultTitle = removeStopword(resultTitle);
//		System.out.println(resultTitle);
		resultTitle = resultTitle.toLowerCase();


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
		resultBody = removeStopword(resultBody);
		// replace duplicate white spaces to one space
		resultBody = resultBody.replaceAll("\\s+"," ");
		resultBody = resultBody.toLowerCase();
		
		if(resultBody.equals("")){
			return null;
		}
		builder.append(resultBody);
		String output = builder.toString();
		
//		System.out.println("before stem:"+output);
		
		Stemmer stemmer = new Stemmer();
		//stemmer.add(output);
		output=stemmer.stem(output);
		
//		System.out.println("after stem:"+stemmer.toString());
		return output;

	}
	
	public static String removeStopword(String content){
		if(content == null){
			return null;
		}
		
		
		if (stopwords == null) {
			stopwords = new ArrayList<String>();
			//First time load the stop word
			File stopwordLocation = new File(stopwordPath);
			try {
				BufferedReader input = new BufferedReader(new FileReader(
						stopwordLocation));
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					stopwords.add(line);
				}
				input.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuilder stopConcat = new StringBuilder();
		for(String s : stopwords){
			stopConcat.append(s + "|");
		}
		if(stopConcat.length() >=1){
			stopConcat.deleteCharAt(stopConcat.length()-1);
		}
		String regex = "(?i)\\b("+ stopConcat.toString() +")\\b";
		content = content.replaceAll(regex, " ");
		// replace duplicate white spaces to one space
		content = content.replaceAll("\\s+"," ");
		
		return content;
	}
	
	public static List<String> parseLink(File file) throws IOException{
		String html = FileOps.readFile(file);
		
		List<String> ret = new ArrayList<String>();
		
		String regex = "<a\\s+href=[\"\']([^\"]*?)[\"\'].*?>.*?</a>";
		Pattern linkPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		Matcher linkMatcher = linkPattern.matcher(html);
		while(linkMatcher.find()){
			ret.add(linkMatcher.group(1));
		}
		
		return ret;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		Options option = new Options("conf/engine.conf");


		String test = "A the an have boy an";
		
//		System.out.println(ProcessHtml.removeStopword(test));
		
	}

}
