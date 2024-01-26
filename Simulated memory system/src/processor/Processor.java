package processor;

import processor.memorysystem.*;
import processor.memorysystem.MainMemory;
import processor.pipeline.EX_IF_LatchType;
import processor.pipeline.EX_MA_LatchType;
import processor.pipeline.Execute;
import processor.pipeline.IF_EnableLatchType;
import processor.pipeline.IF_OF_LatchType;
import processor.pipeline.InstructionFetch;
import processor.pipeline.MA_RW_LatchType;
import processor.pipeline.MemoryAccess;
import processor.pipeline.OF_EX_LatchType;
import processor.pipeline.OperandFetch;
import processor.pipeline.RegisterFile;
import processor.pipeline.RegisterWrite;
import configuration.*;

public class Processor {
	
	RegisterFile registerFile;
	MainMemory mainMemory;
	Cache cache_i;
	Cache cache_d;
	
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	InstructionFetch IFUnit;
	OperandFetch OFUnit;
	Execute EXUnit;
	MemoryAccess MAUnit;
	RegisterWrite RWUnit;
	
	public Processor()
	{
		registerFile = new RegisterFile();
		mainMemory = new MainMemory();
		cache_i = new Cache(this, 0);
		cache_d = new Cache(this, 1);
		
		IF_EnableLatch = new IF_EnableLatchType();
		IF_OF_Latch = new IF_OF_LatchType();
		OF_EX_Latch = new OF_EX_LatchType();
		EX_MA_Latch = new EX_MA_LatchType();
		EX_IF_Latch = new EX_IF_LatchType();
		MA_RW_Latch = new MA_RW_LatchType();
		
		IFUnit = new InstructionFetch(this, IF_EnableLatch, IF_OF_Latch, OF_EX_Latch, EX_IF_Latch,EX_MA_Latch, MA_RW_Latch);
		OFUnit = new OperandFetch(this, IF_EnableLatch, IF_OF_Latch, OF_EX_Latch, EX_IF_Latch,EX_MA_Latch, MA_RW_Latch);
		EXUnit = new Execute(this, IF_EnableLatch, IF_OF_Latch, OF_EX_Latch, EX_IF_Latch,EX_MA_Latch, MA_RW_Latch);
		MAUnit = new MemoryAccess(this, IF_EnableLatch, IF_OF_Latch, OF_EX_Latch, EX_IF_Latch,EX_MA_Latch, MA_RW_Latch);
		RWUnit = new RegisterWrite(this, IF_EnableLatch, IF_OF_Latch, OF_EX_Latch, EX_IF_Latch,EX_MA_Latch, MA_RW_Latch);
	}
	
	public void printState(int memoryStartingAddress, int memoryEndingAddress)
	{
		System.out.println(registerFile.getContentsAsString());
		
		System.out.println(mainMemory.getContentsAsString(memoryStartingAddress, memoryEndingAddress));		
	}

	public RegisterFile getRegisterFile() {
		return registerFile;
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}

	public MainMemory getMainMemory() {
		return mainMemory;
	}

	public void setMainMemory(MainMemory mainMemory) {
		this.mainMemory = mainMemory;
	}

	public Cache getcache_i() {
		return cache_i;
	}

	public void setcache_i(Cache cache) {
		this.cache_i = cache_i;
	}

	public Cache getcache_d() {
		return cache_d;
	}

	public void setcache_d(Cache cache) {
		this.cache_d = cache_d;
	}

	public InstructionFetch getIFUnit() {
		return IFUnit;
	}

	public OperandFetch getOFUnit() {
		return OFUnit;
	}

	public Execute getEXUnit() {
		return EXUnit;
	}

	public MemoryAccess getMAUnit() {
		return MAUnit;
	}

	public RegisterWrite getRWUnit() {
		return RWUnit;
	}

}
