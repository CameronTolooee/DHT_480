package dht.event;

import java.io.Serializable;

import dht.chord.ChordKey;
import dht.chord.ChordNode;

public class SUpdateTableEvent implements DHTEvent, Serializable {

	private static final long serialVersionUID = -3420496378667765852L;
	
	EventType type = DHTEvent.EventType.UPDATE_TABLE;
	private String ip;
	
	public SUpdateTableEvent(String id) {
		ip = id;
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
		return ip;
	}

}
