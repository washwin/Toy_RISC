package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	int alu;
	Instruction instruction;
	
	public EX_MA_LatchType()                   //constructor
	{
		MA_enable = false;
	}

	public boolean isMA_enable() {          //To get state of latch
		return MA_enable;
	}

	public void setMA_enable(boolean ma_enable) {        	//To set the state of the latch
		MA_enable = ma_enable;
	}

	public Instruction getInstruction(){              	//To get the instruction
		return instruction;
	}

	public void setInstruction(Instruction instruction){          	//To set the instruction 
		this.instruction=instruction;
	}

	public int getALU_result(){                        	//To get the ALU result
		return alu;
	}

	public void setALU_result(int alu){                  	//To set ALU Result
		this.alu=alu;
	}

}
