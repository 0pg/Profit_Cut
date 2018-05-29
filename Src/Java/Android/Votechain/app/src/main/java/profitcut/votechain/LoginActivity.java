package profitcut.votechain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginActivity extends AppCompatActivity {
    SQLiteDatabase db;
    dbHelper dh = new dbHelper(this);
    MyApplication myApp = (MyApplication)getApplication();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createDatabase();
        db = dh.getWritableDatabase();
        Cursor c = db.rawQuery("select * from identifier", null);
        if (c.getCount() > 0) {
            while(c.moveToNext()){
                myApp.id = (c.getString(c.getColumnIndex("id")));
                myApp.prk = (c.getString(c.getColumnIndex("prk")));
            }
            Intent ParticipationIntent = new Intent(LoginActivity.this, VoteParticipationActivity.class);
            startActivity(ParticipationIntent);
        }
    }

    public void onButtonAuthentication(View view) throws SocketException {
        TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String num = telManager.getLine1Number();
        Toast.makeText(getApplicationContext(), num, Toast.LENGTH_LONG);
        getRsa rsa = null;
        try {
            rsa = new getRsa();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getApplicationContext(), "failed2", Toast.LENGTH_LONG).show();
        } catch (NoSuchPaddingException e) {
            Toast.makeText(getApplicationContext(), "failed3", Toast.LENGTH_LONG).show();
        }
        PublicKey Puk = (PublicKey) rsa.get_public();
        PrivateKey Prk = (PrivateKey) rsa.get_private();
        String id = vote_block.hash(num);
        String puk = rsa.encode_base64(Puk.getEncoded());
        String prk = rsa.encode_base64(Prk.getEncoded());
        try {
            new PutDataBase(dh).insertIdentifier(id, prk);
            new PutDataBase(dh).insertUsers(id, 1);
        } catch (Exception e) {
        }
        myApp.id = id;
        myApp.prk = prk;
        myApp.puk = puk;
        try {
            myApp.tcp_sock = new ServerSocket(12223);
            myApp.udp_sock = new DatagramSocket(12222);
        } catch (BindException e) {
        } catch (IOException e) {
        }
        myApp.cs = new clientSocket(id, myApp.udp_sock);
        try {
            myApp.cs.broadcast_newbie(Puk, Prk);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "키 생성 오류", Toast.LENGTH_LONG);
        }
        Intent ParticipationActivity = new Intent(LoginActivity.this, VoteParticipationActivity.class);
        startActivity(ParticipationActivity);
    }

    private void createDatabase() {
        String name = "profitcut";

        try {
            db = openOrCreateDatabase(
                    name,
                    Activity.MODE_PRIVATE,
                    null);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "" + name + " Database 생성 오류", Toast.LENGTH_LONG). show();
        }
    }

    private void createChainTable(String subject) {
        db.execSQL("create table if not exists" +subject+ "_chain ("
                + " idx INTEGER not null, "
                + " deadline REAL not null default '0', "
                + " subject TEXT not null default '-', "
                + " constructor TEXT not null default '-', "
                + " ver TEXT not null, "
                + " time REAL not null, "
                + " proof INTEGER not null default '0', "
                + " previous_hash TEXT not null default '-', "
                + " merkle_tree TEXT not null default '-', "
                + " block_hash TEXT not null, "
                + " primary key(idx));"
        );
    }

    private void user_infoTable() {
        String name = "user_info";
        db.execSQL("create table if not exists " + name + "("
                + " id text not null, "
                + " pk text not null, "
                + " primary key(id));"
        );
    }
}
