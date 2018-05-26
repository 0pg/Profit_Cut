package profitcut.votechain;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /*
    public void onButtonTest(View view) {
        Intent TestIntent = new Intent(LoginActivity.this, TestActivity.class);
        startActivity(TestIntent);
    }*/

    public void onButtonAuthentication(View view) {
        Intent ParticipationIntent = new Intent(LoginActivity.this, VoteParticipationActivity.class);

        createDatabase();
        user_infoTable();

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

    private void user_infoTable() {
        String name = "_user_info";
        db.execSQL("create table if not exists " + name + "("
                + " id integer not null, "
                + " pk text not null, "
                + " token integer not null, "
                + " primary key(id));"
        );
    }
}
