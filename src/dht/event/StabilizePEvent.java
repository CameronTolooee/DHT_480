package dht.event;

import java.io.Serializable;

import dht.chord.ChordNode;

public class StabilizePEvent implements Serializable, DHTEvent{

	private static final long serialVersionUID = -8004845506112142445L;
	private ChordNode node;
	private final EventType type = EventType.STABLIZE_P;  
	
	public StabilizePEvent(ChordNode node){
		this.node = node;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}
	
	@Override
	public String getIP() {
		return null;
	}
	
	public ChordNode getNode(){
		return node;
	}
}
