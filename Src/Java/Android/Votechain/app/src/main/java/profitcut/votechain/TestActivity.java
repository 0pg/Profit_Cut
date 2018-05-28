package profitcut.votechain;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {
    private static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void onButtonReturn(View view) {
        Intent ReturnIntent = new Intent(TestActivity.this, LoginActivity.class);

        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        startActivity(ReturnIntent);
    }

    public void onButtonTest2(View view) {
        selectTest();
    }

    public String selectTest() {
        String[] columns = {"id", "pk", "token"};
        Cursor c1 = db.rawQuery("select * from identifier", null);
        int recordCount = c1.getCount();
        String id = c1.getString(0);
        String pk = c1.getString(1);

        Toast.makeText(getApplicationContext(), "as", Toast.LENGTH_LONG). show();

        c1.close();
        return pk;
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
        }
        public void onOpen(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
