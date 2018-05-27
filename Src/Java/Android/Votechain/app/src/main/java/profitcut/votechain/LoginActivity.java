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
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.crypto.NoSuchPaddingException;

public class LoginActivity extends AppCompatActivity {
    private String id;
    private String prk;
    private ServerSocket tcp_sock;
    private DatagramSocket udp_sock;
    {
        try {
            tcp_sock = new ServerSocket(12223);
            udp_sock = new DatagramSocket(12222);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<Object> chain = new ArrayList<>();
    private ArrayList<HashMap> current_transactions = new ArrayList<>();
    private ArrayList<String> voters = new ArrayList<>();
    private ArrayList<String> candidates = new ArrayList<>();
    private ArrayList<ArrayList> chainlist = new ArrayList<>();
    private LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
    private HashMap<String, HashMap> participations = new HashMap<>();
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createDatabase();
        user_infoTable();
//        GetDataBase gd = new GetDataBase();
//        PutDataBase pd = new PutDataBase();
//        ArrayList<Object> info1 = (ArrayList)gd.selectIdentifier("identifier");
//        System.out.println(info1.toString());
        String info1 = new TestActivity().selectTest();
        if(info1.length() > 1){
            ArrayList<String> info = (ArrayList)new GetDataBase().selectIdentifier("identifier");
            Toast.makeText(getApplicationContext(), info1, Toast.LENGTH_LONG). show();
            info = (ArrayList)new GetDataBase().selectIdentifier("identifier");
                this.id = info.get(0);
                this.prk = info.get(1);
                Intent ParticipationIntent = new Intent(LoginActivity.this, VoteParticipationActivity.class);
                startActivity(ParticipationIntent);
        }else{
            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
            identifierTable();
            TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String num = telManager.getLine1Number();
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
            System.out.println(prk);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            new PutDataBase().insertIdentifier("identifier", id, prk);
            ArrayList<Object> info = (ArrayList)new GetDataBase().selectIdentifier("identifier");

        }

    }


    public void onButtonTest(View view) {
        Intent TestIntent = new Intent(LoginActivity.this, TestActivity.class);
        startActivity(TestIntent);
    }

    public void onButtonAuthentication(View view) {
        Intent ParticipationIntent = new Intent(LoginActivity.this, VoteParticipationActivity.class);

        startActivity(ParticipationIntent);
    }

    private void createDatabase() {
        String name = "profitcut";

        try {
            db = openOrCreateDatabase(
                    name,
                    Activity.MODE_PRIVATE,
                    null);

            Toast.makeText(getApplicationContext(), "" + name + " Database 생성 ", Toast.LENGTH_LONG). show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "" + name + " Database 생성 오류", Toast.LENGTH_LONG). show();
        }
    }

    private void identifierTable() {
        String name = "identifier";
        db.execSQL("create table if not exists " + name + "("
                + " id text not null, "
                + " prk text not null);"
    //            + " primary key(id));"
        );
    }

    private void user_infoTable() {
        String name = "user_info";
        db.execSQL("create table if not exists " + name + "("
                + " id text not null, "
                + " pk text not null, "
                + " token integer not null, "
                + " primary key(id));"
        );
    }
}
