package Describe;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PrepareForGeneratingObject {
    DescribeStructure describe;
    DataOfRecord dataOfRecord;

    public class DescribeStructure{
        protected String module;
        protected String label;
        protected JSONArray field;
        protected JSONArray relations;
        protected JSONArray permissions;

        public DescribeStructure() {
            module = "";
            label = "";
            field = new JSONArray();
            relations = new JSONArray();
            permissions = new JSONArray();

        }
        public JSONArray getField(){
            return field;
        }
    }

    public class DataOfRecord{
        HashMap data ;
        public DataOfRecord(){
            data = new HashMap();
        }
    }

    public void prepare(String module){
        describe = new DescribeStructure();
        dataOfRecord = new DataOfRecord();

        generateDescribeFromJSON(module);
        generateRecordDataFromJSON(module);

    }

    public DescribeStructure getDescribeData(){

        return describe;
    }

    public DataOfRecord getRecordData() {
        return dataOfRecord;
    }

    private void generateDescribeFromJSON(String module){
        /*
            Search describe by module value
        */

        final String LEAD_DESCRIBE_JSON = "{\"module\":\"Lead\",\"label\":\"Lead\",\"fields\":[{\"name\":\"id\",\"type\":\"id\",\"length\":50,\"format\":\"\",\"label\":\"id\",\"labelTH\":\"id\",\"required\":true,\"updateable\":false,\"defaultValue\":\"\"},{\"name\":\"LeadName\",\"type\":\"string\",\"length\":25,\"format\":\"\",\"label\":\"Lead Name\",\"labelTH\":\" Lead Name\",\"required\":false,\"updateable\":true,\"defaultValue\":\"\"},{\"name\":\"LeadStatus\",\"type\":\"picklist\",\"length\":0,\"format\":\"\",\"label\":\"Lead Status\",\"labelTH\":\" Lead Status \",\"required\":true,\"updateable\":true,\"defaultValue\":\"\",\"listValue\":[{\"name\":\"Open\"},{\"name\":\"Close\"}]},{\"name\":\"Amount\",\"type\":\"integer\",\"length\":0,\"format\":\"#00.00\",\"label\":\"Amount\",\"labelTH\":\"Amount\",\"required\":true,\"updateable\":true,\"defaultValue\":\"\"},{\"name\":\"CreateDate\",\"type\":\"DateTime\",\"length\":0,\"format\":\"dd-mm-yyyy\",\"label\":\"Create Date\",\"labelTH\":\"Create Date\",\"required\":false,\"updateable\":true,\"defaultValue\":\"\"}],\"Relations\":[{}],\"Permissions\":[{\"access\":true,\"create\":true,\"edit\":true,\"delete\":true}]}";
        try {
            JSONObject totalTmp = new JSONObject(LEAD_DESCRIBE_JSON);
            describe.module = totalTmp.getString("module");
            describe.label = totalTmp.getString("label");
            describe.field = totalTmp.getJSONArray("fields");
            //describe.relations = totalTmp.getJSONArray("Relations");
            describe.permissions = totalTmp.getJSONArray("Permissions");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("generate - Describe",e.getMessage());
        }
    }

    private ArrayList<String> getAllFieldName(DescribeStructure describe){
        ArrayList<String> fieldNames = new ArrayList<>();
        try {
            for (int i = 0;i < describe.field.length();i++){
                fieldNames.add(i, describe.field.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Get field name ", e.getMessage());
        }

        return fieldNames;
    }

    private void generateRecordDataFromJSON(String module){
        /*
            Search record data by module value
        */
        final String LEAD_RECORD1_JSON = "{\"id\":\"962E7DD1-8467-43A2-8803-7DBD52741E41\",\"LeadName\":\"Acme, Inc.\",\"LeadStatus\":\"Open\",\"Amount\":15000,\"CreateDate\":\"20-04-2535\"}";
        try {
            JSONObject totalTmp = new JSONObject(LEAD_RECORD1_JSON);
            ArrayList<String> fieldNames = getAllFieldName(describe);
            for (int i = 0; i<totalTmp.length();i++){
                if(totalTmp.has(fieldNames.get(i))){
                    dataOfRecord.data.put(fieldNames.get(i),totalTmp.get(fieldNames.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
