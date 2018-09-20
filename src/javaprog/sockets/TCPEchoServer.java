package javaprog.sockets;

//Requires a single command line arg - the port number
import java.net.*;	// need this for InetAddress, Socket, ServerSocket 

import java.io.*;	// need this for I/O stuff



// It is iterative server, it can handle only one connection
// Execution functionality happens in the main loop
public class TCPEchoServer {
	// define a constant used as size of buffer 
	static final int BUFSIZE=1024;
	// main starts things rolling
	static public void main(String args[]) { 
		
		if (args.length != 1){
			throw new IllegalArgumentException("Must specify a port!");
		}
		
		int port = Integer.parseInt(args[0]);
		try { 
			// Create Server Socket (passive socket) 
			
			// It is TCP => everything that we send is received 
			// in order and without errors
			
			// Socket is an internal descriptor in the OS
			// It gives access connection
			// And defines addresses and ports
			// Compared to files, the data is sent/streamed from another machine
			// Client port is auto generated
			ServerSocket ss = new ServerSocket(port);
			
			// Listening for incoming connection
			
			while (true) { 
				// Listens for incoming connection
				// and waits for 3 handshakes
				Socket s = ss.accept();
				// it is inside the server loop
				handleClient(s);
			} 
			
		} catch (IOException e) {
			System.out.println("Fatal I/O Error !"); 
			System.exit(0);
		}
	}
	
	// This method handles one client
	// declared as throwing IOException - this means it throws 
	// up to the calling method (who must handle it!)
	// try taking out the "throws IOException" and compiling, 
	// the compiler will tell us we need to deal with this!
	
	static void handleClient(Socket s) throws IOException 
	{ 
		byte[] buff = new byte[BUFSIZE];
		int bytesread = 0;
		
		//print out client's address
		System.out.println("Connection from " + s.getInetAddress().getHostAddress());
		
		//Set up streams 
		InputStream in = s.getInputStream(); 
		OutputStream out = s.getOutputStream();
		
		// To write my name further, save it in the variable
		String myName = " Anastasiia\n\n";

		//read/write loop 
		while ((bytesread = in.read(buff, 0, buff.length)) != -1) {
			// Concatenate read string and my name
			String input = new String(buff);
			input = input.replace("\n", "");
			input = input + myName;
			// Convert to bytes
			byte[] output = input.getBytes();
			// Write
			out.write(output);
			} 
		
		System.out.println("Client has left\n"); 
		
		s.close();
		
	}
	
}