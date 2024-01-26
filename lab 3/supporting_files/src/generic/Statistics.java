package generic;

import java.io.PrintWriter;

public class Statistics {
	
	// TODO add your statistics here
	static int numberOfInstructions;
	static int numberOfCycles;
	

	public static void printStatistics(String statFile)
	{
		try
		{
			// TODO add code here to print statistics in the output file
			PrintWriter writer = new PrintWriter(statFile);			
			writer.println("number of instructions =" + numberOfInstructions);
			writer.println("number of cycles = " + numberOfCycles);
			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	// TODO write functions to update statistics
	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}

	public static int getNumberOfInstructions() {
		return numberOfInstructions;
	}

	public static int getNumberOfCycles() {
		return numberOfCycles;
	}
}
