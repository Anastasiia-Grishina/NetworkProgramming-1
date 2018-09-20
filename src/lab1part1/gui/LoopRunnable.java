package lab1part1.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.scene.control.TextArea;

public class LoopRunnable extends CmdRunnable implements Runnable {
	
	public LoopRunnable(String cmdType, String cmdText, TextArea cmdOut) {
		super (cmdType, cmdText, cmdOut);
	}
	
	@Override
	public void run() {

		try {
			// Get the number of iterations
			int iterNum = Integer.parseInt(cmdText);
			// Create an obj of ConsumingTask class
			ConsumingTask fibnum = new ConsumingTask(iterNum);
			// Print result of Fibonacci numbers method directly to the textArea
			cmdOut.appendText(fibnum.FibonacciNumbers().toString());
			
		} catch (NumberFormatException ex) {
			cmdOut.setText("Please, enter a valid number of Fibonacci numbers");
			
        } catch (Exception e) {
        	System.out.println("Exception occurred\n");
        	// Print all the error stack, since a general exception class is captured
        	e.printStackTrace(System.err);
            
            // Java VM is terminated with non-zero status: abnormal termination
            System.exit(1);
		}		
		
	}
}