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


public class EditContentActivity extends ActionBarActivity implements EditContentAdapter.OnEditContentListener, DatePickerFragment.onDateChangeListener{
    private ListView mListView;
    private EditContentAdapter mAdaptor;
    private HashMap modifiedData;
    private String JSONModifiedData = "";
    private JSONUtils jsonUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        jsonUtils = new JSONUtils();
        modifiedData = new HashMap();
        ContentStructureM content = new ContentStructureM();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String chosen_module = intent.getStringExtra("chosen_module");

        //Fetching JSON data
        JSONUtils generate = new JSONUtils();
        content = generate.generateDescribeFromJSON(chosen_module, content);
        content = generate.generateRecordDataFromJSON(chosen_module, id, content);

        //building listView
        ArrayList<Object> field = content.getField();

        mAdaptor = new EditContentAdapter(this,field, content.getData());
        mListView = (ListView)findViewById(R.id.listEdit);
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

        //Action bar button
        LayoutInflater li = LayoutInflater.from(this);
        ActionBar ab = getSupportActionBar();
        View customView = li.inflate(R.layout.edit_content_action_bar, null);
        ab.setCustomView(customView);

        ImageButton cancelBtn = (ImageButton) customView.findViewById(R.id.cancel_action_bar_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEditDialog();
            }
        });

        ImageButton acceptBtn = (ImageButton) customView.findViewById(R.id.accept_action_bar_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditContentActivity.this);
                alertDialogBuilder.setTitle("Save?").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "" + modifiedData.size(), Toast.LENGTH_SHORT).show();
                        modifiedData = mAdaptor.getModifiedData();
                        JSONModifiedData = jsonUtils.buildJSONString(modifiedData);
                        jsonUtils.updateContent(JSONModifiedData);
                        finish();
                    }
                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                //finish();
            }
        });

    }

    public void transferModifiedData(HashMap modifiedData) {
        this.modifiedData = modifiedData;

    }

    private void saveModifiedData(){

    }

    @Override
    public void onBackPressed() {
        cancelEditDialog();

    }

    private void cancelEditDialog(){
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(EditContentActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

    @Override
    protected void onStop() {

    }
}
