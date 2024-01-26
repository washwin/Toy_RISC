package generic;

import java.io.PrintWriter;

public class Statistics {
	
	// TODO add your statistics here
	static int numberOfInstructions;
	static int numberOfOFStageInstructions;
	static int numberOfCycles;
	static int numberOfBranchTaken;
	static int numberOfRegisterWriteInstructions;
	
	public static void printStatistics(String statFile)
	{
		try
		{
			// TODO add code here to print statistics in the output file
			PrintWriter writer = new PrintWriter(statFile);
			
			writer.println("#instructions =" + numberOfInstructions);
			writer.println("#cycles = " + numberOfCycles);
			writer.println("#wrong branch = " + numberOfBranchTaken);
			int t = numberOfInstructions - numberOfRegisterWriteInstructions;
			writer.println("#stops = " + t);
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

	public static void setNumberOfOFInstructions(int numberOfOFStageInstructions) {
		Statistics.numberOfOFStageInstructions = numberOfOFStageInstructions;
	}

	public static void setnumberOfRegisterWriteInstructions(int numberOfRegisterWriteInstructions) {
		Statistics.numberOfRegisterWriteInstructions = numberOfRegisterWriteInstructions;
	}

	public static void setNumberOfBranchTaken(int numberOfBranchTaken) {
		Statistics.numberOfBranchTaken = numberOfBranchTaken;
	}

	public static int getNumberOfOFInstructions() {
		return numberOfOFStageInstructions;
	}

	public static int getNumberOfBranchTaken() {
		return numberOfBranchTaken;
	}
	
	public static int getNumberOfRegisterWriteInstructions() {
		return numberOfRegisterWriteInstructions;
	}
}

