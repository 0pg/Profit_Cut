package profitcut.votechain;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class VoteParticipationActivity extends AppCompatActivity {
    EditText vote_name_Text;
    SQLiteDatabase db;
    MyApplication myApp = (MyApplication) getApplication();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_participation);
        vote_name_Text = (EditText) findViewById(R.id.vote_name_Text);
    }

    public void onButtonMenu(View view) throws IOException {
        String vote_name = vote_name_Text.getText().toString();

        Intent MenuIntent = new Intent(VoteParticipationActivity.this, MenuActivity.class);

        if (vote_name.equals("execution")) {
        // if (vote_name.equals("컴퓨터과학과")) {
            String subject = vote_name;
            myApp.subject = subject;
            openDatabase();
            createTable(subject);

            open_socket();
            startActivity(MenuIntent);

        } else {
            Toast.makeText(getApplicationContext(), "존재하지 않는 투표입니다.", Toast.LENGTH_LONG). show();
        }
    }

    private void openDatabase() {
        String name = "profitcut";

        try {
            db = openOrCreateDatabase(
                    name,
                    Activity.MODE_PRIVATE,
                    null);
        } catch (Exception ex) {
        }
    }

    private void get_data() {
        new GetDataBase().selectChain(myApp.subject);
    }

    private void open_socket() throws IOException {
        myApp.vb = new vote_block(myApp.merkle_tree, myApp.current_transactions, myApp.voters, myApp.users);
        myApp.vc = new vote_chain(myApp.chain);
        myApp.cs = new clientSocket(myApp.subject, myApp.udp_sock, myApp.id, myApp.users, myApp.chainlist, myApp.addrlist);
        myApp.ss = new serverSocket(myApp.vb, myApp.vc, myApp.tcp_sock, myApp.udp_sock, myApp.chain, myApp.current_transactions, myApp.subject, myApp.users, myApp.cs);
        Thread sth = new Thread(myApp.ss);
        sth.start();
    }

    private void createTable(String subject) {
        createchainTable(subject);
        createtransaction_poolTable(subject);
        createmerkle_treeTable(subject);
        createvotersTable(subject);
        createcandidatesTable(subject);
    }

    private void createchainTable(String subject) {
        String name = subject + "_chain";
        db.execSQL("create table if not exists " + name + "("
                + " idx integer not null, "
                + " deadline real not null default '0', "
                + " subject text not null default '-', "
                + " constructor text not null default '-', "
                + " ver text not null, "
                + " time real not null, "
                + " proof integer not null default '0', "
                + " previous_hash text not null default '-', "
                + " merkle_root text not null default '-', "
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
}
