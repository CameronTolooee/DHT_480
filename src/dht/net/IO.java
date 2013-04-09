/*+----------------------------------------------------------------------
 ||
 ||  	IO Protocol: Specification of the parameters and rules to be used
 ||					 in all communication between servers and clients.
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Files: 1. Filename (includes full path!!!)
 ||				3. File 4096 bytes at a time
 ||
 ||		 Hearbeats: 1. Type 
 ||					2. Data
 ||
 ++-----------------------------------------------------------------------*/
package dht.net;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Set;

import dht.chord.ChordKey;
import dht.event.DHTEvent;
import dht.event.DHTEvent.EventType;
import dht.event.StorageEvent;

public class IO {
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private static final int BUFFER_SIZE = 4096; /* Max buffer size is (2^32)- 1; 4096 bytes is the block size of the new advanced format sector drives*/
	private FileAttribute<Set<PosixFilePermission>> FILE_PERMISSIONS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));
	private String ip;
	public static int numOfEvents = 0;
	
	
	public IO(Socket socket){
		this.setIp(socket.getInetAddress().getHostAddress().toString());
		this.socket = socket;
		try {
		this.output = new ObjectOutputStream(this.socket.getOutputStream());
		this.input = new ObjectInputStream(this.socket.getInputStream());
		System.out.println("in IO CLass");

		} catch (IOException io){
			io.printStackTrace();
		}
	}
	
	
	/* Should send update information to all the nodes that have finger table entry for this node
	 * SERVER SPECIFIC!!!!!!!
	 * */
	public void heartBeat(){
		
	}
	
public void sendMessage(String message){
		try{
			/* SEND THE FILE NAME AS STRING*/
			output.writeObject(message);
			output.flush();
	
		}catch(IOException e){
			e.printStackTrace();
			
		/* WARNING: Socket should NOT be closed at this point. JUST FOR TESTING PURPOSES ONLY */
		}finally{
			try {
				//output.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

public String receiveMessage(){
	Object o;
	try {
		String message = "";
		o = input.readObject();
		
		if(o instanceof String){
			message = (String) o;
		}else
			System.err.println("Class: IO.java, Function: receiveFile(), Error: expected file name as String, received "+(o.toString()));
		return message;	
	} catch(IOException | ClassNotFoundException e){ 
		e.printStackTrace();
	}
	return null;
}

public void sendKey(ChordKey key){
	try{
		/* SEND THE FILE NAME AS STRING*/
		output.writeObject(key);
		output.flush();

	}catch(IOException e){
		e.printStackTrace();
	}
		
	/* WARNING: Socket should NOT be closed at this point. JUST FOR TESTING PURPOSES ONLY
	}finally{
		try {
			//output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
		
	/* Sends a file over the socket
	 * If this file is to be written in a specific directory as specified by the dir_store command the file
	 * must already point to the correct location before being sent to this method
	 * */
	public void sendFile(File file, String path){
		
		System.out.println("Sending file...");
		
		try{
			//ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			/* SEND THE FILE NAME AS STRING*/
			output.writeObject(path+file.getName());
			output.flush();
			
			/* SEND THE FILE 4096 BYTES AT A TIME */
			FileInputStream fileInput = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE]; 
			Integer bytesRead = 0;
	
			while( (bytesRead = fileInput.read(buffer)) > 0) {
				output.writeObject(bytesRead);
				output.flush();
				output.writeObject(buffer);
				output.flush();
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		///* WARNING: Socket should NOT be closed at this point. JUST FOR TESTING PURPOSES ONLY
		finally{
			try {
				//output.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
			
		System.out.println("Sending file complete...");
	}
	
		
	/* Reads a file from the socket; returns the file it read */
	public void receiveFile(){
		
		System.out.println("Receiving file...");
		Object o;
		try {
			/* READ FILE NAME and create parent directories if they don't exist */
			System.out.println("1");
			String filename = "";
			o = input.readObject();
			System.out.println("1");
			if(o instanceof String){
				filename = createParents((String) o);
			}else
				System.err.println("Class: IO.java, Function: receiveFile(), Error: expected file name as String, received "+(o.toString()));
			
			/* READ FILE 4096 BYTES AT A TIME
			 * Note: FileOutputStream creates empty file!!!
			 */
			System.out.println("2");
			FileOutputStream fileOutput = new FileOutputStream(filename);
			System.out.println("3");
			byte[] buffer = new byte[BUFFER_SIZE];
			Integer bytesRead = 0;
			System.out.println("4");
			do {
				System.out.println("5");
				o = input.readObject();
				if(o instanceof Integer){
					bytesRead = (Integer) o;
				}else
					System.err.println("Class: IO.java, Function: receiveFile(), Error: expected file content as Integer, received "+(o.toString()));
				
				o = input.readObject();
				if(o instanceof byte[]){
					buffer = (byte[]) o;
				}else
					System.err.println("Class: IO.java, Function: receiveFile(), Error: expected file content as byte[], received "+(o.toString()));
					
					fileOutput.write(buffer, 0, bytesRead);
			} while(bytesRead==BUFFER_SIZE);

		/* Catching the EOF exception separate from a IOException allows detection of empty files (peeking at the stream did not work because it was not consistent)*/
		}catch(EOFException e){	
			/* Client sent an empty file */
			return;
		}catch(IOException | ClassNotFoundException e){ 
			e.printStackTrace();
		}
		///*
		//WARNING: Socket should NOT be closed at this point. JUST FOR TESTING PURPOSES ONLY
		finally{
			try {
				//input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//*/
		
		System.out.println("Receiving file complete...");
	}
		
	/* Create the parent directories for the given file
	 */
	private String createParents(String filepath){
		
		String filename = filepath;

		if(filepath.contains("/")){
			String path = filepath.substring(0, filepath.lastIndexOf("/"));

			try {
				Files.createDirectories(Paths.get(path), FILE_PERMISSIONS);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return filename;
		
	}

	public DHTEvent getEvent() {
		Object o  = null;
		DHTEvent event = null;

		try {
			//System.out.println("getting event");
			//ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			//out.flush();
			//ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		//	System.out.println("getting event");

			o = input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		if (o instanceof DHTEvent) {
			event = (DHTEvent) o;
		} else {
			return null;
		}
		return event;
	}
	
	public void sendEvent(DHTEvent event) {	
		try{
			long start = System.currentTimeMillis();
			
			// Java's serialization sucks so do a quick primitive serialization of the event
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(baos);
//			oos.writeObject(event);
//			oos.close();
//			byte[] bytes = baos.toByteArray();
//			socket.getOutputStream().flush();
//			socket.getOutputStream().write(bytes);
//			socket.getOutputStream().flush();
			if(event.getEventType() == EventType.STORAGE) {
				output.writeObject(event);
				output.flush();
				StorageEvent se = (StorageEvent) event;
				sendFile(se.getFile(), se.gethPath());
			} else {
				output.writeObject(event);
				output.flush();
				
				long finish = System.currentTimeMillis();
				//System.out.println("EVENT SIZE: "+baos.size()+" bytes");
				System.out.println("Time took: "+(finish - start));
				System.out.println("["+new Date(System.currentTimeMillis()).toString()+"] IO: sent "+event.getEventType()+".");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try {
				System.out.println("closing socket");
				//input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void finalize(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}
}

