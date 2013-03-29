package dht_event;

import chord.ChordKey;
import chord.ChordNode;

public class JoinEvent implements DHTEvent{
	public final EventType type = EventType.JOIN;
	private ChordKey original;
	private ChordKey destination; 
	
	public JoinEvent(ChordKey orig, ChordKey dest){
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
