import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	public static final int PORT = 35005;
	public static final int BUFFER_SIZE = 100;

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			// Should probably have a way to gracfully bring down servers instead
			// of having a script kill the process but I'm lazy
			while (true) {
				Socket s = serverSocket.accept();
				saveFile(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveFile(Socket socket) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(
				socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		FileOutputStream fos = null;
		byte[] buffer = new byte[BUFFER_SIZE];
		// First thing sent is the file name
		Object o = ois.readObject();
		if (o instanceof String) {
			fos = new FileOutputStream("/tmp/Tolooee_480/" + o.toString());
		} else 
			throw new Exception("Expected a String (a filename) from stream but got: "+ o.getClass());
	
		// 2nd is the conntents of the file by one BUFFER_SIZE at a time
		Integer bytesRead = 0;
		do {
			// Get size of buffer from stream
			o = ois.readObject();
			if (!(o instanceof Integer)) 
				throw new Exception("Expected a Integer (the buffer size) from stream but got: "+ o.getClass());
			bytesRead = (Integer) o;
			// read the buffer			
			o = ois.readObject();
			if (!(o instanceof byte[])) 
				throw new Exception("Expected a byte[] (the buffer) from stream but got: "+ o.getClass());
			buffer = (byte[]) o;
			// Write data to output file.
			fos.write(buffer, 0, bytesRead);
		} while (bytesRead == BUFFER_SIZE); // we are done when the buffer is not full or is 0
		
		// If we get to here the file write was successful
		System.out.println("File transfer successful"); // REMOVE ME
		// Clean up
		fos.close();
		ois.close();
		oos.close();
	}

	public static void main(String[] args) {
		// its multi threaded so in order to run this we have to call the start method
		// which takes care of constructions and such..
		new Server().start();
	}
}
