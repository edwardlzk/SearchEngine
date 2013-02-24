package edu.nyu.cs.cs2580.hw1;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


class Evaluator {
  private static String queryType=null;
  private static String rankerType=null;


public static void main(String[] args) throws IOException {
    HashMap < String , HashMap < Integer , Double > > relevance_judgments =
      new HashMap < String , HashMap < Integer , Double > >();
    if (args.length < 1){
      System.out.println("need to provide relevance_judgments");
      return;
    }
    readOutputFile();
    String p = args[0];
    // first read the relevance judgments into the HashMap
    readRelevanceJudgments(p,relevance_judgments);
    // now evaluate the results from stdin
    evaluateStdin(relevance_judgments);
  }

  public static void readOutputFile()
  {
	  try {
		  File dir = new File("./temp");
			if (!dir.exists()) {
				if (dir.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
	      File file =new File("./temp/result.tsv");
	      if (!file.exists()) {
				file.createNewFile();
			}
	      FileWriter fw = new FileWriter(file.getAbsoluteFile());
	      BufferedWriter bw = new BufferedWriter(fw);
	      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	      String line = null;
	      boolean firstLine=true;
	      while ((line = reader.readLine()) != null){
	    	  if(firstLine)
	    	  {
	    		  Scanner s = new Scanner(line).useDelimiter("\t");
	    		  queryType = s.next();
	    		  rankerType = s.next();
	    		  firstLine=false;
	    	  }
	    	   bw.write(line);
	    	   bw.newLine();
		     }
	  	  bw.close();
		  System.out.println("Done");
	 
	    } catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	    }
	  
  }

  public static void readRelevanceJudgments(
    String p,HashMap < String , HashMap < Integer , Double > > relevance_judgments){
    try {
      BufferedReader reader = new BufferedReader(new FileReader(p));
      try {
        String line = null;
        while ((line = reader.readLine()) != null){
          // parse the query,did,relevance line
          Scanner s = new Scanner(line).useDelimiter("\t");
          String query = s.next();
          int did = Integer.parseInt(s.next());
          String grade = s.next();
          double rel = 0.0;
          // convert to binary relevance
          if (grade.equals("Perfect"))
        		  rel=10.0;
          else if(grade.equals("Excellent")) 
        	  	  rel=7.0;
          else if(grade.equals("Good"))
        	  	  rel =5.0;
          else if(grade.equals("Fair"))
        	  	  rel=1.0;
        	  	  
          if (relevance_judgments.containsKey(query) == false){
            HashMap < Integer , Double > qr = new HashMap < Integer , Double >();
            relevance_judgments.put(query,qr);
          }
          HashMap < Integer , Double > qr = relevance_judgments.get(query);
          qr.put(did,rel);
        }
      } finally {
        reader.close();
      }
    } catch (IOException ioe){
      System.err.println("Oops " + ioe.getMessage());
    }
  }
  
  public static double Precision(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K,String path){
	  double result=0.0;
	  try {
		  FileReader fin=new FileReader(path);
	      BufferedReader reader = new BufferedReader(fin);
	   try{     
	      String line = null;
	      int RR = 0;
	      int i=0;
	      
	      while(i<K){
	    	line = reader.readLine();
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        int did = Integer.parseInt(s.next());
	      	//String title = s.next();
	      	//double rel = Double.parseDouble(s.next());
	      	if (relevance_judgments.containsKey(query) == false){
	      		throw new IOException("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	if ((qr.containsKey(did) != false)&&(qr.get(did)>=2.0)){
	      	  RR++;
	      	}
	      	i++;
	      }
	      result=(double)RR/K;
	      return result;
	    
	    } finally {
	        reader.close();
	        } 
	    } catch (Exception e){
		      System.err.println("Error:" + e.getMessage());
		      return result;
		}
  }
  
  public static double Recall(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K,String path){
	  double result=0.0;
	  try {
		  FileReader fin=new FileReader(path);
	      BufferedReader reader = new BufferedReader(fin);
	  try {
	      
	      String line = null;
	      int RR = 0;
	      int i=0;
	      int R=0;//relevant docs
	      
	      while(i<K){
	    	line = reader.readLine();
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        int did = Integer.parseInt(s.next());
	      	//String title = s.next();
	      	//double rel = Double.parseDouble(s.next());
	      	if (relevance_judgments.containsKey(query) == false){

	      		throw new IOException("query not found");

	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	Set<Integer> keys=qr.keySet();
	      	R=0;
	      	for(int key:keys){
	      		if(qr.get(key)>=2.0)
	      			R++;
	      	}
	      	if (qr.containsKey(did) != false&&(qr.get(did)>=2.0)){

	      	  RR++;
	      	}
	      	i++;
	      }
	      result=(double)RR/R;
	      return result;
	    }finally{
	    	reader.close();
	    }
	  } catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return result;
	    }
  }

 public static double F_Measure(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K,String path){
	 double result=0.0;
	 double P=Precision(relevance_judgments,K,path);
	 double R=Recall(relevance_judgments,K,path);
	if((P!=0.0)&&(R!=0.0)){
		result=1/(0.5*(1/P)+0.5*(1/R));
	 }
	return result;
 }
	
 public static  HashMap<Double,Double> PR_Graph(HashMap < String , HashMap < Integer , Double > > relevance_judgments,String path){
	 HashMap<Double,Double> PR=new HashMap<Double,Double>();
	 HashMap<Double,Double> pr=new HashMap<Double,Double>();
	 pr.put(0.0, 1.0);
	 try{
		 FileReader fin=new FileReader(path);
		 BufferedReader reader = new BufferedReader(fin);
	 try {
	         
	      String line = null;
	      int RR = 0;
	      int R=0;//relevant docs
	      int N=1;//position 
	      double r;
	      double p;
	      
	      while((line=reader.readLine())!=null){
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        int did = Integer.parseInt(s.next());
	      	//String title = s.next();
	      	//double rel = Double.parseDouble(s.next());
	      	if (relevance_judgments.containsKey(query) == false){
	      		throw new IOException("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	Set<Integer> keys=qr.keySet();
	      	R=0;
	      	for(int key:keys){
	      		if(qr.get(key)>=2.0)
	      			R++;
	      	}
	      	if (qr.containsKey(did) != false&&(qr.get(did)>=2.0)){
	      	  RR++;
	      	}
	      	r=(double)RR/R;
	      	p=(double)RR/N;
	        //get recall point
	      	if(!PR.containsKey(r)){
	      		PR.put(r, p);
	      		if(r==1.0){
	      			break;
	      		}
	      	} 	
	      	N++;
	      }
	      Set<Double> keys=PR.keySet();
	      for(double j=0.1;j<=1.0;j+=0.1){
	    	    double max=0.0;
	    	  	for(double key:keys){
	    	  		if((key>=j)&&(PR.get(key)>max)){
	    	  			max=PR.get(key);
	    	  		}	
	  
	    	  	}
	    	  	pr.put(j,max);
	      }
	      return pr;
	 }finally{
		 reader.close();
	 }
	    }catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return pr;
	    }
 }
 
 public static double Average(HashMap < String , HashMap < Integer , Double > > relevance_judgments,String path)
 {
	 try{
		FileReader fin=new FileReader(path);
		 BufferedReader reader = new BufferedReader(fin);
	 
	  try {
	      String line = null;
	      double RR = 0.0;
	      int i=0;
	      double AP=0.0;
	     
	      while ((line = reader.readLine()) != null){
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        i++;
	        int did = Integer.parseInt(s.next());
	      	if (relevance_judgments.containsKey(query) == false){

	      		throw new IOException("query not found");

	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	if (qr.containsKey(did) != false)
	      	{
	      		if(qr.get(did)>=2.0)

	      			{
	      			 	RR+=1;
	      			 	AP+=RR/i;
	      			}
	      	}
	      }
	      return AP/RR;
	  }finally{
		  reader.close();
	    }
	  }catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return 0.0;
	    }     
 }
 
 public static double NDCG(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K, String path)
 {
	 try{
			FileReader fin=new FileReader(path);
			 BufferedReader reader = new BufferedReader(fin);
	  try {
	      
	      String line = null;
	      double DCG = 0.0;
	      double IDCG = 0.0;
	      int i=0;
	      double[] score=new double[K];
	      while (i<K && (line = reader.readLine()) != null ){
	    	i++;
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        int did = Integer.parseInt(s.next());
	      	if (relevance_judgments.containsKey(query) == false){
	      	  throw new IOException("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	if (qr.containsKey(did) != false)
	      	{
	      		score[i-1]=qr.get(did);
	      		DCG+=qr.get(did)/(Math.log(i+1)/Math.log(2)); // calculate log of base 
	      	}
	      }
	      if(score.length>0)
	      {
	    	  Arrays.sort(score);
	    	  int len=score.length;
	    	  for(int j=len-1;j>=0;j--)
	    	  {
	    		  IDCG+=score[j]/(Math.log(len-j+1)/Math.log(2));
	    	  }
	      }
	      if(IDCG!=0.0)
	    	  return DCG/IDCG;
	      else
	    	  return 0.0;
	  }finally{
		  reader.close();
	  }
	    } catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return 0.0;
	    }

 }
 
 public static double ReciprocalRank(HashMap < String , HashMap < Integer , Double > > relevance_judgments, String path)
 {
	 try{
		 FileReader fin=new FileReader(path);
		 BufferedReader reader = new BufferedReader(fin);
	  try {
	      
	      String line = null;
	      //double RR = 0.0;
	      int i=0;
	      while ((line = reader.readLine()) != null){
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        i++;
	        int did = Integer.parseInt(s.next());
	      	if (relevance_judgments.containsKey(query) == false){
	      	  throw new IOException("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	if (qr.containsKey(did) != false)
	      	{
	      		if(qr.get(did)>=2.0)
	      			{
	      			 	double val=1/(double)i;
	      			 	return (val);
	      			}
	      	}
	      }
	  }finally{
		  reader.close();
	  }
	    } catch (Exception e){
	      System.err.println("Error:" + e.getMessage()); 
	    }	
	 return 0.0;
} 
  
 
 public static void evaluateStdin(
    HashMap < String , HashMap < Integer , Double > > relevance_judgments){
    // only consider one query per call    

      String path="./temp/result.tsv";// input
      try{
    /*
     * 	  
     */
    	  double d = 2.34568;
          DecimalFormat f = new DecimalFormat("##.00");  // this will helps you to always keeps in two decimal places
          System.out.println(f.format(d)); 
          
          
        //  
      String output_path="../results/hw1.3-"+rankerType+".tsv";
      System.out.println("RankerType is "+rankerType);
      FileWriter fw = new FileWriter(output_path,true);
      BufferedWriter writer = new BufferedWriter(fw); 
      DecimalFormat df = new DecimalFormat("0.00");
      writer.append(queryType+"\t");
	  writer.append(df.format(Precision(relevance_judgments,1,path))+"\t"+
	  df.format(Precision(relevance_judgments,5,path))+"\t"+
	  df.format(Precision(relevance_judgments,10,path))+"\t"+
	  df.format(Recall(relevance_judgments,1,path))+"\t"+
	  df.format(Recall(relevance_judgments,5,path))+"\t"+
	  df.format(Recall(relevance_judgments,10,path))+"\t"+
	  df.format(F_Measure(relevance_judgments,1,path))+"\t"+
	  df.format(F_Measure(relevance_judgments,5,path))+"\t"+
	  df.format(F_Measure(relevance_judgments,10,path))+"\t"
	  );
	  HashMap<Double,Double> pr=PR_Graph(relevance_judgments,path);
      for(double key=0.0;key<=1.0;key+=0.1){
			 writer.append(df.format(pr.get(key))+"\t");
	  }
	  writer.append(df.format(Average(relevance_judgments,path))+"\t"+
	  df.format(NDCG(relevance_judgments,1,path))+"\t"+
	  df.format(NDCG(relevance_judgments,5,path))+"\t"+
	  df.format(NDCG(relevance_judgments,10,path))+"\t"+
	  df.format(ReciprocalRank(relevance_judgments,path))+"\n");
	  
      writer.close();
      
      }catch(Exception e){
    	  System.err.println(e.getMessage());
      }
      

  }
}
