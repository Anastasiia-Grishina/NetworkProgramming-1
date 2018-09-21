package lab1part3.tcpV2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javafx.scene.control.TextArea;

public class TCPClientV2 implements Runnable {
	
	public TextArea cmdOut		= null;
    protected Socket socket 	= null;
    protected String cmdText 	= null;
    protected String cmdType 	= null;
    
    public TCPClientV2(String address, int serverPort, 
    		String cmdType, String cmdText, TextArea cmdOut) throws Exception {
        this.socket 	= new Socket(address, serverPort);
        this.cmdType 	= cmdType;
        this.cmdText 	= cmdText;
        this.cmdOut 	= cmdOut;
    }
    
    @Override
    public void run() {
    	try {
    		
    		System.out.println("Client run");
    		// Send command
        	PrintWriter outBytes = new PrintWriter(socket.getOutputStream(), true);
        	//outBytes.println(this.cmdType);
//        	outBytes.flush();
        	String out = this.cmdType + "#" + this.cmdText;
            outBytes.println(out);
            outBytes.flush();
            
            // Get response
            String dataFeed 	= null;
            StringBuilder sb 	= new StringBuilder();
            
            // Read the bytes from the input stream of the socket line by line
            BufferedReader inBytes = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
            while ( (dataFeed = inBytes.readLine()) != null ) {
            	System.out.println("Received executed data - " + dataFeed);
            	cmdOut.appendText(dataFeed + "\n");
//            	sb.append(dataFeed);
//                sb.append("\n");
            }
            
            // Modify text in gui
//            cmdOut.setText(sb.toString());
            return;
            
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        	return;
        }
    }
    
}
