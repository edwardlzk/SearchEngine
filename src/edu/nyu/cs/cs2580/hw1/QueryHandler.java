package edu.nyu.cs.cs2580.hw1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;

class QueryHandler implements HttpHandler {
	private static String plainResponse = "Request received, but I am not smart enough to echo yet!\n";

	private RankerFactory rankerFactory;

	public QueryHandler(RankerFactory rankerFactory) {
		this.rankerFactory = rankerFactory;
	}



	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			
			try {
				name = URLDecoder.decode(name,
						System.getProperty("file.encoding"));
				value = URLDecoder.decode(value,
						System.getProperty("file.encoding"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put(name, value);
		}
		return map;
	}

	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		if (!requestMethod.equalsIgnoreCase("GET")) { // GET requests only.
			return;
		}

		// Print the user request header.
		Headers requestHeaders = exchange.getRequestHeaders();
		System.out.print("Incoming request: ");
		for (String key : requestHeaders.keySet()) {
			System.out.print(key + ":" + requestHeaders.get(key) + "; ");
		}
		System.out.println();
		String queryResponse = "";
		String uriQuery = exchange.getRequestURI().getQuery();
		String uriPath = exchange.getRequestURI().getPath();

		Ranker ranker = rankerFactory.getRanker("");
		Map<String, String> query_map = null;

		if ((uriPath != null) && (uriQuery != null)) {
			if (uriPath.equals("/search")) {
				query_map = getQueryMap(uriQuery);
				Set<String> keys = query_map.keySet();
				if (keys.contains("query")) {
					if (keys.contains("ranker")) {
						String ranker_type = query_map.get("ranker");


						ranker = rankerFactory.getRanker(ranker_type);
					}
					// @CS2580: The following is instructor's simple ranker that
					// does not
					// use the Ranker class.
					Vector<ScoredDocument> sds = ranker.runquery(query_map
							.get("query"));
					Iterator<ScoredDocument> itr = sds.iterator();
					while (itr.hasNext()) {
						ScoredDocument sd = itr.next();
						if (queryResponse.length() > 0) {
							queryResponse = queryResponse + "\n";
						}
						queryResponse = queryResponse + query_map.get("query")
								+ "\t" + sd.asString();
					}
					if (queryResponse.length() > 0) {
						queryResponse = queryResponse + "\n";
					}

				}
			}
		}
		
		//Write response to file
		Logger logger = Logger.getInstance();
		
		logger.logWriter(ranker.getLogName(), queryResponse, false);
		

		// Construct a simple response.
		
		String sysout = query_map.get("query")+"\t"+query_map.get("ranker")+"\r\n";
		sysout += queryResponse;
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/plain");
		exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(sysout.getBytes());
		responseBody.close();
	}
}
