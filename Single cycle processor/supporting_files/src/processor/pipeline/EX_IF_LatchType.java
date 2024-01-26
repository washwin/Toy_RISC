package processor.pipeline;

public class EX_IF_LatchType {
	boolean is_enable;
	int pc;
	
		public EX_IF_LatchType()    //constructor
		{
			is_enable=false;
		}
		                                          
		public boolean getIS_enable(){                 //To get state of latch
			return is_enable;
		}
	
		
		public void setIS_enable(boolean set_enable){                //To set state of latch
			is_enable=set_enable;
		}
		
		public void setIS_enable(boolean set_enable,int pc_new){      //To set new program couter and state of latch
			is_enable=set_enable;
			pc=pc_new;
		}
		
		public int getPC(){                           //To get the value of PC
			return pc;
		}
	}


