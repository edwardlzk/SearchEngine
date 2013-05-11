package edu.nyu.cs.cs2580.hw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.nyu.cs.cs2580.hw3.SearchEngine.Options;

/**
 * 
 * TODO bytes[] for line
 * 
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedOccurrence extends Indexer {
	private HashMap<String, HashMap<Integer, Vector<Integer>>> _index = new HashMap<String, HashMap<Integer, Vector<Integer>>>();
	// //all unique terms
	// private Vector<String> _terms = new Vector<String>();
	// Stores all Document in memory.
	private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();

	

	public IndexerInvertedOccurrence() {
	}

	public IndexerInvertedOccurrence(Options options) {
		super(options);
		System.out.println("Using Indexer: " + this.getClass().getSimpleName());
	}

	@Override
	public void constructIndex() throws IOException {
		String corpusFile = _options._corpusPrefix + "/";
		System.out.println("Construct index from: " + corpusFile);

		chooseFiles cf = new chooseFiles(_options);
		int times = cf.writeTimes();
		System.out.println(times);
		FileOps filewriter = new FileOps(_options._indexPrefix + "/");
		String[] tempFiles = new String[times];
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			Vector<String> files = cf.loadFile(i);
			for (String name : files) {
				String filepath = corpusFile + name;
				File file = new File(filepath);
				String content = ProcessHtml.process(file, _options);
				if (content != null)
					processDocument(content);
			}
			String name = "temp" + i + ".txt";
			tempFiles[i] = name;
			Map<String, String> content = new HashMap<String, String>();
			for (String term : _index.keySet()) {
				StringBuilder value = new StringBuilder();

				Set<Integer> keys = _index.get(term).keySet();
				Integer[] did = new Integer[keys.size()];
				int count = 0;
				for (Integer key : keys) {
					did[count++] = key;
				}
				Arrays.sort(did);
				for (count = 0; count < did.length; count++) {
					value.append(did[count]).append(",");
					for (int p : _index.get(term).get(did[count])) {
						value.append(p).append(",");
					}
					value.deleteCharAt(value.length() - 1);
					value.append("|");
				}

				/*
				 * for(Integer did:_index.get(term).keySet()) {
				 * value.append(did).append(","); for(int
				 * p:_index.get(term).get(did)){ value.append(p).append(","); }
				 * value.deleteCharAt(value.length()-1); value.append("|"); }
				 */
				value.deleteCharAt(value.length() - 1);
				content.put(term, value.toString());
			}
			filewriter.write(name, content);
			_index.clear();
		}
		String corpus_statistics = _options._indexPrefix + "/" + "statistics";
		BufferedWriter outsta = new BufferedWriter(new FileWriter(
				corpus_statistics));
		// the first line in the corpus_statistics is the number of docs in the
		// corpus
		outsta.write(_numDocs + "\n");
		outsta.write(_totalTermFrequency + "\n");
		outsta.close();
		filewriter.merge(tempFiles, "merge.txt", "|");

	}

	private void processDocument(String content) {
		try {
			Scanner s = new Scanner(content).useDelimiter("\t");
			String title = s.next();
			String body = s.next();
			s.close();
			DocumentIndexed doc = new DocumentIndexed(_documents.size());
			// doc.setTitle(title);
			_documents.add(doc);
			++_numDocs;
			generateIndex(title + body, title);
		} catch (Exception e) {
			System.out.println("The file that has error");
		}

	}

	private void generateIndex(String content, String title) {
		Scanner s = new Scanner(content); // Uses white space by default.
		int pos = 1;
		int totalcount = 0;
		HashMap<String, Vector<Integer>> t_plist = new HashMap<String, Vector<Integer>>();
		Vector<Integer> t_poslist = null;
		int did = _documents.size() - 1;
		while (s.hasNext()) {
			++_totalTermFrequency;
			++totalcount;
			String token = s.next();
			if (!t_plist.containsKey(token)) {
				t_poslist = new Vector<Integer>();
				t_plist.put(token, t_poslist);
			}
			t_poslist = t_plist.get(token);
			t_poslist.add(pos);
			++pos;
		}
		for (String term : t_plist.keySet()) {
			if (!_index.containsKey(term)) {
				HashMap<Integer, Vector<Integer>> n_list = new HashMap<Integer, Vector<Integer>>();
				n_list.put(did, t_plist.get(term));
				_index.put(term, n_list);
			} else {
				HashMap<Integer, Vector<Integer>> plist = _index.get(term);
				plist.put(did, t_plist.get(term));
				_index.put(term, plist);
			}

		}
		try {
			// store the document in our index
			String filePath = _options._indexPrefix + "/" + did;
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			out.write(title + "\n");
			out.write(totalcount + "\n");
			for (String term : t_plist.keySet()) {
				out.write(term + "\t" + t_plist.get(term).size() + "\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	@Override
	public void loadIndex() throws IOException, ClassNotFoundException {
		String indexFile = _options._indexPrefix + "/statistics";
		String docFile = _options._indexPrefix + "/";

		System.out.println("Load index from: " + indexFile);
		BufferedReader reader = new BufferedReader(new FileReader(indexFile));
		String line = null;
		if ((line = reader.readLine()) != null)
			this._numDocs = Integer.parseInt(line);
		if ((line = reader.readLine()) != null)
			this._totalTermFrequency = Integer.parseInt(line);
		reader.close();
		System.out.println("Number of docs: " + this._numDocs);
		System.out.println("TotalTermFrequency: " + this._totalTermFrequency);

	}

	@Override
	public DocumentIndexed getDoc(int docid) {
		DocumentIndexed doc = new DocumentIndexed(docid);
		String docpath = _options._indexPrefix + "/" + docid;
//		System.out.println(docpath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(docpath));
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
				if (count == 1) {
					doc.setTitle(line);
				} else if (count == 2) {
					doc.setTermTotal(Integer.parseInt(line));
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * In HW2, you should be using {@link DocumentIndexed}.
	 */
	@Override
	public DocumentIndexed nextDoc(Query query, int docid) {
		DocumentIndexed doc = new DocumentIndexed(docid);
		String indexFile = _options._indexPrefix + "/merge.txt";
		HashMap<String, Vector<Integer>> terms = new HashMap<String, Vector<Integer>>(); // term
																							// and
																							// its
																							// position
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(indexFile));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] data = line.split("\t");
				if (query._tokens.contains(data[0])) { // if found one token in
														// the query
					String pos = data[1];
					Vector<Integer> docids = new Vector<Integer>();
					for (String s : pos.split("\\|")) {
						docids.add(Integer.parseInt((s.split(","))[0])); // get
																			// all
																			// docids
					}
					terms.put(data[0], docids); // save term and its docids

				}// end for find one term
			}// end for read file
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Vector<Integer> ids = new Vector<Integer>();
		int result;
		int id;
		for (String term : query._tokens) {
			if (terms.get(term) != null) {
				id = next(term, docid, terms.get(term));
				ids.add(id);
			}
		}
		if (ids.size() != query._tokens.size()) // no doc includes all the terms
			return null;

		if (ids.size() == 1 || find(ids)) { // found one
			result = ids.get(0);
			doc = getDoc(result);
			return doc;

		} else {
			return nextDoc(query, max(ids) - 1);
		}

	}

	private boolean find(Vector<Integer> ids) {
		int first = ids.get(0);
		for (int i = 1; i < ids.size(); i++) {
			if (ids.get(i) != first)
				return false;
		}
		return true;
	}

	private int max(Vector<Integer> ids) {
		int max = 0;
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) > max)
				max = ids.get(i);
		}
		return max;
	}

	private int next(String word, int docid, Vector<Integer> docids) {
		// Binary Search
		if (docids.lastElement() <= docid)
			return -1;
		if (docids.firstElement() > docid) {
			return docids.firstElement();
		}
		int high = docids.size() - 1;
		int result = binarySearch(word, 0, high, docid, docids);
		return docids.get(result);
	}

	private int binarySearch(String word, int low, int high, int docid,
			Vector<Integer> docIDs) {
		while (high - low > 1) {
			int mid = (low + high) >>> 1;
			if (docIDs.get(mid) <= docid) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}
		return docIDs.get(low) > docid ? low : high;
	}

	public int nextPhrase(Query query, int docid, int pos) {
		DocumentIndexed idVerify = nextDoc(query, docid - 1);
		if (idVerify._docid != docid) {
			return -1;
		}
		Vector<Integer> ids = new Vector<Integer>();
		int id;
		for (int i = 0; i < query._tokens.size(); i++) { // each term's position
			id = next_pos(query._tokens.get(i), docid, pos);
			if(id == -1)
				return -1;
			ids.add(id);
		}


		int j = 0;
		for (; j < ids.size() - 1; j++) {
			if ((ids.get(j) + 1) != ids.get(j + 1)) {
				break;
			}
		}
		if (j == (ids.size() - 1)) {
			return ids.get(0);
		} else
			return nextPhrase(query, docid, (max(ids) - 1));
	}

	private int next_pos(String word, int docid, int pos) {
		String indexFile = _options._indexPrefix + "/merge.txt";
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(indexFile));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] data = line.split("\t");
				if (word.equals(data[0])) { // if found one token in the query
					String positions = data[1];
					for (String s : positions.split("\\|")) {
						if (docid == Integer.parseInt((s.split(","))[0])) { // found
																			// docid
							String[] allpos = s.split(",");
							for (int i = 1; i < allpos.length; i++) { // get all
																		// pos
																		// in
																		// the
																		// doc
								if (Integer.parseInt(allpos[i]) > pos) {
									reader.close();
									return Integer.parseInt(allpos[i]);
								} // found the pos and return
							} // end for loop for all pos for one term in one
								// doc
						}
					} // end for found docid

				}// end for find the term
			}// end for read file
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // not return yet, indicate not found next pos, return -1
	}

	@Override
	public int corpusDocFrequencyByTerm(String term) {
		String indexFile = _options._indexPrefix + "/corpus.idx";
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(indexFile));
			String line;
			while ((line = reader.readLine()) != null) {
				int termDocFren = 0;
				String title = "";
				String data = "";
				Scanner s = new Scanner(line).useDelimiter("\t");
				while (s.hasNext()) {
					title = s.next();
					data = s.next();
				}
				if (title.equals(term)) {
					String[] docs = data.split("\\|");
					termDocFren = docs.length;
				}
				reader.close();
				return termDocFren;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int corpusTermFrequency(String term) {

		if (queryCache.containsKey(term)) {
			return queryCache.get(term);
		}

		String indexFile = _options._indexPrefix + "/merge.txt";
		int freq = 0;
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(indexFile));
			String line;
			while ((line = reader.readLine()) != null) {
				int termDocFren = 0;
				int termCorpusFren = 0;
				String title = "";
				String data = "";
				title = line.split("\t")[0];
				data = line.split("\t")[1];
				
				if (title.equals(term)) {
					String[] docs = data.split("\\|");
					termDocFren = docs.length;
					Vector<String> Appenddoc = new Vector<String>(); // docs
																		// need
																		// to
																		// update
					for (String doc : docs) {
						String[] docid = doc.split(",");
						Appenddoc.add(docid[0]);
						termCorpusFren += docid.length;
					}
					termCorpusFren -= termDocFren;
					reader.close();
					freq = termCorpusFren;
					break;
				}
				
			}
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		queryCache.put(term, freq);
		return freq;
	}

	@Override
	public int documentTermFrequency(String term, String url) {
		String docpath = _options._indexPrefix + "/" + url;
		int result;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(docpath));
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
				if (count > 2) {
					String[] terms = line.split("\t");
					if (terms[0].equals(term)) {
						result = Integer.parseInt(terms[1]);
						reader.close();
						return result;
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		Options option = new Options("conf/engine.conf");
		IndexerInvertedOccurrence index = new IndexerInvertedOccurrence(option);
		// index.constructIndex();
		index.loadIndex();
		//
		Query query = new Query("web");
		query.processQuery();

		Document nextdoc = index.nextDoc(query, 4122);
		System.out.println(index.corpusTermFrequency("web"));

		if(nextdoc!=null)
		System.out.println(nextdoc._docid);
		else
		System.out.println("Null");

	}
}
