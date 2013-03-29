package chord;


public class ChordKey {
	private int key;
	
	public ChordKey(String ip){
		this.key = ip.hashCode()%ChordHash.KEY_LENGTH;
	}
	
	public ChordKey(int key){
		this.key = key%ChordHash.KEY_LENGTH;
	}
	
	public boolean isBetween(int lower, int upper){
		boolean result = false;
		if (key > lower && key < upper) {
			result = true;
		} else if(lower > upper){
			if (key < upper || key > lower)
				result = true;
		}
		return result;
	}
	
	public boolean isBetween(ChordKey lower, ChordKey upper){
		return isBetween(lower.getKey(), upper.getKey());
	}
	
	public int getKey(){
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
}

