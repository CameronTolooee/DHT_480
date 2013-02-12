import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server extends Thread {
	public static final int PORT = 35005;
	public static final int BUFFER_SIZE = 100;

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			// Should probably have a way to gracfully bring down servers
			// instead
			// of having a script kill the process but I'm lazy
			while (true) {
				Socket s = serverSocket.accept();
				// saveFile(s);
				getCmd(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getCmd(Socket socket) throws Exception {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			// First thing sent is the cmd
			Object o = ois.readObject();
			if (o instanceof String) {
				if (o.equals("store")){
					//store stuff
					oos.writeBoolean(saveFile(oos, ois));
				} else if (o.equals("get")){
					//get stuff
					System.out.println("Getting: "+o+" (but not really)");
				} else {
					throw new Exception(o +" is not a valid command. Commands are \"store\" and \"get\"");
				}
					
			} else {
				throw new Exception("Expected a String (the command) from stream but got: " + o.getClass());
			}
		} finally {
			oos.close();
			ois.close();
		}
	}

	private boolean saveFile(ObjectOutputStream oos, ObjectInputStream ois) throws Exception {
		FileOutputStream fos = null;
		ArrayList<String> serverList = null;
		File fileName = null;
		try {
			fos = null;
			byte[] buffer = new byte[BUFFER_SIZE];
			// First thing sent is the file name
			Object o = ois.readObject();
			if (o instanceof ArrayList){
				serverList = (ArrayList<String>)o;		
			}
						// 2nd is list of remaining servers
			o = ois.readObject();
			if (o instanceof String) {
				fos = new FileOutputStream("/tmp/Tolooee_480/" + o.toString());
			} else
				throw new Exception("Expected a String (a filename) from stream but got: " + o.getClass());


			// 3rd is the conntents of the file by one BUFFER_SIZE at a time
			Integer bytesRead = 0;
			do {
				// Get size of buffer from stream
				o = ois.readObject();
				if (!(o instanceof Integer))
					throw new Exception("Expected a Integer (the buffer size) from stream but got: " + o.getClass());
				bytesRead = (Integer) o;
				// read the buffer
				o = ois.readObject();
				if (!(o instanceof byte[]))
					throw new Exception("Expected a byte[] (the buffer) from stream but got: " + o.getClass());
				buffer = (byte[]) o;
				// Write data to output file.
				fos.write(buffer, 0, bytesRead);
			} while (bytesRead == BUFFER_SIZE); // we are done when the buffer
												// is not full or is 0
			// If we get to here the file write was successful
			System.out.println("File transfer successful"); // REMOVE ME
			
			o = ois.readObject();
			if (o instanceof File)
				fileName = (File) o;

			serverList.remove(0);
			propagate(serverList, fileName);
			return true;
		} finally { // cleanup
			if (fos != null)
				fos.close();
		}
	}

	// this is only for PA1 and I hate this lol 
	// its exactly the same as Client write method I just use it to follow the 
	// rules of PA1. Its ugly and stupid I know.. but it works =]
	private boolean propagate(ArrayList<String> servers, File fileName) throws Exception{
		if(servers.size() == 0){
			return true;		
		}
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			String server = servers.get(0);
			System.out.println("PROPAGATING TO "+server);
			socket = new Socket(server, 35005);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeObject("store");
			oos.writeObject(servers);
			oos.writeObject(fileName.getName());
			File f = new File("/tmp/Tolooee_480/"+fileName.getName());
			fis = new FileInputStream(f);
			byte[] buffer = new byte[Server.BUFFER_SIZE]; 
			Integer bytesRead = 0;
			// read from file stream into buffer
			// each iteration fills one buffer and sends it
			// once the file is read (ie: bytes read == 0) we stop

			while ((bytesRead = fis.read(buffer)) > 0) { // Stop when there is no more to read
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length)); // don't want overwrite reference if it is being used elsewhere so create
			} // a copy instead of passing the reference. idk if this is needed.
			oos.writeObject(f);
			return ois.readBoolean();
		} 			// Cleanup
		 finally {
			if (socket != null)
				socket.close();
			if (fis != null)
				fis.close();
		}	
	}

	public static void main(String[] args) {
		// its multi threaded so in order to run this we have to call the start
		// method
		// which takes care of constructions and such..
		new Server().start();
	}
}
