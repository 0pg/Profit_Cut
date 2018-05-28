package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {
    MyApplication myApp = (MyApplication)getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        exeServer();
    }

    public void onButtonVote(View view) {
        Intent VoteIntent = new Intent(MenuActivity.this, VoteActivity.class);
        startActivity(VoteIntent);
    }

    public void onButtonState(View view) {
        Intent StateIntent = new Intent(MenuActivity.this, VotingStateActivity.class);
        startActivity(StateIntent);
    }

    public void onButtonCheck(View view) {
        Intent CheckIntent = new Intent(MenuActivity.this, CheckChainActivity.class);
        startActivity(CheckIntent);
    }

    public void onButtonParticipation(View view) {
        Intent ParticipationIntent = new Intent(MenuActivity.this, VoteParticipationActivity.class);
        startActivity(ParticipationIntent);
    }

    public void exeServer(){
        myApp.cs.handle_init();
        myApp.cs.handle_verify();
    }
}
