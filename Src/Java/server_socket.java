package votechain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

public class server_socket implements Runnable{
	private vote_block vb;
	private vote_chain vc;
	private ServerSocket tcp_sock;
	private DatagramSocket udp_sock;
	private ArrayList<HashMap> transacs;
	private ArrayList<block> blocks;
	private ArrayList<Object> chains;
	private String subject;
	private HashMap<String, HashMap> attendances; 
	
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

	public server_socket(vote_block vb, vote_chain vc, ServerSocket tcp_sock, DatagramSocket udp_sock, ArrayList<Object> chains, ArrayList<HashMap> transacs, String subject, HashMap attendances) throws IOException {
		super();
		this.vb = vb;
		this.vc = vc;
		this.tcp_sock = tcp_sock;
		this.udp_sock = udp_sock;
		this.transacs = transacs;
		this.subject = subject;
		this.blocks = new ArrayList<block>();
		this.chains = chains;
		this.attendances = attendances;
	}
	
	private void udp_server() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InterruptedException {
		byte[] buf = new byte[2048];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		this.udp_sock.receive(packet);
		
		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
		HashMap data = (HashMap)iStream.readObject();
		iStream.close();
		
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		packet = new DatagramPacket(buf, buf.length, address, port);
		
		if(data.containsKey("Profit_Cut?"+subject)) {
			buf = "Profit_OK".getBytes();
			packet = new DatagramPacket(buf, buf.length, address, 12222);
			this.udp_sock.send(packet);
			ThreadHandler2 th2 = new ThreadHandler2();
			th2.start();
		}
		else if(data.containsKey("Profit_Cut_transac"+subject)) {
			HashMap transac = (HashMap)data.get("Profit_Cut_transac"+subject);
			getRsa rsa = new getRsa();
			String sender = (String) data.get("sender");
			String encrypt_num = (String) data.get("encrypted");
			PublicKey key = (PublicKey) this.attendances.get(sender).get("Key");
			if(!rsa.decryption(encrypt_num, key).equals("false")) {
				this.transacs.add(transac);
			}
		}
		else if(data.containsKey("Profit_Cut_block"+subject)) {
			block b = (block) data.get("Profit_Cut_block"+subject);
			if(vb.valid_block(b, this.chains)) {
				vc.add_block(b);
			}
		}
		else if(data.containsKey("Profit_Cut_newbie"+subject)) {
			String account = (String)data.get("Profit_Cut_newbie"+subject);
			String encrypted = (String)data.get("encrypted");
			getRsa rsa = new getRsa();
			PublicKey key = (PublicKey)(rsa.decode_publickey((String)(data.get("Key"))));
			String user = rsa.decryption(encrypted, key);
			HashMap<String, Object> info = new HashMap<>();
			info.put("Key", key);
			info.put("token", 1);
			if(!user.equals("false") && !this.attendances.containsKey(user)) {
				this.attendances.put(account, info);
			}
		}
	}
	
	private void tcp_send_data() throws IOException, InterruptedException {
		while(true) {
			byte[] chain = message_serialize(this.chains);
			Socket socket = this.tcp_sock.accept();
			ThreadHandler handler = new ThreadHandler(socket, chain);
			handler.start();
			handler.join();
		}
	}
	
	private byte[] message_serialize(ArrayList chain) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream); 
		oo.writeObject(chain);
		byte[] serializedMessage = bStream.toByteArray();
		oo.flush();
		oo.close();

		return serializedMessage;
	}
	@Override
	public void run() {
		while(true) {
			try {
				udp_server();
			} catch (ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeySpecException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	class ThreadHandler extends Thread{
		private Socket connectedClientSocket;
		byte[] chain;

		public ThreadHandler(Socket connectedClientSocket, byte[] chain) {
			super();
			this.connectedClientSocket = connectedClientSocket;
			this.chain = chain;
		}
		public void run() {
			try {
				OutputStream os = this.connectedClientSocket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				dos.write(this.chain);
				dos.flush();
				dos.close();
			} catch(IOException ignored) {
			} finally {
				try {
					this.connectedClientSocket.close();
				} catch(IOException ignored) {}
			}
		}
	}
	
	class ThreadHandler2 extends Thread{
		public void run() {
			try {
				tcp_send_data();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
	

