package edu.nyu.cs.cs2580;
import java.io.*;

public class Spearman {

//	private double preread(String path) throws IOException{
//		BufferedReader pr=new BufferedReader(new FileReader(path));
//		String line;
//		int count=0;
//		int total=0;
//		while((line=pr.readLine())!=null){
//			count++;
//			total+=Integer.decode(line.split(":")[1]);
//		}
//		pr.close();
//		double re=(double)total/count;
//		return re;
//	}
	
	
	
	private double calCoe(String[] path) throws IOException{
		
		BufferedReader pr=new BufferedReader(new FileReader(path[0]));
		BufferedReader nv=new BufferedReader(new FileReader(path[1]));
		String line1,line2;
		double temp=0.0;
		int n=0;
		int x,y;
		while((line1=pr.readLine())!=null&&(line2=nv.readLine())!=null){
			x=Integer.parseInt(line1.split(":")[1]);
			y=Integer.parseInt(line2.split(":")[1]);
			temp+=Math.pow((x-y), 2.0);
			n++;
		}
		double re;
		re=1-(6*temp)/(n*(n*n-1));
		System.out.println(temp);
		System.out.println((n*(n*n-1)));
		pr.close();
		nv.close();
		return re;
	}
	
	
	
//	private double calCoe(String[] path, double z) throws IOException{
//		
//		BufferedReader pr=new BufferedReader(new FileReader(path[0]));
//		BufferedReader nv=new BufferedReader(new FileReader(path[1]));
//		String line1,line2;
//		double temp1,temp2,temp=0.0;
//		double totalx=0.0,totaly=0.0;
//		int x,y;
//		while((line1=pr.readLine())!=null&&(line2=nv.readLine())!=null){
//			x=Integer.decode(line1.split(":")[1]);
//			y=Integer.decode(line2.split(":")[1]);
//			temp1=x-z;   //xk-z
//			temp2=y-z;   //yk-z
//			temp+=temp1*temp2;    //sum((xk-z)(yk-z))
//			totalx+=temp1*temp1;  //sum((xk-z)(xk-z))
//			totaly+=temp2*temp2; //sum((yk-z)(yk-z))
//		}
//		double re;
//		System.out.println(totalx+" "+totaly+" "+temp);
//		re=temp/(totalx*totaly);
//		pr.close();
//		nv.close();
//		return re;
//	}
	
	public static void main(String[] args) throws IOException{
		
		if(args.length<2){
			System.out.println("There should be 2 parameters");
		}
		else if(args.length>2){
			System.out.println("Too many parameters.");
		}
		else{
			Spearman s=new Spearman();
			System.out.println(s.calCoe(args));
		}
	}
}
