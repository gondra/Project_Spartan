package fragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import easset.naviapp.R;

/**
 * Created by easset-01 on 6/18/2015.
 */
public class ContentsAdapter_not_to_use extends BaseAdapter {
    private Context mContext;
    private ListView mListView;

    private ArrayList<String> name;
    private ArrayList<String> type;
    private ArrayList<String> length;
    private ArrayList<String> format;
    private ArrayList<String> label;
    private ArrayList<String> labelTH;
    private ArrayList<String> required;
    private ArrayList<String> updateAble;
    private ArrayList<String> value;
    private ArrayList<ArrayList<String>> listValue;

    private JSONArray fieldsArray;

    private static class ViewHolderItem {
        TextView labelTextView;
        EditText valueEditView;
        Spinner pickListView;
    }

    private static class ContentsConstant{
        static String EDIT_TEXT = "";
        static String PICK_LIST = "picklist";
    }

    public ContentsAdapter_not_to_use(Context mContext, JSONArray fieldsArray, ListView mListView){
        this.fieldsArray = fieldsArray;
        this.mListView = mListView;
        this.mContext = mContext;
        this.name = new ArrayList();
        this.type = new ArrayList();
        this.length = new ArrayList();
        this.format = new ArrayList();
        this.label = new ArrayList();
        this.labelTH = new ArrayList();
        this.required = new ArrayList();
        this.updateAble = new ArrayList();
        this.value = new ArrayList();
        this.listValue = new ArrayList();
        this.populateDataFromJSON(fieldsArray);

    }

    private void populateDataFromJSON(JSONArray fieldsArray){
        try{
            JSONObject fieldTemp = new JSONObject();
            JSONArray listTemp = new JSONArray();
            ArrayList<String> listArray = new ArrayList<>();
            for(int i=0;i<fieldsArray.length();i++)
            {
                fieldTemp = fieldsArray.getJSONObject(i);
                this.name.add(i, fieldTemp.getString("name"));
                this.type.add(i, fieldTemp.getString("type"));
                this.length.add(i, fieldTemp.getString("length"));
                this.format.add(i, fieldTemp.getString("format"));
                this.label.add(i, fieldTemp.getString("label"));
                this.labelTH.add(i, fieldTemp.getString("labelTH"));
                this.required.add(i, fieldTemp.getString("required"));
                this.updateAble.add(i, fieldTemp.getString("updateable"));
                this.value.add(i, fieldTemp.getString("defaultValue"));
                if(this.type.get(i).equalsIgnoreCase(ContentsConstant.PICK_LIST)){
                    listTemp = fieldTemp.getJSONArray("listValue");
                    for (int j=0;j<listTemp.length();j++){
                        listArray.add(j, ((JSONObject)listTemp.get(j)).getString("name"));
                    }
                    this.listValue.add(i,listArray);
                }else{
                    this.listValue.add(i,new ArrayList<String>());
                }
            }

        }catch(JSONException jsonE){
            Log.e("Populate data failed! ",jsonE.getMessage());
        }

    }

    @Override
    public int getCount() {
        return fieldsArray.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int match = 0;
        if(this.type.get(position).equalsIgnoreCase(ContentsConstant.PICK_LIST)){
            match = 1;
        }
        return match;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        int type = getItemViewType(position);

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            if (type == 0) {
                convertView = inflater.inflate(R.layout.list_row_contents_text, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.list_row_contents_pick, parent, false);
            }


            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);

            if (type == 0) {
                viewHolder.valueEditView = (EditText) convertView.findViewById(R.id.value_edit);
            } else {
                viewHolder.pickListView = (Spinner) convertView.findViewById(R.id.pick_list);
            }

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //Set value into each fields
        viewHolder.labelTextView.setText(this.label.get(position));
        if (type == 0) {
            viewHolder.valueEditView.setText(this.value.get(position));
        }else{
            ArrayAdapter<String> dataAdapter = new ArrayAdapter(convertView.getContext(),android.R.layout.simple_spinner_item, this.listValue.get(position));
            viewHolder.pickListView.setAdapter(dataAdapter);
        }


        return convertView;
    }
}
