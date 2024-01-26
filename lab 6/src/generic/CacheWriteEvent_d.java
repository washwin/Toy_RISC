package generic;

public class CacheWriteEvent_d extends Event {

	int writeAddress;
	int result;
	
	public CacheWriteEvent_d(long eventTime, Element requestingElement, Element processingElement, int address, int value) {
		super(eventTime, EventType.CacheWrite_d, requestingElement, processingElement);
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
