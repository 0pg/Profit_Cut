package profitcut.votechain;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MakeVoteActivity extends AppCompatActivity {

    LinearLayout candidatesLayout;
    int getNumber = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_vote);

        candidatesLayout = (LinearLayout)findViewById(R.id.candidatesLayout);

        for (int i = 0; i < getNumber; i++) {
            EditText candidateText = new EditText(this);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.topMargin = 10;

            candidateText.setLayoutParams(param);
            candidateText.setWidth(850);
            candidateText.setGravity(Gravity.CENTER);
            candidateText.setPadding(0, 10, 0, 10);
            candidateText.setTextColor(0xffffffff);
            candidateText.setBackgroundResource(R.color.colorPrimaryDark);
            candidateText.setSingleLine(true);
            candidateText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            candidatesLayout.addView(candidateText);
        }
    }

    public void onButtonMenu(View view) {
        Intent MenuIntent = new Intent(MakeVoteActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }
}
