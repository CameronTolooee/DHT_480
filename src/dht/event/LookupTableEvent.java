package dht.event;

import java.io.Serializable;

import dht.chord.ChordKey;

public class LookupTableEvent implements Serializable, DHTEvent {

	private static final long serialVersionUID = 5716012009199456969L;
	private final EventType type = EventType.LOOKUP_TABLE;
	private ChordKey original;
	private String destination;
	private int position;
	private String ip;
	
	public LookupTableEvent(ChordKey orig, String dest, int position, String ip){
		this.original = orig;
		this.destination = dest;
		this.position = position;
		this.ip = ip;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}

	public ChordKey getOriginal() {
		return original;
	}

	public String getDestination() {
		return destination;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public String getIP() {
		return ip;
	}
}
