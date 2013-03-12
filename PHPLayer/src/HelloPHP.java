import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class HelloPHP {
	private int Iport = 20222;
	private ServerSocket serverSocket;
	private Socket socket;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	
	
	public HelloPHP(){
		
		try {
			serverSocket = new ServerSocket(Iport);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	/* Listen for incoming PHP requests
	 * */
	private void listening(){
		
		try{
			
			while(true){
				socket = serverSocket.accept();
				
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				writer.write("Hello PHP\n");
				writer.flush();
				
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				writer.close();
				reader.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		HelloPHP o = new HelloPHP();
		o.listening();
	}
}
