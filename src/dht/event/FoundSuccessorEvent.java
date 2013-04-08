package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
import dht.chord.ChordNode;



public class FoundSuccessorEvent implements DHTEvent, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7560501059102424896L;
	private final EventType type = EventType.FOUND_SUCCESSOR;
	private final ChordNode node;
	
	public FoundSuccessorEvent(ChordNode node){
		this.node = node;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}

	public ChordNode getNode(){
		return this.node;
	}

	@Override
	public ChordKey getOriginal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChordKey getDestination() {
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
		return null;
	}

	@Override
	public CountDownLatch getLatch() {
		// TODO Auto-generated method stub
		return null;
	}
}
