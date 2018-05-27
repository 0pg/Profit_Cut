package profitcut.votechain;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Config extends AppCompatActivity{
    private String id;
    private String prk;
    private ServerSocket tcp_sock;
    private DatagramSocket udp_sock;
    {
        try {
            tcp_sock = new ServerSocket(12223);
            udp_sock = new DatagramSocket(12222);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<Object> chain = new ArrayList<>();
    private ArrayList<HashMap> current_transactions = new ArrayList<>();
    private ArrayList<String> voters = new ArrayList<>();
    private ArrayList<String> candidates = new ArrayList<>();
    private ArrayList<ArrayList> chainlist = new ArrayList<>();
    private LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
    private HashMap<String, HashMap> participations = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrk() {
        return prk;
    }

    public void setPrk(String prk) {
        this.prk = prk;
    }

    public ServerSocket getTcp_sock() {
        return tcp_sock;
    }

    public void setTcp_sock(ServerSocket tcp_sock) {
        this.tcp_sock = tcp_sock;
    }

    public DatagramSocket getUdp_sock() {
        return udp_sock;
    }

    public void setUdp_sock(DatagramSocket udp_sock) {
        this.udp_sock = udp_sock;
    }

    public ArrayList<Object> getChain() {
        return chain;
    }

    public void setChain(ArrayList<Object> chain) {
        this.chain = chain;
    }

    public ArrayList<HashMap> getCurrent_transactions() {
        return current_transactions;
    }

    public void setCurrent_transactions(ArrayList<HashMap> current_transactions) {
        this.current_transactions = current_transactions;
    }

    public ArrayList<String> getVoters() {
        return voters;
    }

    public void setVoters(ArrayList<String> voters) {
        this.voters = voters;
    }

    public ArrayList<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(ArrayList<String> candidates) {
        this.candidates = candidates;
    }

    public ArrayList<ArrayList> getChainlist() {
        return chainlist;
    }

    public void setChainlist(ArrayList<ArrayList> chainlist) {
        this.chainlist = chainlist;
    }

    public LinkedHashMap<Integer, String> getMerkle_tree() {
        return merkle_tree;
    }

    public void setMerkle_tree(LinkedHashMap<Integer, String> merkle_tree) {
        this.merkle_tree = merkle_tree;
    }

    public HashMap<String, HashMap> getParticipations() {
        return participations;
    }

    public void setParticipations(HashMap<String, HashMap> participations) {
        this.participations = participations;
    }
}
