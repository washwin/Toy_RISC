package generic;

public class MemoryWriteEvent extends Event {

	int writeAddress;
	int result;
	
	public MemoryWriteEvent(long eventTime, Element requestingElement, Element processingElement, int address, int value) {
		super(eventTime, EventType.MemoryWrite, requestingElement, processingElement);
		writeAddress = address;
		result = value;
	}

	public int getAddressToWriteTo() {
		return writeAddress;
	}

	public void setAddressToWriteTo(int address) {
		writeAddress = address;
	}

	public int getValue() {
		return result;
	}

	public void setValue(int value) {
		result = value;
	}
}
