package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	
	boolean EX_enable;
	Instruction inst;
	
	boolean nop;

	public OF_EX_LatchType()
	{
		EX_enable = false;
		//setting nop false
		nop=false;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}
	
	public void setInstruction(Instruction temp){
		//Sets the instruction of passed parameter
		this.inst=temp;
	}
	public Instruction getInstruction(){
		//returns the instruction which is set
		return this.inst;
	}
	//function to get NOP
	public boolean getIsNOP(){
		return  nop;
	}
	//function to set nop
	public void setIsNOP(boolean set){
		nop=set;
	}

}
