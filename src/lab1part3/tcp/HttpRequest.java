package lab1part3.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HttpRequest {
	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		HttpRequest httpReq = new HttpRequest();

		System.out.println("Testing 1 - Send Http GET request");
//		http.sendGet();

	}

	// HTTP GET request
	public String sendGet( String cmdType, String serverPath, String cmdText ) throws Exception {
		
		QueryString qs = new QueryString ("type", cmdType);
		qs.add( "cmd", cmdText );
		String url = serverPath + ":2000?"+ qs;
//		String url = "http://localhost:1234?type=loop&cmd=2";
		System.out.println(url + "\n");
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		System.out.println("URL CHECK LINE 38 " + url + "\n");
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return response.toString();

	}
}
