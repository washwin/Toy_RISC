package processor.pipeline;

import processor.Processor;
import generic.*;
import generic.Operand.OperandType;
import generic.Instruction.OperationType;
import java.math.BigInteger;
import java.util.*;

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

	public int compute_operations(int operator, int op1, int op2) {
		int alu_result=0;
		switch(operator)
				{
					case 0 : 
					case 1 :
					{
						alu_result = op1 + op2;
						break;
					}
					case 2 : 
					case 3 :
					{
						alu_result = op1 - op2;
						break;
					}
					case 4 : 
					case 5:
					{
						alu_result = op1 * op2;
						break;
					}
					case 6 : 
					case 7:
					{
						alu_result = op1 / op2;
						/* Storing remainder in %x31 */
						EX_MA_Latch.setx31_result(op1%op2);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case 8 : 
					case 9:
					{
						alu_result = op1 & op2;
						break;
					}
					case 10  :
					case 11: 
					{
						alu_result = op1 | op2;
						break;
					}
					case 12 : 
					case 13:
					{
						alu_result = op1 ^ op2;
						break;
					}
					case 14 : 
					case 15:
					{
						if(op1<op2)
						{
							alu_result = 1;
						}
						else 
							alu_result = 0;
						break;
					}
					case 16 : 
					case 17:
					{
						alu_result = op1<<op2;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(0,op2);
						int temp = new BigInteger(op1_shifted_out,2).intValue();
						EX_MA_Latch.setx31_result(temp);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case 18 : 
					case 19:
					{
						alu_result = op1>>>op2;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(32-op2,32);
						int temp = new BigInteger(op1_shifted_out,2).intValue();
						/* Storing shifted-out bits in %x31 */
						EX_MA_Latch.setx31_result(temp);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					case 20 :
					case 21:
					{
						alu_result = op1>>op2;
						String op1_string = String.format("%32s",Integer.toBinaryString(op1)).replace(" ","0");
						String op1_shifted_out = op1_string.substring(32-op2,32);
						int temp = new BigInteger(op1_shifted_out,2).intValue();
						EX_MA_Latch.setx31_result(temp);
						EX_MA_Latch.set_ma_dest_x31(31);
						break;
					}
					default:
						break;
				}
				return alu_result;
	}

	public void compute_branches(int operator, int op1, int op2, int target) {
		switch(operator)
				{
					/* if branch taken, then make the instructions in OF and EX stages nop in the next cycle */
					
					case 25 : 
					{

						if(op1 == op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(target);
							Simulator.increment_wrong_branches();
						}
						break;
					}
					case 26 : 
					{
						if(op1 != op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(target);
							Simulator.increment_wrong_branches();
						}
						break;
					}
					case 27 : 
					{
						if(op1 < op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(target);
							Simulator.increment_wrong_branches();
						}
						break;
					}
					case 28 :
					{
						if(op1 > op2)
						{
							IF_EnableLatch.set_wrong_branch_taken(true);
							IF_EnableLatch.set_correct_pc(target);
							Simulator.increment_wrong_branches();
						}
						break;
					}
				}	
	}

	public void performEX()
	{
		if(OF_EX_Latch.isEX_enable())
		{
			Instruction.OperationType optype = OF_EX_Latch.getoptype();
			int op1 = OF_EX_Latch.getop1();
			int op2 = OF_EX_Latch.getop2();
			int imm = OF_EX_Latch.getimm();
			int rd = OF_EX_Latch.getrd();
			int target_branch = OF_EX_Latch.getbranchtarget_conditional();
			int branch_target_jmp = OF_EX_Latch.getbranchtarget_jmp();
			int alu_result = 0;
			
			EX_MA_Latch.setoptype(optype);
			
			List<OperationType> list = Arrays.asList(OperationType.values());
			int inst_code = list.indexOf(optype);

			if(OF_EX_Latch.is_r3 == true)
			{
				alu_result = compute_operations(inst_code, op1, op2);

				EX_MA_Latch.setis_alu();
				EX_MA_Latch.setalu_result(alu_result);
				EX_MA_Latch.setdestination_register(rd);
				EX_MA_Latch.set_ma_destination(rd);
				OF_EX_Latch.is_r3 =false;
			}
			else if(OF_EX_Latch.is_r2i_non_conditional == true)
			{
				alu_result = compute_operations(inst_code, op1, imm);
				
				EX_MA_Latch.setis_alu();
				EX_MA_Latch.setalu_result(alu_result);
				EX_MA_Latch.setdestination_register(rd);
				EX_MA_Latch.set_ma_destination(rd);
				//EX_MA_Latch.setMA_enable(true);	
				OF_EX_Latch.is_r2i_non_conditional = false;
			}
			else if(OF_EX_Latch.is_load_store == true)
			{
				switch(inst_code)
				{
					case 22:
					{
						EX_MA_Latch.setis_load();
						int word_at = op1 + imm;
						EX_MA_Latch.setword_at(word_at);
						EX_MA_Latch.setdestination_register(rd);
						EX_MA_Latch.set_ma_destination(rd);
						break;
					}
					case 23:
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
				compute_branches(inst_code, op1, op2, target_branch);

				OF_EX_Latch.is_r2i_conditional = false;
			}
			else if(OF_EX_Latch.is_jmp == true)
			{	
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
	}
		
}
