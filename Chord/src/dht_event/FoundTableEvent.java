package dht_event;

import java.io.Serializable;

import chord.ChordKey;
import chord.ChordNode;

public class FoundTableEvent implements Serializable, DHTEvent{

	private static final long serialVersionUID = 3102403291267394252L;
	private final EventType type = EventType.FOUND_TABLE;
	private ChordNode node;
	
	public FoundTableEvent(ChordNode node){
		this.node = node;
	}
	
	@Override
	public EventType getEventType() {
		// TODO Auto-generated method stub
		return type;
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
	public ChordNode getNode() {
		// TODO Auto-generated method stub
		return node;
	}

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

}
