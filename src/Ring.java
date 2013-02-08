import java.io.File;
import java.util.TreeMap;
import java.util.Scanner;


public class Ring {
	
	private TreeMap<Integer, String> mappings; // Sorted is convinent later.
	private int ringSize;
	
	public Ring(){

        mappings = new TreeMap<Integer,String>();
		// --------
		ringSize = (1 << 7)-1; // keyspace = 2^7 --> bit shifting is neat!
		Scanner sc = null;
		try { 
			sc = new Scanner(new File("../conf/servers")); // env variables again
			// Hash the ip of servers to the ring to
			while (sc.hasNext()){
				String ip = sc.next();
				mappings.put( (ip.hashCode()) %ringSize, ip); // stores (ring_position, ip)
			}
		} catch (Exception e){
			e.printStackTrace();	
		}
		
	}

	// Takes a file and hashes it to the ring, assigns the file a node based on position on ring
	public String assignFileToNode(File f){
		int position = Math.abs(f.hashCode() % ringSize); // apparently hashCode can be negative?
		String result = mappings.get(mappings.firstKey());
		// loop through each server position around the ring until correct position is found.
		for (Integer i : mappings.keySet()){
			if(position > i){
				result = mappings.get(i);
			}
		}
		
		return result;
	}
	
	// main for debugging purposes only
	public static void main(String args[]){
		Ring r = new Ring();
		System.out.println("Ring Size: "+r.ringSize);
		File f = new File("servers");
		System.out.println("DF" + f.hashCode());
		System.out.println("Assigned to: "+r.assignFileToNode(f));
				
	}	
	
}
