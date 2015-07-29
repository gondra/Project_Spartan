package easset.naviapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import GenerateJSON.JSONUtils;
import Utils.DatePickerFragment;
import model.ContentStructureM;

public class AddActivity extends ActionBarActivity implements EditContentAdapter.OnEditContentListener,  DatePickerFragment.onDateChangeListener{
    private ListView mListView;
    private EditContentAdapter mAdaptor;
    private HashMap modifiedData;
    private String JSONModifiedData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        LayoutInflater li = LayoutInflater.from(this);
        ActionBar ab = getSupportActionBar();
        View customView = li.inflate(R.layout.add_content_action_bar, null);
        ab.setCustomView(customView);

        ImageButton cancelBtn = (ImageButton) customView.findViewById(R.id.cancel_action_bar_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton acceptBtn = (ImageButton) customView.findViewById(R.id.save_action_bar_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "" + modifiedData.size(), Toast.LENGTH_SHORT).show();
                modifiedData = mAdaptor.getModifiedData();
                buildJSONString(modifiedData);
                updateContent();
                finish();
            }
        });

        ContentStructureM content = new ContentStructureM();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String chosen_module = intent.getStringExtra("chosen_module");

        //Fetching JSON data
        JSONUtils generate = new JSONUtils(this,null,null);
        content = generate.generateDescribeFromJSON(chosen_module, content);

        //building listView
        ArrayList<Object> field = content.getField();

        mAdaptor = new EditContentAdapter(this,field, content.getData());
        mListView = (ListView)findViewById(R.id.listAdd);
        try{
            mListView.setAdapter(mAdaptor);
        }catch (Exception e){
            e.printStackTrace();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = mAdaptor.getItemViewType(position);
                if(itemType == 3){

                }
            }
        });
    }

    @Override
    public void transferModifiedData(HashMap modifiedData) {
        this.modifiedData = modifiedData;

    }

    private void buildJSONString(HashMap modifiedData){
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

        JSONModifiedData = JSON.toString();
    }

    private void updateContent(){

    }

    @Override
    public void onBackPressed() {
        cancelEditDialog();
    }

    private void cancelEditDialog(){
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(AddActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        exitDialog.setTitle("Cancel")
                .setMessage("Are you sure you want to discard data changes?")
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        exitDialog.create().show();
    }

    @Override
    public void sendDateBack(String changedDate, int listPosition) {
        TextView dateTextView = mAdaptor.getTempDateView();
        dateTextView.setText(changedDate);

        modifiedData = mAdaptor.getModifiedData();
        modifiedData.put(mAdaptor.getName(listPosition),changedDate );
        mAdaptor.setModifiedData(modifiedData);
    }
}
