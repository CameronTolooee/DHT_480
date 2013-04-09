package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

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

	@Override
	public ChordKey getOriginal() {
		return original;
	}

	public String getDestination() {
		return destination;
	}

	@Override
	public ChordNode getNode() {
		return null;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String getIP() {
		return ip;
	}

	@Override
	public CountDownLatch getLatch() {
		return null;
	}
}
