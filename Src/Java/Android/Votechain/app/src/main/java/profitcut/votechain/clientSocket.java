package profitcut.votechain;

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

public class clientSocket {
    private DatagramSocket udp_sock;
    private String subject;
    private String user;
    private int udp_port;
    private int tcp_port;
    private ArrayList<InetAddress> addrlist;
    private ArrayList<ArrayList> chainlist;

    public clientSocket (String subject, DatagramSocket udp_sock,
                          String user, ArrayList<ArrayList> chainlist, ArrayList<InetAddress> addrlist) throws SocketException {
        this.udp_sock = udp_sock;
        this.subject = subject;
        this.user = user;
        this.udp_port = 12222;
        this.tcp_port = 12223;
        this.chainlist = chainlist;
        this.addrlist = addrlist;
    }

    public void broadcast_verify() {
        try {
            HashMap<String, Object> data = new HashMap<>();
            data.put("Profit_Cut?"+this.subject, "Profit_Cut");
            byte[] serializedMessage = message_serialize(data);
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            broadcast(serializedMessage);
            while(this.addrlist.size() < 1) {
                this.udp_sock.receive(packet);
                String pack = new String(packet.getData(), 0, packet.getLength());
                if(pack.contains("Profit_OK")) {
                    tcp_recv_chain(packet.getAddress());
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

    public void broadcast_newbie(PublicKey puk, PrivateKey pk) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        HashMap<String, Object> data = new HashMap<>();
        getRsa rsa = new getRsa();
        data.put("Profit_Cut_newbie"+this.subject, this.user);
        data.put("encrypted", encrypt(pk));
        data.put("Key", rsa.encode_base64(puk.getEncoded()) );
        byte[] serializedMessage = message_serialize(data);
        broadcast(serializedMessage);
    }

    public void tcp_recv_chain(InetAddress addr) throws ClassNotFoundException, IOException {
        Socket tcp_sock = new Socket(addr, this.tcp_port);
        InputStream is = tcp_sock.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        this.chainlist.add((ArrayList)ois.readObject());
        ois.close();
        tcp_sock.close();
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
        oo.flush();
        byte[] serializedMessage = bStream.toByteArray();
        oo.close();
        return serializedMessage;
    }
}

