package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import easset.naviapp.R;

/**
 * Created by easset-01 on 7/7/2015.
 */
public class ContentAdapter extends BaseAdapter {
    private Context mContext;
    private JSONArray field;
    private static final String PICK_LIST = "picklist";
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PICK_LIST = 1;
    private static final int TYPE_MAX_COUNT = TYPE_PICK_LIST + 1;
    LayoutInflater inflater;

    /**Attribute of each list*/
    private ArrayList<String> name, type, format, label, labelTH, required, updateable, defaultValue ;
    private ArrayList<Integer> length;
    private ArrayList<ArrayList<String>> listValue;
    private String[] value;
    public ContentAdapter(Context mContext, JSONArray field){
        inflater = ((Activity) mContext).getLayoutInflater();
        this.mContext = mContext;
        this.field = field;
        this.name = new ArrayList();
        this.type = new ArrayList();
        this.format = new ArrayList();
        this.label = new ArrayList();
        this.labelTH = new ArrayList();
        this.required = new ArrayList();
        this.updateable = new ArrayList();
        this.defaultValue = new ArrayList();
        this.listValue = new ArrayList();
        this.length = new ArrayList();
        
        /**Set attribute's value*/
        setValue(field);
    }

    public ContentAdapter(Context mContext, String[] field){
        this.mContext = mContext;
        this.value = new String[]{"1","2","3"};
    }
    
    private void setValue(JSONArray field){
        try{
            for(int i=0;i<field.length();i++){
                this.name.add(i,( (JSONObject) field.get(i) ).getString("name"));
                this.type.add(i,( (JSONObject)field.get(i) ).getString("type") );
                this.length.add(i,( (JSONObject)field.get(i) ).getInt("length"));
                this.format.add(i,( (JSONObject)field.get(i) ).getString("format") );
                this.label.add(i,( (JSONObject)field.get(i) ).getString("label") );
                this.labelTH.add(i,( (JSONObject)field.get(i) ).getString("labelTH") );
                this.required.add(i,( (JSONObject)field.get(i) ).getString("required") );
                this.updateable.add(i,( (JSONObject)field.get(i) ).getString("updateable") );
                this.defaultValue.add(i,( (JSONObject)field.get(i) ).getString("defaultValue") );
               // this.listValue.add(i,( (JSONObject)field.get(i) ).getJSONArray("listValue") );

                if(this.type.get(i).equalsIgnoreCase(PICK_LIST)){
                    ArrayList<String> listTemp = new ArrayList<>();
                    JSONArray listArrayTemp = ( (JSONObject)field.get(i) ).getJSONArray("listValue");
                    for(int j=0;j<listArrayTemp.length();j++){
                        listTemp.add(j, ( (JSONObject)listArrayTemp.get(j) ).getString("name") );
                    }
                    this.listValue.add(listTemp);
                }else{
                    this.listValue.add(i,new ArrayList<String>  ());
                }

            }
        }catch(JSONException jsonEx){
            jsonEx.printStackTrace();
        }
    }

    private static class ViewHolderItem {
        TextView labelTextView;
        TextView valueEditView;
        Spinner pickListView;
    }


    @Override
    public int getCount() {
        int size = field.length();
        return size;
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
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return getType(position).equalsIgnoreCase(PICK_LIST) ? TYPE_PICK_LIST : TYPE_TEXT;
    }

    private int checkpickListType(int position){
        int valid = 0;
        if(PICK_LIST.equalsIgnoreCase(getType(position))){
            valid = 1 ;
        }
        return valid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            // inflate the layout
            /*
            if(checkpickListType(position) == 0) {
                convertView = inflater.inflate(R.layout.list_row_contents_text, parent, false);
            }else if(checkpickListType(position) == 1){
                convertView = inflater.inflate(R.layout.list_row_contents_pick, parent, false);
            }else{
                convertView = inflater.inflate(R.layout.list_row_contents_text, parent, false);
            }

            viewHolder = new ViewHolderItem();
            viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
            if(checkpickListType(position) == 0) {
                viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_edit);
            }else if(checkpickListType(position) == 1){
                viewHolder.pickListView = (Spinner) convertView.findViewById(R.id.pick_list);
            }else{
                viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_edit);
            }*/

            viewHolder = new ViewHolderItem();
            switch (viewType) {
                case TYPE_TEXT:
                    convertView = inflater.inflate(R.layout.list_row_contents_text, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_edit);
                    break;
                case TYPE_PICK_LIST:
                    convertView = inflater.inflate(R.layout.list_row_contents_pick, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.pickListView = (Spinner) convertView.findViewById(R.id.pick_list);
                    break;
            }

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        switch (viewType) {
            case TYPE_TEXT:
                viewHolder.labelTextView.setText(this.label.get(position));
                viewHolder.valueEditView.setText(this.defaultValue.get(position));;
                break;
            case TYPE_PICK_LIST:
                viewHolder.labelTextView.setText(this.label.get(position));
                ArrayAdapter<String> dataAdapter = new ArrayAdapter(convertView.getContext(),R.layout.item_pick_list, this.listValue.get(position));
                viewHolder.pickListView.setAdapter(dataAdapter);
                break;
        }

        return convertView;
    }

    public String getFormat(int position) {
        return format.get(position);
    }

    public String getLabel(int position) {
        return label.get(position);
    }

    public String getLabelTH(int position) {
        return labelTH.get(position);
    }

    public int getLength(int position) {
        return length.get(position);
    }

    public ArrayList<String> getListValue(int position) {
        return listValue.get(position);
    }

    public String getName(int position) {
        return name.get(position);
    }

    public String getRequired(int position) {
        return required.get(position);
    }

    public String getType(int position) {
        return type.get(position);
    }

    public String getUpdateable(int position) {
        return updateable.get(position);
    }

    public String getDefaultValue(int position) {
        return defaultValue.get(position);
    }
}
