package generic;

public class CacheReadEvent_d extends Event {

	int readAddress;
	
	public CacheReadEvent_d(long eventTime, Element requestingElement, Element processingElement, int address) {
		super(eventTime, EventType.CacheRead_d, requestingElement, processingElement);
		readAddress = address;
	}

	public int getAddressToReadFrom() {
		return readAddress;
	}

	public void setAddressToReadFrom(int address) {
		readAddress = address;
	}
}
