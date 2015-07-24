package easset.naviapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import GenerateJSON.GenerateJSONUtils;
import model.ContentStructureM;


public class EditContentActivity extends ActionBarActivity {
    private ListView mListView;
    private EditContentAdapter mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        ContentStructureM content = new ContentStructureM();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String chosen_module = intent.getStringExtra("chosen_module");

        GenerateJSONUtils generate = new GenerateJSONUtils();
        content = generate.generateDescribeFromJSON(chosen_module, content);
        content = generate.generateRecordDataFromJSON(chosen_module, id, content);

        LayoutInflater li = LayoutInflater.from(this);
        ActionBar ab = getSupportActionBar();
        View customView = li.inflate(R.layout.edit_content_action_bar, null);
        ab.setCustomView(customView);

        ImageButton cancelBtn = (ImageButton) customView.findViewById(R.id.cancel_action_bar_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton acceptBtn = (ImageButton) customView.findViewById(R.id.accept_action_bar_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

            }
        });
    }


    private void saveModifiedData(){

    }
}
