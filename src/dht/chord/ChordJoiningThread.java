package dht.chord;

import java.net.Socket;

import dht.net.IO;
import dht.event.JoinEvent;

public class ChordJoiningThread implements Runnable{
	
	private String ip;
	private ChordNode node;
	
	public ChordJoiningThread(String ip, ChordNode node){
		this.ip = ip;
		this.node = node;
	}

	@Override
	public void run() {
		try {
		JoinEvent event = new JoinEvent(node.getKey(), new ChordKey(ip), node.getId());
		IO comm = new IO(new Socket(ip, ChordNode.PORT));
		comm.sendEvent(event);
		System.out.println("Sent join event.");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
