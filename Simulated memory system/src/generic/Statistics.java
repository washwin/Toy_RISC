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
			
			writer.println("Number of Static Instructions executed = " + numberOfStaticInstructions);
			writer.println("Number of Dynamic Instructions executed = " + numberOfInstructions);
			writer.println("Number of Cycles taken = " + numberOfCycles);
			//writer.println("Number of OF Stalls = " + numberOfOFStalls);
			//writer.println("Number of times an instruction on a wrong branch path entered the pipeline = " + numberOfwrongbranches);
			double cpi = (double)numberOfCycles/numberOfInstructions;
			double ipc = (double)numberOfInstructions/numberOfCycles;
			double stall_rate = (double)numberOfOFStalls/numberOfInstructions;
			double wrongbranch_rate = (double)numberOfwrongbranches/numberOfInstructions;
			//writer.println("CPI = " + cpi);
			writer.println("IPC = " + ipc);
			//writer.println("OF Stall Rate = " + stall_rate);
			//writer.println("Wrong Branch Rate = " +  wrongbranch_rate);
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
