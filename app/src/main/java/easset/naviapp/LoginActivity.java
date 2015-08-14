package easset.naviapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import GenerateJSON.JSONUtils;


public class LoginActivity extends Activity{
    private EditText mUser;
    private EditText mPass;
    private Bundle mDataBundle;
    private Button btnLogin;
    private String userName;
    private RelativeLayout bar;
    private RelativeLayout form;
    private CheckBox rememberMe;
    private SharedPreferences sp;
    SharedPreferences.Editor editorAcc;
    private TextView alertTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        bar = (RelativeLayout) this.findViewById(R.id.progressSection);
        form = (RelativeLayout) this.findViewById(R.id.formSection);
        rememberMe = (CheckBox) findViewById(R.id.rememberUser);
        alertTxt = (TextView)findViewById(R.id.error_text);
        mUser = (EditText)findViewById(R.id.user_form);
        mPass = (EditText) findViewById(R.id.password_form);
        btnLogin = (Button)findViewById(R.id.button_login);
        mDataBundle = new Bundle();
        String isChecked = sp.getString("CHECK_REMEMBER", "N");
        rememberMe.setChecked(isChecked.equalsIgnoreCase("Y")?true:false);
        mUser.setSingleLine(true);
        String user = sp.getString("REMEMBER_ME_USER", "");
        mUser.setText(user);
        mPass.setSingleLine(true);
        mUser.setSelection(mUser.getText().length());

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = mUser.getText().toString();

                if (userName != null
                        && !userName.equalsIgnoreCase("")
                        && userName.trim().length()> 0) {
                    mDataBundle.putString("user", userName);
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainIntent.putExtras(mDataBundle);
                    try {
                        /*if (authentication(getApplication(), userName)) {
                        }*/
                        String pass = "";
                        logOn(mainIntent, userName);
                    } catch (Exception e) {
                        Log.e("User authentication", e.getMessage());
                    }

                }else{
                    //Toast.makeText(getApplication(),"Please input your user name.",Toast.LENGTH_SHORT).show();
                    alertTxt.setText("• Please input your user name.");
                }
            }
        });
    }

    private void logOn(Intent mainIntent, String userName) throws Exception{
        String[] userPass = {mUser.getText().toString(),mPass.getText().toString(), rememberMe.isChecked()?"Y":"N"};
        try{
            new LogIn(LoginActivity.this, getApplicationContext(), bar, form, mainIntent, userPass, alertTxt).execute(getResources().getString(R.string.login_uri));
        }catch(Exception e){
            alertTxt.setText("• Something has problem!");
        }
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

class LogIn extends AsyncTask<String, String, String> {
    JSONUtils utils;
    View view;
    Context mContext;
    RelativeLayout bar;
    RelativeLayout form;
    Intent mainIntent;
    Activity loginActivity;
    String[] userPass;
    String errorMsg;
    TextView alertTxt;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public LogIn(Activity loginActivity, Context mContext, RelativeLayout bar, RelativeLayout form, Intent mainIntent, String[] userPass, TextView alertTxt) {
        super();
        this.mContext = mContext.getApplicationContext();
        this.bar = bar;
        this.form = form;
        this.mainIntent = mainIntent;
        this.loginActivity=loginActivity;
        this.userPass = userPass;
        this.errorMsg = "";
        this.alertTxt = alertTxt;
        sp = mContext.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
    }

    @Override
    protected String doInBackground(String... params) {
        String responeJSON = "";
        try{
            responeJSON = fetchingLogOnJSON(params[0]);
        }catch(Exception e){
            errorMsg = "• Connection error";
        }
        return responeJSON;
    }

    @Override
    protected void onPostExecute(String JSONResult) {
        bar.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);
        if(!JSONResult.equalsIgnoreCase("") && JSONResult.length()>0){

            editor.putString("logOnJSON", JSONResult);
            editor.putString("REMEMBER_ME_USER", userPass[0]);
            editor.putString("REMEMBER_ME_PASS", userPass[1]);
            editor.putString("CHECK_REMEMBER", userPass[2]);
            editor.commit();

            mContext.startActivity(mainIntent);
            loginActivity.finish();
        }

        if(!errorMsg.equalsIgnoreCase("")){
            alertTxt.setText(errorMsg);
        }

    }

    public String fetchingLogOnJSON(String uri) throws Exception {
        InputStream in = null;
        String result= "";
        URLConnection urlConnection;
        try {
            //InputStream is = (InputStream) new URL(uri).getContent();
            URL url = new URL(uri);
            urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.connect();
            in = new BufferedInputStream(urlConnection.getInputStream());

        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncodingEx", e1.toString());
            e1.printStackTrace();
            throw e1;
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
            throw e2;
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
            throw e3;
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
            throw e4;
        }

        // Convert response to string using String Builder
        try {
            StringBuilder sBuilder = new StringBuilder();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(in, "utf-8"),8);
            String line;
            while((line = bReader.readLine()) != null){
                sBuilder.append(line + "\n");
            }

            in.close();
            result = sBuilder.toString();
        }catch(Exception e){
            Log.e("StringBuilding", e.toString());
            throw e;
        }

        return result;
    }
}
