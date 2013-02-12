import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {	
	
	private String meta;

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
			System.out.println(e);
		} finally { // clean up
			if (socket != null)
				socket.close();
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
		}
	}

	private static void get(String fileName) {
		// TODO Auto-generated method stub
		
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
			System.out.println(e);
		} finally {
			if (sc != null)
				sc.close();
		}
		return result;
	}

	// Takes the filename to write the file to.
	private static boolean write(String fileName) throws Exception {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		FileInputStream fis = null;
		Socket socket = null;
		try {
			socket = new Socket("homer", 35005);
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject("store");
			oos.writeObject(fileName);
			boolean recieved = false;
			ArrayList<String> servers = null;
			while (!recieved){
				ois = new ObjectInputStream(socket.getInputStream());
				servers = (ArrayList<String>)ois.readObject();
				System.out.println("Client: recieved from Meta");
				if(servers != null)
					recieved = true;
				System.out.println("...");
			}

			File file = new File(fileName);
			String server = servers.get(0);
			System.out.println("Storing to server: "+server);
			socket = new Socket(server, 35005); // open socket on port 35005

			// Use object stream to send java objects over the socket connection.
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());

			// First write the filename as it will be saved on the server
			// --- TODO this name should be determined by the user
			oos.writeObject("store");
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
			return ois.readBoolean();

			} catch (Exception e){
				System.out.println(e);
				return false;
			}
			// Cleanup
		 finally {
			if (socket != null)
				socket.close();
			if (fis != null)
				fis.close();
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
		}
	}


	private void update(Socket sock, String mesg){
		// maybe
	}

}
