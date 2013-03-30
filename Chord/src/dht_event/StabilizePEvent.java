package dht_event;

import java.io.Serializable;

import chord.ChordKey;
import chord.ChordNode;

public class StabilizePEvent implements Serializable, DHTEvent{

	/**
	 * 
	 */
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
	
	public ChordNode getNode(){
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

}