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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        db = dh.getWritableDatabase();
        get_features();
        try {
            open_socket();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "openSocket error", Toast.LENGTH_LONG).show();
        }
        if(myApp.flag == true) {
            myApp.flag = false;
            startMining sM = new startMining();
            sM.start();
        }
        Toast.makeText(getApplicationContext(), String.valueOf(myApp.chain.size()), Toast.LENGTH_LONG).show();
    }

    private void get_features() {
        myApp.vb = new vote_block(myApp.merkle_tree, myApp.current_transactions, myApp.voters, myApp.nodes);
        myApp.vc = new vote_chain(myApp.chain);
    }

    private void open_socket() throws IOException  {
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
        PutDataBase pd = new PutDataBase(dh);
        db.beginTransaction();
        for(Object b : myApp.chain) {
            if(b instanceof genesisblock) {
                pd.initTable(myApp.subject+"_chain");
                pd.insertChain(((genesisblock) b).getGenesis_h().getIndex(), ((genesisblock) b).getGenesis_h().getDeadline(),
                        ((genesisblock) b).getSubject(), ((genesisblock) b).getConstructor(), ((genesisblock) b).getGenesis_h().getVer(),
                        ((genesisblock) b).getGenesis_h().getTime(), 0, "-", "-", ((genesisblock) b).getBlock_hash());
                for(String candidate : ((genesisblock) b).getCandidates()) {
                    pd.insertCandidates(myApp.subject+"_candidates", candidate);
                }
            }
            else {
                pd.initTable(myApp.subject+"_transaction_pool");
                pd.initTable(myApp.subject+"_merkle_tree");
                pd.insertChain(((block) b).getBlock_h().getIndex(), 0, "-", "-",
                        ((block) b).getBlock_h().getVer(), ((block) b).getBlock_h().getTime(), ((block) b).getBlock_h().getProof(),
                        ((block) b).getBlock_h().getPrevious_hash(), ((block) b).getBlock_h().getMerkle_root(), ((block) b).getBlock_hash());
                for (HashMap<String, String> map : ((block) b).getTransaction_pool()) {
                    pd.insertTransactionPool(myApp.subject+"_transaction_pool", ((block) b).getBlock_h().getIndex(), map.get("voter"), map.get("candidate"));
                    pd.insertVoters(myApp.subject+"_voters", map.get("voter"));
                }
                for (int i = 0; i < ((block) b).getMerkle_tree().size(); i++) {
                    pd.insertMerkleTree(myApp.subject, ((block) b).getBlock_h().getIndex(), i, ((block) b).getMerkle_tree().get(i));
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        myApp.init();
        myApp.flag = true;
        Intent ParticipationIntent = new Intent(MenuActivity.this, VoteParticipationActivity.class);
        startActivity(ParticipationIntent);
    }

    private void createBlock() throws InterruptedException {
        myApp.vb.add_transaction(myApp.vb.new_transaction(myApp.id, "A"));
        if (myApp.chain.get(myApp.chain.size()-1) instanceof block) {
            int index = myApp.chain.size();
            block b = (block) myApp.chain.get(index - 1);
            myApp.bh = new block_header(myApp.ver, index + 1, b.getBlock_hash(), myApp.merkle_tree.get(1));
            myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
        }
        else  {
            int index = 2;
            genesisblock b = (genesisblock) myApp.chain.get(0);
            myApp.bh = new block_header(myApp.ver, index, b.getBlock_hash(), myApp.merkle_tree.get(1));
            myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
        }
        mining mine = new mining();
        mine.start();
    }

    public void exeServer() {
        myApp.cs.handle_verify();
    }

    class mining extends Thread {
        public void run() {
            do {
                try {
                    myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
                } catch (NullPointerException e) {
                    break;
                }
            } while (!myApp.vb.valid_proof(myApp.bh));
            if(myApp.vb.valid_block(myApp.b, myApp.chain)){
                myApp.current_transactions = new ArrayList<>();
                myApp.chain.add(myApp.b);
                myApp.vc.add_block(myApp.b);
                Toast.makeText(getApplicationContext(), String.valueOf(myApp.chain.size()), Toast.LENGTH_LONG).show();
                try {
                    getRsa rsa = new getRsa();
                    PrivateKey prk = (PrivateKey) rsa.decode_privateKey(myApp.prk);
                    myApp.cs.broadcast_block(myApp.b, prk);
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
    }
    class startMining extends Thread {
        public void run() {
            try {
   //             while(myApp.flag  == false) {
                    createBlock();
     //           }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}