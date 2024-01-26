package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Statistics;

import java.util.*;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	// Functions to perform arithmetic operations
	public static int addition(int a, int b) {
		return a+b;
	}

	public static int multiply(int a, int b) {
		return a*b;
	}

	public static int getQuotient(int a, int b) {
		return a/b;
	}

	public static int getRemainder(int a, int b) {
		return a%b;
	}

	// other functions
	public static int slt_check(int a, int b) {
		if(a<b)
			return 1;
		else
			return 0;
	}
	
	public int compute_operations(int operator, int a, int b) {
		int temp;
		int Res = 0;

		switch(operator) {
			// arithmetic operations
			case 0:
			case 1:
				Res = addition(a, b);
				break;
			case 2:
			case 3:
				Res = addition(a, -1 * b);
				break;
			case 4:
			case 5:
				Res = multiply(a, b);
				break;
			case 6:
			case 7:
				Res = getQuotient(a, b);
				temp = getRemainder(a, b);
				containingProcessor.getRegisterFile().setValue(31, temp);
				break;
			// logical operations
			case 8:
			case 9:
				Res = a & b;
				break;
			case 10:
			case 11:
				Res = a | b;
				break;
			case 12:
			case 13:
				Res = a ^ b;
				break;
			// shift operations
			case 14:
			case 15:
				Res = slt_check(a, b);
				break;
			case 16:
			case 17:
				Res = a << b;
				break;
			case 18:
			case 19:
				Res = a >>> b;
				break;
			case 20:
			case 21:
				Res = a >> b;
				break;
			case 22:
				Res = addition(a, b);
			default:
				break;
		}
		return Res;
	}

	public void performEX()
	{
		//TODO

		if (OF_EX_Latch.getIsNOP()) {
			EX_MA_Latch.setIsNOP(true);
			OF_EX_Latch.setIsNOP(false);
			EX_MA_Latch.setInstruction(null);
		}
		else {
			if(OF_EX_Latch.isEX_enable())
			{
				Instruction inst = OF_EX_Latch.getInstruction();
				// System.out.println(inst);
				EX_MA_Latch.setInstruction(inst);
				OperationType typeOperation = inst.getOperationType();
				List<OperationType> list = Arrays.asList(OperationType.values());
				int inst_code = list.indexOf(typeOperation);
				int PC_cur = containingProcessor.getRegisterFile().getProgramCounter();
				PC_cur--;

				if(inst_code > 23 && inst_code < 30)
				{
					// update no. of branches taken instructions
					int b_taken = Statistics.getNumberOfBranchTaken();
					b_taken += 2;
					Statistics.setNumberOfBranchTaken(b_taken);

					// update latch types
					IF_EnableLatch.setIF_enable(false);
					IF_OF_Latch.setOF_enable(false);
					OF_EX_Latch.setEX_enable(false);
				}

				// Initializations
				int Res = 0;
				int a_operand, b_operand, temp;
				
				// fetch 2 source register values and calculate ALU result
					if(inst_code % 2 == 0 && inst_code < 21)
					{
						temp = inst.getSourceOperand1().getValue();
						a_operand = containingProcessor.getRegisterFile().getValue(temp);

						temp = inst.getSourceOperand2().getValue();
						b_operand = containingProcessor.getRegisterFile().getValue(temp);

						Res = compute_operations(inst_code, a_operand, b_operand);
					}
					// fetch immediate and source register value and calculate ALU result
					else if(inst_code < 23)
					{
						temp = inst.getSourceOperand1().getValue();
						a_operand = containingProcessor.getRegisterFile().getValue(temp);
						b_operand = inst.getSourceOperand2().getValue();
						Res = compute_operations(inst_code, a_operand, b_operand);
					}
				// load, store operation
				else if(inst_code == 23)
				{
					temp = inst.getDestinationOperand().getValue();
					a_operand = containingProcessor.getRegisterFile().getValue(temp);
					b_operand = inst.getSourceOperand2().getValue();
					Res = addition(a_operand, b_operand);
				}
				
				// jump operation
				else if(inst_code == 24)
				{
					OperandType optype = inst.getDestinationOperand().getOperandType();
					int imm = 0;
					if (optype == OperandType.Register)
					{
						temp = inst.getDestinationOperand().getValue();
						imm = containingProcessor.getRegisterFile().getValue(temp);
					}
					else
					{
						imm = inst.getDestinationOperand().getValue();
					}
					Res = imm + PC_cur;
					EX_IF_Latch.setIS_enable(true, Res);
				}
				// conditional branch operations
				else if(inst_code < 29)
				{
					temp = inst.getSourceOperand1().getValue();
					a_operand = containingProcessor.getRegisterFile().getValue(temp);

					temp = inst.getSourceOperand2().getValue();
					b_operand = containingProcessor.getRegisterFile().getValue(temp);

					int imm = inst.getDestinationOperand().getValue();
					switch(inst_code)
					{
						case 25:
							if(a_operand == b_operand)
							{
								Res = imm + PC_cur;
								EX_IF_Latch.setIS_enable(true, Res);
							}
							break;
						case 26:
							if(a_operand != b_operand)
							{
								Res = imm + PC_cur;
								EX_IF_Latch.setIS_enable(true, Res);
							}

							break;
						case 27:
							if(a_operand < b_operand)
							{
								Res = imm + PC_cur;
								EX_IF_Latch.setIS_enable(true, Res);
							}
							break;
						case 28:
							if(a_operand > b_operand)
							{
								Res = imm + PC_cur;
								EX_IF_Latch.setIS_enable(true, Res);
							}
							break;
						default:
							break;
					}
				}		
				EX_MA_Latch.setALU_result(Res);
				EX_MA_Latch.setMA_enable(true);
			}
		}
	}

}
