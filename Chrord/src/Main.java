import java.util.ArrayList;
import java.util.Random;


public class Main {
	public static final String HASH_FUNCTION = "SHA-1";

	public static final int KEY_LENGTH = 8;
	public static final int NUM_OF_NODES = 4;

	public static void main(String[] args) throws Exception {


		ChordHash.setFunction(HASH_FUNCTION);
		ChordHash.setKeyLength(KEY_LENGTH);
		ArrayList<ChordNode> chord = new ArrayList<ChordNode>();
		
		chord.add(new ChordNode("192.168.0.5"));
		chord.add(new ChordNode("10.168.0.15"));
		chord.add(new ChordNode("225.225.225.1"));
		chord.add(new ChordNode("123456"));

		
		System.out.println(NUM_OF_NODES + " nodes are created.");

		for (int i = 1; i < NUM_OF_NODES; i++) {
			ChordNode node = chord.get(i);
			node.join(chord.get(0));			
		}
		System.out.println("Chord ring is established.");
		
		for (int i = 0; i < NUM_OF_NODES; i++) {
			chord.get(i).updateTable(chord.get(i));
		}
		System.out.println("Finger tables are updated");
		for (int i = 0; i < NUM_OF_NODES; i++) {
			chord.get(i).printFingerTable();
		}
		System.out.println("1234567".hashCode()%ChordHash.KEY_LENGTH);
		System.out.println(chord.get(0).lookup("1234567"));
	}

}
