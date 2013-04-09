package dht.event;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import dht.chord.ChordHash;
import dht.chord.ChordKey;
import dht.chord.ChordNode;
import dht.chord.ChordTableEntry;
import dht.event.DHTEvent.EventType;
import dht.net.IO;

public class DHTEventHandler implements Runnable {
	private DHTEvent event;
	private ChordNode node;
	private Socket sock;

	public DHTEventHandler(Socket sock, ChordNode node) {
		this.sock = sock;
		IO comm = new IO(sock);
		event = comm.getEvent();
		System.out.println(event.getEventType());
		if(event.getEventType() == EventType.STORAGE ){
			System.out.println("storing shit");
			comm.receiveFile();
		}else {
			System.out.println("["+new Date(System.currentTimeMillis()).toString()+"] recieved "+event.getEventType());
			this.node = node;
		}
	}

	@Override
	public void run() {
		if(event != null) {
			switch (event.getEventType()) {
			case LOOKUP:  System.out.println("handler: lookup"); lookup();
				break;
			case STORAGE: System.out.println("handler: storage"); 
				break;
			case JOIN: System.out.println("handler: joining"); join();
				break;
			case RBQ_EVENT:
				break;
			case LOOKUP_TABLE: System.out.println("handler: LOOKUP table"); lookupTable();
				break;
			case STABLIZE_S: System.out.println("handler: stabilizing succ: "); stabilizeS();
				break;
			case STABLIZE_P: System.out.println("handler: stabilizing pred: "); stabilizeP();
				break;
			case LEAVE:
				break;
			case SHUTDOWN:
				break;
			case FOUND_SUCCESSOR: System.out.println("handler: found_successor"); found_successor();
				break;
			case FOUND_NODE: System.out.println("handler: found node"); foundNode();
				break;
			case FOUND_TABLE: System.out.println("handler: found table"); foundTable();
				break;
			case UPDATE_TABLE: System.out.println("handler: update table"); updateTable();
			default: 
		}

		}

	}
	
	private void updateTable() {
		try{
			if(event.getIP().equals(node.getId())){
				System.out.println("DONE =)");
				ChordNode.latch.countDown();
			}else{
				ChordNode.latch = new CountDownLatch(ChordHash.TABLE_SIZE);
				node.updateTable(node);
				ChordNode.latch.await();
				DHTEvent updateTable = new SUpdateTableEvent(event.getIP());
				IO comm = new IO(new Socket(node.getSuccessor().getId(), ChordNode.PORT));
				comm.sendEvent(updateTable);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	private void lookupTable() {
		try {
			System.out.println("NODE ID: "+node.getId());
			if(event.getOriginal().getKey() == (new ChordKey(event.getDestination()).getKey())) {
				// I am myself durp durp durp
	//			FoundTableEvent foundEvent = new FoundTableEvent(node);
	//			comm.sendEvent(foundEvent);
	//			System.out.println("Sent found table event");
				DHTEvent foundEvent = new FoundTableEvent(event.getIP(), event.getPosition());
				IO comm = new IO(new Socket(event.getIP(), ChordNode.PORT));
				comm.sendEvent(foundEvent);
				System.out.println("Sent Table Found event 1");
			} else if(event.getOriginal().isBetween(new ChordKey(event.getDestination()), node.getKey()) || event.getOriginal().getKey() == node.getKey().getKey()) {
				// Between me and successor or successor = successor
				//FoundTableEvent foundEvent = new FoundTableEvent(node.getSuccessor());
				//comm.sendEvent(foundEvent);
				//System.out.println("Sent found table event");
				DHTEvent foundEvent = new FoundTableEvent(node.getId(), event.getPosition());
				IO comm = new IO(new Socket(event.getIP(), ChordNode.PORT));
				comm.sendEvent(foundEvent);
				System.out.println("Sent Table Found event 2 from "+node.getId()+" to "+event.getIP());
			} else {
				// look up in table and create another joinEvent and go around in circles
				System.out.println("Looking in table...");
				node.lookupT(event.getOriginal(), event.getPosition(), event.getIP());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	private void foundTable() {
		int tablePos = (1 << event.getPosition()) + node.getKey().getKey();
		node.getTable().setEntry(new ChordTableEntry(event.getIP(), tablePos), event.getPosition());
		ChordNode.latch.countDown();
//		System.out.println("__________________________________________________");
//		System.out.println(event.getNode().getId()+" "+event.getPosition());
//		System.out.println("__________________________________________________");
		System.out.println("Set table entry");
	}

	private void foundNode(){
		try{
			IO comm = new IO(new Socket(event.getIP(), ChordNode.PORT));
			System.out.println("DO I GET HERE???");
		} catch( Exception e){
			e.printStackTrace();
		}
	}
	
	private void stabilizeS() {
		DHTEvent eventP  = new StabilizePEvent(node.getPredecessor()); 
		if(node.getSuccessor().getId().equals(node.getId())){
			node.setSuccessor(event.getNode());
		}
		node.setPredecessor(event.getNode());
		System.out.println("LOOK AT ME@@@@#$$N "+node.getPredecessor());
		try {
			IO comm2 = new IO(new Socket(event.getNode().getId(), ChordNode.PORT));
			comm2.sendEvent(eventP);
			System.out.println("Sent stabilization event 2");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void stabilizeP(){
		try{
		node.setPredecessor(event.getNode());
		DHTEvent foundSucc = new FoundSuccessorEvent(node);
		IO comm78 = new IO(new Socket(event.getNode().getId(), ChordNode.PORT));
		comm78.sendEvent(foundSucc);
		System.out.println("STABLIZED BITCHES");
		System.out.println(node.getId()+" S:"+ node.getSuccessor());
		System.out.println(node.getId()+" P:"+ node.getPredecessor());
		System.out.println(node.getPredecessor().getId()+" Succ:"+ node.getPredecessor().getSuccessor());
		System.out.println(node.getPredecessor().getId()+" Pred:"+ node.getPredecessor().getPredecessor());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void lookup() {
		try {
			System.out.println("OPOPOP "+ event.getIP());
			IO comm = new IO(new Socket(event.getIP(), ChordNode.PORT));
			if(node.getId().equals(node.getSuccessor().getId())) {
				// I am myself durp durp durp
				FoundNodeEvent foundEvent = new FoundNodeEvent(node.getId(), event.getIP());
				comm.sendEvent(foundEvent);
			} else if(event.getOriginal().isBetween(node.getKey(), node.getSuccessor().getKey()) ||event.getOriginal().getKey() == node.getSuccessor().getKey().getKey()) {
				// Between me and successor or successor = successor
				FoundNodeEvent foundEvent = new FoundNodeEvent(node.getSuccessor().getId(), event.getIP());
				comm.sendEvent(foundEvent);
			} else {
				// look up in table and create another joinEvent and go around in circles
				node.lookupL(event.getOriginal(), event.getIP());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

	private void join() {
		try {
			if(node.getId().equals(node.getSuccessor().getId())) {
				// I am myself durp durp durp
				FoundSuccessorEvent foundEvent = new FoundSuccessorEvent(node);
				IO comm2 = new IO(new Socket(event.getIP(), ChordNode.PORT));
				comm2.sendEvent(foundEvent);
				System.out.println("Sent foundSucc event");
			} else if(event.getOriginal().isBetween(node.getKey(), node.getSuccessor().getKey()) || event.getOriginal().getKey() == node.getSuccessor().getKey().getKey()) {
				// Between me and successor or successor = successor
				FoundSuccessorEvent foundEvent = new FoundSuccessorEvent(node.getSuccessor());
				IO comm2 = new IO(new Socket(event.getIP(), ChordNode.PORT));
				comm2.sendEvent(foundEvent);
				System.out.println("Sent foundSecc event");
			} else {
				// look up in table and create another joinEvent and go around in circles
				node.lookupN(event.getOriginal(), event.getIP(), event.getLatch()); // just changed this 1:09 pm 
				System.out.println("looking up");
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void found_successor(){
		try{
		node.setSuccessor(event.getNode());
//		DHTEvent setpred = new StabilizePEvent(node);
//		IO comm77 = new IO(new Socket(event.getIP(), ChordNode.PORT));
//		comm77.sendEvent(setpred);
		System.out.println(node.getSuccessor());
		System.out.println("Added Successor! =]\n"+event.getNode().getId());
		ChordNode.latch.countDown();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
