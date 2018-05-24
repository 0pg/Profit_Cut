package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CheckChainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_chain);
    }

    public void onButtonMenu(View view) {
        Intent MenuIntent = new Intent(CheckChainActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }
}
