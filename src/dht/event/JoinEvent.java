package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

public class JoinEvent implements DHTEvent, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4876774787291560722L;
	public final EventType type = EventType.JOIN;
	private ChordKey original;
	private ChordKey destination;
	private String ip;
	
	public JoinEvent(ChordKey orig, ChordKey dest, String ip){
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
	
	public ChordKey getDestination(){
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
		// TODO Auto-generated method stub
		return ip;
	}
	
	@Override
	public CountDownLatch getLatch(){
		return null;
	}
}
