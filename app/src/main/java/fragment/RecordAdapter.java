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
import java.util.Locale;
import java.util.Vector;

import easset.naviapp.R;

public class RecordAdapter extends BaseAdapter {
    Context mContext;
    Vector<JSONObject> recordVector;
    private ArrayList<JSONObject> arraylist;

    private ArrayList<String> ids;
    private ArrayList<String> names;
    private ArrayList<String> description;

    private static class ViewHolderItem {
        TextView idTextView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView valueTextView;

    }

    public RecordAdapter(Context mContext, Vector<JSONObject> recordVector){
        this.mContext = mContext;
        this.recordVector = recordVector;
        this.arraylist = new ArrayList(recordVector);

        this.ids = new ArrayList<>();
        this.names = new ArrayList<>();
        this.description = new ArrayList<>();

        populateDataFromJSON(recordVector);
    }

    public void populateDataFromJSON(Vector<JSONObject> fieldsVector){
        try{
            for(int i=0;i<fieldsVector.size();i++)
            {
                JSONObject fieldTemp = fieldsVector.get(i);
                this.ids.add(i, fieldTemp.getString("id"));
                this.names.add(i, fieldTemp.getString("Name"));
                this.description.add(i, fieldTemp.getString("Description"));
            }

        }catch(JSONException jsonE){
            Log.e("Populate data failed! ",jsonE.getMessage());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.recordVector.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setItemList(Vector<JSONObject> recordVector) {
        this.recordVector = recordVector;
        this.arraylist = new ArrayList(this.recordVector);
        populateDataFromJSON(this.recordVector);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if(convertView==null) {
            /***inflate the layout*/
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_row_record, parent, false);

            viewHolder = new ViewHolderItem();
            //viewHolder.idTextView = (TextView) convertView.findViewById(R.id.record_id);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.record_name);
            viewHolder.descriptionTextView = (TextView) convertView.findViewById(R.id.record_description);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //Set value into each fields
            //viewHolder.idTextView.setText(this.ids.get(position));
            viewHolder.nameTextView.setText(this.names.get(position));
            viewHolder.descriptionTextView.setText(this.description.get(position));
            //viewHolder.valueTextView.setText(this.description.get(position));

        return convertView;
    }

    public String getChosenItemId(int position) {
        return this.ids.get(position);
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        recordVector.clear();
        names.clear();
        description.clear();
        if (charText.length() == 0) {
            recordVector.addAll(arraylist);
            populateDataFromJSON(recordVector);
        }
        else
        {
            try{
                for (JSONObject record : arraylist)
                {
                    String recordNameTemp = record.getString("Name").toLowerCase(Locale.getDefault());
                    if (recordNameTemp.contains(charText))
                    {
                        recordVector.add(record);
                        names.add(record.getString("Name"));
                        description.add(record.getString("Description"));
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        notifyDataSetChanged();
    }
}
