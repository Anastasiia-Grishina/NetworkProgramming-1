package lab1part3.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HttpRequest {
	private final String USER_AGENT = "Mozilla/5.0";

	public String sendGet( String cmdType, String serverPath, String cmdText ) throws Exception {
		//.......................................
		// Construct HTTP GET request
		//.......................................
		
		// Form an encoded query string from input parameters
		// query format: type=<cmdType>&cmd=<cmdText>
		QueryString qs 	= new QueryString ("type", cmdType);
		qs.add( "cmd", cmdText );
		
		// Form a url from server path, port and the encoded query
		String url 		= serverPath + ":9000?"+ qs;
		
		// Create url with a prepared string & open connection
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		// Optional default connection is GET
		con.setRequestMethod("GET");

		// Add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		// Get response code from the server		
		int responseCode = con.getResponseCode();

		// Get response stream from the server
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Return response in a form of string
		// It will be decoded then and passed to the text area on GUI
		return response.toString();

	}
}
