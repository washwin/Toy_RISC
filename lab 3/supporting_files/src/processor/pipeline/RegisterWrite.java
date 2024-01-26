package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

import java.util.*;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performRW()
	{
		if(MA_RW_Latch.isRW_enable())
		{
			int ld_result, rd;
			int res_alu = MA_RW_Latch.getALU_result();
			Instruction s_inst = MA_RW_Latch.getInstruction();
			OperationType op = s_inst.getOperationType();
			List<OperationType> list = Arrays.asList(OperationType.values());
			int inst_code = list.indexOf(op);
				
			switch(inst_code)
			{
				
				// for load fetch the contents from the memory
				// and save to destination register
				case 22:
				ld_result = MA_RW_Latch.getLoad_result();
				rd = s_inst.getDestinationOperand().getValue();
				containingProcessor.getRegisterFile().setValue(rd, ld_result);
				break;

				// no work for branch and store operations
				case 23:
				case 25:
				case 24:
				case 26:
				case 28:
				case 27:
				break;
					
				case 29://end
				Simulator.setSimulationComplete(true);
				break;

				// for remaining write to destination register
				default:
					rd = s_inst.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(rd, res_alu);
					break;
			}
			
			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);
		}
	}

}
