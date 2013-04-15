package dht.event;

import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

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

	public DHTEventHandler(Socket sock, ChordNode node) {
		IO comm = new IO(sock);
		event = comm.getEvent();
		if (event.getEventType() == EventType.STORAGE) {
			StorageEvent se = (StorageEvent) event;
			String path = se.getPath();
			comm.receiveFile(path+se.getFile().getName());
		} if (event.getEventType() == EventType.QUERY) {
			QueryEvent qe = (QueryEvent) event;
			comm.sendFile(qe.getFile(), qe.getOrig());
		} else {
			this.node = node;
		}
	}

	@Override
	public void run() {
		if(event != null) {
			switch (event.getEventType()) {
				case LOOKUP: lookup();
					break;
				case STORAGE: 
					break;
				case JOIN: join();
					break;
				case RBQ_EVENT: rbq_event();
					break;
				case LOOKUP_TABLE: lookupTable();
					break;
				case STABLIZE_S: stabilizeS();
					break;
				case STABLIZE_P: stabilizeP();
					break;
				case LEAVE:
					break;
				case SHUTDOWN:
					break;
				case FOUND_SUCCESSOR: found_successor();
					break;
				case FOUND_TABLE: foundTable();
					break;
				case UPDATE_TABLE: updateTable();
					break;
				default: 
			}

		}

	}

	private void rbq_event() {
		System.out.println("GOT BBQ?");
		RBQEvent rbq = (RBQEvent) event;
		try {
			if(rbq.getOrig().equals(node.getId()) && !rbq.getFirst()) {
				IO comm = new IO(new Socket(rbq.getIP(), ChordNode.PORT));
				comm.sendEvent(rbq);
			} else {
				rbq.setFirst();
				IO comm = new IO(new Socket(node.getSuccessor().getId(), ChordNode.PORT));
				rbq.addFiles("/tmp/muehlooee");
				comm.sendEvent(rbq);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void updateTable() {
		SUpdateTableEvent sutEvent = (SUpdateTableEvent) event;
		try{
			if(sutEvent.getIP().equals(node.getId())){
				System.out.println("Tables are up-to-date.");
				ChordNode.latch.countDown();
			} else {
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
		LookupTableEvent ltEvent = (LookupTableEvent) event;
		try {
			
			// If this ip is the same as successor then only 1 node in cluster --> send yourself 
			if(ltEvent.getOriginal().getKey() == (new ChordKey(ltEvent.getDestination()).getKey())) {
				DHTEvent foundEvent = new FoundTableEvent(event.getIP(), ltEvent.getPosition());
				IO comm = new IO(new Socket(event.getIP(), ChordNode.PORT));
				comm.sendEvent(foundEvent);
			} 
			
			// If the target is between "this" and successor or target = successor --> send successor
			else if(ltEvent.getOriginal().isBetween(new ChordKey(ltEvent.getDestination()), node.getKey()) || ltEvent.getOriginal().getKey() == node.getKey().getKey()) {
				DHTEvent foundEvent = new FoundTableEvent(node.getId(), ltEvent.getPosition());
				IO comm = new IO(new Socket(event.getIP(), ChordNode.PORT));
				comm.sendEvent(foundEvent);
			} 
			
			// look up in table to find the largest preceding --> lookup
			else {
				node.lookupT(ltEvent.getOriginal(), ltEvent.getPosition(), event.getIP());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	// Found the correct entry for a position in "this" table so set it
	// and tell the latch that it has one less entry to wait for 
	private void foundTable() {
		FoundTableEvent ftEvent = (FoundTableEvent) event;
		int tablePos = (1 << ftEvent.getPosition()) + node.getKey().getKey();
		node.getTable().setEntry(new ChordTableEntry(event.getIP(), tablePos), ftEvent.getPosition());
		ChordNode.latch.countDown();
	}
	
	
	private void stabilizeS() {
		StabilizeSEvent ssEvent = (StabilizeSEvent) event;
		DHTEvent eventP  = new StabilizePEvent(node.getPredecessor());
		
		if (node.getSuccessor().getId().equals(node.getId())){
			node.setSuccessor(ssEvent.getNode());
		}
		
		node.setPredecessor(ssEvent.getNode());
		try {
			IO comm2 = new IO(new Socket(ssEvent.getNode().getId(), ChordNode.PORT));
			comm2.sendEvent(eventP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// Received from the stabilizing node notifying "this"'s predecessor is 
	// the stabilizing node -- set this predecessor the to node in event.
	private void stabilizeP(){
		StabilizePEvent sEvent = (StabilizePEvent) event;
		try {
			node.setPredecessor(sEvent.getNode());
			DHTEvent foundSucc = new FoundSuccessorEvent(node);
			IO comm78 = new IO(new Socket(sEvent.getNode().getId(), ChordNode.PORT));
			comm78.sendEvent(foundSucc);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void lookup() {
		LookupEvent lEvent = (LookupEvent) event;
		try {
			
			// If this ip is the same as successor then only 1 node in cluster --> send yourself 
			if(node.getId().equals(node.getSuccessor().getId())) {
				FoundNodeEvent foundEvent = new FoundNodeEvent(node.getId(), event.getIP());
				IO comm = new IO(new Socket(lEvent.getIP(), ChordNode.PORT));
				comm.sendEvent(foundEvent);
			} 
			
			// If the target is between "this" and successor or target = successor --> send successor
			else if(lEvent.getOriginal().isBetween(node.getKey(), node.getSuccessor().getKey()) || lEvent.getOriginal().getKey() == node.getSuccessor().getKey().getKey()) {
				FoundNodeEvent foundEvent = new FoundNodeEvent(node.getSuccessor().getId(), event.getIP());
				IO comm = new IO(new Socket(lEvent.getIP(), ChordNode.PORT));
				comm.sendEvent(foundEvent);
			} 
			
			// look up in table to find the largest preceding --> lookup
			else {
				node.lookupL(lEvent.getOriginal(), lEvent.getIP());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

	private void join() {
		JoinEvent jEvent = (JoinEvent) event;
		try {
			IO comm2 = new IO(new Socket(jEvent.getIP(), ChordNode.PORT));
			
			// If this ip is the same as successor then only 1 node in cluster --> send yourself 
			if(node.getId().equals(node.getSuccessor().getId())) {
				FoundSuccessorEvent foundEvent = new FoundSuccessorEvent(node);
				comm2.sendEvent(foundEvent);
			} 
			
			// If the target is between "this" and successor or target = successor --> send successor
			else if(jEvent.getOriginal().isBetween(node.getKey(), node.getSuccessor().getKey()) || jEvent.getOriginal().getKey() == node.getSuccessor().getKey().getKey()) {
				FoundSuccessorEvent foundEvent = new FoundSuccessorEvent(node.getSuccessor());
				comm2.sendEvent(foundEvent);
			} 
			
			// look up in table to find the largest preceding --> lookup
			else {
				node.lookupN(jEvent.getOriginal(), jEvent.getIP());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	// Found "this" nodes successor so set it and release the latch for the
	// update table thread that is waiting on this completion
	private void found_successor(){
		try{
			FoundSuccessorEvent fsEvent = (FoundSuccessorEvent) event;
			node.setSuccessor(fsEvent.getNode());
			System.out.println("Node joined successfully");
			ChordNode.latch.countDown();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
