package processor.memorysystem;
import processor.Processor;
import processor.memorysystem.*;
import processor.Clock;
import generic.Simulator;
import generic.*;
import configuration.*;
import java.math.BigInteger;
import java.math.*;
import configuration.*;

public class Cache implements Element{
	Processor containingProcessor;
	public CacheLine block;
	public CacheLine[] buffer;
	public Boolean read_request= false;
	public Boolean write_request= false;
	public int set;
	public int tag;
	public int used;
	public Boolean is_cache_busy = false;
	Element direct_to;
	public Cache(Processor containingProcessor, int cache_type)
	{
		this.containingProcessor = containingProcessor;
		if(cache_type == 0) 
		{
			buffer = new CacheLine[Configuration.L1i_numberOfLines];
			for(int k=0;k<Configuration.L1i_numberOfLines;k++)
			{
				block = new CacheLine();
				buffer[k]=block;
			}
		}
		else 
		{
			buffer = new CacheLine[Configuration.L1d_numberOfLines];
			for(int k=0;k<Configuration.L1d_numberOfLines;k++)
			{
				block = new CacheLine();
				buffer[k]=block;
			}
		}
		
	}

	static String twosComplement(String str_s)
	{
		StringBuffer str = new StringBuffer(str_s);
		int n = str.length();
	 
		int i;
		for (i = n-1 ; i >= 0 ; i--)
			if (str.charAt(i) == '1')
				break;
	 
		if (i == -1)
			return "1" + str;
		  
		for (int k = i-1 ; k >= 0; k--)
		{
			if (str.charAt(k) == '1')
				str.replace(k, k+1, "0");
			else
				str.replace(k, k+1, "1");
		}
		
		return str.toString();
	}

	public int get_lru_i(){
		if(containingProcessor.getcache_i().used == 0)
		{
	 		return 1;
	 	}

		return 0;
	}

	public int get_lru_d(){
		if(containingProcessor.getcache_d().used == 0)
		{
	 		return 1;
	 	}

		return 0;
	}

    public int get_tag(String binary_address, int num_sets)
    {
		int index = (int)(Math.floor(Math.log(num_sets) / Math.log(2))+1);
    	String tag = binary_address.substring(0,32-index+1);
		
    	return(new BigInteger(tag,2).intValue());
    }

    public int get_set(String binary_address, int num_sets)
    {
    	int index = (int)(Math.floor(Math.log(num_sets) / Math.log(2))+1);
    	if(index == 1)
    	{
    		return(new BigInteger(binary_address.substring(31),2).intValue());
    	}
    	String set_bin = binary_address.substring(32-index+1,32);
		
    	return(new BigInteger(set_bin,2).intValue());
    }

	public void cacheRead_i(int address, CacheReadEvent_i event) //used when load request is made
	{
		containingProcessor.getcache_i().is_cache_busy = true;
		int req_val = 0;
		boolean line_is_present = false;
		int num_sets = Configuration.L1i_numberOfLines/Configuration.L1i_associativity;

		String binary_address = null;

		if(address>=0)
		{
			binary_address = String.format("%32s",Integer.toBinaryString(address)).replace(" ","0");
		}
		else
		{
			binary_address = twosComplement(String.format("%32s",Integer.toBinaryString(-address)).replace(" ","0"));
		}

		int tag = get_tag(binary_address,num_sets);
		int set = get_set(binary_address,num_sets);
		
		if(containingProcessor.getcache_i().buffer[Configuration.L1i_associativity*set].tag==tag)
		{
			//hit/* */
			req_val = containingProcessor.getcache_i().buffer[Configuration.L1i_associativity*set].data[0];
			Simulator.getEventQueue().addEvent(new CacheResponseEvent_i(Clock.getCurrentTime(),this, event.getRequestingElement(), req_val));
			containingProcessor.getcache_i().is_cache_busy = false;
			containingProcessor.getcache_i().used = 0;
		}
		else if(containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*set)+1].tag==tag)
		{
			//hit
			req_val = containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*set)+1].data[0];
			Simulator.getEventQueue().addEvent(new CacheResponseEvent_i(Clock.getCurrentTime(),this, event.getRequestingElement(), req_val));
			containingProcessor.getcache_i().is_cache_busy = false;
			containingProcessor.getcache_i().used = 1;
		}
		else
		{
			//miss
			handleCacheMiss(address, 0, 0, 0, set, tag); /* memory read request */
		}
	}

	public void cacheWrite_i(int address, int value, CacheWriteEvent_i event) //used when store request is made
	{
		containingProcessor.getcache_i().is_cache_busy = true;
		int req_val = 0;
		boolean line_is_present = false;
		int num_sets = Configuration.L1i_numberOfLines/Configuration.L1i_associativity;
 
		String binary_address = null;
		if(address>=0)
		{
			binary_address = String.format("%32s",Integer.toBinaryString(address)).replace(" ","0");
		}
		else
		{
			binary_address = twosComplement(String.format("%32s",Integer.toBinaryString(-address)).replace(" ","0"));
		}

		int tag = get_tag(binary_address,num_sets);
		int set = get_set(binary_address,num_sets);
		
		if(containingProcessor.getcache_i().buffer[Configuration.L1i_associativity*set].tag==tag)
		{
			//hit
			containingProcessor.getcache_i().buffer[Configuration.L1i_associativity*set].data[0]=value;
			handleCacheMiss(address, 0, 1, value, set, tag); /* memory write request */
			containingProcessor.getcache_i().used = 0;
		}
		else if(containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*set)+1].tag==tag)
		{
			//hit
			containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*set)+1].data[0]=value;
			handleCacheMiss(address, 0, 1, value, set, tag); /* memory write request */
			containingProcessor.getcache_i().used = 1;
		}
		else
		{
			//miss
			int lru = get_lru_i();
			handleCacheMiss(address, 0, 1, value, set, tag); /* memory write request */
			containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*set)+lru].data[0]=value;//assuming lru
			containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*set)+lru].tag = tag;
		}
	}

	public void cacheRead_d(int address, CacheReadEvent_d event) 
	{
		containingProcessor.getcache_d().is_cache_busy = true;
		int req_val = 0;
		boolean line_is_present = false;
		int num_sets = Configuration.L1d_numberOfLines/Configuration.L1d_associativity;
		String binary_address = null;
		if(address>=0)
		{
			binary_address = String.format("%32s",Integer.toBinaryString(address)).replace(" ","0");
		}
		else
		{
			binary_address = twosComplement(String.format("%32s",Integer.toBinaryString(-address)).replace(" ","0"));
		}
		
		int tag = get_tag(binary_address,num_sets);
		int set = get_set(binary_address,num_sets);
		if(containingProcessor.getcache_d().buffer[Configuration.L1d_associativity*set].tag==tag)
		{
			//hit
			req_val = containingProcessor.getcache_d().buffer[Configuration.L1d_associativity*set].data[0];
			Simulator.getEventQueue().addEvent(new CacheResponseEvent_d(Clock.getCurrentTime(),this, event.getRequestingElement(), req_val));
			containingProcessor.getcache_d().is_cache_busy = false;
			containingProcessor.getcache_d().used = 0;
		}
		else if(containingProcessor.getcache_d().buffer[(Configuration.L1d_associativity*set)+1].tag==tag)
		{
			//hit
			req_val = containingProcessor.getcache_d().buffer[(Configuration.L1d_associativity*set)+1].data[0];
			Simulator.getEventQueue().addEvent(new CacheResponseEvent_d(Clock.getCurrentTime(),this, event.getRequestingElement(), req_val));
			containingProcessor.getcache_d().is_cache_busy = false;
			containingProcessor.getcache_d().used = 1;
		}
		else
		{
			//miss
			handleCacheMiss(address, 1, 0, 0, set, tag);
		}
	}

	public void cacheWrite_d(int address, int value, CacheWriteEvent_d event) //used when store request is made
	{
		int req_val = 0;
		boolean line_is_present = false;
		int num_sets = Configuration.L1d_numberOfLines/Configuration.L1d_associativity;
		
		String binary_address = null;
		if(address>=0)
		{
			binary_address = String.format("%32s",Integer.toBinaryString(address)).replace(" ","0");
		}
		else
		{
			binary_address = twosComplement(String.format("%32s",Integer.toBinaryString(-address)).replace(" ","0"));
		}
		
		int tag = get_tag(binary_address,num_sets);
		int set = get_set(binary_address,num_sets);
		
		if(containingProcessor.getcache_d().buffer[Configuration.L1d_associativity*set].tag==tag)
		{
			containingProcessor.getcache_d().buffer[Configuration.L1d_associativity*set].data[0]=value;
			handleCacheMiss(address, 1, 1, value, set, tag); /* memory write request */
			containingProcessor.getcache_d().used = 0;
		}
		else if(containingProcessor.getcache_d().buffer[(Configuration.L1d_associativity*set)+1].tag==tag)
		{
			containingProcessor.getcache_d().buffer[(Configuration.L1d_associativity*set)+1].data[0]=value;
			handleCacheMiss(address, 1, 1, value, set, tag); /* memory write request */
			containingProcessor.getcache_d().used = 1;
		}
		else
		{
			int lru = get_lru_d();
			handleCacheMiss(address, 1, 1, value, set, tag); /* memory write request */
			containingProcessor.getcache_d().buffer[(Configuration.L1d_associativity*set)+lru].data[0]=value;//assuming lru
			containingProcessor.getcache_d().buffer[(Configuration.L1d_associativity*set)+lru].tag = tag;
		}
	}

	public void handleCacheMiss(int address, int cache_type, int request_type, int write_value, int set, int tag)
	{
		int value = 0;
		if(cache_type ==0 && request_type == 0) /* implies memory read request by L1i cache */
		{
			containingProcessor.getcache_i().read_request = true;
			Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime()+Configuration.mainMemoryLatency,this,containingProcessor.getMainMemory(),address));
			containingProcessor.getcache_i().set = set;
			containingProcessor.getcache_i().tag = tag;
		}
		else if(cache_type ==0 && request_type == 1) /* implies memory write request by L1i cache */
		{
			containingProcessor.getcache_i().write_request = true;
			Simulator.getEventQueue().addEvent(new MemoryWriteEvent(Clock.getCurrentTime()+Configuration.mainMemoryLatency,this,containingProcessor.getMainMemory(),address,write_value));
			containingProcessor.getcache_i().set = set;
			containingProcessor.getcache_i().tag = tag;
		}
		else if(cache_type ==1 && request_type == 0) /* implies memory read request by L1d cache */
		{
			containingProcessor.getcache_d().read_request = true;
			Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime()+Configuration.mainMemoryLatency,this,containingProcessor.getMainMemory(),address));
			containingProcessor.getcache_d().set = set;
			containingProcessor.getcache_d().tag = tag;
		}
		else if(cache_type ==1 && request_type == 1) /* implies memory write request by L1d cache */
		{
			containingProcessor.getcache_d().write_request = true;
			Simulator.getEventQueue().addEvent(new MemoryWriteEvent(Clock.getCurrentTime()+Configuration.mainMemoryLatency,this,containingProcessor.getMainMemory(),address,write_value));
			containingProcessor.getcache_d().set = set;
			containingProcessor.getcache_d().tag = tag;
		}
		
	}

	public void handleEvent(Event e) 
	{
		if(e.getEventType() == Event.EventType.MemoryResponse)
		{
			MemoryResponseEvent event = (MemoryResponseEvent) e;
		 	if(containingProcessor.getcache_i().read_request)
		 	{
				//instruction memory read
		 		int lru = get_lru_i();
		 		int req_val = event.getValue();
		 		containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*(containingProcessor.getcache_i().set))+lru].data[0] = req_val;//assuming lru
		 		containingProcessor.getcache_i().buffer[(Configuration.L1i_associativity*(containingProcessor.getcache_i().set))+lru].tag = containingProcessor.getcache_i().tag;
		 		Simulator.getEventQueue().addEvent(new CacheResponseEvent_i(Clock.getCurrentTime(),this, containingProcessor.getcache_i().direct_to, req_val));
		 		// System.out.println(event.getRequestingElement());
		 		containingProcessor.getcache_i().read_request = false;
		 		containingProcessor.getcache_i().is_cache_busy = false;
		 	}
		 	else if(containingProcessor.getcache_d().read_request)
		 	{
				//data memory read
		 		int lru = get_lru_d();
		 		int req_val = event.getValue();
		 		containingProcessor.getcache_d().buffer[(Configuration.L1i_associativity*(containingProcessor.getcache_d().set))+lru].data[0] = req_val;//assuming lru
		 		containingProcessor.getcache_d().buffer[(Configuration.L1i_associativity*(containingProcessor.getcache_d().set))+lru].tag = containingProcessor.getcache_d().tag;
		 		Simulator.getEventQueue().addEvent(new CacheResponseEvent_d(Clock.getCurrentTime(),this, containingProcessor.getcache_d().direct_to, req_val));
		 		containingProcessor.getcache_d().read_request = false;
		 		containingProcessor.getcache_d().is_cache_busy = false;
		 	}
		 	else if(containingProcessor.getcache_i().write_request)
		 	{
				//instruction memory write
		 		int req_val = event.getValue();
				Simulator.getEventQueue().addEvent(new CacheResponseEvent_i(Clock.getCurrentTime(),this, containingProcessor.getcache_i().direct_to, req_val));
				containingProcessor.getcache_i().write_request = false;
		 		containingProcessor.getcache_i().is_cache_busy = false;
		 	}
		 	else if(containingProcessor.getcache_d().write_request)
		 	{
				//data memory write
		 		int req_val = event.getValue();
				Simulator.getEventQueue().addEvent(new CacheResponseEvent_d(Clock.getCurrentTime(),this, containingProcessor.getcache_d().direct_to, req_val));
				containingProcessor.getcache_d().write_request = false;
		 		containingProcessor.getcache_d().is_cache_busy = false;
		 	}
	 	}

		else if(e.getEventType() == Event.EventType.CacheRead_i)
		{	
			if(containingProcessor.getcache_i().is_cache_busy)
			{
				e.setEventTime(Clock.getCurrentTime()+1);
				Simulator.getEventQueue().addEvent(e);
			}
			else
			{
				//instruction read
				CacheReadEvent_i event = (CacheReadEvent_i) e;
				containingProcessor.getcache_i().direct_to = event.getRequestingElement();
				cacheRead_i(event.getAddressToReadFrom(), event);
			}	
	 	}

	 	else if(e.getEventType() == Event.EventType.CacheRead_d)
		{	
			if(containingProcessor.getcache_d().is_cache_busy)
			{
				e.setEventTime(Clock.getCurrentTime()+1);
				Simulator.getEventQueue().addEvent(e);
			}
			else
			{
				//cache read
				CacheReadEvent_d event = (CacheReadEvent_d) e;
				containingProcessor.getcache_d().direct_to = event.getRequestingElement();
				cacheRead_d(event.getAddressToReadFrom(), event);
			}
			
	 	}
			 	else if(e.getEventType() == Event.EventType.CacheWrite_i)
		{	
			if(containingProcessor.getcache_i().is_cache_busy)
			{
				e.setEventTime(Clock.getCurrentTime()+1);
				Simulator.getEventQueue().addEvent(e);
			}
			else
			{
				//instruction write
				CacheWriteEvent_i event = (CacheWriteEvent_i) e;
				containingProcessor.getcache_i().direct_to = event.getRequestingElement();
				cacheWrite_i(event.getAddressToWriteTo(), event.getValue(), event);
			}
			
	 	}

	 	else if(e.getEventType() == Event.EventType.CacheWrite_d)
		{	
			//cache write
			if(containingProcessor.getcache_d().is_cache_busy)
			{
				e.setEventTime(Clock.getCurrentTime()+1);
				Simulator.getEventQueue().addEvent(e);
			}
			else
			{
				CacheWriteEvent_d event = (CacheWriteEvent_d) e;
				containingProcessor.getcache_d().direct_to = event.getRequestingElement();
				cacheWrite_d(event.getAddressToWriteTo(), event.getValue(), event);
			}
			
	 	}
	 }
}


