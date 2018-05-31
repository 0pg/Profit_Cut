package profitcut.votechain;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ParticipationPopUpActivity extends Activity {
    MyApplication myApp = (MyApplication) getApplication();

    EditText candidate_Num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participation_pop_up);

        candidate_Num = (EditText) findViewById(R.id.candidate_Num);
    }

    public void onButtonMake(View view) {
        myApp.candidate_Num = Integer.valueOf(candidate_Num.getText().toString());

        Intent MakeIntent = new Intent(ParticipationPopUpActivity.this, MakeVoteActivity.class);
        startActivity(MakeIntent);
    }
}
