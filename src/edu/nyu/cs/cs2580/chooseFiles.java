package edu.nyu.cs.cs2580;

import java.io.*;
import java.util.Vector;

import edu.nyu.cs.cs2580.SearchEngine.Options;

public class chooseFiles {
	Options _options;
	int total=0;//file number
	int div=1000;//how many files to process one time

	public chooseFiles(Options options){
		_options=options;
	}
	public int writeFileName(){
		String corpusFile = _options._corpusPrefix+"/";	
	    File folder = new File(corpusFile);
	    File[] listOfFiles = folder.listFiles();
	   
	    String tempFile = _options._tempFolder + "/filenames.txt";
        
	    try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
	        
	        for (File file : listOfFiles) {
		        if (file.isFile() && !file.getName().equals(".DS_Store")) {
		        	String name=file.getName();
		        	out.write(name);
		        	out.write("\n");
		        	total++;
		        }
	        }
	        out.close();
	        
	    } catch (IOException e) {
	    }
	    return total;
	}
	public Vector<String> loadFile(int i) throws IOException{
		Vector<String> files=new Vector<String>();
		String tempFile = _options._tempFolder + "/filenames.txt";
		BufferedReader reader = new BufferedReader(new FileReader(tempFile));
		try{
			int skip=0; //skip how many lines in the file
			int count=i*div;
            String line = null;
        	while(((line = reader.readLine()) != null)&&(count<(i+1)*div)) {
        		if(skip>=count){
        		 files.add(line);
        		 count++;
        		}
        		skip++;
        	}
        }finally{
        	reader.close();
        }
		return files;
	}
	public int writeTimes()
	{
		 int times = 0; 
		 int total = writeFileName();
		 times = (int)Math.ceil((double)total/div);
		 return times;
	}
	public static void main(String[] args) throws IOException
	{
		Options opt = new Options("/Users/Wen/Documents/workspace2/SearchEngine/conf/engine.conf");
		chooseFiles choose = new chooseFiles(opt);
		System.out.println(choose.writeFileName());
	}
}
