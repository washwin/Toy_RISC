package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	Instruction inst;
	int ld;
	int alu;
	boolean nop;
	
	public MA_RW_LatchType()
	{
		RW_enable = false;
		//setting nop false;
		nop=false;

	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}

	//To get the instruction
	public Instruction getInstruction() {
	
		return inst;
	}
	// To set the instruction
	public void setInstruction(Instruction temp) {
	
		inst = temp;
	}
	// TO set the load result
	public void setLoad_result(int temp){
		ld=temp;
		//Sets load with the given parameter
	}
	public void setALU_result(int temp){
		alu=temp;
		//Sets alu with given parameter
	}
	public int getALU_result(){


		return alu;
		//returns alu when calls
	}
	public int getLoad_result(){
		return ld;
	}
	//func to get NOP
	public boolean getIsNOP(){
		return nop;
	}
	//func to set NOP
	public void setIsNOP(boolean set){
		nop=set;
	}

}
