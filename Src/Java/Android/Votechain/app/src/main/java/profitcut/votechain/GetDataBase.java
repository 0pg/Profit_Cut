package profitcut.votechain;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class GetDataBase extends AppCompatActivity {
    private static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    public GetDataBase() {
        super();

        openDatabase();
    }

    public ArrayList selectUserInfo(String name) {
        String[] columns = {"id", "pk", "token"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount * 3; i += 3) {
            c1.moveToNext();
            array.add(i, c1.getString(0));
            array.add(i + 1, c1.getString(1));
            array.add(i + 2, c1.getInt(2));
        }

        return array;
    }

    public ArrayList selectChain(String name) {
        String[] columns = {"idx", "deadline", "subject", "constructor", "ver", "time", "proof", "precious_hash", "merkle_root", "block_hash"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount * 10; i += 10) {
            c1.moveToNext();
            array.add(i, c1.getInt(0));
            array.add(i + 1, c1.getFloat(1));
            array.add(i + 2, c1.getString(2));
            array.add(i + 3, c1.getString(3));
            array.add(i + 4, c1.getString(4));
            array.add(i + 5, c1.getFloat(5));
            array.add(i + 6, c1.getInt(6));
            array.add(i + 7, c1.getString(7));
            array.add(i + 8, c1.getString(8));
            array.add(i + 9, c1.getString(9));
        }

        return array;
    }

    public ArrayList selectTransactionPool(String name) {
        String[] columns = {"idx", "voter", "candidate"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount * 3; i += 3) {
            c1.moveToNext();
            array.add(i, c1.getInt(0));
            array.add(i + 1, c1.getString(1));
            array.add(i + 2, c1.getString(2));
        }

        return array;
    }

    public ArrayList selectVoters(String name) {
        String[] columns = {"account"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount; i++) {
            c1.moveToNext();
            array.add(i, c1.getString(0));
        }

        return array;
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

    public Object selectIdentifier(String name) {
        String[] colums = {"id", "prk"};
        ArrayList<Object> array = new ArrayList<>();
        String sql = "select * from " + name;
        System.out.println(sql);
        Cursor c1 = db.rawQuery(sql, null);

        int recordCount = c1.getCount();

        if(c1.moveToFirst()){
            array.add(c1.getString(0));
            array.add(c1.getString(1));
        }

        if(recordCount == 0)
            return false;
        else
            return array;
    }

    private void openDatabase() {
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();
    }
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, "profitcut", null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
        }
        public void onOpen(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}