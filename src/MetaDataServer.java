import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Scanner;

public class MetaDataServer extends Thread {
	// Treemaps are the same as hashMap but they are sorted
	private TreeMap<File, String> file_map; // map file to server
	private TreeMap<String, Integer> servers; // map server to num of files stored
	private Ring ring; // DHT ring overlay
	private int repLevel; // replication level TBD by client
	private String homeDir;

	public MetaDataServer(String homeDir, int replicationLevel) {
		this.homeDir = homeDir;
		repLevel = replicationLevel;
		ring = new Ring(homeDir);
		file_map = new TreeMap<File, String>();
		servers = new TreeMap<String, Integer>();
		retrieveServerList();
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Server.PORT);
			// Same as server should have a way to gracefully exit
			while (true) {
				Socket s = serverSocket.accept();
				getCmd(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getCmd(Socket socket) throws Exception {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			// First thing sent is the file name
			Object o = ois.readObject();
			if (o instanceof String) {
				if (o.equals("store")) {
					System.out.println("Meta: recieved store cmd :"+o.toString());
					o = ois.readObject();
					if (o instanceof String){
						ArrayList<String> result = store(o.toString());
						oos.writeObject(result);
					}
					// store stuff
				} else if (o.equals("get")) {
					// get stuff
				} else if (o.equals("status")) {
					// print status of servers
					oos.writeObject(status());
				} else {
					throw new Exception(o + " is not a valid command. Commands are \"store\" and \"get\"");
				}

			} else {
				throw new Exception("Expected a String (the command) from stream but got: " + o.getClass());
			}
		} finally { // cleanup
			oos.close();
			ois.close();
		}
	}

	private String status(){
		System.out.println("FDF: "+file_map);
		String result = "";
		result += ("*-----------------------------------*\n");
		result += String.format("|  %-20s%-13s|\n", "ip", "# of files");
		result += ("*-----------------------------------*\n");
		for (String s: servers.keySet()){
				result += String.format("| %-27s%-7d|\n", s, servers.get(s));
		}
		result += ("*-----------------------------------*\n");
		return result;
	}

	// get the list of servers from the config file.
	private void retrieveServerList() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(homeDir+"/conf/servers")); // Ultimately this should be done through environment
			if(sc.hasNext())
				sc.next(); // remove metanode
			while (sc.hasNext()) { // variables to determine path instead of hard coded path.
				servers.put(sc.next(), new Integer(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}
	}

	// hashes files to the DHT and finds location to store
	public ArrayList<String> store(String file) {
		ArrayList<String> list = new ArrayList<String>();
		File f = new File(file);
		list.add(ring.assignFileToNode(f));
		int update = servers.get(list.get(0));
		servers.put(list.get(0), update + 1);
		addReplicas(list);
		return list;
	}

	// This is hard coded for PA1 don't use this after PA1
	// finds least populated servers to add a replicas to
	private void addReplicas(ArrayList<String> list) {
		int cntr = 0;
		while (cntr < repLevel - 1) {
			String tmp = findLowest(list);
			if (!list.contains(tmp)) {
				list.add(tmp);
				++cntr;
				int update = servers.get(tmp);
				servers.put(tmp, update + 1);
			}
		}
	}

	// helper for above method
	private String findLowest(ArrayList<String> ignore) {
		int lowest = Integer.MAX_VALUE;
		String result = null;
		for (String s : servers.keySet()) {
			if (servers.get(s) < lowest) {
				if (!ignore.contains(servers.get(s))) {
					result = s;
					lowest = servers.get(s);
				}
			}
		}

		return result;
	}


	@SuppressWarnings("unused")
	private void update(File f) {
		// update info after storing/removing file
		System.out.println("TODO");
	}

	// Do this still
	public static String retreive() {

		return null;

	}

	public static void main(String args[]) {
		if (args.length != 2){
			System.out.println("Usage: MetaDataServer [path] [replication_level]");
			System.exit(1);
		}
		new MetaDataServer(args[0], Integer.parseInt(args[1])).start();
	}
}
