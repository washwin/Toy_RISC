package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;

import java.util.*;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
		
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType IF_OF_Latch, OF_EX_LatchType OF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = IF_OF_Latch;
		this.OF_EX_Latch = OF_EX_Latch;
	}

	
	public static int binaryStringToInt(String num) {             // Converting binary string to integer
		int integer = Integer.parseInt(num, 2);
		return integer;
	}
	

    static String twosComplement(String binaryString) {                // Implementation of this function is done using sources over internet
        // Step 1: Reverse the binary string
        StringBuilder reversedString = new StringBuilder();
        for (char bit : binaryString.toCharArray()) {
            if (bit == '0') {
                reversedString.append('1');
            } else if (bit == '1') {
                reversedString.append('0');
            } else {
                throw new IllegalArgumentException("Input should be a binary string (containing only 0s and 1s).");
            }
        }

        // Step 2: Add 1 to the reversed string
        StringBuilder twosComplement = new StringBuilder();
        int carry = 1;
        for (int i = reversedString.length() - 1; i >= 0; i--) {
            char bit = reversedString.charAt(i);
            if (bit == '0' && carry == 1) {
                twosComplement.insert(0, '1');
                carry = 0;
            } else if (bit == '1' && carry == 1) {
                twosComplement.insert(0, '0');
            } else {
                twosComplement.insert(0, bit);
            }
        }

        // Pad with leading zeros if necessary
        while (twosComplement.length() < binaryString.length()) {
            twosComplement.insert(0, '0');
        }

        return twosComplement.toString();
    }

	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			OperationType[] opType = OperationType.values();  			// Compute 32-bit binary instruction
			String inst_type = Integer.toBinaryString(IF_OF_Latch.getInstruction());
			while(inst_type.length()!=32)
			{
				inst_type = "0" + inst_type;
			}
			
			String opcode = inst_type.substring(0, 5);           //geting opcode of instruction ie add/sub/store/etc
			int int_op = binaryStringToInt(opcode);
			List<OperationType> list = Arrays.asList(OperationType.values());
			int inst_code = list.indexOf(opType[int_op]);

			int regNo;
			Instruction inst = new Instruction();               //creating objects
			Operand rd = new Operand();
			Operand rs2 = new Operand();
			Operand rs1 = new Operand();
			String imm;
			int imm_val;

			// Perform the operation according to instruction
			switch(inst_code)
			{	
				// operations without immediate
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
				case 20:
					// calculate and set source register 1
					rs1.setOperandType(OperandType.Register);
					regNo = binaryStringToInt(inst_type.substring(5, 10));
					rs1.setValue(regNo);

					// calculate and set source register 2
					rs2.setOperandType(OperandType.Register);
					regNo = binaryStringToInt(inst_type.substring(10, 15));
					rs2.setValue(regNo);

					// calculate and set destination register
					rd.setOperandType(OperandType.Register);
					regNo = binaryStringToInt(inst_type.substring(15, 20));
					rd.setValue(regNo);

					inst.setOperationType(opType[int_op]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;
				// conditional branch operations
				case 25:
				case 26:
				case 27:
				case 28:
					// calculate and set source register
					rs1.setOperandType(OperandType.Register);
					regNo = binaryStringToInt(inst_type.substring(5, 10));
					rs1.setValue(regNo);

					// calculate and set destination register
					rs2.setOperandType(OperandType.Register);
					regNo = binaryStringToInt(inst_type.substring(10, 15));
					rs2.setValue(regNo);

					// calculate and set immediate value
					rd.setOperandType(OperandType.Immediate);
					imm = inst_type.substring(15, 32);
					imm_val = binaryStringToInt(imm);
					if (imm.charAt(0) == '1')
					{
						imm = twosComplement(imm);
						imm_val = binaryStringToInt(imm) * -1;
					}
					rd.setValue(imm_val);

					inst.setOperationType(opType[int_op]);
					inst.setDestinationOperand(rd);
					inst.setSourceOperand2(rs2);
					inst.setSourceOperand1(rs1);
					break;
					
				// jump operation
				case 24:
					Operand op = new Operand();
					imm = inst_type.substring(10, 32);
					imm_val = binaryStringToInt(imm);
					// check if immediate value is -ve
					if (imm.charAt(0) == '1')
					{
						imm = twosComplement(imm);
						imm_val = binaryStringToInt(imm) * -1;
					}
					// calculate and set if immdeiate else register
					if (imm_val != 0)
					{
						op.setOperandType(OperandType.Immediate);
						op.setValue(imm_val);
					}
					else
					{
						regNo = binaryStringToInt(inst_type.substring(5, 10));
						op.setOperandType(OperandType.Register);
						op.setValue(regNo);
					}
					inst.setOperationType(opType[int_op]);
					inst.setDestinationOperand(op);
					break;

				// operations with immediate
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
				case 21:
				case 22:
				case 23:
					// calculate and set source register
					regNo = binaryStringToInt(inst_type.substring(5, 10));
					rs1.setValue(regNo);
					rs1.setOperandType(OperandType.Register);

					// calculate and set destination register
					regNo = binaryStringToInt(inst_type.substring(10, 15));
					rd.setValue(regNo);
					rd.setOperandType(OperandType.Register);

					// calculate and set immediate
					rs2.setOperandType(OperandType.Immediate);
					imm = inst_type.substring(15, 32);
					imm_val = binaryStringToInt(imm);
					if (imm.charAt(0) == '1')
					{
						imm = twosComplement(imm);
						imm_val = binaryStringToInt(imm) * -1;
					}
					rs2.setValue(imm_val);

					inst.setOperationType(opType[int_op]);
					inst.setDestinationOperand(rd);
					inst.setSourceOperand2(rs2);
					inst.setSourceOperand1(rs1);
					break;

				// end operation
				case 29:
					inst.setOperationType(opType[int_op]);
					break;
				
			}

			OF_EX_Latch.setInstruction(inst);
			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		}
	}

}
