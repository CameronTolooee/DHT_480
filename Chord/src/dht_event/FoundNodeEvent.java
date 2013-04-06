package dht_event;

import java.io.Serializable;

import chord.ChordKey;
import chord.ChordNode;

public class FoundNodeEvent implements DHTEvent, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6219821961439396765L;
	private final EventType type = EventType.FOUND_NODE;
	private ChordNode node;
	public FoundNodeEvent(ChordNode node){
		this.node = node;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}


	public ChordNode getNode() {
		return node;
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

}

