package dht_event;

import chord.ChordKey;
import chord.ChordNode;
import dht_event.DHTEvent.EventType;

public class LookupEvent implements DHTEvent {

	private final EventType type = EventType.LOOKUP;
	private ChordKey original;
	private ChordKey destination; 
	
	public LookupEvent(ChordKey orig, ChordKey dest){
		original = orig;
		destination = dest;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}
	
	public ChordKey getOriginal(){
		return original;
	}
	
	public ChordKey getDestination(){
		return destination;
	}

	@Override
	public ChordNode getNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
