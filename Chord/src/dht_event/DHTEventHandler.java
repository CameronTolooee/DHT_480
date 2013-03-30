package dht_event;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import chord.ChordNode;

import sockets_layer.*;

public class DHTEventHandler implements Runnable {
	private IO comm;
	private DHTEvent event;
	private ChordNode node;

	@Override
	public void run() {
		switch (event.getEventType()) {
		case LOOKUP:  System.out.println("handler: lookup"); lookup();
			break;
		case STORAGE:
			break;
		case JOIN: System.out.println("handler: joining"); join();
			break;
		case RBQ_EVENT:
			break;
		case LOOKUP_TABLE: System.out.println("handler: found table"); lookupTable();
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
		default:

		}

	}


	private void lookupTable() {
		if(node == node.getSuccessor()) {
			// I am myself durp durp durp
			FoundNodeEvent foundEvent = new FoundNodeEvent(node);
			comm.sendEvent(foundEvent);
		} else if(event.getDestination().isBetween(event.getOriginal(), node.getSuccessor().getKey()) || event.getDestination().getKey() == node.getSuccessor().getKey().getKey()) {
			// Between me and successor or successor = successor
			FoundNodeEvent foundEvent = new FoundNodeEvent(node.getSuccessor());
			comm.sendEvent(foundEvent);
		} else {
			// look up in table and create another joinEvent and go around in circles
			node.lookupL(event.getDestination());
		}
	}


	private void foundTable() {
		
	}

	private void foundNode(){
		
	}

	public DHTEventHandler(Socket sock, ChordNode node) {
		comm = new IO(sock);
		event = comm.getEvent();		
		this.node = node;
		//run(); doesn't need to get called explicitly
	}
	


	private void stabilizeS() {
		DHTEvent eventP  = new StabilizePEvent(node.getPredecessor()); 
		if(node.getSuccessor().getId().equals(node.getId())){
			node.setSuccessor(event.getNode());
		}
		node.setPredecessor(event.getNode());
		System.out.println(node.getPredecessor());
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
		node.setPredecessor(event.getNode());
		System.out.println("STABLIZED BITCHES");
		System.out.println(node.getId()+" S:"+ node.getSuccessor());
		System.out.println(node.getId()+" P:"+ node.getPredecessor());
		System.out.println(node.getPredecessor().getId()+" Succ:"+ node.getPredecessor().getSuccessor());
		System.out.println(node.getPredecessor().getId()+" Pred:"+ node.getPredecessor().getPredecessor());
	}
	
	public void lookup() {
		if(node == node.getSuccessor()) {
			// I am myself durp durp durp
			FoundNodeEvent foundEvent = new FoundNodeEvent(node);
			comm.sendEvent(foundEvent);
		} else if(event.getDestination().isBetween(event.getOriginal(), node.getSuccessor().getKey()) || event.getDestination().getKey() == node.getSuccessor().getKey().getKey()) {
			// Between me and successor or successor = successor
			FoundNodeEvent foundEvent = new FoundNodeEvent(node.getSuccessor());
			comm.sendEvent(foundEvent);
		} else {
			// look up in table and create another joinEvent and go around in circles
			node.lookupL(event.getDestination());
		}
		
	}

	public void join() {
		if(node.getId().equals(node.getSuccessor().getId())) {
			// I am myself durp durp durp
			FoundSuccessorEvent foundEvent = new FoundSuccessorEvent(node);
			try {
				IO comm2 = new IO(new Socket(comm.getIp(), ChordNode.PORT));
				comm2.sendEvent(foundEvent);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			comm.sendEvent(foundEvent);
			System.out.println("Sent foundSucc event");
		} else if(event.getDestination().isBetween(event.getOriginal(), node.getSuccessor().getKey()) || event.getDestination().getKey() == node.getSuccessor().getKey().getKey()) {
			// Between me and successor or successor = successor
			FoundSuccessorEvent foundEvent = new FoundSuccessorEvent(node.getSuccessor());
			comm.sendEvent(foundEvent);
			System.out.println("Sent foundSecc event");
		} else {
			// look up in table and create another joinEvent and go around in circles
			node.lookupN(event.getDestination());
			System.out.println("looking up");
		}
		
	}
	
	public void found_successor(){
		node.setSuccessor(event.getNode()); 
		System.out.println(node.getSuccessor());
		System.out.println("Added Successor! =]\n"+event.getNode().getId());
		
	}

}
