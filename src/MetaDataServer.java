import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Scanner;

public class MetaDataServer extends Thread {
	// Treemaps are the same as hashMap but they are sorted
	private TreeMap<File, String> file_map; // map file to server
	private TreeMap<String, Integer> servers; // map server to num of files stored
	private Ring ring; // DHT ring overlay
	private int repLevel; // replication level TBD by client
	
	public MetaDataServer(int replicationLevel){
		repLevel = replicationLevel; 
		ring = new Ring();
		file_map = new TreeMap<File, String>();
		servers = new TreeMap<String, Integer>();
		retrieveServerList();
	}
	
	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Server.PORT);
			// Same as server should have a way to gracfully exit
			while (true) {
				Socket s = serverSocket.accept();
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	// get the list of servers from the config file.
	private void retrieveServerList() { 
		Scanner sc = null;
		try { 
			sc = new Scanner(new File("../conf/servers")); // Ultimately this should be done through environment variables to determine path instead of hard coded path.
			while (sc.hasNext()){
				servers.put(sc.next(), new Integer(0));				
			}
		} catch (Exception e){
			e.printStackTrace();	
		}
	}
	

	// hashes files to the DHT and finds location to store
	public ArrayList<String> store (String file){
		ArrayList<String> list = new ArrayList<String>();
		File f = new File(file);
		list.add(ring.assignFileToNode(f));
		int update = servers.get(list.get(0));
		servers.put(list.get(0), update+1);
		addReplicas(list);	
		return list;
	}

	// This is hard coded for PA1 don't use this after PA1
	// finds least populated servers to add a replicas to
	private void addReplicas(ArrayList<String> list){
		int cntr = 0;
		while(cntr < repLevel-1) {	
			String tmp = findLowest(list);	
			if (!list.contains(tmp)){
				list.add(tmp);
				++cntr;
				int update = servers.get(tmp);
				servers.put(tmp, update+1);
			}
		}
	}
	// helper for above method
	private String findLowest(ArrayList<String> ignore){
		int lowest = Integer.MAX_VALUE;
		String result = null;
		for (String s : servers.keySet()){
			if(servers.get(s) < lowest){
				if( !ignore.contains(servers.get(s))){
					result = s;
					lowest = servers.get(s);
				}
			}
		}
		
		return result;
	}
	// initially had this but I don't think its needed. Leaving it for now
	private void update(File f){
		// update info after storing/removing file
	}
	
	// Do this still
	public static String retreive(){
		
		return null;
		
	}

	// main for debugging purposes only
	public static void main(String args[]){
		MetaDataServer mds = new MetaDataServer(3);
		System.out.println("1: "+ mds.store("test.txt"));
		System.out.println("2: "+mds.store("servers"));
		System.out.println("3: "+mds.store("config.sh"));
		System.out.println("4: "+mds.store("killServers.sh"));
		System.out.println("2: "+mds.store("Makefile"));
		System.out.println("2: "+mds.store("Ring.class"));
		System.out.println("2: "+mds.store("Ring.java"));
	}
	
}
