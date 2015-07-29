package easset.naviapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.sql.SQLException;
import GenerateJSON.JSONUtils;


public class LoginActivity extends Activity{
    private EditText mUser;
    private Bundle mDataBundle;
    private Button btnLogin;
    private String userName;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        mDataBundle = new Bundle();
        mUser = (EditText)findViewById(R.id.user_form);
        mUser.setSingleLine(true);
        btnLogin = (Button)findViewById(R.id.button_login);
        mUser.setText("start");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = mUser.getText().toString();
                //Test
                if (userName != null
                        && userName != ""
                        && !userName.equalsIgnoreCase("")
                        && userName.trim().length() != 0) {
                    mDataBundle.putString("user", userName);
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        if (authentication(getApplication(), userName)) {
                            String pass = "";
                            mainIntent.putExtras(mDataBundle);
                            pass = logOn(mainIntent);

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

    private String logOn(Intent mainIntent){
        new LogIn(getApplicationContext(), bar, mainIntent).execute("http://api.flickr.com/services/feeds/photos_public.gne?callback=jsonp1438171694408&_=1438171696224&id=29080075@N02&lang=en-us&format=json&jsoncallback=jsonp1438171694408");
        SharedPreferences sp = getSharedPreferences("JSON", Context.MODE_PRIVATE);
        return sp.getString("logOnJSON", "");
    }
}

class LogIn extends AsyncTask<String, String, String> {
    JSONUtils utils;
    View view;
    Context mContext;
    ProgressBar bar;
    Intent mainIntent;

    public LogIn(Context mContext, ProgressBar bar, Intent mainIntent) {
        this.mContext = mContext.getApplicationContext();
        this.bar = bar;
        this.mainIntent = mainIntent;
    }

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        String responeJSON = "";
        try{
            responeJSON = fetchingLogOnJSON(params[0]);
        }catch(Exception e){

        }
        return responeJSON;
    }

    @Override
    protected void onPostExecute(String JSONResult) {
        bar.setVisibility(View.GONE);
        SharedPreferences sp = mContext.getSharedPreferences("JSON", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("logOnJSON", JSONResult);
        boolean valid = editor.commit();


        mContext.startActivity(mainIntent);
    }

    /**All method section*/
    public void updateContent(String JSON){
        /*
        send JSON to server
         */
    }

    public String fetchingLogOnJSON(String uri){
        InputStream in = null;
        String result= "";
        try {
            //InputStream is = (InputStream) new URL(uri).getContent();
            URL url = new URL(uri);
            URLConnection urlConnection = url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncodingEx", e1.toString());
            e1.printStackTrace();
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
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
        }
        return result;
    }
}
