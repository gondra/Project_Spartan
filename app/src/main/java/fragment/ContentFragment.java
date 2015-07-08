package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Describe.PrepareForGeneratingObject;
import easset.naviapp.R;

public class ContentFragment extends Fragment {
    PrepareForGeneratingObject mPrepareForGeneratingObject;
    private OnMainFragmentInteractionListener mListener;
    private ListView mListView;
    private ContentAdapter mAdaptor;
    private DescribeStructure describe;
    private HashMap data ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contents, container, false);
        String module = getArguments().getString("chosen_module");
        String id = getArguments().getString("id");
        data = new HashMap();
        describe = new DescribeStructure();
        generateDescribeFromJSON(module);
        generateRecordDataFromJSON(module,id);
        JSONArray field = describe.field;
        mAdaptor = new ContentAdapter(rootView.getContext(),field, data);
        //mAdaptor = new ContentAdapter(rootView.getContext(),new String[]{"1","2","3"});
        mListView = (ListView)rootView.findViewById(R.id.contentListView);
        try{
            mListView.setAdapter(mAdaptor);
        }catch (Exception e){
            e.printStackTrace();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMainFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMainFragmentInteractionListener {
        void setTitle(String title);
        View getViewByPosition(int position, ListView mListView);
    }

    /** Data generating section */
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
    }

    private void generateDescribeFromJSON(String module){
        /*
            Search describe by module value
        */

        final String LEAD_DESCRIBE_JSON = "{\"module\":\"Lead\",\"label\":\"Lead\",\"fields\":[{\"name\":\"id\",\"type\":\"id\",\"length\":50,\"format\":\"\",\"label\":\"id\",\"labelTH\":\"id\",\"required\":true,\"updateable\":false,\"defaultValue\":\"\"},{\"name\":\"LeadName\",\"type\":\"string\",\"length\":25,\"format\":\"\",\"label\":\"Lead Name\",\"labelTH\":\" Lead Name\",\"required\":false,\"updateable\":true,\"defaultValue\":\"\"},{\"name\":\"LeadStatus\",\"type\":\"picklist\",\"length\":0,\"format\":\"\",\"label\":\"Lead Status\",\"labelTH\":\" Lead Status \",\"required\":true,\"updateable\":true,\"defaultValue\":\"Open\",\"listValue\":[{\"name\":\"Open\"},{\"name\":\"Close\"}]},{\"name\":\"Amount\",\"type\":\"integer\",\"length\":0,\"format\":\"#00.00\",\"label\":\"Amount\",\"labelTH\":\"Amount\",\"required\":true,\"updateable\":true,\"defaultValue\":\"\"},{\"name\":\"CreateDate\",\"type\":\"DateTime\",\"length\":0,\"format\":\"dd-mm-yyyy\",\"label\":\"Create Date\",\"labelTH\":\"Create Date\",\"required\":false,\"updateable\":true,\"defaultValue\":\"Damn\"}],\"Relations\":[{}],\"Permissions\":[{\"access\":true,\"create\":true,\"edit\":true,\"delete\":true}]}";
        try {
            JSONObject totalTmp = new JSONObject(LEAD_DESCRIBE_JSON);
            describe.module = totalTmp.getString("module");
            describe.label = totalTmp.getString("label");
            describe.field = totalTmp.getJSONArray("fields");
            try{
                describe.relations = totalTmp.getJSONArray("Relations");
            }catch(JSONException e){
                describe.relations = new JSONArray();
            }
            describe.permissions = totalTmp.getJSONArray("Permissions");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("generate - Describe", e.getMessage());
        }
    }

    private void generateRecordDataFromJSON(String module, String id){
        /*
            Search record data by module value
        */
        final String LEAD_RECORD1_JSON = "{\"id\":\"962E7DD1-8467-43A2-8803-7DBD52741E41\",\"LeadName\":\"Acme, Inc.\",\"LeadStatus\":\"Open\",\"Amount\":15000,\"CreateDate\":\"20-04-2557\"}";
        final String LEAD_RECORD2_JSON = "{\"id\":\"295C6D74-6F31-4762-8D5A-37314B4BF358\",\"LeadName\":\"Umbrella, Cooperation.\",\"LeadStatus\":\"Open\",\"Amount\":220000," +
                "\"CreateDate\":\"15-04-2558\"}";


        try {
            /** For test */
            String tempJSON = "";
            if(id.equalsIgnoreCase("962E7DD1-8467-43A2-8803-7DBD52741E41")){
                tempJSON = LEAD_RECORD1_JSON;
            }else if(id.equalsIgnoreCase("0A27D3E6-A3FD-445B-B1A2-2E5D7037DA92")){
                tempJSON = LEAD_RECORD2_JSON;
            }

            JSONObject totalTmp = new JSONObject(tempJSON);
            ArrayList<String> fieldNames = getAllFieldName(describe);
            for (int i = 0; i<totalTmp.length();i++){
                if(totalTmp.has(fieldNames.get(i))){
                    data.put(fieldNames.get(i),totalTmp.get(fieldNames.get(i)));
                }
            }
            data.put("fieldNames",fieldNames);
        } catch (JSONException e) {
            e.printStackTrace();
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
}
