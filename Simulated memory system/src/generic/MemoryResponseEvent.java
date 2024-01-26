package generic;

public class MemoryResponseEvent extends Event {

	int result;
	
	public MemoryResponseEvent(long eventTime, Element requestingElement, Element processingElement, int value) {
		super(eventTime, EventType.MemoryResponse, requestingElement, processingElement);
		result = value;
	}

	public int getValue() {
		return result;
	}

	public void setValue(int value) {
		result = value;
	}

}
