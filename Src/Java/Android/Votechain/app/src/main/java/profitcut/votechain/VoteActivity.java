package profitcut.votechain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class VoteActivity extends AppCompatActivity {
    dbHelper dh = new dbHelper(this);
    LinearLayout tableLayout;
    MyApplication myApp = (MyApplication) getApplication();
    HashMap<CheckBox, TextView> arr = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        tableLayout = (LinearLayout) findViewById(R.id.tableLayout);

        for (String candi : myApp.candidates) {
            TableRow tr = new TableRow(this);
            TextView tv = new TextView(this);
            CheckBox cb = new CheckBox(this);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);
            cb.setGravity(Gravity.CENTER);

            cb.setOnClickListener(new View.OnClickListener() {
                public void onClick (View view) {
                    if (((CheckBox) view).isChecked()) {
                        for (CheckBox c : arr.keySet()) {
                            if (c == view);
                            else c.setChecked(false);
                        }
                    } else {
                    }
                }

            });
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.topMargin = 10;
            tr.setLayoutParams(param);

            tv.setText(candi);

            arr.put(cb, tv);
            tr.addView(tv);
            tr.addView(cb);
            tableLayout.addView(tr);
        }




    }

    public void onButtonVote (View view) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        for(CheckBox c : arr.keySet()) {
            if(c.isChecked()) {
                HashMap transac = myApp.vb.new_transaction(myApp.id, (String)arr.get(c).getText());
                if(myApp.vb.add_transaction(transac)) {
                    myApp.merkle_tree = myApp.vb.getMerkle_tree();
                    myApp.cs.broadcast_transac(transac, (PrivateKey) new getRsa().decode_privateKey(myApp.prk));
                    Toast.makeText(getApplicationContext(), "투표 완료", Toast.LENGTH_LONG).show();
                    Intent MenuIntent = new Intent(VoteActivity.this, MenuActivity.class);
                    startActivity(MenuIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "투표하실 수 없습니다", Toast.LENGTH_LONG).show();
                    Intent MenuIntent = new Intent(VoteActivity.this, MenuActivity.class);
                    startActivity(MenuIntent);
                }
            }
        }

    }
}

