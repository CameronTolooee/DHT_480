package dht.event;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordKey;
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

	public ChordNode getNode() {
		return node;
	}

	@Override
	public ChordKey getOriginal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDestination() {
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

	@Override
	public CountDownLatch getLatch() {
		// TODO Auto-generated method stub
		return null;
	}


}
