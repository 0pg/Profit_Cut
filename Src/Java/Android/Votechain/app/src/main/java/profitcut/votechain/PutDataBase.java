package profitcut.votechain;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

public class PutDataBase extends AppCompatActivity {
    SQLiteDatabase db;
    SQLiteOpenHelper dh;
    public PutDataBase(SQLiteOpenHelper dh) {
        super();
        this.dh = dh;
        this.db = this.dh.getWritableDatabase();
    }

    public void initTable(String name) {
        db.execSQL("delete from "+name+";");
    }

    public void insertUsers( String id, int token) {
        String sql = "Insert into user_info values ('" +
                id + "', " +
                token +");";
        try {
            db.execSQL(sql);
        } catch (SQLiteException e) {
        }
    }

    public void insertUserInfo( String name, String id, String puk) {
        ContentValues recordValues = new ContentValues();
        try {
            String sql = "insert into " + name + " values('" + id + "', '" + puk + "');";
            db.execSQL(sql);
        } catch (SQLiteConstraintException e) {
        }
    }

    public void insertChain(String name, int idx, float deadline, String subject, String constructor, String ver, float time, int proof, String previous_hash, String merkle_root, String block_hash) {
        ContentValues recordValues = new ContentValues();
        String sql = "Insert into "+name+" values (" +
                idx + ", "+
                deadline + ", '"+
                subject + "', '"+
                constructor + "', '"+
                ver + "', "+
                time + ", "+
                proof + ", '"+
                previous_hash + "', '"+
                merkle_root + "', '"+
                block_hash + "');";
            db.execSQL(sql);

        }


    public void insertTransactionPool(String name, int idx, String voter, String candidate) {
        String sql = "Insert into "+name+" values (" +
                idx + ", '" +
                voter + "', '" +
                candidate + "');";
        try {
            db.execSQL(sql);
        } catch (SQLiteException e){
        }    }

    public void insertMerkleTree(String name, int idx, int node_idx, String transaction_hash) {
        String sql = "Insert into "+name+" values (" +
                idx + ", " +
                node_idx + ", '" +
                transaction_hash +"');";
        try {
            db.execSQL(sql);
        } catch (SQLiteException e){
        }
    }

    public void insertVoters(String name, String account) {
        String sql = "Insert into "+name+" values ('" +
                account+"');";
        try {
            db.execSQL(sql);
        } catch (SQLiteException e){
        }    }



    public void insertCandidates(String name, String candidate) {
        String sql = "Insert into "+name+" values ('" +
                candidate + "');";
            db.execSQL(sql);

    }

    public void insertIdentifier( String name, String id, String prk, String puk) {
        ContentValues recoValues = new ContentValues();
        try {
            String sql = "insert into " + name + " values('" + id + "', '" + prk + "', '" + puk + "');";
            db.execSQL(sql);
        } catch (SQLiteException e){

        }
    }

    /**
     * @title : public void updateUserInfo(String name, String id, int token)
     * @author : 임현 (hyunzion@gmail.com)
     * @since : 2018 - 05 - 26
     * @brief : user_info table을 update하기 위한 함수
     * @param : - String name : 테이블 명 (예 : user_info)
                 - String id : 변경의 기준이 될 id
                 - int token : 변경할 token
     */
    public void updateUserInfo(String name, String id, int token) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("token", token);
        String[] whereArgs = {id};

        db.update(name, recordValues, "id = ?", whereArgs);
    }

    public void deleteChain(String name, int idx) {
        String [] whereArgs = {Integer.toString(idx)};

        db.delete(name, "idx = ?", whereArgs);
    }

    public void deleteTransactionPool(String name, int idx) {
        String [] whereArgs = {Integer.toString(idx)};

        db.delete(name, "idx = ?", whereArgs);
    }

    public void deleteMerkleTree(String name, int idx) {
        String [] whereArgs = {Integer.toString(idx)};

        db.delete(name, "idx = ?", whereArgs);
    }
}