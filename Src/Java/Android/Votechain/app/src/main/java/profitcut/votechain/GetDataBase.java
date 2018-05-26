package profitcut.votechain;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class GetDataBase extends AppCompatActivity {
    SQLiteDatabase db;

    public GetDataBase() {
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

    public void selectUserInfo(String name) {
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
    }

    public void selectChain(String name) {
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
    }

    public void selectTransactionPool(String name) {
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
    }

    public void selectVoters(String name) {
        String[] columns = {"account"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount; i++) {
            c1.moveToNext();
            array.add(i, c1.getString(0));
        }
    }

    public void selectCandidates(String name) {
        String[] columns = {"candidate"};
        ArrayList<Object> array = new ArrayList<>();
        Cursor c1 = db.query(name, columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount; i++) {
            c1.moveToNext();
            array.add(i, c1.getString(0));
        }
    }
}