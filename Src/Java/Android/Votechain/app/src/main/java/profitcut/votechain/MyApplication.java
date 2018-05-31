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
    final static String ver = "V1.0.0";
    static int candidate_Num = 0;
    static float deadline;
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
    static boolean flag = true;

    public static void init(){
        candidate_Num = 0;
         deadline = 0;
         constructor = null;
         subject = null;
         chain = new ArrayList<>();
         current_transactions = new ArrayList<>();
         voters = new ArrayList<>();
         candidates = new ArrayList<>();
         chainlist = new ArrayList<>();
         addrlist = new ArrayList<>();
         merkle_tree = new LinkedHashMap<>();
         users = new HashMap<>();
         nodes = new HashMap<>();
         vb = null;
         vc = null;
         ss = null;
         gh = null;
         gb = null;
         bh = null;
         b = null;
    }

}
