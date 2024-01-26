package processor.pipeline;

public class IF_OF_LatchType {
	
	boolean OF_enable;
	int instruction;
	
	public IF_OF_LatchType()
	{
		OF_enable = false;
	}
	//To get state of latch
	public boolean isOF_enable() {
		return OF_enable;
	}
	//To set the state of latch
	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}
	//To get Instruction
	public int getInstruction() {
		return instruction;
	}
	//To set instruction
	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

}
