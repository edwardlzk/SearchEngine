package edu.nyu.cs.cs2580.hw1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

// @CS2580: this class should not be changed.
class ScoredDocument {
  public int _did;
  public String _title;
  public double _score;

  ScoredDocument(int did, String title, double score){
    _did = did;
    _title = title;
    _score = score;
  }

  String asString(){
    return new String(
      Integer.toString(_did) + "\t" + _title + "\t" + Double.toString(_score));
  
  }
  
  String asHTML(int sessionId, String query){
	  String newLine = "\r\n";
	  try {
		query = URLEncoder.encode(query, System.getProperty("file.encoding"));
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	  StringBuilder ret = new StringBuilder();
	  
	  ret.append("<li>").append("<a href='/log?did="+_did+"&sid="+sessionId+"&query="+query+"&action=click'>")
	  .append(_title)
	  .append("</a>").append("</li>");
	  
	 return ret.toString();
  }
}
