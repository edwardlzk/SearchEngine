package edu.nyu.cs.cs2580;

public class Stemmer {
    private static final int INC=50;
    private char[] b;
    private int i,i_end;
    private int j; // temporary position where transformation will happen
    private int k; // end position of current stemming
    
	/**
	 * @param args
	 */
 /*   private final void step1()
    {
    	String strb=new String(b);
    	if(b[k] == 's')
    	{
    		if(strb.endsWith("sses"))
    			k-=2;
    		else if(strb.endsWith("ies"))
    			{
    				b[k-2]='\0';
    				strb=b.toString();
    			}
    		else if(b[k-1]!='s')
    			k--;
    	}
    	if(strb.endsWith("eed"))
    	{
    		if(m()>0)
    			k--;
    	}
    	else if(ends("ed")||ends("ing") && vowelinstem())
    	{
    		k=j;
    		if(ends("at"))
    			setto("ate");
    		else if(ends("bl"))
    			setto("ble");
    		else if(ends("iz"))
    			setto("ize");
    		else if(doublec(k))
    		{
    			k--;
    			{
    				int ch = b[k];
    				if(ch=='1' || ch== 's' || ch =='z')
    					k++;
    			}
    		}
    		else if( m() == 1 && cvc (k))
    			setto ("e")
    			
    	}
    }*/
  /*  public static boolean ends(String suffix)
    {
    	
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       char[] b="abcdes";
       
	}*/

}
