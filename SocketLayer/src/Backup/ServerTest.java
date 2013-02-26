import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class ServerTest {

	public static void main(String[] args){
		
		try {
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			Socket socket = serverSocket.accept();
			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			
			IO io = new IO(socket, in, out);
			
			io.receiveFile();
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}
