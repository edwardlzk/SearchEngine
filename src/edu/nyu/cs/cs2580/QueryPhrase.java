package edu.nyu.cs.cs2580;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 * ["new york city"], the presence of the phrase "new york city" must be
 * recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {

  public QueryPhrase(String query) {
    super(query);
  }

  @Override
  public void processQuery() {
	  
	  //Find phrase first
	  String phrase = "\"(.*?)\"";
	  Pattern phrasePattern = Pattern.compile(phrase);
	  Matcher phraseMatcher = phrasePattern.matcher(_query);
	  _query=_query.toLowerCase();
	    
	  
	  while(phraseMatcher.find()){
		  String s=phraseMatcher.group(1);
		  String[] temp=s.split(" ");
		  StringBuilder sb=new StringBuilder();
		  for(int i=0;i<temp.length;i++){
			  temp[i]=Stemmer.stem(temp[i]);
			  sb.append(temp[i]+" ");
		  }
		  
		  _tokens.add(sb.toString());
	  }
	  _query = _query.replaceAll(phrase, " ");
	  
	  
	  //add the rest terms to the query vector
	  Scanner s = new Scanner(_query);
	  while (s.hasNext()) {
	      _tokens.add(Stemmer.stem(s.next()));
	   }
	  s.close();
  }
  
  
  public static void main(String[] args){
	  String query = "\"new york\" \"york time\" NYU";
	  QueryPhrase test = new QueryPhrase(query);
	  test.processQuery();
	  
	  for(String t : test._tokens){
		  System.out.println(t);
	  }
  }
}
