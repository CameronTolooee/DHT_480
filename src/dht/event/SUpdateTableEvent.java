package dht.event;

import java.io.Serializable;

public class SUpdateTableEvent implements DHTEvent, Serializable {

	private static final long serialVersionUID = -3420496378667765852L;
	
	EventType type = EventType.UPDATE_TABLE;
	private String ip;
	
	public SUpdateTableEvent(String id) {
		ip = id;
	}

	@Override
	public EventType getEventType() {
		return type;
	}

	@Override
	public String getIP() {
		return ip;
	}
}
