import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {	
	
	public static void main(String[] args) throws Exception {

		if (args.length > 2 || args.length < 1) {
			usage();
		}
		
		String cmd = args[0];
		// if no file supplied, prompt user for one
		if (cmd.equals("store")) {
			String fileName = null;
			if (args.length == 1) {
				fileName = getFileFromUser();
			} else {
				fileName = args[1];
			}
			write(fileName);
		} else if (cmd.equals("get")){
			String fileName = null;
			if (args.length == 1) {
				fileName = getFileFromUser();
			} else {
				fileName = args[1];
			}
			get(fileName);
		} else if (cmd.equals("status")){
			status();
		} else {
			System.out.println("Invalid command: " + cmd);
			usage();
		}
	}

	private static void status() throws Exception {
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		String stat = null;
		try {
			socket = new Socket("homer", 35005); // connect to meta server
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject("status"); // write desired command
			boolean recieved = false;
			while (!recieved){ // this will eventually be our time-out check
				ois = new ObjectInputStream(socket.getInputStream());
				stat = (String)ois.readObject(); // get response from the server
				if(stat != null)
					recieved = true;
			}
			System.out.println(stat); // print the string it sends back
		} catch (Exception e){
			e.printStackTrace();
		} finally { // clean up
			if (socket != null)
				socket.close();
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static void get(String fileName) throws IOException {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		Socket sock = null;
		Object o = null;
		byte[] buffer = new byte[Server.BUFFER_SIZE];
		try {
			sock = new Socket(MetaDataServer.name, 35005);
			oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject("get");
			oos.writeObject(fileName);
			boolean recieved = false;
			ArrayList<String> servers = null;
			while (!recieved){
				ois = new ObjectInputStream(sock.getInputStream());
				servers = (ArrayList<String>)ois.readObject();
				System.out.println("Client: recieved from Meta");
				if(servers != null)
					recieved = true;
			}
			System.out.println("Servers: "+servers);
			int cntr = 0;
			while(recieved){
				System.out.println(MetaDataServer.getRep());
				if(cntr == MetaDataServer.getRep()){
					System.out.println("Unable to retrieve file: "+fileName+" servers are down.");
					System.exit(-1);
				}
				try {
					sock = new Socket(servers.get(cntr++), 35005); // socket to data server
					oos = new ObjectOutputStream(sock.getOutputStream());
					ois = new ObjectInputStream(sock.getInputStream());
				}	catch (Exception e) {
					System.out.println(servers.get(cntr-1)+" is not reachable.");
					continue;
				}
				oos.writeObject("get");
				oos.writeObject(fileName); 
				fos = new FileOutputStream("./" + fileName+ ".new");
				
				Integer bytesRead = 0;
				do {
					// Get size of buffer from stream
					o = ois.readObject();
					if (!(o instanceof Integer))
						throw new Exception("Expected a Integer (the buffer size) from stream but got: " + o.getClass());
					bytesRead = (Integer) o;
					System.out.println(bytesRead);
					// read the buffer
					o = ois.readObject();
					if (!(o instanceof byte[]))
						throw new Exception("Expected a byte[] (the buffer) from stream but got: " + o.getClass());
					buffer = (byte[]) o;
					
					
					// Write data to output file.
					try {
						fos.write(buffer, 0, bytesRead);
					} catch (IOException e) {
						System.out.println(servers.get(cntr-1)+" is not reachable.");
						continue;
					}
				} while (bytesRead == Server.BUFFER_SIZE); // we are done when the buffer is not full or is 0
				// If we get to here the file write was successful
				recieved = false;

			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			sock.close();
			oos.close();
			ois.close();
			fos.close();
		}
	}

	private static void usage(){
		System.out.println("Usage: java Client  { store | get } filename");
		System.out.println("Usage: java Client status ");
		System.exit(-1);
	}
	
	private static String getFileFromUser() {
		System.out.print("Enter filename: ");
		Scanner sc = null;
		String result = null;
		try {
			sc = new Scanner(System.in);
			result = sc.next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}
		return result;
	}

	// Takes the filename to write the file to.
	@SuppressWarnings("unchecked")
	private static void write(String fileName) throws Exception {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		FileInputStream fis = null;
		Socket metaSocket = null;
		Socket dataSocket = null;
		try {
			metaSocket = new Socket(MetaDataServer.name, 35005);
			oos = new ObjectOutputStream(metaSocket.getOutputStream());
			oos.writeObject("store");
			oos.writeObject(fileName);
			boolean recieved = false;
			ArrayList<String> servers = null;
			while (!recieved){
				ois = new ObjectInputStream(metaSocket.getInputStream());
				servers = (ArrayList<String>)ois.readObject();
				System.out.println("Client: recieved from Meta");
				if(servers != null)
					recieved = true;
				System.out.println("...");
			}
			metaSocket.close();
			File file = new File(fileName);
			System.out.println(servers);
			String server = servers.get(0);
			System.out.println("Client: Storing to server: "+server);
			
			dataSocket = new Socket(server, 35005); // open socket on port 35005

			// Use object stream to send java objects over the socket connection.
			ois = new ObjectInputStream(dataSocket.getInputStream());
			oos = new ObjectOutputStream(dataSocket.getOutputStream());
			oos.writeObject("store");
			// First write the filename as it will be saved on the server
			// --- TODO this name should be determined by the user
			oos.writeObject(servers);

			oos.writeObject(file.getName());
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Server.BUFFER_SIZE]; 
			Integer bytesRead = 0;
			// read from file stream into buffer
			// each iteration fills one buffer and sends it
			// once the file is read (ie: bytes read == 0) we stop

			while ((bytesRead = fis.read(buffer)) > 0) { // Stop when there is no more to read
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length)); // don't want overwrite reference if it is being used elsewhere so create
			} // a copy instead of passing the reference. idk if this is needed.
			
			oos.writeObject(file);

			} catch (Exception e){
				e.printStackTrace();
			}
			// Cleanup
		 finally {
			 try {
			if (dataSocket != null)
				dataSocket.close();
			if (fis != null)
				fis.close();
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
			 } catch (Exception e){
				 e.printStackTrace();
			 }
		}
	}
}
