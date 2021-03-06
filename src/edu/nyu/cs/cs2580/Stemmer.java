package edu.nyu.cs.cs2580;



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
	public static void main(String[] args){
		String s="web searching";
		System.out.println(Stemmer.stem(s));
	}

}
