package generic;

import java.io.*;
import generic.*;
import java.util.Arrays;
import java.util.ArrayList;
import processor.Clock;
import processor.Processor;
import processor.memorysystem.MainMemory;
import processor.pipeline.*;
import java.util.Scanner;

public class Simulator {
	
	static MainMemory newMemory = new MainMemory(); /* initializing MainMemory */
	static RegisterFile rf = new RegisterFile(); /* initializing RegisterFile */
	static Processor processor;
	static boolean simulationComplete;
	static EventQueue eventQueue = new EventQueue(); /* initializing event queue */
	static FileInputStream fis = null;
	static DataInputStream dis = null;
	static ArrayList<Integer> objInstruction = new ArrayList<Integer>();
	static int number_of_instructions = 0; /* for stats */
	static int number_of_dynamic_instructions = 0; /* for stats */
	static int number_of_OF_stalls = 0; /* for stats */
	static int number_of_wrong_branches = 0; /* for stats */
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{
		/* Loading the program into memory according to the program layout described in the ISA specification */
		
		int index = 0;
		int firstCodeAddress=0;
		try
		{
			fis = new FileInputStream(assemblyProgramFile); /* initializing an instance of fileinputstream */
			dis = new DataInputStream(fis); /* initializing an instance of datainputstream */
			while(dis.available()>0) /* parsing the object program file*/
			{
				int instruction = dis.readInt();
				if(index==0)
				{
					firstCodeAddress = instruction;
					index++;
					continue;
				}
				else
				{
					if(index>=firstCodeAddress)
					{
						number_of_instructions++;
					}
					newMemory.memory[index-1] = instruction; /* storing instruction in memory */
					index++;
				}
			}
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit("objectProgramFile not read: error\n"+e.toString());
		}
		try
		{
			/* closing inputstreams */
			dis.close();
			fis.close();
		}
		catch (Exception e)
		{
			Misc.printErrorAndExit("Inputstream not closed: error\n"+e.toString());
		}

		/*********************************************************************************************************/
		  
		/* Setting PC to the address of the first instruction in the main */
		rf.setProgramCounter(firstCodeAddress);

		/*********************************************************************************************************/
		 
		/* Initializing the registers */
		rf.setValue(0,0);
		for(int i=3;i<31;i++)
		{
			rf.setValue(i,0);
		}
		rf.setValue(1,65535);
		rf.setValue(2,65535);

		/*********************************************************************************************************/
		
		/* Uploading the values of memory and register file in processor */
		processor.setMainMemory(newMemory);
		processor.setRegisterFile(rf);
	}
	
	public static void simulate()
	{
		int count = 0;
		while(simulationComplete == false/* && count<300*/)
		{
			eventQueue.processEvents();
			processor.getRWUnit().performRW();
			processor.getMAUnit().performMA();
			processor.getEXUnit().performEX();
			//eventQueue.processEvents();
			processor.getOFUnit().performOF();
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			count++;
		}
	}
	public static void increment_dynamic_instructions()
	{
		number_of_dynamic_instructions++;
	}
	public static void increment_OF_stalls()
	{
		number_of_OF_stalls++;
	}
	public static void increment_wrong_branches()
	{
		number_of_wrong_branches++;
	}
	public static void set_stats()
	{
		/* Setting statistics */
		Statistics.setNumberOfCycles(Clock.getCurrentTime());
		Statistics.setNumberOfStaticInstructions(number_of_instructions);
		Statistics.setNumberOfInstructions(number_of_dynamic_instructions);
		Statistics.setNumberOfOFStalls(number_of_OF_stalls);
		Statistics.setNumberOfWrongBranches(number_of_wrong_branches);
	}
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
	public static EventQueue getEventQueue()
	{ 
		return eventQueue;
	}


}
