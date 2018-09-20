package javaprog.sockets;


import java.net.*;// need this for InetAddress, Socket, ServerSocket 
import java.io.*;// need this for I/O stuff

public class UDPEchoServer { 
	static final int BUFSIZE=1024;
	
	static public void main(String args[]) throws SocketException 
	{ 
		
		if (args.length != 1) {
			throw new IllegalArgumentException("Must specify a port!"); 
						
		}
		
		int port = Integer.parseInt(args[0]);
		
		// Datagram Packets are received/sent through DatagramSocket
		
		DatagramSocket s = new DatagramSocket(port);
		
		// DatagramPacket stuff bytes of data into UDP packets,
		// which are called datagrams
		DatagramPacket dp = new DatagramPacket(new byte[BUFSIZE], BUFSIZE);
		
		String myName = " Anastasiia\n\n";
		try { 
			while (true) {
				s.receive(dp);
				// print out client's address 
				System.out.println("Message from " + dp.getAddress().getHostAddress());
				
				
				// Concatenate read string and my name
				String input = new String(dp.getData());
				
				System.out.println(input);
				
//				input = input.replace("\n", "");
				input = input + myName;
				// Convert to bytes
				byte[] output = input.getBytes();
				
				DatagramPacket dpOut = new DatagramPacket(output, output.length);
				
				// Send it right back 
				s.send(dpOut); 
				dp.setLength(output.length);// avoid shrinking the packet buffer
				
			} 
		} catch (IOException e) {
			System.out.println("Fatal I/O Error !"); 
			System.exit(0);
			
		} 

	}
}