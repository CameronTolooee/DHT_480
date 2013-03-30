package chord;

import java.io.Serializable;


public class ChordTableEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4452611836805577436L;
	private ChordNode node;
	private int position;

	public ChordTableEntry(ChordNode node, int position) {
		this.node = node;
		this.position = position % ChordHash.KEY_LENGTH;
	}

	public ChordNode getNode() {
		return node;
	}

	public void setNode(ChordNode node) {
		this.node = node;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
