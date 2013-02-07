import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	public static void main(String[] args) throws Exception {
		// Don't know what kind of commandline args we will have
		if (args.length > 1){
			System.out.println("Some usage message");
		} 
		// if no args supplied, prompt user for one
		String fileName = null;
		if (args.length == 0) {
			fileName = getFileFromUser();
		} else {
			fileName = args[0];
		}
		
		Client c = new Client();
		int repLevel = 3; // Should make this a commandline arg
		MetaDataServer meta = new MetaDataServer(repLevel); // 
		ArrayList<String> list = meta.store(fileName);
		System.out.println(list);
		c.write(fileName, list); // change 
	}

	private static String getFileFromUser() {
		System.out.print("Enter filename: ");
		Scanner sc = null;
		String result = null;
		try {
			sc = new Scanner(System.in);
			result = sc.next();
			sc.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}
	
	// Takes the filename and a list of servers to write the file to. 
	public void write(String file_name, ArrayList<String> servers) throws Exception {

		File file = new File(file_name);
		for(String server : servers){
			Socket socket = new Socket(server, 35005); // open socket on port 35005

			// Use object stream to send java objects over the socket connection.
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			
			//  First write the filename as it will be saved on the server
			//     --- TODO this name should be determined by the user
			oos.writeObject(file.getName());
			// 
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[Server.BUFFER_SIZE];
			Integer bytesRead = 0;
			// read from file stream into buffer
			// each iteration fills one buffer and sents it 
			// once the file is read (ie: bytes read == 0) we stop
			while ((bytesRead = fis.read(buffer)) > 0) { // Stop when there is no more to read
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length)); // don't want concurrancy to bite us in the ass so create a copy instead of passing the reference. idk if this is needed.
			}

			// Cleanup
			socket.close();
			fis.close();
			oos.close();
			ois.close();
		}
	}

}
