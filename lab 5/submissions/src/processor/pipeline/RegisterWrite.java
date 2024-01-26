package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.*;
public class RegisterWrite 
{
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public RegisterWrite(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}
	
	public void performRW()
	{
		if(MA_RW_Latch.isRW_enable()==true)
		{	
			if(MA_RW_Latch.getoptype()!=Instruction.OperationType.valueOf("nop"))
			{
				if(MA_RW_Latch.getoptype()==Instruction.OperationType.valueOf("end"))
				{

					Simulator.setSimulationComplete(true);/* ending simulation if end is encountered */
					int currentPC = MA_RW_Latch.get_end_pc();
					containingProcessor.getRegisterFile().setProgramCounter(currentPC -1);
				}
				else
				{
					containingProcessor.getRegisterFile().setValue(MA_RW_Latch.getload_register(),MA_RW_Latch.getload_result());
					containingProcessor.getRegisterFile().setValue(31,MA_RW_Latch.getx31_result());
					if(MA_RW_Latch.get_rw_dest_x31()==31)
					{
						MA_RW_Latch.set_rw_dest_x31(0);
					}
				}
			}
		}
		IF_EnableLatch.setIF_enable(true);
	}

}
