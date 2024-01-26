package generic;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import processor.Clock;
import processor.Processor;
import generic.Statistics;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		try
		{
			loadProgram(assemblyProgramFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile) throws IOException
	{
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		InputStream input_stream = null;
		try
		{
			input_stream = new FileInputStream(assemblyProgramFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		DataInputStream data_input_stream = new DataInputStream(input_stream);

		int address = -1;
		while(data_input_stream.available() > 0)
		{
			int next = data_input_stream.readInt();
			if(address == -1)
			{
				processor.getRegisterFile().setProgramCounter(next);
			}
			else
			{
				processor.getMainMemory().setWord(address, next);
			}
			address += 1;
		}
        
        processor.getRegisterFile().setValue(0, 0);
        processor.getRegisterFile().setValue(1, 65535);
        processor.getRegisterFile().setValue(2, 65535);
        
        //System.out.println(processor.getRegisterFile().getProgramCounter());
        //String output = processor.getMainMemory().getContentsAsString(0, 15);
        //System.out.println(output);
	}
			
	public static void simulate()
	{
		while(simulationComplete == false)
		{
			processor.getRWUnit().performRW();
			// Clock.incrementClock();
			processor.getMAUnit().performMA();
			// Clock.incrementClock();
			processor.getEXUnit().performEX();
			// Clock.incrementClock();
			processor.getOFUnit().performOF();
			// Clock.incrementClock();
			processor.getIFUnit().performIF();
			Clock.incrementClock();

			// TODO
			// set statistics
			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			Statistics.setNumberOfCycles(Statistics.getNumberOfCycles() + 1);
		}


		//Statistics
		System.out.print("#cycles = "+Statistics.getNumberOfCycles()+"\n");
		System.out.print("#wrong branches = "+Statistics.getNumberOfBranchTaken()+"\n");
		System.out.print("#stops = "+(Statistics.getNumberOfInstructions() - Statistics.getNumberOfRegisterWriteInstructions())+"\n");

	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
}
