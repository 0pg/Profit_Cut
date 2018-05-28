package profitcut.votechain;

import android.app.Application;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MyApplication extends Application {
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
    static LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
    static HashMap<String, HashMap> participations = new HashMap<>();

}
