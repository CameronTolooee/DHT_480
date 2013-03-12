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
import java.util.Set;

public class IO {
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private static final int BUFFER_SIZE = 4096; /* Max buffer size is (2^32)- 1; 4096 bytes is the block size of the new advanced format sector drives*/
	private FileAttribute<Set<PosixFilePermission>> FILE_PERMISSIONS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));

	
	
	public IO(Socket socket, ObjectInputStream input, ObjectOutputStream output){
		this.socket = socket;
		this.input = input;
		this.output = output;
		
	}
	
	
	/* Should send update information to all the nodes that have finger table entry for this node
	 * SERVER SPECIFIC!!!!!!!
	 * */
	public void heartBeat(){
		
	}
	
	
	/* Sends a file over the socket
	 * If this file is to be written in a specific directory as specified by the dir_store command the file
	 * must already point to the correct location before being sent to this method
	 * */
	public void sendFile(File file, String path){
		
		System.out.println("Sending file...");
		
		try{
			
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
			
		/* WARNING: Socket should NOT be closed at this point. JUST FOR TESTING PURPOSES ONLY */
		}finally{
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
			String filename = "";
			o = input.readObject();
			
			if(o instanceof String){
				filename = createParents((String) o);
			}else
				System.err.println("Class: IO.java, Function: receiveFile(), Error: expected file name as String, received "+(o.toString()));
			
			/* READ FILE 4096 BYTES AT A TIME */
			FileOutputStream fileOutput = new FileOutputStream(filename);
			byte[] buffer = new byte[BUFFER_SIZE];
			Integer bytesRead = 0;

			/* IF THE FILE IS EMPTY just create it, don't write */
			/*
			System.out.println(input.available());
			if(input.available() <= 0){
				Files.createFile(Paths.get(filename), FILE_PERMISSIONS);
			}
			*/


			do {
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

		}catch(IOException | ClassNotFoundException e){ 
			e.printStackTrace();
			
		/* WARNING: Socket should NOT be closed at this point. JUST FOR TESTING PURPOSES ONLY */
		}finally{
			try {
				//input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
}

