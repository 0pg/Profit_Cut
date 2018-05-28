package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

public class VoteActivity extends AppCompatActivity {

    CheckBox limBox;
    CheckBox kimBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        limBox = (CheckBox) findViewById(R.id.limBox);
        kimBox = (CheckBox) findViewById(R.id.kimBox);

        limBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (limBox.isChecked()) {
                    if (kimBox.isChecked()) {
                        kimBox.setChecked(false);
                    }
                    Toast.makeText(getApplicationContext(), "임현 선택됨", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });

        kimBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kimBox.isChecked()) {
                    if (limBox.isChecked()) {
                        limBox.setChecked(false);
                    }
                    Toast.makeText(getApplicationContext(), "자훈 선택됨", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });
    }

    public void onButtonVote(View view) {
        Intent MenuIntent = new Intent(VoteActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }
}
