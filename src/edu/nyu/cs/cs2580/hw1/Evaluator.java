package edu.nyu.cs.cs2580.hw1;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


class Evaluator {

  public static void main(String[] args) throws IOException {
    HashMap < String , HashMap < Integer , Double > > relevance_judgments =
      new HashMap < String , HashMap < Integer , Double > >();
    if (args.length < 1){
      System.out.println("need to provide relevance_judgments");
      return;
    }
    String p = args[0];
    // first read the relevance judgments into the HashMap
    readRelevanceJudgments(p,relevance_judgments);
    // now evaluate the results from stdin
    evaluateStdin(relevance_judgments);
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
	      	 System.out.println("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	if ((qr.containsKey(did) != false)&&(qr.get(did)>1.0)){
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
	      	  System.out.println("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	Set<Integer> keys=qr.keySet();
	      	R=0;
	      	for(int key:keys){
	      		if(qr.get(key)>1.0)
	      			R++;
	      	}
	      	if (qr.containsKey(did) != false&&(qr.get(did)>1.0)){
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
	 PR.put(0.0, 1.0);
	 int r_point=1;
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
	      
	      while((line=reader.readLine())!=null&&r_point<=10){
	        Scanner s = new Scanner(line).useDelimiter("\t");
	        String query = s.next();
	        int did = Integer.parseInt(s.next());
	      	//String title = s.next();
	      	//double rel = Double.parseDouble(s.next());
	      	if (relevance_judgments.containsKey(query) == false){
	      	  System.out.println("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	Set<Integer> keys=qr.keySet();
	      	R=0;
	      	for(int key:keys){
	      		if(qr.get(key)>1.0)
	      			R++;
	      	}
	      	if (qr.containsKey(did) != false&&(qr.get(did)>1.0)){
	      	  RR++;
	      	}
	      	r=(double)RR/R;
	      	p=(double)RR/N;
	        //get recall point
	      	if(!PR.containsKey(r)){
	      	if(r==0.1||r==0.2||r==0.3||r==0.4||r==0.5||r==0.6||r==0.7||r==0.8||r==0.9){
	      		PR.put(r, p);
	      		r_point++;
	      	}
	      	if(r==1.0){
	      		PR.put(r, p);
	      		r_point=11;
	      		}
	      	} 	
	      	N++;
	      }
	      
	      return PR;
	 }finally{
		 reader.close();
	 }
	    }catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return PR;
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
	      	  System.out.println("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	if (qr.containsKey(did) != false)
	      	{
	      		if(qr.get(did)>1.0)
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

  public static void evaluateStdin(
    HashMap < String , HashMap < Integer , Double > > relevance_judgments){
    // only consider one query per call    
      String path="/Users/banduo/Documents/workspace/SearchEngine/testdata/test1.tsv";
	  //System.out.println(Precision(relevance_judgments,1,path));
	  //System.out.println(Precision(relevance_judgments,4,path));
	  //System.out.println(Precision(relevance_judgments,10,path));
	  //System.out.println(Recall(relevance_judgments,1,path));
	  //System.out.println(Recall(relevance_judgments,4,path));
	  //System.out.println(Recall(relevance_judgments,10,path));
	  //System.out.println(F_Measure(relevance_judgments,1,path));
	  //System.out.println(F_Measure(relevance_judgments,4,path));
	  //System.out.println(F_Measure(relevance_judgments,10,path));
	 /* HashMap<Double,Double> pr=PR_Graph(relevance_judgments,path);
	  for(double i=0.0;i<=1.0;i+=0.1){
		 if(pr.containsKey(i)){
			 System.out.println(i+":"+pr.get(i));
		 }
	  }*/
	  System.out.println(Average(relevance_judgments,path));
  }
}
