package profitcut.votechain;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void onButtonReturn(View view) {
        Intent ReturnIntent = new Intent(TestActivity.this, LoginActivity.class);

        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

<<<<<<< HEAD
        Intent ReturnIntent = new Intent(TestActivity.this, LoginActivity.class);;
        startActivity(ReturnIntent);
    }

    public void onButtonTest2(View view) {
        selectTest();
    }

    public String selectTest() {
        String[] columns = {"id", "pk", "token"};
        Cursor c1 = db.query("user_info", columns, null, null, null, null, null);

        int recordCount = c1.getCount();
=======
        String PhoneNum = telManager.getLine1Number();
>>>>>>> parent of 0a64137... Add SQLiteOpenHelper

        Toast.makeText(getApplicationContext(), PhoneNum, Toast.LENGTH_LONG). show();

<<<<<<< HEAD
        Toast.makeText(getApplicationContext(), id + pk + Integer.toString(token), Toast.LENGTH_LONG). show();

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
=======
        startActivity(ReturnIntent);
>>>>>>> parent of 0a64137... Add SQLiteOpenHelper
    }
}
