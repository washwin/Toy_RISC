package processor.pipeline;

import processor.Processor;
import generic.*;
import generic.Simulator;
import java.math.BigInteger;

public class OperandFetch {
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public OperandFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}

	/* Method to detect conflict (data hazards) between instructions */
	public boolean DetectConflict(Instruction.OperationType opcodeA, int rs1A, int rs2A, int rdA, int immA)
	{
		boolean does_conflict_exist = false;
		/* checking for benign instructions */
		if(opcodeA == Instruction.OperationType.valueOf("nop"))
		{
			/* pass nop to next stage */
			OF_EX_Latch.setoptype(opcodeA);
			does_conflict_exist = false;
			return(does_conflict_exist);
		}
		else if (opcodeA == Instruction.OperationType.valueOf("jmp"))
		{
			/* pass jmp to next stage */
			OF_EX_Latch.setoptype(opcodeA);
			does_conflict_exist = false;
			return(does_conflict_exist);
		}
		else if (opcodeA == Instruction.OperationType.valueOf("end"))
		{
			/* pass end to next stage */
			OF_EX_Latch.setoptype(opcodeA);
			does_conflict_exist = false;
			return(does_conflict_exist);
		}

		/* fetching optype and values of destination registers of instructions in EX, MA and RW stages */
		Instruction.OperationType ex_opcodeB = OF_EX_Latch.getoptype();
		Instruction.OperationType ma_opcodeB = EX_MA_Latch.getoptype();
		Instruction.OperationType rw_opcodeB = MA_RW_Latch.getoptype();
		int ex_dest = OF_EX_Latch.get_ex_destination();
		int ma_dest = EX_MA_Latch.get_ma_destination();
		int rw_dest = MA_RW_Latch.get_rw_destination();
		int ma_dest_x31 = EX_MA_Latch.get_ma_dest_x31();
		int rw_dest_x31 = MA_RW_Latch.get_rw_dest_x31();
		boolean ex_conflict = false; //make false
		boolean ma_conflict = false; //make false
		boolean rw_conflict = false; //make false

		/* checking for benign instructions */
		if(!(ex_opcodeB == Instruction.OperationType.valueOf("nop") || ex_opcodeB == Instruction.OperationType.valueOf("beq") || ex_opcodeB == Instruction.OperationType.valueOf("bne") || ex_opcodeB == Instruction.OperationType.valueOf("blt") || ex_opcodeB == Instruction.OperationType.valueOf("bgt") || ex_opcodeB == Instruction.OperationType.valueOf("store") || ex_opcodeB == Instruction.OperationType.valueOf("jmp")))
		{
			ex_conflict = true;
		}
		if(!(ma_opcodeB == Instruction.OperationType.valueOf("nop") || ma_opcodeB == Instruction.OperationType.valueOf("beq") || ma_opcodeB == Instruction.OperationType.valueOf("bne") || ma_opcodeB == Instruction.OperationType.valueOf("blt") || ma_opcodeB == Instruction.OperationType.valueOf("bgt") || ma_opcodeB == Instruction.OperationType.valueOf("store") || ma_opcodeB == Instruction.OperationType.valueOf("jmp")))
		{
			ma_conflict = true;
		}
		if(!(rw_opcodeB == Instruction.OperationType.valueOf("nop") || rw_opcodeB == Instruction.OperationType.valueOf("beq") || rw_opcodeB == Instruction.OperationType.valueOf("bne") || rw_opcodeB == Instruction.OperationType.valueOf("blt") || rw_opcodeB == Instruction.OperationType.valueOf("bgt") || rw_opcodeB == Instruction.OperationType.valueOf("store") || rw_opcodeB == Instruction.OperationType.valueOf("jmp")))
		{
			rw_conflict = true;
		}
		
		if(!(ex_conflict||ma_conflict||rw_conflict))
		{
			does_conflict_exist = false;
			return(does_conflict_exist); 
		}
		ex_conflict = false; //make false
		ma_conflict = false; //make false
		rw_conflict = false; //make false

		/* variables to store the source operands of the instruction in OF stage */
		int src1;
		int src2;
		src1 = rs1A;
		src2 = rs2A;
		
		OF_EX_Latch.setoptype(opcodeA); /* passing optype to next stage */

		/* setting the source operands to appropriate values according to the optype */
		switch(opcodeA)
			{
				case add : 
				case sub : 
				case mul : 
				case div : 
				case and : 
				case or  : 
				case xor : 
				case slt : 
				case sll : 
				case srl : 
				case sra :
				{
					src1 = rs1A;
					src2 = rs2A;
					break;
				}
				case addi : 
				case subi : 
				case muli : 
				case divi : 
				case andi : 
				case ori  : 
				case xori : 
				case slti : 
				case slli : 
				case srli : 
				case srai :
				{
					src1 = rs1A;
					src2 = immA;
					break;
				}
				case beq : 
				case bne : 
				case blt : 
				case bgt :
				{
					src1 = rs1A;
					src2 = rdA;
					break;
				}
				case load :
				{
					src1 = rs1A;
					src2 = immA;
					break;
				}
				case store :
				{
					
					src1 = rs1A;
					src2 = rdA;
					break;
				}
			}
			
		/* checking for conflicts */
		switch(opcodeA)
			{
				case add : 
				case sub : 
				case mul : 
				case div : 
				case and : 
				case or  : 
				case xor : 
				case slt : 
				case sll : 
				case srl : 
				case sra :
				case addi : 
				case subi : 
				case muli : 
				case divi : 
				case andi : 
				case ori  : 
				case xori : 
				case slti : 
				case slli : 
				case srli : 
				case srai :
				case beq : 
				case bne : 
				case blt : 
				case bgt :
				case load :
				case store :
				{
					/* checking if the source operand registers of the instruction in OF stage are equal to the destination operand register of the instruction in EX, MA or RW stages */
					if(src1 == ex_dest || src2 == ex_dest)
					{
						ex_conflict = true;
						
					}
					else if(src1 == ma_dest || src2 == ma_dest)
					{
						ma_conflict = true;
						
					}
					else if(src1 == rw_dest || src2 == rw_dest)
					{
						rw_conflict = true;
						
					}
					/* checking if the source operand registers of the instruction in OF stage are equal to the %x31 register */
					if(src1 == 31 || src2 == 31)
					{
						if(ma_dest_x31 == 31)
						{
							ma_conflict = true;
						}
						else if(rw_dest_x31 == 31)
						{
							rw_conflict = true;
						}
					}
					break;
				}
			}
		if(ex_conflict||ma_conflict||rw_conflict)
		{
			does_conflict_exist = true;
			return(does_conflict_exist);
		}
		return false;
	}
	/* Method to find 2's complement of a binary string */
	public String twosComplement(String bin) 
	{
        String twos = "", ones = "";

        for (int i = 0; i < bin.length(); i++) 
        {
            ones += flip(bin.charAt(i));
        }
        int number0 = new BigInteger(ones,2).intValue();
        StringBuilder builder = new StringBuilder(ones);
        boolean b = false;
        for (int i = ones.length()-1; i>0; i--) 
        {
            if (ones.charAt(i) == '1') 
            {
                builder.setCharAt(i, '0');
            } 
            else 
            {
                builder.setCharAt(i, '1');
                b = true;
                break;
            }
        }
        if (!b)
        {
            builder.append("1", 0, 7);
        }
        twos = builder.toString();
        return twos;
    }
    /* Method to flip 0s and 1s in a string */
    public char flip(char c) 
    {
        return (c == '0') ? '1' : '0';
    }

    /* Method to check opcode and return optype of an instruction */
	public Instruction.OperationType check_opcode(String binary_instruction)
	{
		String opcode = binary_instruction.substring(0,5);
		Instruction.OperationType optype = null;
		switch (opcode) 
		{
			case "11111": /* considering a virtual nop instruction */
			{
				optype = Instruction.OperationType.valueOf("nop");
				break;
			}
			case "00000":
			{
				optype = Instruction.OperationType.valueOf("add");
				break;
			}
			case "00010":
			{
				optype = Instruction.OperationType.valueOf("sub");
				break;
			}
			case "00100":
			{
				optype = Instruction.OperationType.valueOf("mul");
				break;
			}
			case "00110":
			{
				optype = Instruction.OperationType.valueOf("div");
				break;
			}
			case "01000":
			{
				optype = Instruction.OperationType.valueOf("and");
				break;
			}
			case "01010":
			{
				optype = Instruction.OperationType.valueOf("or");
				break;
			}
			case "01100":
			{
				optype = Instruction.OperationType.valueOf("xor");
				break;
			}
			case "01110":
			{
				optype = Instruction.OperationType.valueOf("slt");
				break;
			}
			case "10000":
			{
				optype = Instruction.OperationType.valueOf("sll");
				break;
			}
			case "10010":
			{
				optype = Instruction.OperationType.valueOf("srl");
				break;
			}
			case "10100":
			{
				optype = Instruction.OperationType.valueOf("sra");
				break;
			}
			case "00001":
			{
				optype = Instruction.OperationType.valueOf("addi");
				break;
			}
			case "00011":
			{
				optype = Instruction.OperationType.valueOf("subi");
				break;
			}
			case "00101":
			{
				optype = Instruction.OperationType.valueOf("muli");
				break;
			}
			case "00111":
			{
				optype = Instruction.OperationType.valueOf("divi");
				break;
			}
			case "01001":
			{
				optype = Instruction.OperationType.valueOf("andi");
				break;
			}
			case "01011":
			{
				optype = Instruction.OperationType.valueOf("ori");
				break;
			}
			case "01101":
			{
				optype = Instruction.OperationType.valueOf("xori");
				break;
			}
			case "01111":
			{
				optype = Instruction.OperationType.valueOf("slti");
				break;
			}
			case "10001":
			{
				optype = Instruction.OperationType.valueOf("slli");
				break;
			}
			case "10011":
			{
				optype = Instruction.OperationType.valueOf("srli");
				break;
			}
			case "10101":
			{
				optype = Instruction.OperationType.valueOf("srai");
				break;
			}
			case "10110":
			{
				optype = Instruction.OperationType.valueOf("load");
				break;
			}
			case "10111":
			{
				optype = Instruction.OperationType.valueOf("store");
				break;
			}
			case "11001":
			{
				optype = Instruction.OperationType.valueOf("beq");
				break;
			}
			case "11010":
			{
				optype = Instruction.OperationType.valueOf("bne");
				break;
			}
			case "11011":
			{
				optype = Instruction.OperationType.valueOf("blt");
				break;
			}
			case "11100":
			{
				optype = Instruction.OperationType.valueOf("bgt");
				break;
			}
			case "11000":
			{
				optype = Instruction.OperationType.valueOf("jmp");
				break;
			}
			case "11101":
			{
				optype = Instruction.OperationType.valueOf("end");
				break;
			}
		}
		return optype;
	}

	/* Method to get register number */
	public int get_register_value(String binary_instruction, int start_index, int end_index)
	{
		String register = binary_instruction.substring(start_index,end_index+1);
		int register_value = new BigInteger(register,2).intValue();
		return register_value;	
	}  

	/* Method to get value of immediate */
	public int get_immediate_value(String binary_instruction, int start_index, int end_index)
	{
		String register = binary_instruction.substring(start_index,end_index+1);
		if(register.charAt(0)=='1')
		{
			int register_value = 0 - new BigInteger(twosComplement(register),2).intValue();
			return register_value;
		}
		int register_value = new BigInteger(register,2).intValue();
		return register_value;
		
	}  
	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			int pc = containingProcessor.getRegisterFile().getProgramCounter(); /* fetching current PC */
			
			/* ignoring nop instruction and processing otherwise */
			if(IF_OF_Latch.is_nop)
			{
				int rs1 = 0;
				int rs2 = 0;
				int rd = 0;
				int imm = 0;
				/* checking for conflicts */
				DetectConflict(Instruction.OperationType.valueOf("nop"), rs1, rs2, rd, imm); 
				/* for verification and visualization purpose */
				
				//System.out.println("OF -> "+Instruction.OperationType.valueOf("nop"));
				
				/* setting nop indicator flag to false in IF-OF latch */
				IF_OF_Latch.set_is_nop(false);
			}

			else
			{
			
				Instruction newInstruction = new Instruction();
				int instruction = IF_OF_Latch.getInstruction(); /* Fetching instruction from IF-OF Latch */
				String binary_instruction = null;

				/* Converting instruction into binary */
				if(instruction>=0)
				{
					binary_instruction = String.format("%32s",Integer.toBinaryString(instruction)).replace(" ","0");
				}
				else
				{
					binary_instruction = twosComplement(String.format("%32s",Integer.toBinaryString(-instruction)).replace(" ","0"));
				}
				int rs1 = 0;
				int rs2 = 0;
				int rd = 0;
				int imm = 0;

				/* for verification and visualization purpose */
				
				//System.out.println("OF -> "+check_opcode(binary_instruction));
				
				/* any one setting up of opcode in next stage has to be removed (DetectConflict already does this) */
				/* Processing instruction by optype */
				switch(check_opcode(binary_instruction))
				{
							/* nop Instruction */
							case nop :
							{
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), rs1, rs2, rd, imm))
								{
									IF_EnableLatch.set_stall_switch(false);
									OF_EX_Latch.force_nop();
									Simulator.increment_OF_stalls();
								}
								break;
							}
							/* Instructions of R3 type */
							case add : 
							case sub : 
							case mul : 
							case div : 
							case and : 
							case or  : 
							case xor : 
							case slt : 
							case sll : 
							case srl : 
							case sra :
							{
								/* Fetching values stored in registers and immediate values */
								rs1 =containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 5, 9));
								rs2 = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 10, 14));
								rd = get_register_value(binary_instruction, 15, 19);
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), get_register_value(binary_instruction, 5, 9), get_register_value(binary_instruction, 10, 14), rd, imm))
								{
									IF_EnableLatch.set_stall_switch(false); /* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop(); /* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									/* Setting r3 type to true in OF_EX latch */
									OF_EX_Latch.set_is_r3();
									IF_EnableLatch.set_stall_switch(true);
									/* storing required values in OF_EX latch */
									OF_EX_Latch.setop1(rs1);
									OF_EX_Latch.setop2(rs2);
									OF_EX_Latch.setrd(rd);
									OF_EX_Latch.set_ex_destination(rd);
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
								}
								break;
							} 
							
							/* Non-conditional instructions of R2I type */
							case addi :
							case subi :
							case muli :
							case divi : 
							case andi : 
							case ori : 
							case xori : 
							case slti : 
							case slli : 
							case srli : 
							case srai :
							{
								/* Fetching values stored in registers and immediate values */
								rs1 = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 5, 9));
								rd = get_register_value(binary_instruction, 10, 14);
								imm = get_immediate_value(binary_instruction, 15, 31);
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), get_register_value(binary_instruction, 5, 9), rs2, rd, imm))
								{
									IF_EnableLatch.set_stall_switch(false);/* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop();/* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									/* setting non-conditional r2i type to true in OF_EX latch */
									OF_EX_Latch.set_is_r2i_non_conditional();
									IF_EnableLatch.set_stall_switch(true);
									/* storing required values in OF_EX latch */
									OF_EX_Latch.setop1(rs1);
									OF_EX_Latch.setrd(rd);
									OF_EX_Latch.set_ex_destination(rd);
									OF_EX_Latch.setimm(imm);
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
								}
								break;
							}

							/* Load / Store instructions */
							case load :
							{
								/* Fetching values stored in registers and immediate values */
								rs1 = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 5, 9));
								rd = get_register_value(binary_instruction, 10, 14);
								imm = get_immediate_value(binary_instruction, 15, 31);
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), get_register_value(binary_instruction, 5, 9), rs2, rd, imm))
								{
									IF_EnableLatch.set_stall_switch(false);/* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop();/* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									/* Setting load / store to true in OF_EX latch */
									OF_EX_Latch.set_is_load_store();
									IF_EnableLatch.set_stall_switch(true);
									/* Storing required values in OF_EX latch */
									OF_EX_Latch.setop1(rs1);
									OF_EX_Latch.setrd(rd);
									OF_EX_Latch.set_ex_destination(rd);
									OF_EX_Latch.setimm(imm);
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
								}
								break;
							}
							case store :
							{
								/* Fetching values stored in registers and immediate values */
								rs1 = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 5, 9));
								rd = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 10, 14));
								imm = get_immediate_value(binary_instruction, 15, 31);
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), get_register_value(binary_instruction, 5, 9), rs2, get_register_value(binary_instruction, 10, 14), imm))
								{
									IF_EnableLatch.set_stall_switch(false);/* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop();/* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									/* Setting load / store to true in OF_EX latch */
									OF_EX_Latch.set_is_load_store();
									IF_EnableLatch.set_stall_switch(true);
									/* Storing required values in OF_EX latch */
									OF_EX_Latch.setop1(rs1);
									OF_EX_Latch.setrd(rd);
									OF_EX_Latch.set_ex_destination(rs1);
									OF_EX_Latch.setimm(imm);
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
								}
								break;
							}

							/* Conditional instructions of R2I type */
							case beq : 
							case bne : 
							case blt : 
							case bgt : 
							{
								/* Fetching values stored in registers and immediate values */
								rs1 = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 5, 9));
								rd = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 10, 14));
								imm = get_immediate_value(binary_instruction, 15, 31);
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), get_register_value(binary_instruction, 5, 9), rs2, get_register_value(binary_instruction, 10, 14), imm))
								{
									IF_EnableLatch.set_stall_switch(false);/* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop();/* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									/* Setting r2i conditional type to true in OF_EX latch */
									OF_EX_Latch.set_is_r2i_conditional();
									
									IF_EnableLatch.set_stall_switch(true);
									/* Storing required values in OF_EX latch */
									OF_EX_Latch.setop1(rs1);
									OF_EX_Latch.setop2(rd);
									OF_EX_Latch.setimm(imm);
									OF_EX_Latch.setpc(pc);
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
									OF_EX_Latch.setbranchtarget_conditional(pc -1 + imm);/* as PC is already incremented in the IF stage, to set the offset for branch target, we subtract 1 from current PC to get the correct value */
								}
								break;
							}

							/* Jmp instruction */
							case jmp :
							{
								/* Fetching values stored in registers and immediate values */
								rd = containingProcessor.getRegisterFile().getValue(get_register_value(binary_instruction, 5, 9));
								imm = get_immediate_value(binary_instruction, 10, 31);
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), rs1, rs2, get_register_value(binary_instruction, 5, 9), imm))
								{
									IF_EnableLatch.set_stall_switch(false);/* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop();/* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									/* Setting jmp to true in OF_EX latch */
									OF_EX_Latch.set_is_jmp();
									IF_EnableLatch.set_stall_switch(true);
									/* Storing required values in OF_EX latch */
									OF_EX_Latch.setop2(rd);
									OF_EX_Latch.setimm(imm);
									OF_EX_Latch.setpc(pc);
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
									OF_EX_Latch.setbranchtarget_jmp(rd + pc -1 + imm);/* as PC is already incremented in the IF stage, to set the offset for branch target, we subtract 1 from current PC to get the correct value */
								}
								break;
							}

							/* End instruction */
							case end :
							{
								/* Checking for conflicts */
								if(DetectConflict(check_opcode(binary_instruction), rs1, rs2, rd, imm))
								{
									IF_EnableLatch.set_stall_switch(false);/* enable stall of PC and OF stage */
									OF_EX_Latch.force_nop();/* forcing nop into EX and subsequent stages */
									Simulator.increment_OF_stalls();
								}
								else
								{
									IF_EnableLatch.set_stall_switch(true);
									OF_EX_Latch.set_is_end();
									OF_EX_Latch.setoptype(check_opcode(binary_instruction));
								}
								break;
							}

				}
			}
			/* Enabling EX stage */
			OF_EX_Latch.setEX_enable(true);
		}
		/* Enabling EX stage */
		//OF_EX_Latch.setEX_enable(true);
	}

}
