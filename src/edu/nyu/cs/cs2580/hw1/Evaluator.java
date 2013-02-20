package edu.nyu.cs.cs2580.hw1;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
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
          if ((grade.equals("Perfect")) ||
            (grade.equals("Excellent")) ||
            (grade.equals("Good"))){
            rel = 1.0;
          }
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
  
  public static double Precision(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K){
	  double result=0.0;
	  try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	      
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
	      	if ((qr.containsKey(did) != false)&&(qr.get(did)==1.0)){
	      	  RR++;
	      	}
	      	i++;
	      }
	      result=(double)RR/K;
	      return result;
	    }catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return result;
	    }
  }
  
  public static double Recall(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K){
	  double result=0.0;
	  try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	      
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
	      		if(qr.get(key)==1.0)
	      			R++;
	      	}
	      	if (qr.containsKey(did) != false&&(qr.get(did)==1.0)){
	      	  RR++;
	      	}
	      	i++;
	      }
	      result=(double)RR/R;
	      return result;
	    }catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return result;
	    }
  }

 public static double F_Measure(HashMap < String , HashMap < Integer , Double > > relevance_judgments,int K){
	 double result;
	 double P=Precision(relevance_judgments,K);
	 double R=Recall(relevance_judgments,K);
	 result=1/(0.5*(1/P)+0.5*(1/R));
	 return result;
 }
	
 public static  HashMap<Double,Double> PR_Graph(HashMap < String , HashMap < Integer , Double > > relevance_judgments){
	 HashMap<Double,Double> PR=new HashMap<Double,Double>();
	 PR.put(0.0, 1.0);
	 int r_point=1;
	 try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	      
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
	      	  throw new IOException("query not found");
	      	}
	      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
	      	R=0;
	      	for(int j=0;j<qr.size();j++){
	      		if(qr.containsValue(1.0))
	      			R++;
	      	}
	      	if (qr.containsKey(did) != false){
	      	  RR++;
	      	}
	      	
	      	
	      	r=RR/R;
	      	p=RR/N;
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
	    }catch (Exception e){
	      System.err.println("Error:" + e.getMessage());
	      return PR;
	    }
 }
  
  public static void evaluateStdin(
    HashMap < String , HashMap < Integer , Double > > relevance_judgments){
    // only consider one query per call    
 
	  System.out.println(Precision(relevance_judgments,1));
	  System.out.println(Precision(relevance_judgments,4));
	  System.out.println(Precision(relevance_judgments,10));
	  System.out.println(Recall(relevance_judgments,1));
	  System.out.println(Recall(relevance_judgments,4));
	  System.out.println(Recall(relevance_judgments,10));
	  //System.out.println(F_Measure(relevance_judgments,1));
	  //System.out.println(F_Measure(relevance_judgments,5));
	  //System.out.println(F_Measure(relevance_judgments,10));
	  //HashMap<Double,Double> pr=PR_Graph(relevance_judgments);
	 /* for(double i=0.0;i<=1.0;){
		 if(pr.containsKey(i)){
			 System.out.println(i+":"+pr.get(i));
		 }
	  }*/
  }
}
