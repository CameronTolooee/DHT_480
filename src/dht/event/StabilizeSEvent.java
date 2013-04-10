package dht.event;

import java.io.Serializable;

import dht.chord.ChordNode;

public class StabilizeSEvent implements Serializable, DHTEvent{
	
	private static final long serialVersionUID = 8712131866756771586L;
	private final EventType type = EventType.STABLIZE_S;
	private ChordNode node;
	
	public StabilizeSEvent(ChordNode self){ // true is S
		node = self;
	}

	@Override
	public EventType getEventType() {
		return type;
	}

	@Override
	public String getIP() {
		return null;
	}

	public ChordNode getNode() {
		return node;
	}
}
