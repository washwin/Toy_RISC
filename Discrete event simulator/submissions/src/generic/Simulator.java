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
	
	static MainMemory newMemory = new MainMemory();
	static RegisterFile register_file = new RegisterFile(); 
	static Processor processor;
	static boolean simulationComplete;
	static EventQueue eventQueue = new EventQueue(); 
	static FileInputStream input_stream = null;
	static DataInputStream data_input_stream = null;
	static ArrayList<Integer> objInstruction = new ArrayList<Integer>();
	static int number_of_wrong_branches = 0; 
	static int number_of_instructions = 0; 
	static int number_of_OF_stalls = 0; 
	static int number_of_dynamic_instructions = 0; 
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{	
		int index = 0;
		int firstCodeAddress=0;
		try
		{
			input_stream = new FileInputStream(assemblyProgramFile);
			data_input_stream = new DataInputStream(input_stream);
			while(data_input_stream.available()>0) 
			{
				int instruction = data_input_stream.readInt();
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
			data_input_stream.close();
			input_stream.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		register_file.setProgramCounter(firstCodeAddress);
		register_file.setValue(0,0);
		for(int i=3;i<31;i++)
		{
			register_file.setValue(i,0);
		}
		register_file.setValue(1,65535);
		register_file.setValue(2,65535);

		processor.setMainMemory(newMemory);
		processor.setRegisterFile(register_file);
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
