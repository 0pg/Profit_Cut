package votechain;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class client_socket {
	private DatagramSocket udp_sock;
	private String subject;
	private String user;
	private int udp_port;
	private int tcp_port;
	private ArrayList<InetAddress> addrlist;
	private ArrayList<Object> chainlist;
	
	public client_socket (String subject,
			String user, ArrayList<Object> chainlist) throws SocketException {
		this.udp_sock = new DatagramSocket(12222);
		this.subject = subject;
		this.user = user;
		this.udp_port = 12222;
		this.tcp_port = 12223;
		this.addrlist = new ArrayList<>();
		this.chainlist = chainlist;
	}

	public void broadcast_verify() {
		try {
		String message = "Profit_Cut?"+this.subject;
		int msg_len = message.length();
		byte[] msg = message.getBytes();
		broadcast(msg);
		byte[] buf = new byte[512];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		while(this.addrlist.size() < 1) {
			this.udp_sock.receive(packet);
			String data = new String(packet.getData(), 0, packet.getLength());
			if(data.contains("Profit_OK")) {
				this.addrlist.add(packet.getAddress());
			}
		}
		} catch(Exception ignored) {
		}
	}
	
	public void broadcast_transac(HashMap transac, PrivateKey pk) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		HashMap<String, Object> data = new HashMap<>();
		data.put("Profit_Cut_transac"+this.subject, transac);
		data.put("sender", this.user);
		data.put("encrypted", encrypt(pk));
		byte[] serializedMessage = message_serialize(data);
		broadcast(serializedMessage);
	}
	
	public void broadcast_block(block b, PrivateKey pk) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		HashMap<String, Object> data = new HashMap<>();
		data.put("Profit_Cut_block"+this.subject, b);
		data.put("sender", this.user);
		data.put("encrypted", encrypt(pk));
		byte[] serializedMessage = message_serialize(data);
		broadcast(serializedMessage);
	}
	
	public void broadcast_newbie(String account, PublicKey pk) throws IOException {
		HashMap<String, Object> data = new HashMap<>();
		data.put("Profit_Cut_noob"+this.subject, account);
		data.put("Key", pk);
		byte[] serializedMessage = message_serialize(data);
		broadcast(serializedMessage);
	}
	
	public void tcp_recv_chain() {
		for(InetAddress ad : this.addrlist) {
			try {
				Socket tcp_sock = new Socket(ad, this.tcp_port);
				InputStream is = tcp_sock.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				this.chainlist.add(ois);
				tcp_sock.close();
			} catch (IOException ignored) { 
			}
		}
	}
	
	private String encrypt(PrivateKey pk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		getRsa rsa = new getRsa();
		byte[] b = rsa.encryption(this.user, pk);
		String s = rsa.encode_base64(b);
		return s;
	}
	
	private void broadcast(byte[] msg) throws IOException {
		byte[] ipAddr = new byte[] {-1, -1, -1, -1};
		InetAddress addr = InetAddress.getByAddress(ipAddr);
		DatagramPacket p = new DatagramPacket(msg, msg.length, addr, this.udp_port);
		this.udp_sock.send(p);
	}
	
	private byte[] message_serialize(HashMap data) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream); 
		oo.writeObject(data);
		oo.close();
		byte[] serializedMessage = bStream.toByteArray();
		
		return serializedMessage;
	}
}
