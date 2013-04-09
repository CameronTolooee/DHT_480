package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

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
	
	public ChordKey getOriginal(){
		return original;
	}
	
	public String getDestination(){
		return destination;
	}

	@Override
	public ChordNode getNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getIP() {
		return ip;
	}

	@Override
	public CountDownLatch getLatch() {
		// TODO Auto-generated method stub
		return null;
	}


}
