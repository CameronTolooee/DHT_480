package dht_event;

import chord.ChordKey;
import chord.ChordNode;

public class FoundNodeEvent implements DHTEvent{

	private final EventType type = EventType.FOUND_NODE;
	private ChordNode node;
	public FoundNodeEvent(ChordNode node){
		this.node = node;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}

	@Override
	public ChordKey getOriginal() {
		return null;
	}

	@Override
	public ChordKey getDestination() {
		return null;
	}

	@Override
	public ChordNode getNode() {
		return null;
	}

}
