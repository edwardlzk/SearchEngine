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
import java.util.concurrent.atomic.AtomicInteger;

class QueryHandler implements HttpHandler {
	private static String plainResponse = "Request received, but I am not smart enough to echo yet!\n";

	private RankerFactory rankerFactory;
	private AtomicInteger session;

	public QueryHandler(RankerFactory rankerFactory) {
		this.rankerFactory = rankerFactory;
		this.session = new AtomicInteger(0);
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
		boolean isHTML = false;

		Ranker ranker = rankerFactory.getRanker("");
		Map<String, String> query_map = null;
		int currentSession = session.get();
		
		if ((uriPath != null) && (uriQuery != null)) {
			if (uriPath.equals("/search")) {
				query_map = getQueryMap(uriQuery);
				Set<String> keys = query_map.keySet();
				if (keys.contains("query")) {
					if (keys.contains("ranker")) {
						String ranker_type = query_map.get("ranker");


						ranker = rankerFactory.getRanker(ranker_type);
						
						if(keys.contains("format")){
							if(query_map.get("format").equals("HTML")){
								isHTML = true;
								session.incrementAndGet();
							}
						}
					}
					Vector<ScoredDocument> sds = ranker.runquery(query_map
							.get("query"));
					Iterator<ScoredDocument> itr = sds.iterator();
					while (itr.hasNext()) {
						ScoredDocument sd = itr.next();
						if (queryResponse.length() > 0) {
							queryResponse = queryResponse + "\n";
						}
						if(isHTML){
							queryResponse += sd.asHTML(currentSession, query_map
									.get("query"));
						}
						else{
							queryResponse = queryResponse + query_map.get("query")
									+ "\t" + sd.asString();
						}
					}
					if (queryResponse.length() > 0) {
						queryResponse = queryResponse + "\n";
					}

				}
				if (!isHTML) {
					// Write response to file
					Logger logger = Logger.getInstance();

					logger.logWriter(ranker.getLogName(), queryResponse, false);
					// Construct a simple response.
					String first = query_map.get("query") + "\t"
							+ query_map.get("ranker") + "\r\n";
					queryResponse = first + queryResponse;
				}
			}
			
		}
		
		
		

		
		Headers responseHeaders = exchange.getResponseHeaders();
		if(isHTML){
			responseHeaders.set("Content-Type", "text/html");
		}
		else{
			responseHeaders.set("Content-Type", "text/plain");
		}
		exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(queryResponse.getBytes());
		responseBody.close();
	}
}
