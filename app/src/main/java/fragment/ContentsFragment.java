package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;


import easset.naviapp.MainActivity;
import easset.naviapp.R;

public class ContentsFragment extends Fragment{

    private OnMainFragmentInteractionListener mListener;
    private ListView mListView;
    private ContentsAdapter mAdaptor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contents, container, false);

        /** Begin : Get data from JSONString section. */
        String contentJSONStr = getArguments().getString("content_json_str");
        String chosenMenu = getArguments().getString("chosen_menu");
        String title = "";
        Vector<JSONObject> columns = new Vector();
        Vector<JSONObject> fields = new Vector();

        try{
            JSONObject contentJSON = new JSONObject(contentJSONStr);
            JSONObject menuContent = contentJSON.getJSONObject(chosenMenu);

            //get data for drawing screen
            title = menuContent.getString("Title");
            JSONArray columnArrayTemp = menuContent.getJSONArray("Columns");
            for (int i=0; i<columnArrayTemp.length(); i++) {
                JSONObject columnObject = columnArrayTemp.getJSONObject(i);
                columns.add(i,columnObject);
            }
            JSONArray fieldArrayTemp = menuContent.getJSONArray("Fields");
            for (int i=0; i<fieldArrayTemp.length(); i++) {
                JSONObject fieldObject = fieldArrayTemp.getJSONObject(i);
                fields.add(i,fieldObject);
            }



        }catch(JSONException e){
            Log.e("Main Fragment ",e.getMessage());
        }

        if (mListener != null) {
            mListener.setTitle(title);
        }
        /* End : Get data from JSONString section. */

        /* List View Process*/
        mAdaptor = new ContentsAdapter(rootView.getContext(), fields);
        mListView = (ListView)rootView.findViewById(R.id.contentListView);
        mListView.setAdapter(mAdaptor);
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

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("Check", "onActivityCreated " + ((MainActivity) getActivity()).getActionBarTitle());
    }

    public interface OnMainFragmentInteractionListener {void setTitle(String title);
    }
}
