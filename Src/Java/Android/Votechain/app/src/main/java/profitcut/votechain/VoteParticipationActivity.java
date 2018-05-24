package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VoteParticipationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_participation);
    }

    public void onButtonParticipation(View view) {
        Intent VoteIntent = new Intent(VoteParticipationActivity.this, VoteActivity.class);
        startActivity(VoteIntent);
    }
}
