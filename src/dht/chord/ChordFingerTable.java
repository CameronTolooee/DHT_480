package dht.chord;

import java.io.Serializable;


public class ChordFingerTable implements Serializable{

	private static final long serialVersionUID = -8675399012403797889L;
	private ChordTableEntry[] table;
	
	public ChordFingerTable(String node) {
		table = new ChordTableEntry[ChordHash.TABLE_SIZE];
		for (int i = 0; i < table.length; ++i){
			table[i] = new ChordTableEntry(node, new ChordKey(node).getKey() + (1 << i));
		}
	}

	public ChordTableEntry getEntry(int i) {
		return table[i];
	}
	
	public void setEntry(ChordTableEntry entry, int i){
		table[i] = entry;
	}
	
	public int size() {
		return table.length;
	}
	
}
