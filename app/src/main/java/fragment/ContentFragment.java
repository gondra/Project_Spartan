package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import Describe.PrepareForGeneratingObject;
import GenerateJSON.GenerateJSONUtils;
import easset.naviapp.EditContentActivity;
import easset.naviapp.R;
import model.ContentStructureM;

public class ContentFragment extends Fragment {
    PrepareForGeneratingObject mPrepareForGeneratingObject;
    private OnMainFragmentInteractionListener mListener;
    private ListView mListView;
    private ContentAdapter mAdaptor;
    private ContentStructureM content;
    private String recordName;
    private String chosen_module;
    private String id;
    private GenerateJSONUtils generate;
    private int isInitJSON = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_contents, container, false);
        recordName = getArguments().getString("chosen_record");
        chosen_module = getArguments().getString("chosen_module");
        id = getArguments().getString("id");

        /**Preparing data*/
        content = new ContentStructureM();

        generate = new GenerateJSONUtils();
        content = generate.generateDescribeFromJSON(chosen_module, content);
        content = generate.generateRecordDataFromJSON(chosen_module, id, content);
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
        if(isInitJSON == 0){
            content = generate.generateDescribeFromJSON(chosen_module, content);
            content = generate.generateRecordDataFromJSON(chosen_module, id, content);
        }else{
            isInitJSON = 0;
        }

        mAdaptor.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent editIntent = new Intent(getActivity().getApplicationContext(), EditContentActivity.class);
                editIntent.putExtra("chosen_module", chosen_module);
                editIntent.putExtra("id", id);
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
