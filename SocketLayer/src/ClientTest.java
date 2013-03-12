import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientTest {

	public static void main(String[] args){
		
		
		
		try {
			Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			
			IO io = new IO(socket, in, out);
			File file = new File(args[2]);
			io.sendFile(file, "/tmp/CA/");
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}
