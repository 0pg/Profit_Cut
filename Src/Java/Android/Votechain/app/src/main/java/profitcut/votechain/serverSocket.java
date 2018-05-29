package profitcut.votechain;

import android.widget.Toast;

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

public class serverSocket implements Runnable{
    private vote_block vb;
    private vote_chain vc;
    private ServerSocket tcp_sock;
    private DatagramSocket udp_sock;
    private ArrayList<HashMap> transacs;
    private ArrayList<block> blocks;
    private ArrayList<Object> chains;
    private String subject;
    private HashMap<String, PublicKey> attendances;
    private HashMap<String, Integer> nodes;
    private clientSocket cs;

    public ArrayList<HashMap> getTransacs() {
        return transacs;
    }

    public ArrayList<block> getBlocks() {
        return blocks;
    }

    public ArrayList<Object> getChains() {
        return chains;
    }

    public HashMap<String, PublicKey> getAttendances() {
        return attendances;
    }

    public serverSocket(vote_block vb, vote_chain vc, ServerSocket tcp_sock, DatagramSocket udp_sock, ArrayList<Object> chains, ArrayList<HashMap> transacs, String subject, HashMap attendances, HashMap nodes, clientSocket cs) throws IOException {
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
        this.nodes = nodes;
        this.cs = cs;
    }

    private void udp_server() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InterruptedException {
        byte[] buf = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        System.out.println(packet.getAddress());
        while(packet.getAddress()==null);
        this.udp_sock.receive(packet);

        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        HashMap data = (HashMap)iStream.readObject();
        iStream.close();

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);

        if(data.containsKey("Profit_Cut_info"+this.subject)){
            buf = "Profit_OK".getBytes();
            packet = new DatagramPacket(buf, buf.length, address, 12222);
            this.udp_sock.send(packet);
            ThreadHandler3 th3 = new ThreadHandler3();
            th3.start();
        }
        else if(data.containsKey("Profit_Cut?"+this.subject)) {
            buf = "Profit_OK".getBytes();
            packet = new DatagramPacket(buf, buf.length, address, 12222);
            this.udp_sock.send(packet);
            ThreadHandler2 th2 = new ThreadHandler2();
            th2.start();
        }
        else if(data.containsKey("Profit_Cut_transac"+this.subject)) {
            HashMap transac = (HashMap)data.get("Profit_Cut_transac"+subject);
            getRsa rsa = new getRsa();
            String sender = (String) data.get("sender");
            String encrypt_num = (String) data.get("encrypted");
            PublicKey key = this.attendances.get(sender);
            if(!rsa.decryption(encrypt_num, key).equals("false") && vb.add_transaction(transac)) {
                byte[] pack = message_serialize2(data);
                cs.broadcast(pack);
            }
        }
        else if(data.containsKey("Profit_Cut_block"+this.subject)) {
            block b = (block) data.get("Profit_Cut_block"+this.subject);
            if(vb.valid_block(b, this.chains)) {
                vc.add_block(b);
                charge(b);
                byte[] pack = message_serialize2(data);
                cs.broadcast(pack);
            }
        }
        else if(data.containsKey("Profit_Cut_userinfo"+this.subject)) {
            HashMap<String, PublicKey> users = (HashMap)data.get("Profit_Cut_userinfo"+this.subject);
            String encrypted = (String)data.get("encrypted");
            getRsa rsa = new getRsa();
            PublicKey key = (this.attendances.get("Key"));
            String user = rsa.decryption(encrypted, key);
            if(!user.equals("false")) {
                this.attendances.putAll(users);
            }
        }

        else if(data.containsKey("Profit_Cut_newbie")) {
            String account = (String)data.get("Profit_Cut_newbie");
            String encrypted = (String)data.get("encrypted");
            getRsa rsa = new getRsa();
            PublicKey key = (PublicKey)(rsa.decode_publickey((String)(data.get("Key"))));
            String user = rsa.decryption(encrypted, key);
            if(!user.equals("false") && !this.attendances.containsKey(user)) {
                this.attendances.put(account, key);
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

    private void tcp_send_attendance() throws IOException, InterruptedException {
        while(true) {
            byte[] att = message_serialize2(this.attendances);
            Socket socket = this.tcp_sock.accept();
            ThreadHandler handler = new ThreadHandler(socket, att);
            handler.start();
            handler.join();
        }
    }

    private void charge(block b) {
        for(HashMap<String, String> map : b.getTransaction_pool()) {
            String voter = map.get("voter");
            int token = this.nodes.get("token");
            this.nodes.put(voter, token - 1);
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

    private byte[] message_serialize2(HashMap attend) throws IOException {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(attend);
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

    class ThreadHandler3 extends Thread{
        public void run() {
            try {
                tcp_send_attendance();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}