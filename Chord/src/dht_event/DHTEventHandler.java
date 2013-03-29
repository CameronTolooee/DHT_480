package dht_event;

import java.net.Socket;

import chord.ChordNode;

import sockets_layer.*;

public class DHTEventHandler implements Runnable {
	private IO comm;
	private DHTEvent event;
	private ChordNode node;

	@Override
	public void run() {
		switch (event.getEventType()) {
		case LOOKUP: lookup();
			break;
		case STORAGE:
			break;
		case JOIN: System.out.println("handler: joining"); join();
			break;
		case RBQ_EVENT:
			break;
		case STABLIZE:
			break;
		case LEAVE:
			break;
		case SHUTDOWN:
			break;
		case FOUND_SUCCESSOR: found_successor();
			break;
		case FOUND_NODE:
		default:

		}

	}

	public DHTEventHandler(Socket sock, ChordNode node) {
		comm = new IO(sock);
		System.out.println("Got back from io");
		event = comm.getEvent();
		System.out.println("got evetn");
		
		this.node = node;
		//run(); doesn't need to get called explicitly
	}
	
	public void lookup() {
		if(node == node.getSuccessor()) {
			// I am myself durp durp durp
			FoundNodeEvent foundEvent = new FoundNodeEvent(node);
			comm.sendEvent(foundEvent);
		} else if(event.getDestination().isBetween(event.getOriginal(), node.getSuccessor().getKey()) || event.getDestination().getKey() == node.getSuccessor().getKey().getKey()) {
			// Between me and successor or successor = successor
			FoundNodeEvent foundEvent = new FoundNodeEvent(node.getSuccessor());
		} else {
			// look up in table and create another joinEvent and go around in circles
			node.lookup(event.getDestination());
		}
		
	}

	public void join() {
		System.out.println("in joinging method1");
		if(node == node.getSuccessor()) {
			// I am myself durp durp durp
			System.out.println("in joinging method2");

			Found_SuccessorEvent foundEvent = new Found_SuccessorEvent(node);
			comm.sendEvent(foundEvent);
		} else if(event.getDestination().isBetween(event.getOriginal(), node.getSuccessor().getKey()) || event.getDestination().getKey() == node.getSuccessor().getKey().getKey()) {
			// Between me and successor or successor = successor
			System.out.println("in joinging method3");
			Found_SuccessorEvent foundEvent = new Found_SuccessorEvent(node.getSuccessor());
		} else {
			// look up in table and create another joinEvent and go around in circles
			System.out.println("in joinging method4");
			node.lookup(event.getDestination());
		}
		
	}
	
	public void found_successor(){
		node.setSuccessor(event.getNode()); 
		System.out.println("ADDDE SUCCESSOR!!! =]\n"+event.getNode().getId());
	}

}
