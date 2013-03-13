package edu.nyu.cs.cs2580.hw0;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Instructors' simple version. As implemented now, this version does not "echo"
 * the user query. It simply returns the same string and logs the user request
 * every time.
 */
public class EchoServer {

	// @CS2580: please use a port number 258XX, where XX corresponds
	// to your group number.
	private static int port = 25812;

	public static void main(String[] args) throws IOException {
		// Create the server.
		InetSocketAddress addr = new InetSocketAddress(port);
		HttpServer server = HttpServer.create(addr, -1);

		// Attach specific paths to their handlers.
		server.createContext("/search", new EchoHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Listening on port: " + Integer.toString(port));
	}
}

/**
 * Instructors' simple version.
 */
class EchoHandler implements HttpHandler {

	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		String response = "";
		if (!requestMethod.equalsIgnoreCase("GET")) { // GET requests only.
			return;
		}

		URI requestedUri = exchange.getRequestURI();
		String query = requestedUri.getRawQuery();
		Map<String, String> param = parseQuery(query);
		if (param.containsKey("query")) {
			response = param.get("query");
		}

		// Construct a echo response.
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/plain");
		exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(response.getBytes());
		responseBody.close();
	}

	/**
	 * Parse the query, separate the parameters.
	 */
	private Map<String, String> parseQuery(String query)
			throws UnsupportedEncodingException {
		if (query == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			String param[] = pair.split("[=]");

			String key = null;
			String value = null;
			if (param.length > 0) {
				key = URLDecoder.decode(param[0],
						System.getProperty("file.encoding"));
			}

			if (param.length > 1) {
				value = URLDecoder.decode(param[1],
						System.getProperty("file.encoding"));
			}

			if (key != null && value != null) {
				result.put(key, value);
			}
		}
		return result;
	}
}
