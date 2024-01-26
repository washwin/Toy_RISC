package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	Instruction instruction;
	int load;
	int alu_result;
	
	public MA_RW_LatchType()             //constructor
	{
		RW_enable = false;
	}

	public boolean isRW_enable() {           //To get state of latch
		return RW_enable;
	}

	public void setRW_enable(boolean rw_enable) {          //To set state of latch
		RW_enable = rw_enable;
	}

	public Instruction getInstruction() {               //To get the instruction
	
		return instruction;
	}

	public void setInstruction(Instruction instruction) {           	// To set the instruction
	
		this.instruction = instruction;
	}
	
	public int getALU_result(){                     // To get the ALU result
		return alu_result;
	}

	public int getLoad_result(){                     // To  get the load result
		return load;
	}

	public void setALU_result(int alu){                // To set the ALU result
		this.alu_result=alu;
	}

	public void setLoad_result(int load){                // To  set the load result
		this.load=load;
	}



}
