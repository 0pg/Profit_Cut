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
import java.security.Permission;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginActivity extends AppCompatActivity {
    SQLiteDatabase db;
    dbHelper dh = new dbHelper(this);
    MyApplication myApp = (MyApplication)getApplication();
    String num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        try {
            this.num = telManager.getLine1Number();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "어플 설정에서 전화번호 권한을 설정해 주세요", Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                this.finish();
            }
            this.finish();
        }
        createDatabase();
        createIdentifier();
        db = dh.getWritableDatabase();
        new GetDataBase(db).selectUsers();
        Cursor c = db.rawQuery("select * from identifier", null);
        if (c.getCount() > 0) {
            while(c.moveToNext()){
                myApp.id = (c.getString(c.getColumnIndex("id")));
            }
            try {
                myApp.tcp_sock = new ServerSocket(12223);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                myApp.udp_sock = new DatagramSocket(12222);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            Intent ParticipationIntent = new Intent(LoginActivity.this, VoteParticipationActivity.class);
            startActivity(ParticipationIntent);
        }
    }

    public void onButtonAuthentication(View view)  {
        getRsa rsa = null;
        try {
            rsa = new getRsa();
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        }
        PublicKey Puk = (PublicKey) rsa.get_public();
        PrivateKey Prk = (PrivateKey) rsa.get_private();
        String id = vote_block.hash(this.num);
        db.execSQL("insert into identifier values('" +id+ "')");
        new PutDataBase(dh).insertUsers(id, 1);
        myApp.id = id;
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

    private void createIdentifier() {
        db.execSQL("create table if not exists identifier ("
                + " id text not null, "
                + " primary key(id));"
        );
    }
}
