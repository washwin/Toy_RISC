package processor.pipeline;

public class EX_IF_LatchType {
	boolean is_ena;
	int pc;
	
		public EX_IF_LatchType()
		{
			is_ena=false;
		}
		//To check whether is enable or not
		public boolean getIS_enable(){
			return is_ena;
		}
		//To set new program couter and state of latch
		public void setIS_enable(boolean b,int new_pc){
			is_ena=b;
			pc=new_pc;
		}
		//To set state of latch
		public void setIS_enable(boolean b){
			is_ena=b;
		}
		//To get the value of PC
		public int getPC(){
			return pc;
		}
	}


