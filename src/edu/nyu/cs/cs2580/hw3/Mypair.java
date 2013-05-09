package edu.nyu.cs.cs2580.hw3;



public class  Mypair implements Comparable<Object>{
		  int key;
		  int value;
		  
public void setKey(int k){
	this.key=k;
}

public void setValue(int v){
	this.value=v;
}


		@Override
		public int compareTo(Object arg) {
			Mypair o=new Mypair();
			o=(Mypair)arg;
			 if(this.value<o.value)
				  return 1;
			  else if(this.value>o.value)
				  return -1;
			  else{  
				  if(this.key<o.key)
					  return -1;
				  else
					  return 1;
		  }
			
		}
		  
	  }


