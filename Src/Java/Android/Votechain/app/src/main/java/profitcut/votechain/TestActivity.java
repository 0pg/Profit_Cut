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

        String PhoneNum = telManager.getLine1Number();

        Toast.makeText(getApplicationContext(), PhoneNum, Toast.LENGTH_LONG). show();

        startActivity(ReturnIntent);
    }
}
