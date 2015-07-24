package GenerateJSON;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class APIConnectionInfo {
    public final static String urlString = "http://192.168.1.151:8080/FreeWriting/login";
    public APIConnectionInfo() {
        super();
    }

    public static void main(String arg[ ] ){
        System.out.print("Test");

        String result = "";

        InputStream in;
        BufferedReader bufferedReader = null;
        // HTTP Get
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);


        } catch (Exception e ) {

            System.out.println(e.getMessage());

        }

        System.out.print(result);
    }
}

class CallAPI extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... params) {
        String urlString  = params[0]; // URL to call

        String result = "";

        InputStream in;
        BufferedReader bufferedReader = null;
        // HTTP Get
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());

            // convert inputstream to string
            if(in != null){
                bufferedReader = new BufferedReader( new InputStreamReader(in));
            }

            while(bufferedReader.readLine()!=null){
                result += bufferedReader.readLine();
            }
            in.close();

        } catch (Exception e ) {

            System.out.println(e.getMessage());

            return e.getMessage();

        }

        return result;

    }

    protected void onPostExecute(String result) {

    }

} // end CallAPI

