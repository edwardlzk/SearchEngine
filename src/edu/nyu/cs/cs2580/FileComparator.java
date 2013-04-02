package edu.nyu.cs.cs2580;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {


	@Override
	public int compare(File o1, File o2) {
		
		if(!o1.isFile()){
			return 1;
		}
		else{
			if(!o2.isFile()){
				return -1;
			}
			else{
				return o1.getName().compareTo(o2.getName());
			}
		}

	}

}
