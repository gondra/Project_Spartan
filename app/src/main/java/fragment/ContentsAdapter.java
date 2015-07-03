package fragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import easset.naviapp.R;

/**
 * Created by easset-01 on 6/18/2015.
 */
public class ContentsAdapter extends BaseAdapter {
    private Context mContext;
    private String id;
    private String type;
    private String name;
    private String value;
    private String required;
    private String fieldType;

    private ArrayList<String> ids;
    private ArrayList<String> types;
    private ArrayList<String> names;
    private ArrayList<String> values;
    private ArrayList<String> requires;
    private ArrayList<String> fieldTypes;

    private Vector<JSONObject> fieldsVector;

    private static class ViewHolderItem {
        TextView idTextView;
        TextView typeTextView;
        TextView nameTextView;
        TextView valueTextView;
        TextView requiredTextView;
        TextView fieldTypeTextView;
    }

    public ContentsAdapter(Context mContext, String id, String type, String name, String value, String required, String fieldType){
        this.mContext = mContext;
        this.id = id;
        this.type = type;
        this.name = name;
        this.value = value;
        this.required = required;
        this.fieldType = fieldType;
    }

    public ContentsAdapter(Context mContext, Vector<JSONObject> fieldsVector){
        this.fieldsVector = fieldsVector;
        this.mContext = mContext;
        this.ids = new ArrayList();
        this.types = new ArrayList();
        this.names = new ArrayList();
        this.values = new ArrayList();
        this.requires = new ArrayList();
        this.fieldTypes = new ArrayList();
        this.populateDataFromJSON(fieldsVector);

    }

    private void populateDataFromJSON(Vector<JSONObject> fieldsVector){
        try{
            for(int i=0;i<fieldsVector.size();i++)
            {
                JSONObject fieldTemp = fieldsVector.get(i);
                this.ids.add(i, fieldTemp.getString("Id"));
                this.types.add(i, fieldTemp.getString("Type"));
                this.names.add(i, fieldTemp.getString("Name"));
                this.values.add(i, fieldTemp.getString("Values"));
                this.requires.add(i, fieldTemp.getString("Required"));
                this.fieldTypes.add(i, fieldTemp.getString("FieldType"));
            }

        }catch(JSONException jsonE){
            Log.e("Populate data failed! ",jsonE.getMessage());
        }

    }

    @Override
    public int getCount() {
        return fieldsVector.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_row_contents, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.idTextView = (TextView) convertView.findViewById(R.id.id_text);
            viewHolder.typeTextView = (TextView) convertView.findViewById(R.id.type_text);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_text);
            viewHolder.valueTextView = (TextView) convertView.findViewById(R.id.values_text);
            viewHolder.requiredTextView = (TextView) convertView.findViewById(R.id.required_text);
            viewHolder.fieldTypeTextView = (TextView) convertView.findViewById(R.id.field_type_text);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //Set value into each fields
        viewHolder.idTextView.setText(this.ids.get(position));
        viewHolder.typeTextView.setText(this.types.get(position));
        viewHolder.nameTextView.setText(this.names.get(position));
        viewHolder.valueTextView.setText(this.values.get(position));
        viewHolder.requiredTextView.setText(this.requires.get(position));
        viewHolder.fieldTypeTextView.setText(this.fieldTypes.get(position));

        return convertView;
    }
}
