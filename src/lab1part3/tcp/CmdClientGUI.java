package lab1part3.tcp;

import java.net.URLDecoder;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CmdClientGUI extends Application {

	private TextField 	cmdField; 				// one input field for cmds and a loop
	private Button 		runButton, loopButton;	// buttons for cmds and a loop
	private TextField 	urlField;				// pass a url
	private TextArea 	cmdOut, loopOut;		// two output text fields for cmds and a loop
	
    protected boolean      isStopped    = false;// thread state flag
    protected Thread       runningThread= null; // pointer to the running thread
    
    //.................Educational Info...................
    // Volatile keyword: if there are several threads, 
    // they will write/read command variable directly 
    // to the main memory, no confusion of unsyncronization
    //.....................................................
    public volatile String cmdText 		= null; // text of the command or number given to the loop
	public volatile String cmdType		= null; // type of cmd: "cmd" or "loop"
	
	private GridPane createCommandsGUI() {
		//...................................
		// GridPane - create a window layout
		//...................................
		
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.TOP_CENTER);

        // Set a paddings on each side
        gridPane.setPadding(new Insets(10, 40, 10, 40));

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 120, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(100, 200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
    
        return gridPane;
    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	//........................................
		// Starts automatically to draw a gridpane
		//........................................
    	
        primaryStage.setTitle("Command Executor");

        // Create the commands submission pane
        GridPane gridPane = createCommandsGUI();
        
        // Add UI controls to the grid pane
        addUIControls(gridPane);
        
        // Create a scene with the gridPane as scene (it is a root node).
        Scene scene = new Scene(gridPane, 800, 500);
        // Set the scene in primary stage
        primaryStage.setScene(scene);        
        primaryStage.show();
    }
    
    private void addUIControls(GridPane gridPane) {
    	//............................................
		// Adds buttons/textfields into the pane cells
		//............................................
    	
        // Add Header
        Label headerLabel = new Label("Command Executor");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        // Add a node (node, colIndex, rowIndex, colSpan, rowSpan) 
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);

    	// Add URL Label - for server implementation
        Label urlLabel = new Label("Enter server address ");
        gridPane.add(urlLabel, 0,1);

        // Add URL Text Field
        urlField = new TextField();
        urlField.setPrefHeight(30);
        urlField.setEditable(true);
		urlField.setText("http://localhost");
        gridPane.add(urlField, 1,1,2,1);
    	
        // Add Command Label
        Label cmdLabel = new Label("Enter command ");
        gridPane.add(cmdLabel, 0,2);
        
        // Add Command Text Field
        cmdField = new TextField();
        cmdField.setPrefHeight(30);
        cmdField.setEditable(true);
        gridPane.add(cmdField, 1,2,2,1);
        

        // Add Run Button
        runButton = new Button("Run commands");
        runButton.setPrefHeight(30);
        runButton.setDefaultButton(true);
        runButton.setPrefWidth(100);
        gridPane.add(runButton, 0,3,2,1);
        GridPane.setHalignment(runButton, HPos.CENTER);
        
        // Add Loop Button
        loopButton = new Button("Run loop");
        loopButton.setPrefHeight(30);
        loopButton.setDefaultButton(true);
        loopButton.setPrefWidth(100);
        gridPane.add(loopButton, 2,3,2,1);
        GridPane.setHalignment(loopButton, HPos.CENTER);
        
        
        // Add Action Listeners to Buttons
        runButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
        loopButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
        
        // Add Cmd & Loop Output Labels
        Label cmdOutLabel = new Label("Command Output ");
        gridPane.add(cmdOutLabel, 0,6);
        Label loopOutLabel = new Label("Loop Output ");
        gridPane.add(loopOutLabel, 0,7);
        
        // Add Text Area for Command Execution Output
        cmdOut = new TextArea();
        cmdOut.setEditable(false);
        cmdOut.setPrefSize(500, 200);
        gridPane.add(cmdOut, 1,6,2,1);
        
        // Add Text Area for Loop/Fibonacci Output
        loopOut = new TextArea();
        loopOut.setEditable(false);
        loopOut.setPrefSize(500, 200);
        gridPane.add(loopOut, 1,7,2,1);
    }
    
    EventHandler<javafx.scene.input.MouseEvent> eventHandler = 
       new EventHandler<javafx.scene.input.MouseEvent>() { 
    	//............................................
		// Handle mouse clicks: 
    	// - identify button, 
    	// - send http get request
    	// - set response to a corresponding text area
		//............................................
    	
       @Override 
       public void handle(javafx.scene.input.MouseEvent e) { 

    	   // Read user input
    	   String serverPath	= urlField.getText();
    	   Object src 			= e.getSource();
    	   cmdText 				= cmdField.getText();
    	   
    	   // Create empty request and empty response
    	   HttpRequest httpReq 	= new HttpRequest();
    	   String serverResponse= null;
    	   
    	   if (src == loopButton) {
    		   //...................................
    		   // Loop execution - Fibonacci numbers
    		   //................................... 
    		   
    		   cmdType = "loop";
    		   
    		   try {
    			   // Check if input is a number
    			   int iterNumber = Integer.parseInt(cmdText);
    			   
    			   // Send request and get encoded response
    			   // Request is properly formed in sendGet
    			   serverResponse = httpReq.sendGet( cmdType, serverPath, cmdText);
    			   
    			   // Decode response (to print new lines, etc)
    			   String decodedText = URLDecoder.decode(serverResponse, "UTF-8");

    			   // Set server response in a loop text area
    			   loopOut.setText(decodedText);
    			   
    		   } catch (NumberFormatException ex) {
    			   loopOut.setText("Please, enter a valid number of Fibonacci numbers");
    		   } catch (Exception e1) {
    			   System.out.println("Loop request - unknown exception");
    			   e1.printStackTrace();
    		   } 
    	   } else {
    		   //...................................
    		   // Command execution
    		   //................................... 
    		   
    		   cmdType = "cmd";
			   
    		   try {
				   
				   // Replace && with ; to avoid en-/decoding mistakes 
				   cmdText = cmdText.replaceAll("&&", ";");
				   
				   // Send request and get encoded response
    			   // Request is properly formed in sendGet
				   serverResponse = httpReq.sendGet( cmdType, serverPath, cmdText);
				   
				   // Decode response (to print new lines, etc) 
				   String decodedText = URLDecoder.decode(serverResponse, "UTF-8");
				   
				   // Set server response in a cmd text area
				   cmdOut.setText(decodedText);
			   } catch (Exception e1) {
				   System.out.println("Cmd request - unknown exception");
				   e1.printStackTrace();
			   }
		   }
       }    
    };
        
    public static void main(String[] args) {
        Application.launch(args);
    }
}

