package processor.pipeline;

public class IF_EnableLatchType {
	
	boolean IF_enable;

	public IF_EnableLatchType()
	{
		IF_enable = true;
		stall_switch = true;
	}

	public boolean isIF_enable() {
		return IF_enable;
	}

	public void setIF_enable(boolean iF_enable) {
		IF_enable = iF_enable;
	}

	boolean stall_switch;
	public boolean stall_switch() {
		return stall_switch;
	}

	public void set_stall_switch(boolean stall_Switch) {
		stall_switch = stall_Switch;
	}

	boolean wrong_branch_taken;
	public boolean wrong_branch_taken() {
		return wrong_branch_taken;
	}

	public void set_wrong_branch_taken(boolean wrong_branch_Taken) {
		wrong_branch_taken = wrong_branch_Taken;
	}

	int correct_pc;
	
	public int get_correct_pc() {
		return correct_pc;
	}

	public void set_correct_pc(int correct_PC) {
		correct_pc = correct_PC;
	}

	boolean is_IF_busy;

	public boolean isIF_busy() {
		return is_IF_busy;
	}

	public void setIF_busy(boolean is_true) {
		is_IF_busy = is_true;
	}

}
