import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server implements Runnable {
	public static final int PORT = 35005;
	public static final int BUFFER_SIZE = 100;
	private Socket metaSocket;
	
	public Server(Socket s){
		metaSocket = s;
	}
	
	@Override
	public void run() {

		try {
			getCmd(metaSocket);
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
					oos.writeObject(saveFile(oos, ois));
				} else if (o.equals("get")){
					//get stuff
					System.out.println("Server: recieved cmd get from: "+socket.getInetAddress().getHostName());
					get(oos, ois);
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

	private void get(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
		String fileName = null;
		FileInputStream fis = null;
		try {
			Object o = ois.readObject();
			if (o instanceof String)
				fileName = o.toString();
			File file = new File("/tmp/Tolooee_480/"+fileName);
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
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			fis.close();
		}
	}

	@SuppressWarnings("unchecked") // I do check but it still complains
	private boolean saveFile(ObjectOutputStream oos, ObjectInputStream ois) throws Exception {
		FileOutputStream fos = null;
		ArrayList<String> serverList = null;
		String fileName = null;
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
				fileName = o.toString();
				fos = new FileOutputStream("/tmp/Tolooee_480/" + o.toString());

			} else {
				throw new Exception("Expected a String (a filename) from stream but got: " + o.getClass());
			}
			// 3rd is the contents of the file by one BUFFER_SIZE at a time
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
				try {
					fos.write(buffer, 0, bytesRead);
				} catch (IOException e) {
					update(fileName.toString(), false);
					return false;
				}
				
				
			} while (bytesRead == BUFFER_SIZE); // we are done when the buffer is not full or is 0
			// If we get to here the file write was successful
			System.out.println("File transfer successful"); // REMOVE ME
			update(fileName.toString(), true);
			o = ois.readObject();
			File file = null;
			if (o instanceof File)
				file = (File) o;

			serverList.remove(0);
			propagate(serverList, file);
			return true;
		} finally { // cleanup
			if (fos != null)
				fos.close();
		}
	}

	private void update(String fileName, boolean outcome){
		Socket sock = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			sock = new Socket(MetaDataServer.name, 35005);
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			oos.writeObject("update");
			oos.writeObject(fileName);
			oos.writeObject(new Boolean(outcome));
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
			if (sock != null)
				sock.close();
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
			}catch (IOException ioe){
				ioe.printStackTrace();
			}
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
			Object o = ois.readObject();
			Boolean b = false;
			if (o instanceof Boolean)
				b = (Boolean)o;
			return b;
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
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			// Should probably have a way to gracfully bring down servers
			// instead
			// of having a script kill the process but I'm lazy
			while (true) {
				Socket s = serverSocket.accept();
				new Thread(new Server(s)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
