package fragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import easset.naviapp.R;

/**
 * Created by easset-01 on 7/3/2015.
 */
public class RecordAdapter extends BaseAdapter {
    Context mContext;
    JSONArray recordVector;

    private static class ViewHolderItem {
        TextView idTextView;
        TextView nameTextView;
        TextView valueTextView;

    }

    public RecordAdapter(Context mContext, JSONArray recordVector){
        this.mContext = mContext;
        this.recordVector = recordVector;
    }

    @Override
    public int getCount() {
        return this.recordVector.length();
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

        if(convertView==null) {
            /***inflate the layout*/
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_row_record, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.idTextView = (TextView) convertView.findViewById(R.id.record_id);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.record_name);
            viewHolder.valueTextView = (TextView) convertView.findViewById(R.id.record_value);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //Set value into each fields
        try{
            viewHolder.idTextView.setText(this.recordVector.getJSONObject(position).getString("Id"));
            viewHolder.nameTextView.setText(this.recordVector.getJSONObject(position).getString("Name")+"YO YO");
            viewHolder.valueTextView.setText(this.recordVector.getJSONObject(position).getString("Description"));

        }catch(JSONException e){
            Log.e("Populate data failed! ", e.getMessage());
        }


        return convertView;
    }
}
