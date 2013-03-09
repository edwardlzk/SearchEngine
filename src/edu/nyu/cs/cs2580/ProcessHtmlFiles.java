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



public class ProcessHtmlFiles {
	private Options _options = null;
	
	public ProcessHtmlFiles(Options option)
	{
		_options=option;
	}
	public void process() throws IOException
	{
		String srcFolder=_options._corpusPrefix;
		File folder = new File(srcFolder);
		if (!folder.exists())
			throw new IOException("Source Folder doesn't exist!");
	    File[] listOfFiles = folder.listFiles();
	    for(File f:listOfFiles)
	    {
	    	processHtml(f.getName());
	    }
	    System.out.println("File extraction done!");
	}
	private void processHtml(String filename)
	{
		String filePath = _options._corpusPrefix+"/"+filename;
		File input = new File(filePath);
		try {
			Document doc = Jsoup.parse(input, "UTF-8", "");
			String title = doc.title(); //process the title of the html
			
			// replace all the non-word characters except ' and - to space
			String resultTitle = title.replaceAll("[^\\w&&[^'-]]", " ");
			// replace duplicate white spaces to one space
			resultTitle = resultTitle.replaceAll("\\s+"," ");
			
			String body = doc.body().text(); //process the body of the html
			// replace all the non-word characters except ' and - to space
			String resultBody = body.replaceAll("[^\\w&&[^'-]]", " ");
			// replace duplicate white spaces to one space
			resultBody = resultBody.replaceAll("\\s+"," ");
			
			// store the processed file to the destination folder with the same name
			File folder = new File(_options._tempFolder);
			if (!folder.exists()) {
				if (folder.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			
			// create the output file
			String convertedFile = _options._tempFolder + "/" + filename;
			File output = new File(convertedFile);
			
			// if file doesnt exists, then create it
			if (!output.exists()) {
				output.createNewFile();
			}
			FileWriter fw = new FileWriter(output.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(resultTitle.trim());
			bw.write("\t");
			bw.write(resultBody.trim());
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Options option = new Options("/Users/Wen/Documents/workspace2/SearchEngineHW2/conf/engine.conf");
		ProcessHtmlFiles proc = new ProcessHtmlFiles(option);
		try {
			proc.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
