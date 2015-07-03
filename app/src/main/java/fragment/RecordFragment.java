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

import easset.naviapp.R;

public class RecordFragment extends Fragment {

    private static class Record{
        int currentPage;
        int rowPerPage;
        int totalPage;
        JSONArray recordArray;
    }

    private OnMainFragmentInteractionListener mListener;
    private ListView mListView;
    private RecordAdapter mAdaptor;

    public interface OnMainFragmentInteractionListener {
        void setTitle(String title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);
        Record record= new Record();

        String chosenMenu = getArguments().getString("chosen_menu");
        generateRecordData(record); /**Convert Record JSON to Record Data */

        /**Set title*/
        if (mListener != null) {
            mListener.setTitle(chosenMenu);
        }

        /**Prepare ListView*/
        mAdaptor = new RecordAdapter(rootView.getContext(),record.recordArray);
        mListView = (ListView)rootView.findViewById(R.id.recordListView);
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


    private String generateRecordData(Record record){
        final String LEAD_RECORD_JSON = "{\"currentPage\":1,\"rowPerPage\":10,\"totalPage\":10,\"records\":[{\"id\":\"962E7DD1-8467-43A2-8803-7DBD52741E41\",\"Name\":\"Lead Name 1\",\"Description\":\"Lead Description 1\"},{\"id\":\"0A27D3E6-A3FD-445B-B1A2-2E5D7037DA92\",\"Name\":\"Lead Name 2\",\"Description\":\"Lead Description 2\"}]}";
        try{
            JSONObject leadRecordJSON = new JSONObject(LEAD_RECORD_JSON);
            record.currentPage = leadRecordJSON.getInt("currentPage");
            record.rowPerPage = leadRecordJSON.getInt("rowPerPage");
            record.totalPage = leadRecordJSON.getInt("totalPage");
            record.recordArray = leadRecordJSON.getJSONArray("records");
        }
        catch(JSONException e){
            Log.e("convert record JSON : ",e.getMessage());
        }
      return LEAD_RECORD_JSON;
    }


}
