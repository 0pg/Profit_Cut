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
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MenuActivity extends AppCompatActivity {
    MyApplication myApp = (MyApplication) getApplication();
    SQLiteDatabase db;
    dbHelper dh = new dbHelper(this);
    startMining sM = new startMining();
    pow p = new pow();
    valid v = new valid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        db = dh.getWritableDatabase();
        get_features();
        try {
            open_socket();
        } catch (IOException e) {
        }
        if (myApp.flag == true) {
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

    public void onButtonParticipation(View view) throws InterruptedException {
        Toast.makeText(getApplicationContext(), "체인 정보 저장하는 중...", Toast.LENGTH_LONG).show();
        myApp.flag = true;
        v.join();
        sM.join();
        PutDataBase pd = new PutDataBase(dh);
        db.beginTransaction();
        pd.initTable(myApp.subject + "_chain");
        pd.initTable(myApp.subject + "_transaction_pool");
        pd.initTable(myApp.subject + "_merkle_tree");
        pd.initTable(myApp.subject+"_candidates");
        pd.initTable(myApp.subject+"_voters");
        for (Object b : myApp.chain) {
            if (b instanceof genesisblock) {
                pd.insertChain(myApp.subject + "_chain", ((genesisblock) b).getGenesis_h().getIndex(), ((genesisblock) b).getGenesis_h().getDeadline(),
                        ((genesisblock) b).getSubject(), ((genesisblock) b).getConstructor(), ((genesisblock) b).getGenesis_h().getVer(),
                        ((genesisblock) b).getGenesis_h().getTime(), 0, "-", "-", ((genesisblock) b).getBlock_hash());
                for (String candidate : ((genesisblock) b).getCandidates()) {
                    pd.insertCandidates(myApp.subject + "_candidates", candidate);
                }
            } else {
                pd.insertChain(myApp.subject + "_chain", ((block) b).getBlock_h().getIndex(), 0, "-", "-",
                        ((block) b).getBlock_h().getVer(), ((block) b).getBlock_h().getTime(), ((block) b).getBlock_h().getProof(),
                        ((block) b).getBlock_h().getPrevious_hash(), ((block) b).getBlock_h().getMerkle_root(), ((block) b).getBlock_hash());
                for (HashMap<String, String> map : ((block) b).getTransaction_pool()) {
                    pd.insertTransactionPool(myApp.subject + "_transaction_pool", ((block) b).getBlock_h().getIndex(), map.get("voter"), map.get("candidate"));
                    pd.insertVoters(myApp.subject + "_voters", map.get("voter"));
                }
                for (int i = 1; i <= ((block) b).getMerkle_tree().size(); i++) {
                    pd.insertMerkleTree(myApp.subject, ((block) b).getBlock_h().getIndex(), i, ((block) b).getMerkle_tree().get(i));
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        myApp.init();
        Intent ParticipationIntent = new Intent(MenuActivity.this, VoteParticipationActivity.class);
        startActivity(ParticipationIntent);
    }

    private synchronized void createBlock() throws InterruptedException {
        myApp.vb.add_transaction(myApp.vb.new_transaction(myApp.id, "A"));
        System.out.println(myApp.chain.size());
        if (myApp.chain.get(myApp.chain.size() - 1) instanceof genesisblock) {
            genesisblock gb = (genesisblock) myApp.chain.get(0);
            myApp.bh = new block_header("V.1.0.0", myApp.chain.size() + 1,
                    ((genesisblock) myApp.chain.get(myApp.chain.size() - 1)).getBlock_hash(),
                    myApp.merkle_tree.get(1));
        } else {
            myApp.bh = new block_header("V.1.0.0", myApp.chain.size() + 1,
                    ((block) myApp.chain.get(myApp.chain.size() - 1)).getBlock_hash(),
                    myApp.merkle_tree.get(1));
        }
        p.start();
        p.join();

        myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);

        v.start();

        hashing();
    }

    public void exeServer() {
        myApp.cs.handle_verify();
    }

    public void hashing() {
        System.out.println(myApp.b.getBlock_hash());
        System.out.println(myApp.vb.hash(myApp.b.getBlock_h().toString()));
        if (myApp.vb.valid_block(myApp.b, myApp.chain)) {
            System.out.println(myApp.b.getBlock_hash().equals(myApp.vb.hash(myApp.b.getBlock_h().toString())));
            myApp.current_transactions = new ArrayList<>();
            myApp.vc.add_block(myApp.b);
            try {
                getRsa rsa = new getRsa();
                PrivateKey prk = (PrivateKey) rsa.decode_privateKey(myApp.prk);
                new clientSocket(myApp.id, new DatagramSocket(12222)).broadcast_block(myApp.b, prk);
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
    }

    class pow extends  Thread {
        public void run() {
            myApp.vb.proof_of_work(myApp.bh);
        }
    }

    class valid extends  Thread {
        public void run() {
            do {
                try {
                    myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
                } catch (NullPointerException e) {
                    break;
                }
            } while (!myApp.vb.valid_proof(myApp.bh));
        }
    }

    class startMining extends Thread {
        public synchronized void run() {
            try {
                while (myApp.flag == false) {
                    createBlock();
                }
            } catch (InterruptedException e) {
                myApp.flag = true;
                try {
                    join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
