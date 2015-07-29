package GenerateJSON;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import easset.naviapp.LoginActivity;
import easset.naviapp.MainActivity;
import easset.naviapp.R;
import model.ContentStructureM;

public class JSONUtils extends AsyncTask<String, String, String>{
    JSONUtils utils;
    Context mContext;
    View view;
    private ProgressBar bar;

    public JSONUtils(){

    }

    public JSONUtils(Context mContext, View view, ProgressBar bar){
        this.mContext = mContext;
        this.view = view;
        this.bar = bar;
    }

    @Override
    protected String doInBackground(String... params) {
        String responeJSON = "";
        try{
                responeJSON = fetchingLogOnJSON(params[0]);
                bar.setVisibility(View.VISIBLE);

        }catch(Exception e){

        }
        return responeJSON;
    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String JSONResult) {
        bar.setVisibility(View.GONE);
        SharedPreferences sp = mContext.getSharedPreferences("JSON", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("logOnJSON", JSONResult);
        boolean valid = editor.commit();
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



    public ContentStructureM generateDescribeFromJSON(String chosen_module, ContentStructureM content){
        /*
            Search describe by chosen_module value
        */

        final String LEAD_DESCRIBE_JSON = "{\"module\":\"Lead\",\"label\":\"Lead\",\"fields\":[{\"name\":\"id\",\"type\":\"id\",\"length\":50,\"format\":\"\",\"label\":\"id\",\"labelTH\":\"id\",\"required\":true,\"updateable\":false,\"defaultValue\":\"\"},{\"name\":\"LeadName\",\"type\":\"string\",\"length\":25,\"format\":\"\",\"label\":\"Lead Name\",\"labelTH\":\" Lead Name\",\"required\":false,\"updateable\":true,\"defaultValue\":\"\"},{\"name\":\"LeadStatus\",\"type\":\"picklist\",\"length\":0,\"format\":\"\",\"label\":\"Lead Status\",\"labelTH\":\" Lead Status \",\"required\":true,\"updateable\":true,\"defaultValue\":\"Open\",\"listValue\":[{\"name\":\"Open\"},{\"name\":\"Close\"}]},{\"name\":\"CreateDate\",\"type\":\"DateTime\",\"length\":0,\"format\":\"dd-mm-yyyy\",\"label\":\"Create Date\",\"labelTH\":\"Create Date\",\"required\":false,\"updateable\":true,\"defaultValue\":\"28-07-2015\"},{\"name\":\"Amount\",\"type\":\"integer\",\"length\":0,\"format\":\"#00.00\",\"label\":\"Amount\",\"labelTH\":\"Amount\",\"required\":true,\"updateable\":true,\"defaultValue\":\"\"}],\"Relations\":[{}],\"Permissions\":[{\"access\":true,\"create\":true,\"edit\":true,\"delete\":true}]}";
        try {
            JSONObject totalTmp = new JSONObject(LEAD_DESCRIBE_JSON);
            content.setModule(totalTmp.getString("module"));
            content.setLabel(totalTmp.getString("label"));

            ArrayList<Object> list = new ArrayList();
            JSONArray jsonFieldArray = totalTmp.getJSONArray("fields");
            if (jsonFieldArray != null) {
                int len = jsonFieldArray.length();
                for (int i=0;i<len;i++){
                    list.add((JSONObject)jsonFieldArray.get(i));
                }
            }
            content.setField(list);

            try{
                ArrayList<Object> reList = new ArrayList();
                JSONArray jsonRelationsArray = totalTmp.getJSONArray("Relations");
                if (jsonRelationsArray != null) {
                    reList.clear();
                    int len = jsonRelationsArray.length();
                    for (int i=0;i<len;i++){
                        reList.add((Object)jsonRelationsArray.get(i));
                    }
                }
                content.setRelations(reList);
            }catch(JSONException e){
                content.setRelations(new ArrayList<Object>());
            }

            JSONArray jsonPermissionsArray = totalTmp.getJSONArray("Permissions");
            ArrayList<Object> perList = new ArrayList();
            if (jsonPermissionsArray != null) {
                perList.clear();
                int len = jsonPermissionsArray.length();
                for (int i=0;i<len;i++){
                    perList.add((JSONObject)jsonPermissionsArray.get(i));
                }
            }
            content.setPermissions(perList);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("generate - content", e.getMessage());
        }

        return content;
    }

    public ContentStructureM generateRecordDataFromJSON(String chosen_module, String id, ContentStructureM content){
        /*
            Search record data by id value
        */
        final String LEAD_RECORD1_JSON = "{\"id\":\"962E7DD1-8467-43A2-8803-7DBD52741E41\",\"LeadName\":\"Acme, Inc.\",\"LeadStatus\":\"Open\",\"Amount\":15000,\"CreateDate\":\"20-04-2557\"}";
        final String LEAD_RECORD2_JSON = "{\"id\":\"295C6D74-6F31-4762-8D5A-37314B4BF358\",\"LeadName\":\"Umbrella, Cooperation.\",\"LeadStatus\":\"Open\",\"Amount\":220000," +
                "\"CreateDate\":\"15-04-2558\"}";
        final String LEAD_RECORD1_JSON_TEST = "{\"id\":\"962E7DD1-8467-43A2-8803-7DBD52741E41\",\"LeadName\":\"Acme, Inc. Yo\",\"LeadStatus\":\"Open\",\"Amount\":17000,\"CreateDate\":\"20-07-2558\"}";
        HashMap data = new HashMap();

        try {
            /** For test */
            String tempJSON = "";
            if(id.equalsIgnoreCase("962E7DD1-8467-43A2-8803-7DBD52741E41")){
                Random r = new Random();
                if(r.nextInt(1) == 0){
                    tempJSON = LEAD_RECORD1_JSON;
                }else{
                    tempJSON = LEAD_RECORD1_JSON_TEST;
                }
            }else if(id.equalsIgnoreCase("0A27D3E6-A3FD-445B-B1A2-2E5D7037DA92")){
                tempJSON = LEAD_RECORD2_JSON;
            }

            JSONObject totalTmp = new JSONObject(tempJSON);
            ArrayList<String> fieldNames = getAllFieldName(content);
            for (int i = 0; i<totalTmp.length();i++){
                if(totalTmp.has(fieldNames.get(i))){
                    data.put(fieldNames.get(i), totalTmp.get(fieldNames.get(i)));
                }
            }
            data.put("fieldNames",fieldNames);

            content.setData(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return content;
    }


    private ArrayList<String> getAllFieldName(ContentStructureM content){
        ArrayList<String> fieldNames = new ArrayList<>();
        try {
            for (int i = 0;i < content.getField().size();i++){
                fieldNames.add(i, ((JSONObject) content.getField().get(i)).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Get field name ", e.getMessage());
        }

        return fieldNames;
    }

    public String buildJSONString(HashMap modifiedData){
        StringBuilder JSON = new StringBuilder();
        JSON.append("{");
        for(int i = 0 ;i<modifiedData.size();i++){
            JSON.append("'Status_"+i+"' : 'Pending',");
        }
        /*JSON.append("'Status' : 'Pending',");
        JSON.append("'Priority' : '1-High',");
        JSON.append("'Case Area' : 'Maintenance',");
        JSON.append("'Case Type' : 'Enchantment Request',");
        JSON.append("'Case Source' : 'Phone',");
        JSON.append("'Satisfaction' : 'Very Dissatisfied',");
        JSON.append("'Description' : 'Description1',");
        JSON.append("'Subject' : 'Subject1',");
        JSON.append("'Notify to Customer Status' : 'COMPLETED',");
        JSON.append("'Closed Date':'2014-04-26T20:24:44.3529664+07:00'");*/
        JSON.append("}");

        return  JSON.toString();
    }




}
