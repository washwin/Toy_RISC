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
					Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime()+Configuration.mainMemoryLatency,this,containingProcessor.getMainMemory(),currentPC));
					IF_EnableLatch.setIF_busy(true);
					IF_OF_Latch.setOF_enable(false); /* Disabling OF stage */
					OF_EX_Latch.setEX_enable(false); /* Disabling EX stage */
					EX_MA_Latch.setMA_enable(false); /* Disabling MA stage */
					MA_RW_Latch.setRW_enable(false); /* Disabling RW stage */
				}
				
			}
			
			
		}
		//IF_OF_Latch.setOF_enable(true);
	}

	public void update_latches() {
		IF_EnableLatch.setIF_busy(false);
		IF_OF_Latch.setOF_enable(true); /* Enabling OF stage */
		OF_EX_Latch.setEX_enable(true); /* Enabling EX stage */
		EX_MA_Latch.setMA_enable(true); /* Enabling MA stage */
		MA_RW_Latch.setRW_enable(true); /* Enabling RW stage */
	}

	@Override
	public void handleEvent(Event e)
	{
		if(IF_OF_Latch.isOF_busy()||!IF_EnableLatch.stall_switch())
		{
			e.setEventTime(Clock.getCurrentTime()+1);
			Simulator.getEventQueue().addEvent(e);
		}
		else
		{
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			IF_OF_Latch.setInstruction(event.getValue());

			containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);
			Simulator.increment_dynamic_instructions();
			if(IF_EnableLatch.wrong_branch_taken())
			/* nop for wrong branch */
			{
				IF_OF_Latch.set_is_nop(true);
				OF_EX_Latch.setoptype(Instruction.OperationType.valueOf("nop"));
				containingProcessor.getRegisterFile().setProgramCounter(IF_EnableLatch.get_correct_pc());
				IF_EnableLatch.set_wrong_branch_taken(false);
			}
			update_latches();
		}
	}

}
