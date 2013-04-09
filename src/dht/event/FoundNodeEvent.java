package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

public class FoundNodeEvent implements DHTEvent, Serializable{

	/**
	 * 
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


	public ChordNode getNode() {
		return null;
	}

	@Override
	public ChordKey getOriginal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDestination() {
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
		return node;
	}

	@Override
	public CountDownLatch getLatch() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getClientIP(){
		return clientIP;
	}

}

