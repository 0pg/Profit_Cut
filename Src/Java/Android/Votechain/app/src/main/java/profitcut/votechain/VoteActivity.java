package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
    }

    public void onButtonVote(View view) {
        Intent MenuIntent = new Intent(VoteActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }
}
