package profitcut.votechain;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MenuActivity extends AppCompatActivity {
    MyApplication myApp = (MyApplication) getApplication();
    SQLiteDatabase db;
    dbHelper dh = new dbHelper(this);
    static int runningThread = 0;
    startMining sM = new startMining();
 //   valid v = new valid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        db = dh.getWritableDatabase();
        get_features();
        Toast.makeText(getApplicationContext(), String.valueOf(myApp.flag), Toast.LENGTH_LONG).show();
        try {
            open_socket();
        } catch (IOException e) {
        }
        if (myApp.flag) {
            myApp.flag = false;
            sM.start();
        }
    }

    private void get_features() {
        myApp.vb = new vote_block(myApp.merkle_tree, myApp.current_transactions, myApp.voters, myApp.nodes);
        myApp.vc = new vote_chain(myApp.chain);
    }

    private void open_socket() throws IOException {
        myApp.cs = new clientSocket(myApp.subject, myApp.udp_sock, myApp.id, myApp.users, myApp.chainlist, myApp.addrlist);
        myApp.ss = new serverSocket(myApp.vb, myApp.vc, myApp.tcp_sock, myApp.udp_sock, myApp.chain, myApp.current_transactions, myApp.subject, myApp.users, myApp.nodes, myApp.cs);
        Thread sth = new Thread(myApp.ss);
        sth.start();
        exeServer();
    }


    public void onButtonVote(View view) {
        Intent VoteIntent = new Intent(MenuActivity.this, VoteActivity.class);
        startActivity(VoteIntent);
    }

    public void onButtonState(View view) {
        Intent StateIntent = new Intent(MenuActivity.this, VotingStateActivity.class);
        startActivity(StateIntent);
    }

    public void onButtonCheck(View view) {
        Intent CheckIntent = new Intent(MenuActivity.this, CheckChainActivity.class);
        startActivity(CheckIntent);
    }

    public void onButtonParticipation(View view) throws InterruptedException, NoSuchPaddingException, NoSuchAlgorithmException {
        String subject = myApp.subject;
        HashMap<String, PublicKey> users = myApp.users;
        ArrayList<Object> chain = myApp.chain;
        myApp.flag = true;
      //  sM.join();
        Toast.makeText(getApplicationContext(), "체인 정보 저장하는 중...", Toast.LENGTH_LONG).show();
        PutDataBase pd = new PutDataBase(dh);
        System.out.println(subject);
        db.beginTransaction();
        pd.initTable(subject + "_chain");
        pd.initTable(subject + "_transaction_pool");
        pd.initTable(subject + "_merkle_tree");
        pd.initTable(subject+"_candidates");
        pd.initTable(subject+"_voters");
        pd.initTable(subject+"_user_info");
        for (Object b : chain) {
            if (b instanceof genesisblock) {
                pd.insertChain(subject + "_chain", ((genesisblock) b).getGenesis_h().getIndex(), ((genesisblock) b).getGenesis_h().getDeadline(),
                        ((genesisblock) b).getSubject(), ((genesisblock) b).getConstructor(), ((genesisblock) b).getGenesis_h().getVer(),
                        ((genesisblock) b).getGenesis_h().getTime(), 0, "-", "-", ((genesisblock) b).getBlock_hash());
                for (String candidate : ((genesisblock) b).getCandidates()) {
                    pd.insertCandidates(subject + "_candidates", candidate);
                }
            } else {
                pd.insertChain(subject + "_chain", ((block) b).getBlock_h().getIndex(), 0, "-", "-",
                        ((block) b).getBlock_h().getVer(), ((block) b).getBlock_h().getTime(), ((block) b).getBlock_h().getProof(),
                        ((block) b).getBlock_h().getPrevious_hash(), ((block) b).getBlock_h().getMerkle_root(), ((block) b).getBlock_hash());
                for (HashMap<String, String> map : ((block) b).getTransaction_pool()) {
                    pd.insertTransactionPool(subject + "_transaction_pool", ((block) b).getBlock_h().getIndex(), map.get("voter"), map.get("candidate"));
                    pd.insertVoters(subject + "_voters", map.get("voter"));
                }
                for (int i = 1; i <= ((block) b).getMerkle_tree().size(); i++) {
                    pd.insertMerkleTree(subject+"_merkle_tree", ((block) b).getBlock_h().getIndex(), i, ((block) b).getMerkle_tree().get(i));
                }
                for (String user : users.keySet()) {
                    pd.insertUserInfo(subject+"_user_info", user, new getRsa().encode_base64(users.get(user).getEncoded()));
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Thread.sleep(1500);
        Intent ParticipationIntent = new Intent(MenuActivity.this, VoteParticipationActivity.class);
        startActivity(ParticipationIntent);
    }

    private void createBlock() throws InterruptedException {
        System.out.println("in create");
        if (myApp.chain.get(myApp.chain.size() - 1) instanceof genesisblock) {
            System.out.println("if");
            genesisblock gb = (genesisblock) myApp.chain.get(0);
            myApp.bh = new block_header("V.1.0.0", myApp.chain.size() + 1,
                    ((genesisblock) myApp.chain.get(myApp.chain.size() - 1)).getBlock_hash(),
                    myApp.merkle_tree.get(1));

        } else {
            System.out.println("else");
            myApp.bh = new block_header("V.1.0.0", myApp.chain.size() + 1,
                    ((block) myApp.chain.get(myApp.chain.size() - 1)).getBlock_hash(),
                    myApp.merkle_tree.get(1));
        }
        pow p = new pow();
        p.start();
        p.join();
        System.out.println("pstart");
        hashing(myApp.bh);
    }

    public void exeServer() {
        myApp.cs.handle_verify();
    }

    public void hashing(block_header bh) {
        try {
            System.out.println("hashin");
            String hash = myApp.vb.hash(bh.toString());
            System.out.println("wrong: "+ hash);
            block b = new block(myApp.current_transactions,myApp.merkle_tree, bh);
            if (myApp.vb.valid_block(b, myApp.chain)) {
                System.out.println("right!!!!!: "+ hash);
                myApp.current_transactions = new ArrayList<>();
                myApp.chain.add(b);
                try {
                    getRsa rsa = new getRsa();
                    PrivateKey prk = (PrivateKey) rsa.decode_privateKey(myApp.prk);
                    new clientSocket(myApp.id, myApp.udp_sock).broadcast_block(b, prk);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {

        }
    }

    class pow extends  Thread {
        public synchronized void run() {
            myApp.vb.proof_of_work(myApp.bh);
        }
    }

//    class valid extends  Thread {
//        block_header bh;
//        public void setBh(block_header bh){
//            this.bh = bh;
//        }
//        public synchronized void run() {
//            while (!myApp.vb.valid_proof(bh) && !myApp.flag){
//                hashing(this.bh);
//            }
//            ;
//        }
//    }

    class startMining extends Thread {
        public synchronized void run() {
            MenuActivity.runningThread ++;
            try {
                while (!myApp.flag) {
                    System.out.println("_createblock");
                    createBlock();
                    System.out.println("creat_");
                    System.out.println("2222222222222");
                }
            } catch (InterruptedException e) {

            }
            MenuActivity.runningThread --;
        }
    }
}
