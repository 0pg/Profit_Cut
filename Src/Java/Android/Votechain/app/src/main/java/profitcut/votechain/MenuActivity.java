package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MenuActivity extends AppCompatActivity {
    MyApplication myApp = (MyApplication) getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        exeServer();
        try {
            createBlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public void onButtonParticipation(View view) {
        Intent ParticipationIntent = new Intent(MenuActivity.this, VoteParticipationActivity.class);
        startActivity(ParticipationIntent);
    }

    private void createBlock() throws InterruptedException {
        if (myApp.chain.size() > 1) {
            int index = myApp.chain.size();
            block b = (block) myApp.chain.get(index - 1);
            myApp.bh = new block_header(myApp.ver, index, b.getBlock_hash(), myApp.merkle_tree.get(1));
            myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
            startHashing();
        }
        else if(myApp.chain.size() == 1) {
            int index = 2;
            genesisblock b = (genesisblock) myApp.chain.get(0);
            myApp.bh = new block_header(myApp.ver, index, b.getBlock_hash(), myApp.merkle_tree.get(1));
            myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
            startHashing();
        }
        else {
            int index = 1;
            myApp.gh = new genesisblock_header(myApp.ver, index, Calendar.getInstance().getTimeInMillis() / 1000, myApp.deadline);
            myApp.gb = new genesisblock(myApp.vb.hash(myApp.gh.toString()), myApp.subject, myApp.constructor, myApp.candidates ,myApp.gh);
            myApp.chain.add(myApp.gb);
        }
    }

    private void startHashing() throws InterruptedException {
        ThreadHandler th = new ThreadHandler();
        ThreadHandler2 th2 = new ThreadHandler2();
        th.start();
        th2.start();
        th.join();
        th2.join();

    }

    public void exeServer() {
        myApp.cs.handle_init();
        myApp.cs.handle_verify();
    }

    class ThreadHandler extends Thread {
        public void run() {
            myApp.vb.proof_of_work(myApp.bh);
        }
    }

    class ThreadHandler2 extends Thread {
        public void run() {
            do {
                try {
                    myApp.b = new block(myApp.current_transactions, myApp.merkle_tree, myApp.bh);
                } catch (NullPointerException e) {
                    break;
                }
            } while (!myApp.vb.valid_proof(myApp.bh));
            if(myApp.vb.valid_block(myApp.b, myApp.chain)){
                myApp.vc.add_block(myApp.b);
                myApp.current_transactions = new ArrayList<>();
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
}