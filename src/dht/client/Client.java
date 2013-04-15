package dht.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dht.chord.ChordKey;
import dht.chord.ChordNode;
import dht.event.DHTEvent;
import dht.event.FoundNodeEvent;
import dht.event.LookupEvent;
import dht.event.QueryEvent;
import dht.event.RBQEvent;
import dht.event.StorageEvent;
import dht.net.IO;

public class Client {

	private String ip; 
	
	public Client(){
		try {
			ip = InetAddress.getLocalHost().toString();
			ip = ip.substring(ip.indexOf('/')+1);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param String absolute path to file
	 * 		  String cluster server
	 */
	public static void main(String[] args) {
		if(args.length < 1)
			usage();
		Client client = new Client();
		switch (args[0]){
		case "store" : 
			if (args.length == 4)
				client.store(args[1], args[2], args[3]);
			else 
				usage();
			break;
		case "query": 
			if (args.length == 4)
				client.query(args[1], args[2], args[3]);
			else if (args.length == 5){
				client.rangeBasedQuery(args[1], args[2], args[3], args[4]);
			} else
				usage();
			break;
		case "count": 
			if (args.length == 4)
				client.count(args[1], args[2], args[3]);
		}
	}
	
	private static void usage(){
		System.out.println("Invalid params, make this a better message later..");
	}
	
	private void store(String server, String path, String fileName){
		ServerSocket ss = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket sock = null;
		if (!path.endsWith("/")){
			path += "/";
		}
		try {
			ss = new ServerSocket(ChordNode.PORT); // ss to listen for response from cluster after lookup
			sock = new Socket(server,  ChordNode.PORT); // connection to the server specified in args
			IO comm = new IO(sock); 
			
			File file = new File(fileName);
			DHTEvent lookup = new LookupEvent(new ChordKey(file.hashCode()), server, ip);
			comm.sendEvent(lookup);
			
			Socket socket = ss.accept();
			
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			FoundNodeEvent found = (FoundNodeEvent) in.readObject();
			IO comm2 = new IO(new Socket(found.getIP(), ChordNode.PORT));
			StorageEvent store = new StorageEvent(file, path);
			comm2.sendEvent(store);
		} catch (Exception e){
			e.printStackTrace();
		} finally { // cleanup
			try {
				ss.close();
				out.close();
				in.close();
				sock.close();
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	private void query(String server, String fileName, String destination){
		ServerSocket ss = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket sock = null;
		try {
			ss = new ServerSocket(ChordNode.PORT); // ss to listen for responce from cluster after lookup
			sock = new Socket(server,  ChordNode.PORT); // connection to the server specified in args
			IO comm = new IO(sock); 
			
			File file = new File(fileName);
			DHTEvent lookup = new LookupEvent(new ChordKey(file.hashCode()), server, ip);
			comm.sendEvent(lookup);
			
			Socket socket = ss.accept();
			
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			FoundNodeEvent found = (FoundNodeEvent) in.readObject();
			
			IO comm2 = new IO(new Socket(found.getIP(), ChordNode.PORT));
			QueryEvent store = new QueryEvent(file, fileName, destination);
			comm2.sendEvent(store);
			
		} catch (Exception e){
				e.printStackTrace();
			} finally { // cleanup
				try {
					ss.close();
					out.close();
					in.close();
					sock.close();
				} catch (Exception e){
					e.printStackTrace();
				}
				
			}
			
	}
	
	private void rangeBasedQuery(String server, String start, String end, String dest){
		ServerSocket ss = null;
		Socket sock = null;
		try {
			ss = new ServerSocket(ChordNode.PORT); // ss to listen for responce from cluster after lookup
			sock = new Socket(server,  ChordNode.PORT); // connection to the server specified in args
			IO comm = new IO(sock); 
			// January 2, 2010
			Date startTime = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(start);
			Date endTime = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(end);
			RBQEvent rbq = new RBQEvent(ip, server, startTime, endTime );
			comm.sendEvent(rbq);
			
			Socket socket = ss.accept();
			
			IO comm2 = new IO(socket);
			rbq = (RBQEvent) comm2.getEvent();
			
			System.out.println(rbq.getList());
			
		} catch (Exception e){
			e.printStackTrace();
		} finally { // cleanup
			try {
				ss.close();
				sock.close();
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	private void count(String server, String start, String end){
		ServerSocket ss = null;
		Socket sock = null;
		try {
			ss = new ServerSocket(ChordNode.PORT); // ss to listen for response from cluster after lookup
			sock = new Socket(server,  ChordNode.PORT); // connection to the server specified in args
			IO comm = new IO(sock); 
			// January 2, 2010
			Date startTime = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(start);
			Date endTime = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(end);
			RBQEvent rbq = new RBQEvent(ip, server, startTime, endTime );
			comm.sendEvent(rbq);
			
			Socket socket = ss.accept();
			
			IO comm2 = new IO(socket);
			rbq = (RBQEvent) comm2.getEvent();
			
			System.out.println("Count: "+rbq.getList().size());
			
		} catch (Exception e){
			e.printStackTrace();
		} finally { // cleanup
			try {
				ss.close();
				sock.close();
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
}
