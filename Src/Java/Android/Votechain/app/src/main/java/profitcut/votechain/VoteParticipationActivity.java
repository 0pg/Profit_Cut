package profitcut.votechain;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VoteParticipationActivity extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_participation);
    }

    public void onButtonMenu(View view) {
        Intent MenuIntent = new Intent(VoteParticipationActivity.this, MenuActivity.class);

        String subject = "컴퓨터과학과";

        openDatabase();
        createTable(subject);

        startActivity(MenuIntent);
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

    private void createTable(String subject) {
        createuser_infoTable(subject);
        createchainTable(subject);
        createtransaction_poolTable(subject);
        createmerkle_treeTable(subject);
        createvotersTable(subject);
        createcandidatesTable(subject);
    }

    private void createuser_infoTable(String subject) {
        String name = subject + "_user_info";
        db.execSQL("create table if not exists " + name + "("
        + " id integer not null, "
        + " pk text not null, "
        + " token integer not null, "
        + " primary key(id));"
        );
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
}
