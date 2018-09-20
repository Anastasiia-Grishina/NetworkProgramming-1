package lab1part1.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javafx.scene.control.TextArea;

public class CmdRunnable implements Runnable {
	
	protected String cmdText   = null, cmdType = null;
	// Get OS type
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	// We will pass here cmdOut not to implement callbacks
	// but to directly modify variable of an object from CmdGUI 
	// and then print result of commands execution into the textfield
	protected TextArea cmdOut = null;
	
	// Constructor save variables to use/modify during cmd runs
	public CmdRunnable(String cmdType, String cmdText, TextArea cmdOut) {
		this.cmdType	= cmdType;		
		this.cmdText 	= cmdText;
		this.cmdOut 	= cmdOut;	
	}
	
	// Command initiation will include on of the following
	// depending on the OS type
	public String shellPath() {
		String sh = null;
		if (OS.indexOf("win") >= 0) {
			sh = "bash";
		} else {
			sh = "/bin/sh";
		}	
		System.out.println("OS bash path is " + sh);
		return sh;
	}
	
	public void run() {
		// Create a command as an array of String elems
		String[] fullCmd = {shellPath(), "-c", cmdText};
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
            String s;
            cmdOut.appendText("Standard output for " + cmdText + "\n\n");
            System.out.println("Standard output for " + cmdText);
            try { 
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	                s = s + "\n";
	                
//	                String sEncode 	= new String(s.getBytes("CP866"));
//	                byte ptext[] 	= s.getBytes("ISO-8859-1"); 
//	                String sEncoded = new String( ptext, StandardCharsets.UTF_8 );
	                cmdOut.appendText(s);
	            }
            } catch (Exception ex) {
        		System.out.println("Readline exception\n");
        	}

            // Read command standard errors and save
            if ((s = stdError.readLine()) != null) { 
            	cmdOut.appendText	("Standard error for " + cmdText + "\n\n");
            	cmdOut.appendText(s+"\n");
            	
            	System.out.println	("Standard error for " + cmdText);
            	System.out.println(s);
        		
            	try { 
		        	while ((s = stdError.readLine()) != null) {
		        		System.out.println(s);
		        		cmdOut.appendText(s+"\n");
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
		
	}
}
