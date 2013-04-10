package dht.event;

import java.io.Serializable;

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
	public String getIP() {
		return null;
	}
	
}
