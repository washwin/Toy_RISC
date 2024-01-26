package processor.pipeline;
import generic.*;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	boolean is_end = false;
	Instruction.OperationType optype;
	
	int load_result;
	int load_register;
	int x31_result;
	int rw_destination;
	int rw_dest_x31;
	int end_pc;

	public MA_RW_LatchType()
	{
		load_result = 0;
		load_register = 0;
		x31_result = 0;
		rw_destination = 0;
		rw_dest_x31 = 0;
		RW_enable = false;
	}

	public boolean isRW_enable() 
	{
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) 
	{
		RW_enable = rW_enable;
	}

	public int getload_result()
	{
		return load_result;
	}

	public void setload_result(int result)
	{
		load_result = result;
	}

	public int getload_register()
	{
		return load_register;
	}

	public void setload_register(int rd)
	{
		load_register = rd;
	}
	
	public int getx31_result()
	{
		return x31_result;
	}

	public void setx31_result(int x31)
	{
		x31_result = x31;
	}

	public int get_rw_destination() 
	{
		return rw_destination;
	}

	public void set_rw_destination(int rW_destination) 
	{
		rw_destination = rW_destination;
	}

	public int get_rw_dest_x31() 
	{
		return rw_dest_x31;
	}

	public void set_rw_dest_x31(int rW_dest_x31) 
	{
		rw_dest_x31 = rW_dest_x31;
	}

	public int get_end_pc() 
	{
		return end_pc;
	}

	public void set_end_pc(int pc) 
	{
		end_pc = pc;
	}

	public Instruction.OperationType getoptype() 
	{
		return optype;
	}
	
	public void setoptype(Instruction.OperationType given_optype) 
	{
		optype = given_optype;
	}
	
}
