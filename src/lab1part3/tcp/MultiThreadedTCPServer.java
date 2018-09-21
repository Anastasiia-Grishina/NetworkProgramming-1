package lab1part3.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import lab1part3.tcpV2.TCPServerV2;


public class MultiThreadedTCPServer implements Runnable {
	
    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    
//    private final static Logger LOGGER = Logger.getLogger(MultiThreadedTCPServer.class.getName());
//    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Logger LOGGER = Logger.getLogger(
    	    Thread.currentThread().getStackTrace()[0].getClassName() );

    public MultiThreadedTCPServer(int port){
        this.serverPort = port;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        int i=0;
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                	// Severe: server was stopped, program ended
                	LOGGER.severe("Server was already stopped, "
                			+ "client connection not accepted");
                    System.out.println("Server Stopped.") ;
                    return;
                }
                // Warning log: client not accepted, but server is working
                LOGGER.warning("Error accepting client connection");
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            Thread clientThread = new Thread(
                new CmdWrkrRunnable(
                    clientSocket, "Multithreaded Server")
            );
            clientThread.start();
            LOGGER.info("Client thread started. Client thread number: " + i++ 
            		+ ". Thread name " + clientThread.getName());
        }
        // Sever log: server stopped, program finished
    	LOGGER.severe("Error closing server");
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
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

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
        	// Warning log: the server port was not opened
        	// A problem, but the program did not crash
        	LOGGER.warning("Cannot open port " + this.serverPort);
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }
    
    public static void main(String[] args){
    	int port = 0;
    	
    	if (args.length != 1){
			throw new IllegalArgumentException("Must specify a port!");
		}
		port = Integer.parseInt(args[0]);
		
		
//		port = Integer.parseInt("2000");
		
		setLogger();
		
    	MultiThreadedTCPServer server = new MultiThreadedTCPServer(port);
    	new Thread(server).start();
    	
    	// Info log: informative message
    	LOGGER.info("Server started successfully");
    }
    
    public static void setLogger() {
		Handler fileHandler = null;
		Formatter simpleFormatter = null;
		try{
			
			// Creating FileHandler
			fileHandler = new FileHandler("./TCPServer.log");
			
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
