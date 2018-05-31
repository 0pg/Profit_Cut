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
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MakeVoteActivity extends AppCompatActivity {
    dbHelper dh = new dbHelper(this);
    MyApplication myApp = (MyApplication)getApplication();
    LinearLayout candidatesLayout;
    EditText year, month, day, hour;
    int getNumber = 4;
    ArrayList<EditText> arr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_vote);
        year = findViewById(R.id.deadline_year_Text);
        month = findViewById(R.id.deadline_month_Text);
        day = findViewById(R.id.deadline_day_Text);
        hour = findViewById(R.id.deadline_hour_Text);

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
            candidateText.setId(i);
            arr.add(candidateText);
            candidatesLayout.addView(candidateText);
        }
    }

    public void onButtonMenu(View view) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        for (EditText can : arr) {
            myApp.candidates.add(String.valueOf(can.getText()));
        }
        myApp.deadline = myApp.vb.deadline(Integer.valueOf(String.valueOf(year.getText())), Integer.valueOf(String.valueOf(month.getText())),
                Integer.valueOf(String.valueOf(day.getText())),Integer.valueOf(String.valueOf(hour.getText())));
        createGenesis();
        getRsa rsa = null;
        try {
            rsa = new getRsa();
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        }
        PublicKey Puk = (PublicKey) rsa.get_public();
        PrivateKey Prk = (PrivateKey) rsa.get_private();
        String puk = rsa.encode_base64(Puk.getEncoded());
        String prk = rsa.encode_base64(Prk.getEncoded());
        myApp.puk = puk;
        myApp.prk = prk;
        myApp.users.put(myApp.id, Puk);
        new PutDataBase(dh).insertIdentifier(myApp.subject + "_identifier", myApp.id, prk, puk);
        new PutDataBase(dh).insertUserInfo(myApp.subject + "_user_info", myApp.id, myApp.puk);
        Toast.makeText(getApplicationContext(), "투표가 생성되었습니다.", Toast.LENGTH_LONG).show();
        Intent MenuIntent = new Intent(MakeVoteActivity.this, MenuActivity.class);
        startActivity(MenuIntent);
    }

    private void createGenesis() {
        int index = 1;
        myApp.constructor = myApp.id;
        myApp.gh = new genesisblock_header(myApp.ver, index, Calendar.getInstance().getTimeInMillis() / 1000, myApp.deadline);
        myApp.gb = new genesisblock(myApp.vb.hash(myApp.gh.toString()), myApp.subject, myApp.constructor, myApp.candidates, myApp.gh);
        myApp.chain.add(0, myApp.gb);
    }
}
