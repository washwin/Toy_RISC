package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	int alu;
	Instruction inst;
	boolean nop;
	
	public EX_MA_LatchType()
	{
		MA_enable = false;
		//setting nop false
		nop=false;
	}
	//To check whether is enabled or not
	public boolean isMA_enable() {
		return MA_enable;
	}
	//To set the state of the latch
	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}
	//To return the instruction
	public Instruction getInstruction(){
		return inst;
	}
	//To set the instruction with given value
	public void setInstruction(Instruction temp){
		inst=temp;
	}
	//To get the alu_result
	public int getALU_result(){
		return alu;
	}
	//To set ALU Result
	public void setALU_result(int res){
		alu=res;
	}
	//function to get nop
	public boolean getIsNOP(){
		return nop;
	}
	//function to set nop
	public void setIsNOP(boolean a){
		nop=a;
	}

}
