package generic;

import java.io.PrintWriter;

public class Statistics {
	
	static int numberOfInstructions;
	static int numberOfStaticInstructions;
	static long numberOfCycles;
	static int numberOfOFStalls;
	static int numberOfwrongbranches;
	

	public static void printStatistics(String statFile)
	{
		try
		{
			PrintWriter writer = new PrintWriter(statFile);
			
			double cpi = (double)numberOfCycles/numberOfInstructions;
			double ipc = (double)numberOfInstructions/numberOfCycles;
			double stall_rate = (double)numberOfOFStalls/numberOfInstructions;
			double wrongbranch_rate = (double)numberOfwrongbranches/numberOfInstructions;
			
			writer.println("#static instructions = " + numberOfStaticInstructions);
			writer.println("#dynamic instructions = " + numberOfInstructions);
			writer.println("#cycles = " + numberOfCycles);
			writer.println("IPC = " + ipc);

			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}
	
	public static void setNumberOfStaticInstructions(int numberOfInstructions) {
		Statistics.numberOfStaticInstructions = numberOfInstructions;
	}
	
	public static void setNumberOfCycles(long numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}
	
	public static void setNumberOfOFStalls(int numberofOFStalls) {
		Statistics.numberOfOFStalls = numberofOFStalls;
	}
	
	public static void setNumberOfWrongBranches(int numberofWrongbranches) {
		Statistics.numberOfwrongbranches = numberofWrongbranches;
	}
}
