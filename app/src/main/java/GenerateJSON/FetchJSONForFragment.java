package GenerateJSON;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

//Fetching JSON section
public class FetchJSONForFragment extends AsyncTask<String, String, String> {
    Context mContext;
    ProgressBar bar;
    Fragment fragment;
    String errorMsg;

    public FetchJSONForFragment(Fragment fragment, Context mContext,  ProgressBar bar) {
        super();
        this.mContext = mContext.getApplicationContext();
        this.bar = bar;
        this.fragment=fragment;
        this.errorMsg = "";
        delegate = (AsyncResponse) fragment;
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
            errorMsg = " Connection error";
        }
        return responeJSON;
    }

    @Override
    protected void onPostExecute(String JSONResult) {
        bar.setVisibility(View.GONE);
        try{
            delegate.processFinish(JSONResult);
        }catch (Exception e){
            e.printStackTrace();
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

    public AsyncResponse delegate=null;

    public interface AsyncResponse {
        void processFinish(String output);
    }
}
