package easset.naviapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import Utils.DatePickerDialog;

public class EditContentAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Object> field;
    private static final String PICK_LIST = "picklist";
    private static final String DATE_TYPE = "DateTime";
    private static final String TEXT_EXPAND = "DateTime";
    private static final String INTEGER = "integer";
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PICK_LIST = 1;
    private static final int TYPE_TEXT_EDIT = 2;
    private static final int TYPE_TEXT_EXPAND = 3;
    private static final int TYPE_MAX_COUNT = TYPE_TEXT_EXPAND + 1;
    LayoutInflater inflater;

    /**Attribute of each list*/
    private ArrayList<String> name, type, format, label, labelTH, required, defaultValue ;
    private ArrayList<Boolean> updateable;
    private ArrayList<Integer> length;
    private ArrayList<ArrayList<String>> listValue;
    private HashMap modifiedData;
    private HashMap data ;

    public EditContentAdapter(Context mContext, ArrayList<Object> field, HashMap data){

        inflater = ((Activity) mContext).getLayoutInflater();
        this.modifiedData = new HashMap();
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
        try {
            mOnEditContentListener = (OnEditContentListener) this.mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.mContext.toString()
                    + " must implement OnEditContentListener");
        }

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
                this.updateable.add(i,( (JSONObject) field.get(i)).getString("updateable").equalsIgnoreCase("true") );
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

    private static class ViewHolderItem {
        TextView labelTextView;
        TextView valueEditView;
        Button pickListBtnView;
        DatePicker datePickerView;
        LinearLayout expandSection;
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
        int result;
        boolean updateable = getUpdateable(position);
        if(!updateable){
            result = TYPE_TEXT;
        }else{
            if(getType(position).equalsIgnoreCase(PICK_LIST)){
                result = TYPE_PICK_LIST;
            }else if(getType(position).equalsIgnoreCase(DATE_TYPE)){
                result = TYPE_TEXT_EXPAND;
            }
            else if(getType(position).equalsIgnoreCase(INTEGER)){
                result = TYPE_TEXT_EDIT;
            }
            else{
                result = TYPE_TEXT_EDIT;
            }
        }


        return result;
    }



    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolderItem viewHolder;
        ArrayList<String> tempFieldName = null;
        boolean hasData = false;
        if(data!=null){
            tempFieldName = (ArrayList<String>) data.get("fieldNames");
        }

        if(tempFieldName.contains(this.getName(position) )&& tempFieldName!=null ){
            hasData = true;
        }

        int viewType = getItemViewType(position);


        if (convertView == null) {
            viewHolder = new ViewHolderItem();
            switch (viewType) {
                case TYPE_TEXT:
                    convertView = inflater.inflate(R.layout.list_row_contents_text, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_edit);
                    break;

                case TYPE_TEXT_EDIT:
                    convertView = inflater.inflate(R.layout.list_row_contents_edit, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_edit);
                    break;

                case TYPE_TEXT_EXPAND:
                    convertView = inflater.inflate(R.layout.list_row_contents_text_expand, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.valueEditView = (TextView) convertView.findViewById(R.id.value_view);
                    viewHolder.datePickerView = (DatePicker) convertView.findViewById(R.id.expand_section);
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    viewHolder.datePickerView.init(year, month, day, null);
                    break;

                case TYPE_PICK_LIST:
                    convertView = inflater.inflate(R.layout.list_row_contents_pick, parent, false);
                    viewHolder.labelTextView = (TextView) convertView.findViewById(R.id.label_text);
                    viewHolder.pickListBtnView = (Button) convertView.findViewById(R.id.pick_list_btn);
                    break;
            }

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        switch (viewType) {
            case TYPE_TEXT:
                viewHolder.labelTextView.setText(this.getLabel(position));
                if(hasData ){
                    try{
                        viewHolder.valueEditView.setText((String)data.get(this.getName(position)));
                    }catch(Exception e){
                        viewHolder.valueEditView.setText(String.valueOf(data.get(this.getName(position))));
                    }

                }else{
                    viewHolder.valueEditView.setText(getDefaultValue(position));
                }
                break;

            case TYPE_TEXT_EDIT:
                viewHolder.labelTextView.setText(this.getLabel(position));
                if(hasData){
                    try{
                        viewHolder.valueEditView.setText((String)data.get(this.getName(position)));
                    }catch(Exception e){
                        viewHolder.valueEditView.setText(String.valueOf(data.get(this.getName(position))));
                    }

                }else{
                    viewHolder.valueEditView.setText(getDefaultValue(position));
                }
                break;

            case TYPE_TEXT_EXPAND: //Calendar
                viewHolder.labelTextView.setText(this.getLabel(position));
                if(hasData){
                    try{
                        viewHolder.valueEditView.setText((String)data.get(this.getName(position)));
                    }catch(Exception e){
                        viewHolder.valueEditView.setText(String.valueOf(data.get(this.getName(position))));
                    }

                }else{
                    viewHolder.valueEditView.setText(getDefaultValue(position));
                }

                viewHolder.datePickerView.setVisibility(View.GONE);
                viewHolder.valueEditView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int visibility = viewHolder.datePickerView.getVisibility();
                        if(visibility == View.VISIBLE){
                            collapse(viewHolder.datePickerView);
                        }else if(visibility == View.INVISIBLE){
                            expand(viewHolder.datePickerView);
                        }else if(visibility == View.GONE){
                            expand(viewHolder.datePickerView);
                        }else{

                        }

                    }
                });
                break;

            case TYPE_PICK_LIST:
                viewHolder.labelTextView.setText(this.getLabel(position));
                if(hasData){
                    viewHolder.pickListBtnView.setText((String)data.get(this.getName(position)));
                }else{
                    viewHolder.pickListBtnView.setText(this.getDefaultValue(position));
                }
                viewHolder.pickListBtnView.setOnClickListener(new View.OnClickListener() {
                    int biggerWhere;
                    @Override
                    public void onClick(View v) {
                        final ArrayList<String> itemsTemp = listValue.get(position);
                        final String[] items = itemsTemp.toArray(new String[itemsTemp.size()]);
                        if(viewHolder.pickListBtnView.getText().equals("") || viewHolder.pickListBtnView.getText() == null){
                            biggerWhere = 0;
                        }else{
                            biggerWhere = Arrays.asList(items).indexOf(viewHolder.pickListBtnView.getText());
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                        alertDialogBuilder.setTitle("Status").setSingleChoiceItems(items, biggerWhere, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }

                        )
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        int selectedPosition;
                                        selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                        viewHolder.pickListBtnView.setText(items[selectedPosition]);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }
                });
                break;
        }

        //set modified data
        modifiedData.put(this.getName(position), data.get(data.get(this.getName(position))));

        //send back to activity
        if(position == (this.name.size()-1)){

            if(mOnEditContentListener!=null){
                mOnEditContentListener.transferModifiedData(modifiedData);
            }
        }
        return convertView;
    }

    //Calendar show/hide
    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        int speed = (int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density);
        a.setDuration(150);
        v.startAnimation(a);

    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        int speed = (int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density);
        a.setDuration(150);
        v.startAnimation(a);
    }

    //Listener interface
    public interface OnEditContentListener{
        public void transferModifiedData(HashMap modifiedData);
    }
    OnEditContentListener mOnEditContentListener;


    //send data to other activity


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

    public boolean getUpdateable(int position) {
        return updateable.get(position);
    }

    public String getDefaultValue(int position) {
        return defaultValue.get(position);
    }
}