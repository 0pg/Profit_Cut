package votechain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

public class server_socket implements Runnable{
	private ServerSocket tcp_sock;
	private DatagramSocket udp_sock;
	private ArrayList<HashMap> transacs;
	private ArrayList<block> blocks;
	private ArrayList<Object> chains;
	private String subject;
	private HashMap<String, HashMap> attendances; 
	private ArrayList<Object> chain;
	
	public ArrayList<HashMap> getTransacs() {
		return transacs;
	}

	public ArrayList<block> getBlocks() {
		return blocks;
	}

	public ArrayList<Object> getChains() {
		return chains;
	}

	public HashMap<String, HashMap> getAttendances() {
		return attendances;
	}

	public server_socket(ArrayList<Object> chains, String subject, HashMap attendances) throws IOException {
		super();
		this.tcp_sock = new ServerSocket(12223);
		this.udp_sock = new DatagramSocket(12222);
		this.transacs = new ArrayList<HashMap>();
		this.subject = subject;
		this.blocks = new ArrayList<block>();
		this.chains = chains;
		this.attendances = attendances;
	}
	
	private void udp_server() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] buf = new byte[512];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		this.udp_sock.receive(packet);
		
		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
		HashMap data = (HashMap) iStream.readObject();
		iStream.close();
		
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		packet = new DatagramPacket(buf, buf.length, address, port);
		
		if(data.containsKey("Profit_Cut?"+subject)) {
			buf = "Profit_OK".getBytes();
			packet = new DatagramPacket(buf, buf.length, address, 122222);
			this.udp_sock.send(packet);
			tcp_send_data();
		}
		else if(data.containsKey("Profit_Cut_transac"+subject)) {
			HashMap transac = (HashMap)data.get("Profit_Cut_transac"+subject);
			getRsa rsa = new getRsa();
			String sender = (String) data.get("sender");
			String encrypt_num = (String) data.get("encrypted");
			PublicKey key = (PublicKey) this.attendances.get(sender).get("Key");
			if(rsa.decryption(encrypt_num, key) != "false")
				transacs.add((HashMap) data.get("Profit_Cut_transac"+subject));
		}
		else if(data.containsKey("Profit_Cut_block"+subject)) {
			blocks.add((block) data.get("Profit_Cut_block"+subject));
		}
		else if(data.containsKey("Profit_Cut_newbie"+subject)) {
			String account = (String)data.get("Profit_Cut_newbie"+subject);
			getRsa rsa = new getRsa();
			PublicKey key =  (PublicKey)data.get("key");
			String user = rsa.decryption(account, key);
			HashMap<String, Object> info = new HashMap<>();
			info.put("account", user);
			info.put("Key", key);
			info.put("token", 1);
			if(account != "false") {
				attendances.put(account, info);
			}
		}
	}
	
	public void tcp_send_data() throws IOException {
		while(true) {
			Socket socket = this.tcp_sock.accept();
			ThreadHandler handler = new ThreadHandler(socket);
			handler.start();
		}
	}
	
	private byte[] message_serialize(ArrayList<Object> chain) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream); 
		oo.writeObject(chain);
		oo.close();
		byte[] serializedMessage = bStream.toByteArray();
		
		return serializedMessage;
	}
	@Override
	public void run() {
		while(true) {
			try {
				udp_server();
			} catch (ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class ThreadHandler extends Thread{
		private Socket connectedClientSocket;
		private ArrayList<Object> chain;

		public ThreadHandler(Socket connectedClientSocket) {
			super();
			this.connectedClientSocket = connectedClientSocket;
		}
		public void run() {
			try {
				byte[] chain = message_serialize(this.chain);
				OutputStream os = this.connectedClientSocket.getOutputStream();
				ObjectOutputStream dis = new ObjectOutputStream(os);
				((ObjectOutput) os).writeObject(chain);
				os.flush();
			} catch(IOException ignored) {
			} finally {
				try {
					this.connectedClientSocket.close();
				} catch(IOException ignored) {}
			}
		}
	}
	

}
	

