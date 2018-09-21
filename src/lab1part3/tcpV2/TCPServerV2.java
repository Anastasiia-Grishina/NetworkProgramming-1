package lab1part3.tcpV2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class TCPServerV2  {
	
    protected int          serverPort   = 8080;
    public ServerSocket serverSocket = null;
    public boolean      isStopped    = false;
    protected Thread       runningThread= null;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public TCPServerV2(int port){
        this.serverPort = port;
    }

    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
        	// Severe log: could not stop the server, although required
        	LOGGER.severe("Error closing server");
            throw new RuntimeException("Error closing server", e);
        }
    }

    public ServerSocket openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
        	// Warning log: the server port was not opened
        	// A problem, but the program did not crash
        	LOGGER.warning("Cannot open port " + this.serverPort);
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
        return this.serverSocket;
    }
    
    public static void main(String[] args){
    	int port;
    	
    	if (args.length != 1){
			throw new IllegalArgumentException("Must specify a port!");
		}
    	port = Integer.parseInt(args[0]);
    	
    	setLogger();
    	
    	TCPServerV2 server = new TCPServerV2(port);
    	LOGGER.info("Server created successfully");
    	
    	// Client thread counter
    	int i=0;
        try (ServerSocket listener = server.openServerSocket()) {
        	for(;;) {
                Socket s=listener.accept();
                // Info log: informative message
                LOGGER.info("Client accepted");
                
                ServerRunnableV2 obj_worker = new ServerRunnableV2(s, "Multithreaded server");
                Thread serverThread = new Thread(obj_worker);
            	serverThread.start();
            	// Info log: informative message
            	LOGGER.info("Client thread started. Client thread number: " + i++ 
                		+ ". Thread name " + serverThread.getName());
                }
        } catch (IOException e) {
            if(server.isStopped()) {
            	// Sever log: server stopped, program finished
            	LOGGER.severe("Error closing server");
                System.out.println("Server Stopped.") ;
                return;
            }
            // Warning log: client not accepted, but server is working
            LOGGER.warning("Error accepting client connection");
            throw new RuntimeException(
                "Error accepting client connection", e);
    	} finally {
    		server.stop();
    	}
    }
    
    public static void setLogger() {
		Handler fileHandler = null;
		Formatter simpleFormatter = null;
		try{
			
			// Creating FileHandler
			fileHandler = new FileHandler("./TCPServerV2.log");
			
			// Creating SimpleFormatter
			simpleFormatter = new SimpleFormatter();
			
			// Assigning handler to logger
			LOGGER.addHandler(fileHandler);
			
			// Setting formatter to the handler
			fileHandler.setFormatter(simpleFormatter);
			
			// Setting Level to ALL
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			
			// Logging message of Level finest (this should be publish in the simple format)
			LOGGER.finest("Finnest message: Logger with SIMPLE FORMATTER");
		}catch(IOException exception){
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
    }
    
}
