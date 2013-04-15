package dht.event;

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
		 FOUND_TABLE,
		 UPDATE_TABLE, QUERY
	 }
	 
	 public EventType getEventType();
	 public String getIP();
	 
}
