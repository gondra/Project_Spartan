package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import easset.naviapp.R;
import model.RecordM;

public class ViewGroupFragment extends Fragment{

    private OnMainFragmentInteractionListener mListener;
    private ListView mListView;
    private RecordAdapter mAdaptor;
    private RecordM record;
    private String chosen_module;
    DrawerLayout mDrawerLayout;

    public interface OnMainFragmentInteractionListener {
        void setTitle(String title,Activity childActivity);
        View getViewByPosition(int position, ListView mListView);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_record, container, false);
        record = new RecordM();

        final FragmentManager fragmentManager = getFragmentManager();
        //

        chosen_module = getArguments().getString("chosen_module");
        generateViewData(record); /**Convert View JSON to View Data */

        /**Prepare ListView*/
        mAdaptor = new RecordAdapter(rootView.getContext(),record.getRecordArray());
        mListView = (ListView)rootView.findViewById(R.id.recordListView);
        mListView.setAdapter(mAdaptor);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ContentFragment();
                Bundle args = new Bundle();
                View itemView = null;
                /**Store value for passing to fragment*/
                if (mListener != null) {
                    itemView = mListener.getViewByPosition(position, mListView);
                }
                String recordName = ((TextView) itemView.findViewById(R.id.record_name)).getText().toString();
                args.putString("chosen_record", recordName);
                args.putString("chosen_module", chosen_module);
                try {
                    args.putString("id", record.getRecordArray().get(position).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /**Set argument to fragment*/
                fragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                /**Open fragment instead of previous view*/
                transaction.replace(R.id.content_frame, fragment, "CONTENT_FRAGMENT");
                transaction.addToBackStack(null);
                transaction.commit();

                /** update selected item and title, then close the drawer*/
                //mListView.setItemChecked(position, true);

            }
        });

        return rootView;
    }




    private String generateViewData(RecordM record){
        final String LEAD_VIEW_JSON = "{\"currentPage\":1,\"rowPerPage\":10,\"totalPage\":10,\"records\":[{\"id\":\"962E7DD1-8467-43A2-8803-7DBD52741E41\",\"Name\":\"Lead Name 1\",\"Description\":\"Lead Description 1\"},{\"id\":\"0A27D3E6-A3FD-445B-B1A2-2E5D7037DA92\",\"Name\":\"Lead Name 2\",\"Description\":\"Lead Description 2\"}]}";
        try{
            JSONObject leadRecordJSON = new JSONObject(LEAD_VIEW_JSON);
            record.setCurrentPage(leadRecordJSON.getInt("currentPage"));
            record.setRowPerPage(leadRecordJSON.getInt("rowPerPage"));
            record.setTotalPage(leadRecordJSON.getInt("totalPage"));
            JSONArray tmpArray = leadRecordJSON.getJSONArray("records");
            Vector<JSONObject> recordVec = new Vector<>();
            for(int i=0;i<tmpArray.length();i++){
                recordVec.add(i, (JSONObject) tmpArray.get(i));
            }
            record.setRecordArray(recordVec);
        }
        catch(JSONException e){
            Log.e("convert view JSON : ", e.getMessage());
        }
        return LEAD_VIEW_JSON;
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
    public void onResume() {
        super.onResume();
        /**Set title*/
        if (mListener != null) {
            mListener.setTitle(chosen_module,getActivity());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.w("App destroyed", "App destroyed");

        super.onDestroy();
    }
}
