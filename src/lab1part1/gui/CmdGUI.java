package lab1part1.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CmdGUI extends Application {

	private TextField 	cmdField; 				// one input field for cmds and a loop
	private Button 		runButton, loopButton;	// buttons for cmds and a loop
	private TextArea 	cmdOut, loopOut;		// two output text fields for cmds and a loop
	
    protected boolean      isStopped    = false;// thread state flag
    protected Thread       runningThread= null; // pointer to the running thread
    
    // volatile keyword: if there are several threads, 
    // they will write/read command variable directly 
    // to the main memory, no confusion of unsyncronization
	public volatile String cmdText 		= null; // text of the command or number given to the loop
	public volatile String cmdType		= null; // type of cmd: "cmd" or "loop"
	
	private GridPane createCommandsGUI() {
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
        // Add Header
        Label headerLabel = new Label("Command Executor");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        // Add a node (node, colIndex, rowIndex, colSpan, rowSpan) 
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);

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
        
        // Enable scroll bar
//        ScrollBar scrollBarv = (ScrollBar)loopOut.lookup(".scroll-bar:vertical");
//        scrollBarv.setDisable(false);
    }
    
    // Creating the mouse event handler for buttons
    EventHandler<javafx.scene.input.MouseEvent> eventHandler = 
       new EventHandler<javafx.scene.input.MouseEvent>() { 
       
       @Override 
       public void handle(javafx.scene.input.MouseEvent e) { 
    	   Object src 	= e.getSource();
    	   cmdText 		= cmdField.getText();
    	   
    	   if (src == loopButton) {
    		   cmdType = "loop";
    		   
    		   try {
    			   int iterNumber = Integer.parseInt(cmdText);
    			   
    			   // sync the threads to work with the same state
    			   synchronized(this) {
    				   runningThread = Thread.currentThread();
    			   }
    			   // Make a thread for loop execution with Thread(target)
    			   LoopRunnable cmdRun 	= new LoopRunnable(cmdType, cmdText, loopOut);
    			   Thread loopThread 	= new Thread(cmdRun);
    			   
    			   // Clean the output text field, since it will be changed 
    			   // by the further thread 
    			   loopOut.setText("");
    			   
    			   loopThread.start();
    			   
    		   } catch (NumberFormatException ex) {
    			   loopOut.setText("Please, enter a valid number of Fibonacci numbers");
    		   } 
    		   
    	   } else {
    		   cmdType = "cmd";
				
    		   // sync the threads to work with the same state
    		   synchronized(this){
    			   runningThread = Thread.currentThread();
    		   }
    		   // Make a thread for loop execution with Thread(target)
    		   CmdRunnable cmdRun 	= new CmdRunnable(cmdType, cmdText, cmdOut);
    		   Thread cmdThread 	= new Thread(cmdRun);
			   
    		   // Clean the output text field, since it will be changed 
    		   // by the further thread 
    		   cmdOut.setText("");
	    		   
    		   cmdThread.start();
    	   }
       }    
    };
        
    public static void main(String[] args) {
        Application.launch(args);
    }
      

}
