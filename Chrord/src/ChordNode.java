import java.io.File;


public class ChordNode {
	private String id;
	private ChordKey key;
	private ChordNode predecessor;
	private ChordNode successor;
	private ChordFingerTable fingerTable;

	public ChordNode(String nodeId) {
		this.id = nodeId;
		this.key = new ChordKey(Math.abs(id.hashCode()));
		this.fingerTable = new ChordFingerTable(this);
		predecessor = null;
		successor = this;
	}

	public void join(ChordNode node) {
		predecessor = null;
		successor = node.findSuccessor(this.getKey());
		stabilize();
		node.updateTable(successor);
	}

	public ChordNode findSuccessor(ChordKey target_key) {
		// if node is its own successor return itself
		if (this == successor) {
			return this;
		}
		// if target is between this and successor then return successor or they are equal
		if (target_key.isBetween(this.key, successor.getKey()) || target_key.getKey() == successor.getKey().getKey()) {
			return successor;
		} else { // otherwise get the largest node st the key is in range
			ChordNode node = largestPrecedingNode(target_key);
			if (node == this) {
				return successor.findSuccessor(target_key);
			}
			return node.findSuccessor(target_key);
		}
	}
	
	public String lookup(ChordKey key){
		ChordNode succ = this.findSuccessor(key);
		return succ.getId();
	}
	
	public String lookup(File file){
		return lookup(new ChordKey(file.hashCode()));
	}

	public String lookup(String filename){
		return lookup(new ChordKey(filename.hashCode()));
	}
	
	private ChordNode largestPrecedingNode(ChordKey target_key) {
		for (int i = ChordHash.TABLE_SIZE - 1; i >= 0; i--) {
			ChordTableEntry entry = fingerTable.getEntry(i);
			ChordKey table_key = entry.getNode().getKey();
			if (table_key.isBetween(this.getKey(), target_key)) {
				return entry.getNode();
			}
		}
		return this;
	}

	/**
	 * Verifies the successor, and tells the successor about this node. Should be called periodically.
	 */
	public void stabilize() {
		this.predecessor = successor.predecessor;
		if (this.predecessor == null) {
			this.predecessor = successor;
		}
		this.successor.predecessor = this;
		this.predecessor.successor = this;
	}

	/**
	 * Refreshes finger table entries.
	 */
	public void updateTable(ChordNode node) {
		for (int i = 0; i < fingerTable.size(); i++) {
			ChordTableEntry entry = fingerTable.getEntry(i);
			ChordNode corr = findNextTableEntry(entry.getPosition(), node, node);
			entry.setNode(corr);
		}
	}

	private ChordNode findNextTableEntry(int pos, ChordNode node, ChordNode orig) {
		ChordKey i = new ChordKey(pos);
		if (i.isBetween(orig.getKey(), node.getKey()) || pos == node.getKey().getKey()) {
			return node;
		} else {
			ChordNode corr = findNextTableEntry(pos, node.successor, orig);
			return corr;
		}
	}

	public void printFingerTable() {
		System.out.println("=======================================================");
		System.out.println("FingerTable: " + this + " --> position: " + key.getKey());
		System.out.println("-------------------------------------------------------");
		System.out.println("Predecessor: " + predecessor);
		System.out.println("Successor: " + successor);
		System.out.println("-------------------------------------------------------");
		System.out.println("i    position          id");
		System.out.println("-------------------------------------------------------");
		for (int i = 0; i < fingerTable.size(); i++) {
			ChordTableEntry entry = fingerTable.getEntry(i);
			System.out.println(i + "\t " + entry.getPosition() + "\t    " + entry.getNode());
		}
		System.out.println("=======================================================");
	}

	public String toString() {
		return id;
	}

	public ChordKey getKey() {
		return key;
	}

	public String getId() {
		return id;
	}

	public ChordNode getPredecessor() {
		return predecessor;
	}

	public ChordNode getSuccessor() {
		return successor;
	}
}
