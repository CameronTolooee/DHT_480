package dht.event;

import java.io.Serializable;

import dht.chord.ChordKey;

public class LookupEvent implements DHTEvent, Serializable {

	private static final long serialVersionUID = 6225445613706140246L;
	private final EventType type = EventType.LOOKUP;
	private ChordKey original;
	private String destination; 
	private String ip;
	
	public LookupEvent(ChordKey orig, String dest, String ip){
		original = orig;
		destination = dest;
		this.ip = ip;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}
	
	@Override
	public String getIP() {
		return ip;
	}
	
	public ChordKey getOriginal(){
		return original;
	}
	
	public String getDestination(){
		return destination;
	}
}
