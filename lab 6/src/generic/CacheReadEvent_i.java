package generic;

public class CacheReadEvent_i extends Event {

	int readAddress;
	
	public CacheReadEvent_i(long eventTime, Element requestingElement, Element processingElement, int address) {
		super(eventTime, EventType.CacheRead_i, requestingElement, processingElement);
		readAddress = address;
	}

	public int getAddressToReadFrom() {
		return readAddress;
	}

	public void setAddressToReadFrom(int address) {
		readAddress = address;
	}
}
