package generic;

public class CacheResponseEvent_d extends Event {

	int result;
	
	public CacheResponseEvent_d(long eventTime, Element requestingElement, Element processingElement, int value) {
		super(eventTime, EventType.CacheResponse_d, requestingElement, processingElement);
		result = value;
	}

	public int getValue() {
		return result;
	}

	public void setValue(int value) {
		result = value;
	}

}
