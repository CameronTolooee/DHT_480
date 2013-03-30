package dht_event;

import java.io.Serializable;

import chord.ChordKey;
import chord.ChordNode;

public class LookupTableEvent implements Serializable, DHTEvent {

	private static final long serialVersionUID = 5716012009199456969L;
	private final EventType type = EventType.LOOKUP_TABLE;
	private int position;
	
	public LookupTableEvent(int position){
		this.position = position;
	}
	
	@Override
	public EventType getEventType() {
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
		return null;
	}
	
	@Override
	public int getPosition(){
		return position;
	}

}
