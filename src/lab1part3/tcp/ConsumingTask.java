package lab1part3.tcp;

public class ConsumingTask {
	//....................................................................
	// A class to compute Fibonacci numbers
	//....................................................................
	
	private int iterNumber;		// number of Fibonacci values to return
	
	public ConsumingTask( int inIterNumber) {
		this.iterNumber = inIterNumber;	
	}

	// Computes Fibonacci numbers and returns StringBuffer
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
