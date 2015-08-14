package easset.naviapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import GenerateJSON.FetchJSONForActivity;
import GenerateJSON.FetchJSONForFragment;
import GenerateJSON.JSONUtils;
import Utils.DatePickerFragment;
import fragment.ContentFragment;
import model.ContentStructureM;

public class AddActivity extends ActionBarActivity implements EditContentAdapter.OnEditContentListener,  DatePickerFragment.onDateChangeListener, FetchJSONForActivity.AsyncResponse{
    private ListView mListView;
    private EditContentAdapter mAdaptor;
    private HashMap modifiedData;
    private String JSONModifiedData = "";
    private ProgressBar bar;
    private String describeJSON;
    private JSONUtils generate;
    ContentStructureM content;
    String chosen_module;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        LayoutInflater li = LayoutInflater.from(this);
        ActionBar ab = getSupportActionBar();
        View customView = li.inflate(R.layout.add_content_action_bar, null);
        ab.setCustomView(customView);
        bar = (ProgressBar)findViewById(R.id.addProgressBar);

        ImageButton cancelBtn = (ImageButton) customView.findViewById(R.id.cancel_action_bar_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEditDialog();
            }
        });

        ImageButton acceptBtn = (ImageButton) customView.findViewById(R.id.save_action_bar_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifiedData = mAdaptor.getModifiedData();
                if(modifiedData!=null && modifiedData.size()>0){
                    Toast.makeText(getApplicationContext(), "" + modifiedData.size(), Toast.LENGTH_SHORT).show();
                    //modifiedData = mAdaptor.getModifiedData();
                    buildJSONString(modifiedData);
                    updateContent();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please fill in all input fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        content = new ContentStructureM();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        chosen_module = intent.getStringExtra("chosen_module");

        try{
            // fetching describe
            new FetchJSONForActivity(AddActivity.this, getApplicationContext(), bar).execute("http://192.168.1.151:8055/spartan/leadDescribe.js?chosen_module="+chosen_module);
        }catch(Exception e){
            e.printStackTrace();
        }

        generate = new JSONUtils();
        //Fetching JSON data
        /*JSONUtils generate = new JSONUtils();
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
        });*/
    }

    public void getDataFromBundle(){

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
        modifiedData.put(mAdaptor.getName(listPosition), changedDate);
        mAdaptor.setModifiedData(modifiedData);
    }

    @Override
    public void processFinish(String jsonOutput) {
        describeJSON = jsonOutput;
        content = generate.generateDescribeFromJSON(chosen_module, content, describeJSON);
        mAdaptor.setItemList(content.getField(), content.getData());
        mAdaptor.notifyDataSetChanged();
    }
}
