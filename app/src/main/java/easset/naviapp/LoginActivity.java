package easset.naviapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends Activity{
    private EditText mUser;
    private Bundle mDataBundle;
    private Button btnLogin;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDataBundle = new Bundle();
        mUser = (EditText)findViewById(R.id.user_form);
        mUser.setSingleLine(true);
        btnLogin = (Button)findViewById(R.id.button_login);
        mUser.setText("start");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = mUser.getText().toString();

                if (userName != null
                        && userName != ""
                        && !userName.equalsIgnoreCase("")
                        && userName.trim().length() != 0) {
                    mDataBundle.putString("user", userName);
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    try {
                        if (authentication(getApplication(), userName)) {
                            mainIntent.putExtras(mDataBundle);
                            startActivity(mainIntent);
                            finish();
                        }
                    } catch (Exception e) {
                        Log.e("User authentication", e.getMessage());
                    }

                }else{
                    Toast.makeText(getApplication(),"Please input your user name.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean authentication(Context mContext, String userName) throws SQLException{
        boolean valid = false;
        SQLiteDatabase mDb;
        Cursor mCursor;
        DBaseHelper dbHelper;

        dbHelper = new DBaseHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        mCursor =  mDb.rawQuery("SELECT * FROM "+DBaseHelper.TABLE_NAME+" WHERE "+DBaseHelper.COL_USER+" = ? ",new String[]{userName} );

        if ((mCursor.moveToFirst()) && mCursor.getCount()>0){
            valid =true;
        }

        return valid;
    }
}
