package processor.pipeline;

import processor.Processor;
import generic.*;
import generic.Instruction.OperationType;
import generic.Simulator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

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

	public static Map<String, Instruction.OperationType> mapping = new HashMap<String, Instruction.OperationType>() {{
		put("00000", Instruction.OperationType.add);//0
		put("00001", Instruction.OperationType.addi);//1
		put("00010", Instruction.OperationType.sub);//2
		put("00011", Instruction.OperationType.subi);//3
		put("00100", Instruction.OperationType.mul);//4
		put("00101", Instruction.OperationType.muli);//5
		put("00110", Instruction.OperationType.div);//6
		put("00111", Instruction.OperationType.divi);//7
		put("01000", Instruction.OperationType.and);//8
		put("01001", Instruction.OperationType.andi);//9
		put("01010", Instruction.OperationType.or);//10
		put("01011", Instruction.OperationType.ori);//11
		put("01100", Instruction.OperationType.xor);//12
		put("01101", Instruction.OperationType.xori);//13
		put("01110", Instruction.OperationType.slt);//14
		put("01111", Instruction.OperationType.slti);//15
		put("10000", Instruction.OperationType.sll);//16
		put("10001", Instruction.OperationType.slli);//17
		put("10010", Instruction.OperationType.srl);//18
		put("10011", Instruction.OperationType.srli);//19
		put("10100", Instruction.OperationType.sra);//20
		put("10101", Instruction.OperationType.srai);//21
		put("10110", Instruction.OperationType.load);//22
		put("10111", Instruction.OperationType.store);//23
		put("11000", Instruction.OperationType.jmp);//24
		put("11001", Instruction.OperationType.beq);//25
		put("11010", Instruction.OperationType.bne);//26
		put("11011", Instruction.OperationType.blt);//27
		put("11100", Instruction.OperationType.bgt);//28
		put("11101", Instruction.OperationType.end);//29
		put("11111", Instruction.OperationType.nop);//31
	}};
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
		List<OperationType> list = Arrays.asList(OperationType.values());
		int inst_code = list.indexOf(opcodeA);

		if(inst_code<22 && inst_code%2==0){
			src1 = rs1A;
			src2 = rs2A;
		}
		else if(inst_code<22){
			src1 = rs1A;
			src2 = immA;
		}
		else if(inst_code==22){
			src1 = rs1A;
			src2 = immA;
		}
		else if(inst_code==23){
			src1 = rs1A;
			src2 = rdA;
		}
		else if(inst_code< 29 && inst_code > 24){
			src1 = rs1A;
			src2 = rdA;
		}
			
		/* checking for conflicts */
				if(inst_code<29 && inst_code!=24)
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
				}
		if(ex_conflict||ma_conflict||rw_conflict)
		{
			does_conflict_exist = true;
			return(does_conflict_exist);
		}
		return false;
	}
	
	/* Method to find 2's complement of a binary string */
	static String twosComplement(String str_s)
	{
		StringBuffer str = new StringBuffer(str_s);
		int n = str.length();
	 
		int i;
		for (i = n-1 ; i >= 0 ; i--)
			if (str.charAt(i) == '1')
				break;
	 
		if (i == -1)
			return "1" + str;
		  
		for (int k = i-1 ; k >= 0; k--)
		{
			if (str.charAt(k) == '1')
				str.replace(k, k+1, "0");
			else
				str.replace(k, k+1, "1");
		}
		
		return str.toString();
	}

    /* Method to check opcode and return optype of an instruction */
	public Instruction.OperationType check_opcode(String binary_instruction)
	{
		String opcode = binary_instruction.substring(0,5);
		Instruction.OperationType optype = mapping.get(opcode);
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
				
				/* any one setting up of opcode in next stage has to be removed (DetectConflict already does this) */
				/* Processing instruction by optype */
				List<OperationType> list = Arrays.asList(OperationType.values());
				int inst_code = list.indexOf(check_opcode(binary_instruction));
				switch(inst_code)
				{
							/* nop Instruction */
							case 31 :
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
							case 0 : 
							case 2 : 
							case 4 : 
							case 6 : 
							case 8 : 
							case 10  : 
							case 12 : 
							case 14 : 
							case 16 : 
							case 18 : 
							case 20 :
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
							case 1 :
							case 3 :
							case 5 :
							case 7 : 
							case 9 : 
							case 11 : 
							case 13 : 
							case 15 : 
							case 17 : 
							case 19 : 
							case 21 :
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
							case 22 :
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
							case 23 :
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
							case 25 : 
							case 26 : 
							case 27 : 
							case 28 : 
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
							case 24 :
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
							case 29 :
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