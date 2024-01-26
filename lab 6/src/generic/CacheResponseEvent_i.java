package generic;

public class CacheResponseEvent_i extends Event {

	int result;
	
	public CacheResponseEvent_i(long eventTime, Element requestingElement, Element processingElement, int value) {
		super(eventTime, EventType.CacheResponse_i, requestingElement, processingElement);
		result = value;
	}

	public int getValue() {
		return result;
	}

	public void setValue(int value) {
		result = value;
	}

}
