package dht.chord;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dht.event.DHTEvent;
import dht.event.DHTEvent.EventType;
import dht.event.DHTEventHandler;
import dht.event.JoinEvent;
import dht.event.LookupEvent;
import dht.event.LookupTableEvent;
import dht.event.StabilizeSEvent;
import dht.net.IO;


public class ChordNode implements Serializable{

	private static final long serialVersionUID = 6648930404036643565L;
	public static final int PORT = 1884;
	public static CountDownLatch latch = new CountDownLatch(1);
	
	private String id;
	private ChordKey key;
	private ChordNode predecessor;
	private ChordNode successor;
	private ChordFingerTable fingerTable;

	public ChordNode(String nodeId) {
		this.id = nodeId;
		this.key = new ChordKey(id.hashCode());
		this.fingerTable = new ChordFingerTable(this.getId());
		predecessor = null;
		successor = this;
	}

	@SuppressWarnings("unused") // Will use this for adding a join function if we get to it
	private void join(ChordNode destNode) {
		predecessor = null;
		destNode.findSuccessor(this.getKey(), EventType.JOIN, 0, null, null);
		if(this != successor){
			DHTEvent stabilizeEventS = new StabilizeSEvent(this);
			try {
				IO comm = new IO(new Socket(successor.getId(), PORT));
				comm.sendEvent(stabilizeEventS);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		destNode.updateTable(successor);
	}
	
	private void findSuccessor(ChordKey target_key, EventType type, int position, String ip, CountDownLatch latch) {
		try{
			String node = largestPrecedingNode(target_key);
			DHTEvent event = null; 
			if (type == EventType.LOOKUP){
				event = new LookupEvent(target_key, node, ip);
			} else if (type == EventType.JOIN){
				event = new JoinEvent(target_key, node, ip); // changed
			}else {
				event = new LookupTableEvent(target_key, node, position, ip);
			}
			if (node.equals(this.getId())) {
				Socket socket = new Socket(successor.getId(), PORT);
				IO communicator = new IO(socket);
				communicator.sendEvent(event);
			} else{
				Socket socket = new Socket(node, PORT);
				IO communicator = new IO(socket);
				communicator.sendEvent(event);
			}
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void lookupT(ChordKey key, int position, String ip){
		this.findSuccessor(key, EventType.LOOKUP_TABLE, position, ip, null);
	}
	
	public void lookupL(ChordKey key, String ip){
		this.findSuccessor(key, EventType.LOOKUP, 0, ip, null);
	}
	
	public void lookupN(ChordKey key, String ip){
		this.findSuccessor(key, EventType.JOIN, 0, ip, null);
	}

	// Tables MUST be up-to-date before this is invoked!
	private String largestPrecedingNode(ChordKey target_key) {
		for (int i = ChordHash.TABLE_SIZE - 1; i >= 0; i--) {
			ChordTableEntry entry = fingerTable.getEntry(i);
			ChordKey table_key = new ChordKey(entry.getNode());
			if (table_key.isBetween(this.getKey(), target_key)) {
				return entry.getNode();
			}
		}
		return this.getId();
	}

	/**
	 * Refreshes finger table entries.
	 */
	public void updateTable(ChordNode node) {
		for (int i = 0; i < fingerTable.size(); ++i){
			try{
				ChordTableEntry entry = fingerTable.getEntry(i);
				DHTEvent LTevent = new LookupTableEvent(new ChordKey(entry.getPosition()), this.getId(), i, this.id);
				//System.out.println("WHICH ONE: "+ getId()+" OR "+successor.getId());
				IO comm = new IO(new Socket(successor.getId(), PORT));
				comm.sendEvent(LTevent);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

	public void printFingerTable() {
		System.out.println("=======================================================");
		System.out.println("FingerTable: " + this + " --> position: " + key.getKey());
		System.out.println("-------------------------------------------------------");
		System.out.println("Predecessor: " + predecessor);
		System.out.println("Successor: " + successor);
		System.out.println("-------------------------------------------------------");
		System.out.println("i    position          id");
		System.out.println("-------------------------------------------------------");
		for (int i = 0; i < fingerTable.size(); i++) {
			ChordTableEntry entry = fingerTable.getEntry(i);
			System.out.println(i + "\t " + entry.getPosition() + "\t    " + entry.getNode());
		}
		System.out.println("=======================================================");
	}

	public String toString() {
		return id;
	}

	public ChordKey getKey() {
		return key;
	}

	public String getId() {
		return id;
	}

	public ChordNode getPredecessor() {
		return predecessor;
	}
	
	public void setPredecessor(ChordNode node){
		predecessor = node;
	}

	public ChordNode getSuccessor() {
		return successor;
	}
	
	public void setSuccessor(ChordNode node) {
		successor = node;
	}
	
	public ChordFingerTable getTable(){
		return fingerTable;
	}

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		ChordNode node = null;
		try {
			String ip = InetAddress.getLocalHost().toString();
			node = new ChordNode(ip.substring(ip.indexOf('/')+1));
			serverSocket = new ServerSocket(PORT);
			boolean joining = (args.length == 1) ? true : false;
			System.out.println("Created Server socket. Listening..");
			boolean first = true;
			ExecutorService threadPool = Executors.newFixedThreadPool(15);
			while (true) {
				if(joining) {
					threadPool.execute(new Thread(new ChordJoiningThread(args[0], node)));
					threadPool.execute(new Thread(new ChordStabilizeThread(node)));
					joining = false;
				} 
				if (first && !joining){ // for first node only set pred and succ to itself for stabilzaitons
					node.setPredecessor(node);
					node.setSuccessor(node);
					first = false;
				}
				Socket s = serverSocket.accept();
				threadPool.execute(new Thread(new DHTEventHandler(s, node)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
