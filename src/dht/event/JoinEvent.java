package dht.event;

import java.io.Serializable;

import dht.chord.ChordKey;

public class JoinEvent implements DHTEvent, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4876774787291560722L;
	public final EventType type = EventType.JOIN;
	private ChordKey original;
	private String destination;
	private String ip;
	
	public JoinEvent(ChordKey orig, String dest, String ip){
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
