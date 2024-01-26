package processor.pipeline;

import processor.Processor;
import processor.Clock;
import generic.Simulator;
import generic.*;
import configuration.*;

public class InstructionFetch implements Element{
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}
	
	public void performIF()
	{
		if(IF_EnableLatch.isIF_enable())
		{
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
			if(IF_EnableLatch.stall_switch())
			{
				if(!IF_EnableLatch.isIF_busy())
				{
					Simulator.getEventQueue().addEvent(new CacheReadEvent_i(Clock.getCurrentTime()+Configuration.L1i_latency,this,containingProcessor.getcache_i(),currentPC));
					IF_EnableLatch.setIF_busy(true);
					IF_OF_Latch.setOF_enable(false); 
					OF_EX_Latch.setEX_enable(false); 
					EX_MA_Latch.setMA_enable(false); 
					MA_RW_Latch.setRW_enable(false); 
				}
				
			}
		}
	}
	
	@Override
	public void handleEvent(Event e)
	{
		if(IF_OF_Latch.isOF_busy()||!IF_EnableLatch.stall_switch())
		{
			e.setEventTime(Clock.getCurrentTime()+1);
			Simulator.getEventQueue().addEvent(e);
			// System.out.println("IF: Busy");
		}
		else
		{
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
			CacheResponseEvent_i event = (CacheResponseEvent_i) e;
			IF_OF_Latch.setInstruction(event.getValue());
			// System.out.println("IF: Received Cache Response");
			containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);
			Simulator.increment_dynamic_instructions(); 
			if(IF_EnableLatch.wrong_branch_taken()) 
			{
				IF_OF_Latch.set_is_nop(true);
				OF_EX_Latch.setoptype(Instruction.OperationType.valueOf("nop"));
				containingProcessor.getRegisterFile().setProgramCounter(IF_EnableLatch.get_correct_pc());
				IF_EnableLatch.set_wrong_branch_taken(false);
			}
			IF_EnableLatch.setIF_busy(false);
			IF_OF_Latch.setOF_enable(true); 
			OF_EX_Latch.setEX_enable(true);
			EX_MA_Latch.setMA_enable(true);
			MA_RW_Latch.setRW_enable(true);
			
		}
	}

}
