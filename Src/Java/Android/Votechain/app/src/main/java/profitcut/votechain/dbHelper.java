package profitcut.votechain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class dbHelper extends SQLiteOpenHelper {

    public dbHelper(Context context) {
        super(context, "profitcut", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

            String name = "user_info";
            db.execSQL("create table if not exists " + name + "("
                    + " id text not null, "
                    + " token integer not null, "
                    + " primary key(id));"
            );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
