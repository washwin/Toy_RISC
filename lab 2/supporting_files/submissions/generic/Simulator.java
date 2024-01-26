package generic;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import generic.Operand.OperandType;


public class Simulator {
		
	static FileInputStream inputcodeStream = null;

	public static String tobinary(int val, int size)
	{
		String binaryVal = Long.toBinaryString( Integer.toUnsignedLong(val) | 0x100000000L ).substring(1);
        return binaryVal.substring(32-size);
	}
	
	public static String getlabel(Operand obj, int size)
	{
		if(obj.getOperandType() == OperandType.Label)
		{
			return tobinary(ParsedProgram.symtab.get(obj.getLabelValue()), size);
		}

		if(obj == null)
		{
		 return tobinary(0, size);
		}

		return tobinary(obj.value, size);
	}

	public static void setupSimulation(String assemblyProgramFile)
	{	
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	
	public static void assemble(String objectProgramFile)
	{
		//TODO your assembler code
		
		try {
			FileOutputStream file = new FileOutputStream(objectProgramFile);
			
			//1. open the objectProgramFile in binary mode
			byte [] codeAddress = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();

				//2. write the firstCodeAddress to the file
				file.write(codeAddress);
			
			//3. write the data to the file
			for(int i = 0; i < ParsedProgram.data.size(); i++)
			{
				byte [] data = ByteBuffer.allocate(4).putInt(ParsedProgram.data.get(i)).array();
			
					file.write(data);
			}
			
			//4. assemble one instruction at a time, and write to the file
			for (int i = 0; i < ParsedProgram.code.size(); i++)
			{
				
				Instruction ins = ParsedProgram.code.get(i);
				int opcode = Arrays.asList(ins.operationType.values()).indexOf(ins.getOperationType());
				String encoded = tobinary(opcode, 5);
				int type = 22;			//dummy value 

				switch(encoded)
				{
					case "00000":
					case "00010":
					case "00100":
					case "00110":
					case "01000":
					case "01010":
					case "01100":
					case "01110":
					case "10000":
					case "10010":
					case "10100": type = 3;
					break;
					case "00001":
					case "00011":
					case "00101":
					case "00111":
					case "01001":
					case "01011":
					case "01101":
					case "01111":
					case "10001":
					case "10011":
					case "10101":
					case "10110":
					case "10111": type=2;
					break;
					case "11000": type=1;
					break;
					case "11001":
					case "11010":
					case "11011":
					case "11100":type=21;
					break;
					case "11101":type=0;
				}
								
				if(type == 3)						//R3 Type
				{
					encoded += getlabel(ins.sourceOperand1, 5);
					encoded += getlabel(ins.sourceOperand2, 5);
					encoded += getlabel(ins.destinationOperand, 5);
					encoded += tobinary(0, 12);		
				}

				else if(type == 2)					//R2I Type
				{       
					encoded += getlabel(ins.sourceOperand1, 5);
					encoded += getlabel(ins.destinationOperand, 5);
					encoded += getlabel(ins.sourceOperand2, 17);			
				}
				
				else if(type == 21)					//R2I - branching type
				{				
					encoded += getlabel(ins.sourceOperand1, 5);
					encoded += getlabel(ins.sourceOperand2, 5);
					Integer immediate = Integer.parseInt(getlabel(ins.destinationOperand,17),2) - ins.programCounter;
					encoded += tobinary(immediate, 17);	
				}

				else if(type == 1)					//R1 Type
				{		
					if(ins.destinationOperand.getOperandType() == Operand.OperandType.Register)
					{
						encoded += getlabel(ins.destinationOperand, 5);
						encoded += tobinary(0, 22);
					}
					else
					{
						encoded += tobinary(0, 5);
						Integer immediate = Integer.parseInt(getlabel(ins.destinationOperand,22),2) - ins.programCounter;
						encoded += tobinary(immediate, 22);
					}  		
				}

				else							//Only opcode
				{
					encoded += tobinary(0,27);      
				}
				
				byte [] data = ByteBuffer.allocate(4).putInt((int)Long.parseLong(encoded,2)).array();
				
			
					file.write(data);	
			}
			
				//5. close the file
				file.close();

		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();

		}catch(IOException e){

			e.printStackTrace();

		}
	}
	
}
