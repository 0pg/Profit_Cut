package profitcut.votechain;

import android.app.Application;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MyApplication extends Application {
    final static String ver = new String("V1.0.0");
    static float deadline;
    static String constructor;
    static String id;
    static String prk;
    static String subject;
    static ServerSocket tcp_sock;
    static DatagramSocket udp_sock;
    {
        try {
            tcp_sock = new ServerSocket(12223);
            udp_sock = new DatagramSocket(12222);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static ArrayList<Object> chain = new ArrayList<>();
    static ArrayList<HashMap> current_transactions = new ArrayList<>();
    static ArrayList<String> voters = new ArrayList<>();
    static ArrayList<String> candidates = new ArrayList<>();
    static ArrayList<ArrayList> chainlist = new ArrayList<>();
    static ArrayList<InetAddress> addrlist = new ArrayList<>();
    static LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
    static HashMap<String, PublicKey> users = new HashMap<>();
    static HashMap<String, Integer> nodes = new HashMap<>();
    static vote_block vb;
    static vote_chain vc;
    static serverSocket ss;
    static clientSocket cs;
    static genesisblock_header gh;
    static genesisblock gb;
    static block_header bh;
    static block b;

}
