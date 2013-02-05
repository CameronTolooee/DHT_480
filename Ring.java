import java.io.File;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Comparator;
import java.util.HashMap;


public class Ring {
	
	private TreeMap<Integer, String> mappings;
	private int ringSize;
	
	public Ring(){

        mappings = new TreeMap<Integer,String>();
		// --------
		ringSize = (1 << 7)-1;
		Scanner sc = null;
		try { 
			sc = new Scanner(new File("servers"));
			while (sc.hasNext()){
				String ip = sc.next();
				mappings.put( (ip.hashCode()) %ringSize, ip);
			}
		} catch (Exception e){
			e.printStackTrace();	
		}
		
	}

	public String assignFileToNode(File f){
		int position = Math.abs(f.hashCode() % ringSize);
		String result = mappings.get(mappings.firstKey());
		for (Integer i : mappings.keySet()){
			if(position > i){
				result = mappings.get(i);
			}
		}
		
		return result;
	}
	
	public static void main(String args[]){
		Ring r = new Ring();
		System.out.println("Ring Size: "+r.ringSize);
		File f = new File("servers");
		System.out.println("DF" + f.hashCode());
		System.out.println("Assigned to: "+r.assignFileToNode(f));
				
	}	
	
}

