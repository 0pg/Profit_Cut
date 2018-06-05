package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class VotingStateActivity extends AppCompatActivity {

    TableLayout candidateTableLayout;
    TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_state);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        candidateTableLayout = (TableLayout) findViewById(R.id.candidateTableLayout);

        dateTextView.setText("선거일 : 2018 - 05 - 24");

        for (int i = 0; i < 2; i++) {
            TableRow tr = new TableRow(this);
            TextView candidate = new TextView(this);
            TextView rate = new TextView(this);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.topMargin = 10;
            tr.setLayoutParams(param);

            candidate.setText("임현");
            rate.setText("득표율 : 41.08%");

            candidate.setGravity(Gravity.CENTER);
            candidate.setTextSize(20);
            rate.setGravity(Gravity.CENTER);
            rate.setTextSize(20);

            tr.addView(candidate);
            tr.addView(rate);

            candidateTableLayout.addView(tr);
        }
    }

    public void onButtonMenu(View view) {
        Intent MenuIntent = new Intent(VotingStateActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }
}
