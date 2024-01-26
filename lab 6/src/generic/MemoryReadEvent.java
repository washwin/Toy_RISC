package generic;

public class MemoryReadEvent extends Event {

	int readAddress;
	
	public MemoryReadEvent(long eventTime, Element requestingElement, Element processingElement, int address) {
		super(eventTime, EventType.MemoryRead, requestingElement, processingElement);
		readAddress = address;
	}

	public int getAddressToReadFrom() {
		return readAddress;
	}

	public void setAddressToReadFrom(int address) {
		readAddress = address;
	}
}
