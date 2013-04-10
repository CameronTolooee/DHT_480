package dht.event;

import java.io.Serializable;

public class FoundNodeEvent implements DHTEvent, Serializable{

	/**
	 * Event carrying the ip of the destination node after a lookup for a file.
	 * Should be sent directly to the client (not the event handler).
	 */
	private static final long serialVersionUID = -6219821961439396765L;
	private final EventType type = EventType.FOUND_NODE;
	private String node;
	private String clientIP;
	
	public FoundNodeEvent(String node, String clientIP){
		this.node = node;
		this.clientIP = clientIP;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}

	@Override
	public String getIP() {
		return node;
	}

	public String getClientIP(){
		return clientIP;
	}

}

