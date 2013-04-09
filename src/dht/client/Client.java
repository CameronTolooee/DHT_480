package dht.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import dht.chord.ChordKey;
import dht.chord.ChordNode;
import dht.event.DHTEvent;
import dht.event.FoundNodeEvent;
import dht.event.LookupEvent;
import dht.event.StorageEvent;
import dht.net.IO;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		client.store(args[0], args[1]);
	}
	
	private void store(String fileName, String server){
		try {
			ServerSocket ss = new ServerSocket(ChordNode.PORT);
			Socket sock = new Socket(server,  ChordNode.PORT);
			IO comm = new IO(sock);
			File file = new File(fileName);
			String myIP = InetAddress.getLocalHost().toString();
			System.out.println(myIP.substring(myIP.indexOf('/')+1));
			DHTEvent lookup = new LookupEvent(new ChordKey(file.hashCode()), server, myIP.substring(myIP.indexOf('/')+1));
			comm.sendEvent(lookup);
			Socket socket = ss.accept();
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			FoundNodeEvent found = (FoundNodeEvent) in.readObject();
			IO comm2 = new IO(new Socket(found.getIP(), ChordNode.PORT));
			StorageEvent store = new StorageEvent(file, "/tmp/muehlooee/");
			comm2.sendEvent(store);
		
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void storeDir(String dirName){
		
	}
	
	private void query(String fileName){
		
	}
	
	private void rangeBasedQuery(String start, String end){
		
	}
	
	private int count(){
		return 0;
	}
	
	private String findClosestNode(){
		return null;
	}
	
	// Methods for testing purposes only:
	
	private void getNodeInfo(){
		
	}
	
	

}
