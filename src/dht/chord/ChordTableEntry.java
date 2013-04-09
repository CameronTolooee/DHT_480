package dht.chord;

import java.io.Serializable;


public class ChordTableEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4452611836805577436L;
	private String node;
	private int position;

	public ChordTableEntry(String node, int position) {
		this.node = node;
		this.position = position % ChordHash.KEY_LENGTH;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public String toString(){
		return node+" "+ position;
	}

}
