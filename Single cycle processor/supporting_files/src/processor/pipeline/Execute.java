package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;

import java.util.*;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
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
		if(!OF_EX_Latch.isEX_enable())
			return;
		
		Instruction inst = OF_EX_Latch.getInstruction();
		// System.out.println(inst);
		EX_MA_Latch.setInstruction(inst);
		OperationType typeOperation = inst.getOperationType();
		List<OperationType> list = Arrays.asList(OperationType.values());
		int inst_code = list.indexOf(typeOperation);
		int PC_cur = containingProcessor.getRegisterFile().getProgramCounter();
		PC_cur--;

		// Initializations
		int Res = 0;
		int a_operand, b_operand, temp, imm;		
			
		switch(inst_code)
		{
			case 0:
			case 2:
			case 4:
			case 6:
			case 8:
			case 10:
			case 12:
			case 14:
			case 16:
			case 18:
			case 20:// fetch 2 source register values and calculate ALU result
				temp = inst.getSourceOperand2().getValue();
				b_operand = containingProcessor.getRegisterFile().getValue(temp);
				temp = inst.getSourceOperand1().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				Res = compute_operations(inst_code, a_operand, b_operand);
				break;
			case 1:
			case 3:
			case 5:
			case 7:
			case 9:
			case 11:
			case 13:
			case 15:
			case 17:
			case 19:
			case 21:// fetch immediate and source register value and calculate ALU result
				b_operand = inst.getSourceOperand2().getValue();
				temp = inst.getSourceOperand1().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				Res = compute_operations(inst_code, a_operand, b_operand);
				break;
			case 22:
			case 23:// load, store operation
				b_operand = inst.getSourceOperand2().getValue();			
				temp = inst.getDestinationOperand().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				Res = addition(a_operand, b_operand);
				break;
			case 24:// jump operation
				OperandType optype = inst.getDestinationOperand().getOperandType();
				imm = 0;
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
				break;
			case 25://beq
				imm = inst.getDestinationOperand().getValue();
				temp = inst.getSourceOperand1().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				temp = inst.getSourceOperand2().getValue();
				b_operand = containingProcessor.getRegisterFile().getValue(temp);
				if(a_operand == b_operand)
				{
					Res = imm + PC_cur;
					EX_IF_Latch.setIS_enable(true, Res);
				}
				break;
			case 26://bne
				imm = inst.getDestinationOperand().getValue();
				temp = inst.getSourceOperand1().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				temp = inst.getSourceOperand2().getValue();
				b_operand = containingProcessor.getRegisterFile().getValue(temp);
				if(a_operand != b_operand)
				{
					Res = imm + PC_cur;
					EX_IF_Latch.setIS_enable(true, Res);
				}
				break;
			case 27://blt
				temp = inst.getSourceOperand1().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				temp = inst.getSourceOperand2().getValue();
				b_operand = containingProcessor.getRegisterFile().getValue(temp);
				imm = inst.getDestinationOperand().getValue();
				if(a_operand < b_operand)
				{
					Res = imm + PC_cur;
					EX_IF_Latch.setIS_enable(true, Res);
				}
				break;
			case 28://bgt
				temp = inst.getSourceOperand1().getValue();
				a_operand = containingProcessor.getRegisterFile().getValue(temp);
				temp = inst.getSourceOperand2().getValue();
				b_operand = containingProcessor.getRegisterFile().getValue(temp);
				imm = inst.getDestinationOperand().getValue();
				if(a_operand > b_operand)
				{
					Res = imm + PC_cur;
					EX_IF_Latch.setIS_enable(true, Res);
				}
				break;
			default:
				break;
		}		
		EX_MA_Latch.setALU_result(Res);		
		EX_MA_Latch.setMA_enable(true);
		OF_EX_Latch.setEX_enable(false);
	}

}