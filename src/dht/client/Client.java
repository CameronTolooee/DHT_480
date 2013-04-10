package dht.client;

import java.io.File;
import java.io.IOException;
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

	private String ip; 
	
	public Client(){
		try {
			ip = InetAddress.getLocalHost().toString();
			ip = ip.substring(ip.indexOf('/')+1);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param String absolute path to file
	 * 		  String cluster server
	 */
	public static void main(String[] args) {
		if(args.length < 1)
			usage();
		Client client = new Client();
		switch (args[0]){
		case "store" : 
			if (args.length == 3)
				client.store(args[1], args[2]);
			else 
				usage();
			break;
		case "query": 
			if (args.length == 3)
				client.query(args[1], args[2]);
			else 
				usage();
			break;
		}
	}
	
	private static void usage(){
		System.out.println("Invalid params, make this a better message later..");
	}
	
	private void store(String fileName, String server){
		ServerSocket ss = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket sock = null;
		try {
			ss = new ServerSocket(ChordNode.PORT); // ss to listen for responce from cluster after lookup
			sock = new Socket(server,  ChordNode.PORT); // connection to the server specified in args
			IO comm = new IO(sock); 
			
			File file = new File(fileName);
			DHTEvent lookup = new LookupEvent(new ChordKey(file.hashCode()), server, ip);
			comm.sendEvent(lookup);
			
			Socket socket = ss.accept();
			
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			FoundNodeEvent found = (FoundNodeEvent) in.readObject();
			IO comm2 = new IO(new Socket(found.getIP(), ChordNode.PORT));
			StorageEvent store = new StorageEvent(file, "/tmp/muehlooee/");
			comm2.sendEvent(store);
		}catch (Exception e){
			e.printStackTrace();
		} finally { // cleanup
			try {
				ss.close();
				out.close();
				in.close();
				sock.close();
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
	}

	private void storeDir(String dirName){
		
	}
	
	private void query(String fileName, String server){
		
	}
	
	private void rangeBasedQuery(String start, String end){
		
	}
	
	private int count(){
		return 0;
	}
	
	private String findClosestNode(){
		return null;
	}
}
