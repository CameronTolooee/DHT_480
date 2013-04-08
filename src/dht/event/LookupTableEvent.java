package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

public class LookupTableEvent implements Serializable, DHTEvent {

	private static final long serialVersionUID = 5716012009199456969L;
	private final EventType type = EventType.LOOKUP_TABLE;
	private ChordKey original;
	private ChordNode destination;
	private int position;
	private String ip;
	
	public LookupTableEvent(ChordKey orig, ChordNode dest, int position, String ip){
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
		// TODO Auto-generated method stub
		return original;
	}

	@Override
	public ChordKey getDestination() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChordNode getNode() {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String getIP() {
		// TODO Auto-generated method stub
		return ip;
	}

	@Override
	public CountDownLatch getLatch() {
		// TODO Auto-generated method stub
		return null;
	}
}
