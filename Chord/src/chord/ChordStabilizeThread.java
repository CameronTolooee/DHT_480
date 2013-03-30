package chord;

import java.net.Socket;

import sockets_layer.IO;
import dht_event.DHTEvent;
import dht_event.StabilizeSEvent;

public class ChordStabilizeThread implements Runnable {

	private ChordNode node;
	
	public ChordStabilizeThread(ChordNode node){
		this.node = node;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			System.out.println("Done waiting");
			if(node != node.getSuccessor()){
				DHTEvent stabilizeEventS = new StabilizeSEvent(node);
				IO comm3 = new IO(new Socket(node.getSuccessor().getId(), ChordNode.PORT));
				comm3.sendEvent(stabilizeEventS);
				System.out.println("Sent stablization event");
			}
			Thread.sleep(1000);
			node.updateTable(node.getSuccessor());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
