import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class MetaDataServer implements Runnable {
	// Treemaps are the same as hashMap but they are sorted
	private static TreeMap<String, ArrayList<String>> file_map; // map file to server
	private static TreeMap<String, Integer> servers; // map server to num of files stored
	private Ring ring; // DHT ring overlay
	private static int repLevel = 3; // replication level TBD by client
	private String homeDir;
	public static String name = "homer.cs.colostate.edu";
	private Socket clientSocket;

	public MetaDataServer(String homeDir, int replicationLevel, Socket s) {
		this.homeDir = homeDir;
		repLevel = replicationLevel;
		clientSocket = s;
		ring = new Ring(homeDir);
		file_map = new TreeMap<String, ArrayList<String>>();
		servers = new TreeMap<String, Integer>();
		retrieveServerList();
	}

	public MetaDataServer(String homeDir, int replicationLevel) {
		this.homeDir = homeDir;
		repLevel = replicationLevel;
		ring = new Ring(homeDir);
		file_map = new TreeMap<String, ArrayList<String>>();
		servers = new TreeMap<String, Integer>();
		retrieveServerList();
	}

	@Override
	public void run() {
		try {
			getCmd(clientSocket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getCmd(Socket socket) throws Exception {
		pulse();
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			// First thing sent is the file name
			Object o = ois.readObject();
			if (o instanceof String) {
				if (o.equals("store")) {
					System.out.println("Meta: recieved store cmd: " + o.toString());
					o = ois.readObject();
					if (o instanceof String) {
						ArrayList<String> result = store(o.toString());
						oos.writeObject(result);
					}
					// store stuff
				} else if (o.equals("get")) {
					System.out.println("Meta: recieved get cmd: " + o.toString());
					o = ois.readObject();
					if (o instanceof String){
						String filename = (String) o;
						oos.writeObject(this.retreive(filename));
					}
					// get stuff
				} else if (o.equals("status")) {
					System.out.println("Meta: recieved store status: " + o.toString());

					// print status of servers
					oos.writeObject(status());
				} else if (o.equals("update")) {
					System.out.println("Meta: recieved store update: " + o.toString());

					update(ois, socket.getInetAddress().getHostName());
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

	private void pulse() { // This only checks network connection but not server connection..
		boolean reachable = false;
		for (String server : servers.keySet()) {
			try {
				reachable = InetAddress.getByName(server).isReachable(100);
				if (!reachable) {
					System.out.println(server + " is down.");
				} //else
					//System.out.println("pusle: " + server + " is up");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String status() {
		String result = "";
		synchronized(MetaDataServer.class){
			result += ("*-----------------------------------*\n");
			result += String.format("|  %-20s%-13s|\n", "ip", "# of files");
			result += ("*-----------------------------------*\n");
			for (String s : servers.keySet()) {
				result += String.format("| %-27s%-7d|\n", s, servers.get(s));
			}
			result += ("*-----------------------------------*\n");
		}
		return result;
	}

	// get the list of servers from the config file.
	private synchronized void retrieveServerList() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(homeDir + "/conf/servers")); // Ultimately this should be done through environment
			if (sc.hasNext())
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
				//int update = servers.get(tmp);
				//servers.put(tmp, update + 1);
			}
		}
	}

	// helper for above method
	private String findLowest(ArrayList<String> ignore) {
		int lowest = Integer.MAX_VALUE;
		String result = null;
		for (String s : servers.keySet()) {
			if (servers.get(s) < lowest) {
				if (!ignore.contains(s)) {
					result = s;
					lowest = servers.get(s);
				}
			}
		}

		return result;
	}

	private static void update(ObjectInputStream ois, String host) throws Exception {
		// update info after storing/removing file
		Object o = ois.readObject();
		String fileName = null;
		if (o instanceof String)
			fileName = (String) o;
		boolean success = (Boolean) ois.readObject();
		System.out.println("UPDATING: " + host);
		if (success) {
			if (servers.containsKey(host)) {
					servers.put(host, servers.get(host) + 1);
			}
			if (file_map.containsKey(fileName)) {
				file_map.get(fileName).add(host);
			} else {
				ArrayList<String> newEntry = new ArrayList<String>();
				newEntry.add(host);
				file_map.put(fileName, newEntry);
			}
		} else {
			System.out.println("Failed to write");
		}
	}
		// Do this still
	private ArrayList<String> retreive(String filename) {
		return file_map.get(filename);
	}
	
	public static synchronized int getRep(){
		return repLevel;
	}

	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("Usage: MetaDataServer [path] [replication_level]");
			System.exit(1);
		}
		ServerSocket serverSocket = null;

		try {
			servers = new TreeMap<String, Integer>();
			file_map = new TreeMap<String, ArrayList<String>>();
			repLevel = 3;
			
			serverSocket = new ServerSocket(Server.PORT);
			MetaDataServer mds = new MetaDataServer(args[0], Integer.parseInt(args[1]));
			// Same as server should have a way to gracefully exit
			while (true) {
				Socket s = serverSocket.accept();
				mds.clientSocket = s;
				new Thread(mds).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
