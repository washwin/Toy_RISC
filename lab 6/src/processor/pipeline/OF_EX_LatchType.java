package processor.pipeline;
import generic.*;

public class OF_EX_LatchType {
	
	boolean EX_enable;

	boolean is_r3 = false;
	boolean is_r2i_non_conditional = false;
	boolean is_load_store = false;
	boolean is_r2i_conditional = false;
	boolean is_jmp = false;
	boolean is_end = false;
	boolean is_nop = false;
	boolean ex_enable_all;

	Instruction.OperationType optype = null;
	int pc ; 
	int rd ;
	int op1 ;
	int op2 ;
	int imm ;
	int branch_target_conditional ;
	int branch_target_jmp ;
	int ex_destination ;

	/* Methods to set and get various values, and to set instruction format type */
	public OF_EX_LatchType()
	{
		EX_enable = false;
		pc = 0; 
		rd = 0;
		op1 = 0;
		imm = 0;
		op2 = 0;
		branch_target_conditional = 0;
		branch_target_jmp = 0;
		ex_destination = 0;
	}

	public boolean isEX_enable() 
	{
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) 
	{
		EX_enable = eX_enable;
	}

	public void set_is_r3() 
	{
		is_r3 = true;
	}

	public void set_is_r2i_non_conditional() 
	{
		is_r2i_non_conditional = true;
	}

	public void set_is_r2i_conditional() 
	{
		is_r2i_conditional = true;
	}

	public void set_is_load_store() 
	{
		is_load_store = true;
	}

	public void set_is_jmp() 
	{
		is_jmp = true;
	}

	public void set_is_end() 
	{
		is_end = true;
	}

    public Instruction.OperationType getoptype() 
	{
		return optype;
	}

	public void setoptype(Instruction.OperationType given_optype) 
	{
		optype = given_optype;
	}

	public void force_nop()
	{
		optype = Instruction.OperationType.valueOf("nop");
	}

	public void set_is_nop(boolean noP)
	{
		is_nop=noP;
	}

	public boolean get_is_nop()
	{
		return is_nop;
	}
	
	public void setEX_enable_all(boolean val)
	{
		ex_enable_all=val;
	}

	public boolean getEX_enable_all()
	{
		return ex_enable_all;
	}


	public int getpc ()
	{
		return pc;
	}

	public void setpc(int given_pc) 
	{
		pc = given_pc;
	}
	
	public int getrd() 
	{
		return rd;
	}

	public void setrd(int given_rd) 
	{
		rd = given_rd;
	}

	public int getop1() 
	{
		return op1;
	}

	public void setop1(int given_op1) 
	{
		op1 = given_op1;
	}

	public int getop2() 
	{
		return op2;
	}

	public void setop2(int given_op2) 
	{
		op2 = given_op2;
	}

	public int getimm() 
	{
		return imm;
	}

	public void setimm(int given_imm) 
	{
		imm = given_imm;
	}
	public int getbranchtarget_conditional() 
	{
		return branch_target_conditional;
	}

	public void setbranchtarget_conditional(int branch_target) 
	{
		branch_target_conditional = branch_target;
	}
	public int getbranchtarget_jmp() 
	{
		return branch_target_jmp;
	}

	public void setbranchtarget_jmp(int branch_target) 
	{
		branch_target_jmp = branch_target;
	}

	public int get_ex_destination() 
	{
		return ex_destination;
	}

	public void set_ex_destination(int eX_destination) 
	{
		ex_destination = eX_destination;
	}

	
	
}
