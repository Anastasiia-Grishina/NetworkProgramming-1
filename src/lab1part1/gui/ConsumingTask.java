package lab1part1.gui;

public class ConsumingTask {
	// The class accepts a number of values to count in Fibonacci sequence
	// And returns StringBuffer with the sequence
	private int iterNumber;
	
	public ConsumingTask( int inIterNumber) {
		this.iterNumber = inIterNumber;	
	}
	
	public StringBuffer FibonacciNumbers () {
			
		long f1 = 0;
		long f2 = 1;
		
		StringBuffer result = new StringBuffer( iterNumber + " Fibonacci numbers: \n");
		for (int i=0; i<iterNumber; i++) {
			result.append("Number " + i + ": " + f2 + "\n");
			long temp = f2;
			f2 = f1 + f2;
			f1 = temp;
		}
		return result;
	}
	
}
