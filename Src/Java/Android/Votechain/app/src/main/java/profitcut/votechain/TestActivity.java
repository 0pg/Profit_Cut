package profitcut.votechain;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {
    String id;
    String pk;
    int token;

    private static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        openDatabase();
    }

    public void onButtonReturn(View view) {


        Intent ReturnIntent = new Intent(TestActivity.this, LoginActivity.class);;
        startActivity(ReturnIntent);
    }

    public void onButtonTest2(View view) {
        selectTest();
    }

    private void selectTest() {
        String[] columns = {"id", "pk", "token"};
        Cursor c1 = db.query("user_info", columns, null, null, null, null, null);

        int recordCount = c1.getCount();

        for (int i = 0; i < recordCount; i++) {
            c1.moveToNext();
            id = c1.getString(0);
            pk = c1.getString(1);
            token = c1.getInt(2);
        }

        Toast.makeText(getApplicationContext(), id + pk + Integer.toString(token), Toast.LENGTH_LONG). show();

        c1.close();
    }

    private void openDatabase() {
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, "profitcut", null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists " + "user_info" + "("
                    + " id text not null, "
                    + " pk text not null, "
                    + " token integer not null, "
                    + " primary key(id));"
            );

            db.execSQL("insert into user_info (id, pk, token) values ('test', 'test2', 10);");
        }
        public void onOpen(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
