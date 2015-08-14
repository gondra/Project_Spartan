package fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import Describe.PrepareForGeneratingObject;
import GenerateJSON.FetchJSONForFragment;
import GenerateJSON.JSONUtils;
import easset.naviapp.EditContentActivity;
import easset.naviapp.R;
import model.ContentStructureM;

public class ContentFragment extends Fragment implements FetchJSONForFragment.AsyncResponse{
    PrepareForGeneratingObject mPrepareForGeneratingObject;
    private OnMainFragmentInteractionListener mListener;
    private ListView mListView;
    private ContentAdapter mAdaptor;
    private ContentStructureM content;
    private String recordName;
    private String chosen_module;
    private String id;
    private JSONUtils generate;
    private int isInitJSON = 0;
    private String describeJSON;
    private String contetnJSON;
    private ProgressBar bar;
    private String uri = "";
    @Override
    public void processFinish(String jsonOutput) {
        if(describeJSON == null || describeJSON.equalsIgnoreCase("")){
            describeJSON = jsonOutput;
            try{
                // fetching content
                content = generate.generateDescribeFromJSON(chosen_module, content, describeJSON);

                new FetchJSONForFragment(ContentFragment.this, getActivity().getApplicationContext(), bar).execute(uri);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            contetnJSON = jsonOutput;
            content = generate.generateRecordDataFromJSON(chosen_module, id, content, contetnJSON);
            mAdaptor.setItemList(content.getField(), content.getData());
            mAdaptor.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_contents, container, false);
        recordName = getArguments().getString("chosen_record");
        chosen_module = getArguments().getString("chosen_module");
        id = getArguments().getString("id");
        bar = (ProgressBar)rootView.findViewById(R.id.contentProgressBar);

        /**Preparing data*/
        content = new ContentStructureM();

        generate = new JSONUtils();
        isInitJSON = 1;

        ArrayList<Object> field = content.getField();

        mAdaptor = new ContentAdapter(rootView.getContext(),field, content.getData());
        mListView = (ListView)rootView.findViewById(R.id.contentListView);
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

        if(id.equalsIgnoreCase("962E7DD1-8467-43A2-8803-7DBD52741E41")){
            uri = "http://192.168.1.151:8055/spartan/leadRecord01.js?chosen_module="+chosen_module;
        }else if(id.equalsIgnoreCase("295C6D74-6F31-4762-8D5A-37314B4BF358")){
            uri = "http://192.168.1.151:8055/spartan/leadRecord02.js?chosen_module="+chosen_module;
        }

        try{
            // fetching describe
            new FetchJSONForFragment(ContentFragment.this, getActivity().getApplicationContext(), bar).execute("http://192.168.1.151:8055/spartan/leadDescribe.js?chosen_module="+chosen_module);
        }catch(Exception e){
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMainFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnMainFragmentInteractionListener {
        void setTitle(String title,Activity childActivity);
        View getViewByPosition(int position, ListView mListView);
    }



    @Override
    public void onResume() {
        super.onResume();
        /**Set title*/
        if (mListener != null) {
            mListener.setTitle(recordName,getActivity());
        }
        /*
            reload JSON
         */
        try{
            if(bar != null){
                if(isInitJSON == 0){
                    try{
                        // fetching describe
                        new FetchJSONForFragment(ContentFragment.this, getActivity().getApplicationContext(), bar).execute("http://192.168.1.151:8055/spartan/leadDescribe.js?chosen_module="+chosen_module);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    isInitJSON = 0;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent editIntent = new Intent(getActivity().getApplicationContext(), EditContentActivity.class);
                editIntent.putExtra("id", id);
                editIntent.putExtra("chosen_module", chosen_module);
                editIntent.putExtra("Describe_JSON", describeJSON);
                editIntent.putExtra("Record_JSON", contetnJSON);
                getActivity().startActivity(editIntent);
                return true;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
