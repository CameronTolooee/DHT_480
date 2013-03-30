package dht_event;

import chord.ChordKey;
import chord.ChordNode;
import sockets_layer.*;

public interface DHTEvent {
	 public enum EventType {
		 LOOKUP,
		 STORAGE,
		 JOIN,
		 RBQ_EVENT,
		 STABLIZE_P,
		 LEAVE,
		 SHUTDOWN,
		 FOUND_SUCCESSOR,
		 FOUND_NODE, 
		 STABLIZE_S,
		 LOOKUP_TABLE,
		 FOUND_TABLE
	 }
	 
	 public EventType getEventType();
	 // does this make sense??
	 public ChordKey getOriginal();
	 public ChordKey getDestination();
	 public ChordNode getNode();
	 int getPosition();
	 
}
