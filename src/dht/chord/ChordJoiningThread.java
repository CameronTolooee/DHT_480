package dht.chord;

import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import dht.event.JoinEvent;
import dht.net.IO;

public class ChordJoiningThread implements Runnable{
	
	private String ip;
	private ChordNode node;
	private CountDownLatch cdl;
	
	public ChordJoiningThread(String ip, ChordNode node, CountDownLatch cdl){
		this.ip = ip;
		this.node = node;
		this.cdl = cdl;
	}

	@Override
	public void run() {
		try {
		JoinEvent event = new JoinEvent(node.getKey(), new ChordKey(ip), node.getId(), cdl);
		IO comm = new IO(new Socket(ip, ChordNode.PORT));
		comm.sendEvent(event);
		System.out.println("Sent join event.");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
