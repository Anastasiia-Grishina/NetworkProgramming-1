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
	//...................................................
	// Server Runnable Class
	// (needed to enable multithreading)
	//...................................................
	
    protected Socket clientSocket = null;
    protected String serverText   = null;
    private String OUTPUT 		  = null;
    
    //.................Educational Info...................
    // Save necessary http get response headers:
 	// date, server, content-length, content-type
    //....................................................
    private static final String OUTPUT_HEADER_START = "HTTP/1.1 200 OK\r\n" +
    	    "Content-Type: text/html\r\n" + 
    	    "Content-Length: ";
	private static final String OUTPUT_HEADER_END 	= "\r\n\r\n";

	
    public CmdWrkrRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
    	//...................................................
    	// Process a request
    	// - parse http get request to get query
    	//   query format: type=<cmdType>&cmd=<cmdText>
    	// - identify type of cmd: cmd or loop
    	// - execute & form output with necessary headers
    	//...................................................
    	
        try {
        	System.out.println("Connected to the server");
        	// Read input - a get request in a url
            InputStream input = clientSocket.getInputStream();
            
            // Get query from input url
            String query = extractDecodeQuery(input);

            // Get command type and text
            String[] cmd 	= query.split("&");
            String cmdType = cmd[0].split("=")[1];
            String cmdText = cmd[1].split("=")[1];
            
            if (cmdType.equals("loop")) {
            	// Compute Fibonacci sequence
            	try {
            		// Get integer
            		// Integer input is required in method FibonacciNumbers()
            		int iterNumber 	= Integer.parseInt(cmdText);
            		
            		// Encode output
            		// it may have spaces, new lines, which are not permitted for http
            		OUTPUT 			= URLEncoder.encode(new ConsumingTask(iterNumber).
            									FibonacciNumbers().toString(), "UTF-8");
            		
            	} catch (NumberFormatException ex) {
            		OUTPUT 			= "Please, enter a valid number of Fibonacci numbers";
            	}
            } else if (cmdType.equals("cmd")) {
            	// Execute a command entered in gui
            	// And encode output
            	OUTPUT = URLEncoder.encode(cmdExecute(cmdText), "UTF-8");
            }
            
            // Initialize output stream to then send as a server response
            OutputStream output = clientSocket.getOutputStream();
            
            output.write((OUTPUT_HEADER_START 
            		+ OUTPUT.length() 
            		+ OUTPUT_HEADER_END 
            		+ OUTPUT).getBytes());
            
            // Server response is sent with the flush
            // Otherwise it may get stuck in the stream
            output.flush();
            
            output.close();
            input.close();
        } catch (IOException e) {
        	// Input/output streams exception?
            e.printStackTrace();
        }
    }
    
 	public String shellPath() {
 		//...................................................
 		// Create a shell path depending on OS type
 		//...................................................
 		
 		String sh = "/bin/sh";
 		String OS = System.getProperty("os.name").toLowerCase();
 		if (OS.indexOf("win") >= 0) {
 			sh = "bash";
 		}	
 		System.out.println("OS bash path is " + sh);
 		return sh;
 	}

 	
    public String extractQuery(InputStream inputStream) throws IOException {
    	//...................................................
 		// Create a shell path depending on OS type
 		//...................................................
    	LineNumberReader lr = new LineNumberReader(new InputStreamReader(inputStream));  
    	String inputLine 	= null;  
    	String uri 			= null; 
    	
    	inputLine = lr.readLine();
    	// inputline = GET /?type=<cmdType>&cmd=<cmdText> HTTP/1.1
    	// Split by space
        String[] requestCols = inputLine.split("\\s");  
        uri = requestCols[1];  
        uri = uri.split("\\?")[1];
        
    	return uri;
    }
    
    public String extractDecodeQuery (InputStream inputStream) throws MalformedURLException, IOException {
    	//...................................................
 		// Extract and decode query from url passed as inputStream
 		//...................................................
    	
    	String queryDecode = "";
    	try {
	    	String query 	= extractQuery(inputStream);
	    	queryDecode 	= URLDecoder.decode(query);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return queryDecode;
    }
    
    
    public String cmdExecute(String cmdText) {
    	//...................................................
 		// Execute an OS command
    	// Return String of std out or std errors
 		//...................................................
    	
    	// Create a command as a String array
		String[] fullCmd	= {shellPath(), "-c", cmdText};
		String output		= "Unknown command";
		
		try {
			// Execute a command
			Process process = Runtime.getRuntime().exec(fullCmd);
            // Get input streams
            BufferedReader stdInput = new BufferedReader(
            		new InputStreamReader(
            				process.getInputStream()) 
            		);
            BufferedReader stdError = new BufferedReader(
            		new InputStreamReader(
            				process.getErrorStream()) 
            		);
            
            // Read command standard output and save
            String s = null;
            output = "Standard output for " + cmdText + "\n\n";

            try { 
	            while ((s = stdInput.readLine()) != null) {
	                output = output + "\n" + s;
	            }
            } catch (Exception ex) {
        		System.out.println("Readline exception\n");
        	}

            
            // Read command standard errors and save
            if ((s = stdError.readLine()) != null) { 
            	output = "Standard error for " + cmdText + "\n\n";
            	output = output + s;
            	try { 
		        	while ((s = stdError.readLine()) != null) {
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
		return output;
    }
    
}
