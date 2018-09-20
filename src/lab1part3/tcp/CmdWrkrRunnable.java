package lab1part3.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.sun.xml.internal.fastinfoset.Decoder;

public class CmdWrkrRunnable implements Runnable{

	// necessary http get response headers
	// date, server, content-length, content-type
	
	
	
    protected Socket clientSocket = null;
    protected String serverText   = null;
    private String OUTPUT 		  = null;
    
    private static final String OUTPUT_HEADER_START = "HTTP/1.1 200 OK\r\n" +
    	    "Content-Type: text/html\r\n" + 
    	    "Content-Length: ";
	private static final String OUTPUT_HEADER_END 	= "\r\n\r\n";

    public CmdWrkrRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
        	System.out.println("Hello from server");
        	// input is a get request - url
            InputStream input = clientSocket.getInputStream();
            
            // get query from input url
            String query = extractDecodeQuery(input);
            System.out.println(query);
            
            
            // get command type and text
            String[] cmd 	= query.split("&");
            String cmdType = cmd[0].split("=")[1];
            String cmdText = cmd[1].split("=")[1];
//            String cmdType 	= cmd[Arrays.asList(cmd).indexOf("type") + 1];
//            String cmdText 	= cmd[Arrays.asList(cmd).indexOf("cmd") + 1];
            
            if (cmdType.equals("loop")) {
            	try {
            		int iterNumber 	= Integer.parseInt(cmdText);
            		OUTPUT 			= URLEncoder.encode(new ConsumingTask(iterNumber).
            				FibonacciNumbers().toString(), "UTF-8");
            	} catch (NumberFormatException ex) {
            		OUTPUT 			= "Please, enter a valid number of Fibonacci numbers";
            	}
            } else if (cmdType.equals("cmd")) {
            	System.out.println("Command being executed is ---- " + cmd);
            	OUTPUT = cmdExecute(cmdText);
            }
            
            OutputStream output = clientSocket.getOutputStream();
            
            output.write((OUTPUT_HEADER_START 
            		+ OUTPUT.length() 
            		+ OUTPUT_HEADER_END 
            		+ OUTPUT).getBytes());
            
            // this is the server response sent
            output.flush();
            
            output.close();
            input.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    
    // Command initiation will include on of the following
 	// depending on the OS type
 	public String shellPath() {
 		String sh = "/bin/sh";
 		String OS = System.getProperty("os.name").toLowerCase();
 		if (OS.indexOf("win") >= 0) {
 			sh = "bash";
 		}	
 		System.out.println("OS bash path is " + sh);
 		return sh;
 	}
    
    public String convert(InputStream inputStream) throws IOException {
    	 
    	StringBuilder stringBuilder = new StringBuilder();
    	String line = null;
    	
    	//try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset))) {	
    	//	while ((line = bufferedReader.readLine()) != null) {
    	//		stringBuilder.append(line);
    	//	}
    	//}
    	
    	LineNumberReader lr = new LineNumberReader(new InputStreamReader(inputStream));  
    	 String inputLine = null;  
         String method = null;  
         String httpVersion = null;  
         String uri = null; 
    	
    	inputLine = lr.readLine();  
        System.out.println("URL HIT FROM CLIENT IS " + inputLine);
        String[] requestCols = inputLine.split("\\s");  
        uri = requestCols[1];  
        System.out.println(uri);
        uri = uri.split("\\?")[1];
    	return uri;
    }
    
    public String extractDecodeQuery (InputStream inputStream) throws MalformedURLException, IOException {
    	System.out.println("HEy im here");
    	String decode = "";
    	try {
    		//URL urlObj = new URL(convert(inputStream, StandardCharsets.UTF_8));
	    	//String query = urlObj.getQuery();
	    	//queryDecode = URLDecoder.decode(query, "UTF-8");
	    	String query = convert(inputStream);
	    	decode = URLDecoder.decode(query);
	    	System.out.println("\nDecoded query: " + decode);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return decode;
    }
    
    
    
    
    
    public String cmdExecute(String cmdText) {
    	// Create a command as an array of String elems
		String[] fullCmd	= {shellPath(), "-c", cmdText};
		String output		= "Unknown command";
		try {
			// Execute a command
			Process process = Runtime.getRuntime().exec(fullCmd);
            // Get input streams
            BufferedReader stdInput = new BufferedReader(
            		new InputStreamReader(
            				process.getInputStream()) //, "UTF-8"
            		);
            BufferedReader stdError = new BufferedReader(
            		new InputStreamReader(
            				process.getErrorStream()) // , "UTF-8"
            		);
            
            // Read command standard output and save
            String s = null;
            output = "Standard output for " + cmdText + "\n\n";

            System.out.println("Standard output for " + cmdText);
            try { 
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	                output = output + "\n" + s;
	            }
            } catch (Exception ex) {
        		System.out.println("Readline exception\n");
        	}

            
            // Read command standard errors and save
            if ((s = stdError.readLine()) != null) { 
            	output = "Standard error for " + cmdText + "\n\n";
            	output = output + s;
            	
            	System.out.println	("Standard error for " + cmdText);
            	System.out.println(s);
        		
            	try { 
		        	while ((s = stdError.readLine()) != null) {
		        		System.out.println(s);
		        		output = output + "\n" + s;
		        	}
            	} catch (Exception ex) {
            		System.out.println("Readline exception\n");
            	}
            }
            
        } catch (Exception e) {
        	System.out.println("Exception occurred\n");
        	// Print all the error stack, since a general exception class is captured
        	e.printStackTrace(System.err);
            
            // Java VM is terminated with non-zero status: abnormal termination
            System.exit(1);
		}
		try {
			output = URLEncoder.encode(output, "UTF-8");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return output;
    }
    
}
