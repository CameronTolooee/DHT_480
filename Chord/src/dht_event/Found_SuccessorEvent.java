package dht_event;

import chord.*;

public class Found_SuccessorEvent implements DHTEvent{

	private final EventType type = EventType.FOUND_SUCCESSOR;
	private final ChordNode node;
	
	public Found_SuccessorEvent(ChordNode node){
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
	
	public ChordNode getNode(){
		return this.node;
	}

}
