package profitcut.votechain;

import android.app.Application;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MyApplication extends Application {
    final static String ver = new String("V1.0.0");
    static float deadline = Calendar.getInstance().getTimeInMillis() / 1000 + 10000;
    static String constructor;
    static String id;
    static String prk;
    static String puk;
    static String subject;
    static ServerSocket tcp_sock;
    static DatagramSocket udp_sock;
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

    public static void init(){
         float deadline = Calendar.getInstance().getTimeInMillis() / 1000 + 10000;
         String constructor = null;
         String prk = new String();
         String subject = null;
         ServerSocket tcp_sock = null;
         DatagramSocket udp_sock;
         ArrayList<Object> chain = new ArrayList<>();
         ArrayList<HashMap> current_transactions = new ArrayList<>();
         ArrayList<String> voters = new ArrayList<>();
         ArrayList<String> candidates = new ArrayList<>();
         ArrayList<ArrayList> chainlist = new ArrayList<>();
         ArrayList<InetAddress> addrlist = new ArrayList<>();
         LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
         HashMap<String, PublicKey> users = new HashMap<>();
         HashMap<String, Integer> nodes = new HashMap<>();
         vote_block vb = null;
         vote_chain vc = null;
         serverSocket ss = null;
         clientSocket cs = null;
         genesisblock_header gh = null;
         genesisblock gb = null;
         block_header bh = null;
         block b = null;
    }

}
