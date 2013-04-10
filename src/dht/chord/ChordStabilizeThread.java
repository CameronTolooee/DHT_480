package dht.chord;

import java.net.Socket;

import java.util.concurrent.CountDownLatch;

import dht.event.DHTEvent;
import dht.event.SUpdateTableEvent;
import dht.event.StabilizeSEvent;
import dht.net.IO;

public class ChordStabilizeThread implements Runnable {

	private ChordNode node;
	
	public ChordStabilizeThread(ChordNode node){
		this.node = node;
	}
	
	@Override
	public void run() {
		try {
			ChordNode.latch.await();
			if(!node.getId().equals(node.getSuccessor().getId())){
				DHTEvent stabilizeEventS = new StabilizeSEvent(node);
				IO comm3 = new IO(new Socket(node.getSuccessor().getId(), ChordNode.PORT));
				comm3.sendEvent(stabilizeEventS);
			}
			ChordNode.latch = new CountDownLatch(ChordHash.TABLE_SIZE);
			node.updateTable(node);
			ChordNode.latch.await();
			DHTEvent updateTable = new SUpdateTableEvent(node.getId());
			IO comm4 = new IO(new Socket(node.getSuccessor().getId(), ChordNode.PORT));
			comm4.sendEvent(updateTable);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
