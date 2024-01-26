package processor.pipeline;

import processor.Processor;
import generic.*;
import java.math.BigInteger;
public class Execute 
{
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public Execute(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}

	public void performEX()
	{
		//System.out.println("is ex enabled" + OF_EX_Latch.isEX_enable());
		if(OF_EX_Latch.isEX_enable())
		{
			/*
			if(OF_EX_Latch.getEX_enable_all())
			{
				IF_OF_Latch.setOF_enable(true);
				MA_RW_Latch.setRW_enable(true); 
				EX_MA_Latch.setMA_enable(true); 
				IF_EnableLatch.setIF_enable(true);
				OF_EX_Latch.setEX_enable_all(false);
				System.out.println("heyyy");
			}
			*/
			/* Fetching values from OF-EX Latch */
			Instruction.OperationType optype = OF_EX_Latch.getoptype();
			int op1 = OF_EX_Latch.getop1();
			int op2 = OF_EX_Latch.getop2();
			int imm = OF_EX_Latch.getimm();
			int rd = OF_EX_Latch.getrd();
			int branch_target_conditional = OF_EX_Latch.getbranchtarget_conditional();
			int branch_target_jmp = OF_EX_Latch.getbranchtarget_jmp();
			int alu_result = 0;

			/* Processing instruction by format and optype */
			/* ALU unit */
			
			EX_MA_Latch.setoptype(optype);/* passing optype to next stage */
			/* for verification and visualization purpose */
			
			//System.out.println("EX -> "+optype);
			
			if(OF_EX_Latch.is_r3 == true)
			{
				switch(optype)
				{
					case nop :
					{
						break;
					}
					case add : 
					{
						alu_result = op1 + op2;
						break;
					}
					case sub : 
					{
						alu_result = op1 - op2;
						break;
					}
					case mul : 
					{
						alu_result = op1 * op2;
						break;
					}
					case div : 
					{
						alu_result = op1 / op2;
						/* Storing remainder in %x31 */
						EX_MA_Latch.setx31_result(op1%op2);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case and : 
					{
						alu_result = op1 & op2;
						break;
					}
					case or  : 
					{
						alu_result = op1 | op2;
						break;
					}
					case xor : 
					{
						alu_result = op1 ^ op2;
						break;
					}
					case slt : 
					{
						if(op1<op2)
						{
							alu_result = 1;
						}
						else 
							alu_result = 0;
						break;
					}
					case sll : 
					{
						alu_result = op1<<op2;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(0,op2);
						int shifted_out_bits = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(shifted_out_bits);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case srl : 
					{
						alu_result = op1>>>op2;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(32-op2,32);
						int shifted_out_bits = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(shifted_out_bits);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case sra :
					{
						alu_result = op1>>op2;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(32-op2,32);
						int shifted_out_bits = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(shifted_out_bits);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
				}
				EX_MA_Latch.setis_alu();
				EX_MA_Latch.setalu_result(alu_result);
				EX_MA_Latch.setdestination_register(rd);
				EX_MA_Latch.set_ma_destination(rd);
				//EX_MA_Latch.setMA_enable(true);
				OF_EX_Latch.is_r3 =false;
			}
			else if(OF_EX_Latch.is_r2i_non_conditional == true)
			{
				switch(optype)
				{
					case addi : 
					{
						alu_result = op1 + imm;
						break;
					}
					case subi : 
					{
						alu_result = op1 - imm;
						break;
					}
					case muli : 
					{
						alu_result = op1 * imm;
						break;
					}
					case divi : 
					{
						alu_result = op1 / imm;
						/* Storing remainder in %x31 */
						EX_MA_Latch.setx31_result(op1%imm);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case andi : 
					{
						alu_result = op1 & imm;
						break;
					}
					case ori  : 
					{
						alu_result = op1 | imm;
						break;
					}
					case xori : 
					{
						alu_result = op1 ^ imm;
						break;
					}
					case slti : 
					{
						if(op1<imm)
						{
							alu_result = 1;
						}
						else 
							alu_result = 0;
						break;
					}
					case slli : 
					{
						alu_result = op1<<imm;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(0,imm);
						int shifted_out_bits = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(shifted_out_bits);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case srli : 
					{
						alu_result = op1>>>imm;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(32-imm,32);
						int shifted_out_bits = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(shifted_out_bits);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case srai :
					{
						alu_result = op1>>imm;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(32-imm,32);
						int shifted_out_bits = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(shifted_out_bits);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
				}
				EX_MA_Latch.setis_alu();
				EX_MA_Latch.setalu_result(alu_result);
				EX_MA_Latch.setdestination_register(rd);
				EX_MA_Latch.set_ma_destination(rd);
				//EX_MA_Latch.setMA_enable(true);	
				OF_EX_Latch.is_r2i_non_conditional = false;
			}
			else if(OF_EX_Latch.is_load_store == true)
			{
				switch(optype)
				{
					case load:
					{
						EX_MA_Latch.setis_load();
						int word_at = op1 + imm;
						EX_MA_Latch.setword_at(word_at);
						EX_MA_Latch.setdestination_register(rd);
						EX_MA_Latch.set_ma_destination(rd);
						break;
					}
					case store:
					{
						EX_MA_Latch.setis_store();
						int word_at = rd + imm;
						EX_MA_Latch.setword_at(word_at);
						EX_MA_Latch.setsource_register(op1);
						EX_MA_Latch.set_ma_destination(op1);
						break;
					}
				}	
				OF_EX_Latch.is_load_store = false;
			}
			else if(OF_EX_Latch.is_r2i_conditional == true)
			{
				switch(optype)
				{
					/* if branch taken, then make the instructions in OF and EX stages nop in the next cycle */
					
					case beq : 
					{

						if(op1 == op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(branch_target_conditional);
							Simulator.increment_wrong_branches();
						}
						break;
					}
					case bne : 
					{
						if(op1 != op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(branch_target_conditional);
							Simulator.increment_wrong_branches();
						}
						break;
					}
					case blt : 
					{
						if(op1 < op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(branch_target_conditional);
							Simulator.increment_wrong_branches();
						}
						break;
					}
					case bgt :
					{
						if(op1 > op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(branch_target_conditional);
							Simulator.increment_wrong_branches();
						}
						break;
					}
				}	
				OF_EX_Latch.is_r2i_conditional = false;
			}
			else if(OF_EX_Latch.is_jmp == true)
			{	
				/* handling jmp in the same way as done for conditional branch instructions */
				IF_EnableLatch.set_wrong_branch_taken(true);
				IF_EnableLatch.set_correct_pc(branch_target_jmp);
				OF_EX_Latch.is_jmp = false;
				Simulator.increment_wrong_branches();
			}
			else if(OF_EX_Latch.is_end == true)
			{
				OF_EX_Latch.is_end = false;
				MA_RW_Latch.set_end_pc(containingProcessor.getRegisterFile().getProgramCounter());
			}
			/* Enabling MA stage */
			EX_MA_Latch.setMA_enable(true);
		}
		/* Enabling MA stage */
		//EX_MA_Latch.setMA_enable(true);
	}
		
}
