package processor.pipeline;
import generic.*;

public class IF_OF_LatchType {
	
	boolean OF_enable;

	public IF_OF_LatchType()
	{
		OF_enable = false;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}

	int instruction;

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	Instruction.OperationType optype = null;

	public void force_nop()
	{
		optype = Instruction.OperationType.valueOf("nop");

	}

	boolean is_nop;

	public void set_is_nop(boolean noP)
	{
		is_nop=noP;
	}
	public boolean get_is_nop()
	{
		return is_nop;
	}

	boolean is_OF_busy;

	public boolean isOF_busy() {
		return is_OF_busy;
	}

	public void setOF_busy(boolean is_true) {
		is_OF_busy = is_true;
	}
}
