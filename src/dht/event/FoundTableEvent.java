package dht.event;

import java.io.Serializable;

public class FoundTableEvent implements Serializable, DHTEvent{

	private static final long serialVersionUID = 3102403291267394252L;
	private final EventType type = EventType.FOUND_TABLE;
	private String node;
	private int tablePos;
	
	public FoundTableEvent(String node, int tablePos){
		this.node = node;
		this.tablePos = tablePos;
	}
	
	@Override
	public EventType getEventType() {
		return type;
	}

	@Override
	public String getIP() {
		return node;
	}

	public int getPosition() {
		return tablePos;
	}
}
