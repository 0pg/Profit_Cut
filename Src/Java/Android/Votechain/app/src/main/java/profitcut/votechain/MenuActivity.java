package profitcut.votechain;

import android.content.Intent;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MenuActivity extends AppCompatActivity {
    MyApplication myApp = (MyApplication) getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        get_features();
        try {
            open_socket();
            createBlock();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "openSocket error", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), "createblock error", Toast.LENGTH_LONG).show();
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

    public void onButtonParticipation(View view) {
        Intent ParticipationIntent = new Intent(MenuActivity.this, VoteParticipationActivity.class);
        myApp.init();
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

    }

    private void startHashing() throws InterruptedException {
        ThreadHandler th = new ThreadHandler();
        ThreadHandler2 th2 = new ThreadHandler2();
        th.start();
        th2.start();
        th.join();
        th2.join();
        startHashing();
    }

    public void exeServer() {
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
                    Toast.makeText(getApplicationContext(), myApp.b.toString(), Toast.LENGTH_LONG).show();
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