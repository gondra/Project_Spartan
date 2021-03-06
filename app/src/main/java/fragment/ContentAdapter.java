package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import easset.naviapp.R;

public class ContentAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Object> field;
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
    private HashMap data ;

    public ContentAdapter(Context mContext, ArrayList<Object> field, HashMap data){
        inflater = ((Activity) mContext).getLayoutInflater();
        this.data = data;
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
    
    private void setValue(ArrayList<Object> field){
        try{
            for(int i=0;i<field.size();i++){
                this.name.add(i,( (JSONObject) field.get(i) ).getString("name"));
                this.type.add(i,( (JSONObject)field.get(i) ).getString("type") );
                this.length.add(i,( (JSONObject)field.get(i) ).getInt("length"));
                this.format.add(i,( (JSONObject)field.get(i) ).getString("format") );
                this.label.add(i,( (JSONObject)field.get(i) ).getString("label") );
                this.labelTH.add(i,( (JSONObject)field.get(i) ).getString("labelTH") );
                this.required.add(i,( (JSONObject)field.get(i) ).getString("required") );
                this.updateable.add(i,( (JSONObject)field.get(i) ).getString("updateable") );
                this.defaultValue.add(i,( (JSONObject)field.get(i) ).getString("defaultValue") );

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

    public void setItemList(ArrayList<Object> field, HashMap data) {
        this.field = field;
        this.data = data;
        setValue(field);
    }

    private static class ViewHolderItem {
        TextView labelTextView;
        TextView valueEditView;
        Button pickListBtnView;
    }


    @Override
    public int getCount() {
        int size = field.size();
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


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolderItem viewHolder;
        int viewType = getItemViewType(position);
        ArrayList<String> tempFieldName = (ArrayList<String>) data.get("fieldNames");

        if (convertView == null) {
            viewHolder = new ViewHolderItem();
                    convertView = inflater.inflate(R.layout.list_row_contents_text, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_edit);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

                viewHolder.labelTextView.setText(this.getLabel(position));
                if(tempFieldName.contains(this.getName(position)) ){
                    try{
                        viewHolder.valueEditView.setText((String)data.get(this.getName(position)));
                    }catch(Exception e){
                        viewHolder.valueEditView.setText(String.valueOf(data.get(this.getName(position))));
                    }

                }else{
                    viewHolder.valueEditView.setText(getDefaultValue(position));
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
