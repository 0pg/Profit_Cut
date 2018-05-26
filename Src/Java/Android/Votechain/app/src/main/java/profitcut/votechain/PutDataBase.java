package profitcut.votechain;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

public class PutDataBase extends AppCompatActivity {
    SQLiteDatabase db;

    public PutDataBase() {
        super();

        openDatabase();
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

    public void insertUserInfo(String name, String id, String pk, int token) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("id", id);
        recordValues.put("pk", pk);
        recordValues.put("token", token);

        db.insert(name, null, recordValues);
    }

    public void insertChain(String name, int idx, float deadline, String subject, String constructor, String ver, float time, int proof, String previous_hash, String merkle_root, String block_hash) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("idx", idx);
        recordValues.put("deadline", deadline);
        recordValues.put("subject", subject);
        recordValues.put("constructor", constructor);
        recordValues.put("ver", ver);
        recordValues.put("time", time);
        recordValues.put("proof", proof);
        recordValues.put("previous_hash", previous_hash);
        recordValues.put("merkle_root", merkle_root);
        recordValues.put("block_hash", block_hash);

        db.insert(name, null, recordValues);
    }

    public void insertTransactionPool(String name, int idx, String voter, String candidate) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("idx", idx);
        recordValues.put("voter", voter);
        recordValues.put("candidate", candidate);

        db.insert(name, null, recordValues);
    }

    public void insertMerkleTree(String name, int idx, int node_idx, String transaction_hash) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("idx", idx);
        recordValues.put("node_idx", node_idx);
        recordValues.put("transaction_hash", transaction_hash);

        db.insert(name, null, recordValues);
    }

    public void insertVoters(String name, String account) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("account", account);

        db.insert(name, null, recordValues);
    }

    public void insertCandidates(String name, String candidate) {
        ContentValues recordValues = new ContentValues();

        recordValues.put("candidate", candidate);

        db.insert(name, null, recordValues);
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