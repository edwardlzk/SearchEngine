package edu.nyu.cs.cs2580;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class Stemmer {
	
	private static SnowballStemmer _stemmer=null;
	
	private static void initialize(){
		_stemmer=(SnowballStemmer)(new englishStemmer());
	}
	
	public static String stem(String term){
		if(_stemmer==null){
			initialize();
		}
		_stemmer.setCurrent(term);
		_stemmer.stem();
		return _stemmer.getCurrent();
	}

}
