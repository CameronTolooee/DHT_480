package dht.client;

import java.io.File;
import java.net.Socket;

import dht.chord.ChordNode;
import dht.event.DHTEvent;
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
			IO comm = new IO(new Socket(server,  ChordNode.PORT));
			File file = new File(fileName);
			DHTEvent store = new StorageEvent(file, "/tmp/muelooee/");
			comm.sendEvent(store);
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
