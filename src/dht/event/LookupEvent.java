package dht.event;

import java.io.Serializable;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

public class LookupEvent implements DHTEvent, Serializable {

	private static final long serialVersionUID = 6225445613706140246L;
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
