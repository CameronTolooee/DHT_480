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
		if (args.length > 1){
			System.out.println("Some usage message");
		} 
		String fileName = null;
		if (args.length == 0) {
			fileName = getFileFromUser();
		} else {
			fileName = args[0];
		}
		Client c = new Client();
		int repLevel = 3; // Should make this a commandline arg
		MetaDataServer meta = new MetaDataServer(repLevel);
		ArrayList<String> list = meta.store(fileName);
		System.out.println(list);
		c.write(fileName, list); // change
	}

	private static String getFileFromUser() {
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

	public void write(String file_name, ArrayList<String> servers) throws Exception {

		File file = new File(file_name);
		for(String server : servers){
			Socket socket = new Socket(server, 35005);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());

			oos.writeObject(file.getName());

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[Server.BUFFER_SIZE];
			Integer bytesRead = 0;

			while ((bytesRead = fis.read(buffer)) > 0) {
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length));
			}

			// Cleanup
			socket.close();
			fis.close();
			oos.close();
			ois.close();
		}
	}

}
