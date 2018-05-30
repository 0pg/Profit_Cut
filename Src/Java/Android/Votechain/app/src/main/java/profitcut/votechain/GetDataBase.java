package profitcut.votechain;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

public class GetDataBase extends AppCompatActivity {
    MyApplication myApp = (MyApplication)getApplication();
    SQLiteDatabase db;
    public GetDataBase(SQLiteDatabase db) {
        super();
        this.db = db;
    }

    public HashMap<String, Integer> selectUserInfo() {
        String[] columns = {"id", "token"};
        HashMap<String, Integer> map = new HashMap<>();
        Cursor c1 = db.query("user_info", columns, null, null, null, null, null);
        while(c1.moveToNext()) {
            map.put(c1.getString(c1.getColumnIndex("id")),c1.getInt(c1.getColumnIndex("token")));
        }
        myApp.nodes = map;

        return map;
    }
    public HashMap<String, PublicKey> selectUsers(String name) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        String[] columns = {"id", "pk"};
        HashMap<String, PublicKey> map = new HashMap<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);
        while(c1.moveToNext()) {
            map.put(c1.getString(c1.getColumnIndex("id")), (PublicKey) new getRsa().decode_publickey(c1.getString(c1.getColumnIndex("pk"))));
        }
        myApp.users = map;

        return map;
    }

    public ArrayList selectChain(String name) {
        String[] columns = {"idx", "deadline", "subject", "constructor", "ver", "time", "proof", "previous_hash", "merkle_root", "block_hash"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();
        c1.moveToNext();
        if(c1.getCount() == 0){
            return array;
        }
        array.add(new genesisblock(c1.getString(c1.getColumnIndex("block_hash")), c1.getString(c1.getColumnIndex("subject")),
                selectCandidates(myApp.subject+"_candidates"), new genesisblock_header(c1.getString(c1.getColumnIndex("ver")), c1.getInt(c1.getColumnIndex("idx")),
                c1.getFloat(c1.getColumnIndex("time")), c1.getFloat(c1.getColumnIndex("deadline")))));
        while(c1.moveToNext()){
            array.add(new block(c1.getString(c1.getColumnIndex("block_hash")),
                    selectTransactionPool(myApp.subject, c1.getInt(c1.getColumnIndex("idx"))),
                    selectMerkle(myApp.subject, c1.getInt(c1.getColumnIndex("idx")))
                    , new block_header(c1.getString(c1.getColumnIndex("ver"))
                    , c1.getInt(c1.getColumnIndex("idx"))
                    , c1.getInt(c1.getColumnIndex("proof"))
                    , c1.getFloat(c1.getColumnIndex("time"))
                    , c1.getString(c1.getColumnIndex("previous_hash"))
                    ,c1.getString(c1.getColumnIndex("merkle_root")))));

        }

        myApp.chain = array;
        return array;
    }


    public ArrayList selectTransactionPool(String name, int idx) {
        String[] columns = {"idx", "voter", "candidate"};
        ArrayList<HashMap> array = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        Cursor c1 = db.rawQuery("select voter, candidate from "+name+"_transaction_pool where idx="+idx, null);
        while(c1.moveToNext()) {
            map.put("voter", c1.getString(c1.getColumnIndex("voter")));
            map.put("candidate", c1.getString(c1.getColumnIndex("candidate")));
            array.add(map);
        }
        return array;
    }

    public ArrayList selectVoters(String name) {
        String[] columns = {"account"};
        ArrayList<String> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        while(c1.moveToNext()) {
            array.add(c1.getString(c1.getColumnIndex("account")));
        }
        myApp.voters = array;
        return array;
    }

    public HashMap selectMerkle(String name, int idx) {
        String[] colums = {"idx", "node_idx", "transaction_hash"};
        HashMap<Integer, String> map = new HashMap<>();
        Cursor c1 = db.rawQuery("select node_idx transaction_hash from "+name+"_merkle_tree where idx="+idx, null);
        while(c1.moveToNext()){
            map.put(c1.getInt(0), c1.getString(1));
        }

        return map;
    }

    public ArrayList selectCandidates(String name) {
        String[] columns = {"candidate"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount; i++) {
            c1.moveToNext();
            array.add(i, c1.getString(0));
        }
        return array;
    }

    public Object selectIdentifier() {
        String[] colums = {"id", "prk"};
        ArrayList<Object> array = new ArrayList<>();
        String sql = "select * from identifier";
        Cursor c1 = db.rawQuery(sql, null);

        int recordCount = c1.getCount();

        if (c1.moveToFirst()) {
            array.add(c1.getString(0));
            array.add(c1.getString(1));
        }

        if (recordCount == 0)
            return false;
        else
            return array;
    }
}
