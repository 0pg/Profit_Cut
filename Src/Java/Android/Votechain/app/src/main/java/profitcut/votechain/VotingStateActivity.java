package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VotingStateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_state);
    }

    public void onButtonMenu(View view) {
        Intent MenuIntent = new Intent(VotingStateActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }
}
