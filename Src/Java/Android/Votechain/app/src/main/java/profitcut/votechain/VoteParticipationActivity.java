package profitcut.votechain;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class VoteParticipationActivity extends AppCompatActivity {
    EditText vote_name_Text;
    SQLiteDatabase db;
    dbHelper dh = new dbHelper(this);
    MyApplication myApp = (MyApplication) getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = dh.getWritableDatabase();
        setContentView(R.layout.activity_vote_participation);
        vote_name_Text = (EditText) findViewById(R.id.vote_name_Text);

    }

    public void onButtonMenu(View view) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        myApp.subject = vote_name_Text.getText().toString();
        createTable(myApp.subject);
        if(getData()) {
            Toast.makeText(getApplicationContext(), "투표 생성을 먼저 해주세요! (이미 진행중인 같은 주제의 투표와 합쳐질 수 있습니다)", Toast.LENGTH_LONG).show();
            Intent MakeIntent = new Intent(VoteParticipationActivity.this, MakeVoteActivity.class);
            startActivity(MakeIntent);
        }else {
                new PutDataBase(dh).insertIdentifier(myApp.subject + "_identifier", myApp.id, myApp.prk, myApp.puk);
                new PutDataBase(dh).insertUserInfo(myApp.subject + "_user_info", myApp.id, myApp.puk);
                if (((myApp.id != null) && (myApp.puk != null) && (myApp.prk != null))) {
                    new clientSocket(myApp.id, myApp.udp_sock).broadcast_users(myApp.prk, myApp.puk);
                }
            Intent MenuIntent = new Intent(VoteParticipationActivity.this, MenuActivity.class);
            startActivity(MenuIntent);
        }
    }

    private void createTable(String subject) {
        createidentifier(subject);
        createchainTable(subject);
        createtransaction_poolTable(subject);
        createmerkle_treeTable(subject);
        createvotersTable(subject);
        createcandidatesTable(subject);
        createusersTable(subject);
    }

    private void createchainTable(String subject) {
        String name = subject + "_chain";
        db.execSQL("create table if not exists " + name + "("
                + " idx integer not null, "
                + " deadline real not null, "
                + " subject text not null, "
                + " constructor text not null, "
                + " ver text not null, "
                + " time real not null, "
                + " proof integer not null, "
                + " previous_hash text not null, "
                + " merkle_root text not null, "
                + " block_hash text not null, "
                + " primary key(idx));");
    }

    private void createtransaction_poolTable(String subject) {
        String name = subject + "_transaction_pool";
        db.execSQL("create table if not exists " + name + "("
                + " idx integer not null, "
                + " voter text not null, "
                + " candidate text not null, "
                + " primary key(idx));");
    }

    private void createmerkle_treeTable(String subject) {
        String name = subject + "_merkle_tree";
        db.execSQL("create table if not exists " + name + "("
                + " idx integer not null, "
                + " node_idx integer not null, "
                + " transaction_hash text not null, "
                + " primary key(idx));");
    }

    private void createvotersTable(String subject) {
        String name = subject + "_voters";
        db.execSQL("create table if not exists " + name + "("
                + " account text not null, "
                + " primary key(account));");
    }

    private void createcandidatesTable(String subject) {
        String name = subject + "_candidates";
        db.execSQL("create table if not exists " + name + "("
                + " candidate text not null, "
                + " primary key(candidate));");
    }

    private void createusersTable(String subject) {
        String name = subject+ "_user_info";
        db.execSQL("create table if not exists " + name + "("
                + " id text not null, "
                + " pk text not null, "
                + " primary key(id));"
        );
    }

    private void createidentifier(String subject) {
        String name = subject+"_identifier";
        db.execSQL("create table if not exists " + name + "("
                + " id text not null, "
                + " prk text not null, "
                + " puk test not null, "
                + " primary key(id));"
        );
    }

    private boolean getData() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        GetDataBase gd = new GetDataBase(db);
        ArrayList<Object> arr = gd.selectChain(myApp.subject + "_chain");
        if(arr.size() == 0) {
            return true;
        }
        myApp.users = (HashMap)gd.selectIdentifier(myApp.subject+"_identifier");
        gd.selectUserinfo(myApp.subject + "_user_info");
        gd.selectVoters(myApp.subject + "_voters");
        gd.selectCandidates(myApp.subject + "_candidates");

        return false;
    }
}
