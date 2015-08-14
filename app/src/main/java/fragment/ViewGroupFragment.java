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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import GenerateJSON.FetchJSONForFragment;
import GenerateJSON.JSONUtils;
import easset.naviapp.R;
import model.RecordM;

public class ViewGroupFragment extends Fragment implements FetchJSONForFragment.AsyncResponse{

    private OnMainFragmentInteractionListener mListener;
    private FetchJSONForFragment.AsyncResponse mAsyncResponse;
    private ListView mListView;
    private RecordAdapter mAdaptor;
    private RecordM record;
    private String chosen_module;
    private ProgressBar bar;
    private String viewJSON;
    DrawerLayout mDrawerLayout;
    private JSONUtils jsonUtils;

    @Override
    public void processFinish(String jsonResult) {
        this.viewJSON = jsonResult;
        jsonUtils.generateViewData(record, viewJSON);
        mAdaptor.setItemList(record.getRecordArray());
        mAdaptor.notifyDataSetChanged();
    }

    public interface OnMainFragmentInteractionListener {
        void setTitle(String title,Activity childActivity);
        View getViewByPosition(int position, ListView mListView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view, container, false);
        record = new RecordM();
        jsonUtils = new JSONUtils();
        mAsyncResponse = this;
        chosen_module = getArguments().getString("chosen_module");
        final FragmentManager fragmentManager = getFragmentManager();
        bar = (ProgressBar)rootView.findViewById(R.id.viewProgressBar);

        /**Prepare ListView*/
        mAdaptor = new RecordAdapter(rootView.getContext(),record.getRecordArray());
        mListView = (ListView)rootView.findViewById(R.id.ViewGroupListView);
        mListView.setAdapter(mAdaptor);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new RecordFragment();
                Bundle args = new Bundle();
                View itemView = null;

                /**Store value for passing to fragment*/
                if (mListener != null) {
                    itemView = mListener.getViewByPosition(position, mListView);
                }
                String recordName = ((TextView) itemView.findViewById(R.id.record_name)).getText().toString();
                String chosen_id = mAdaptor.getChosenItemId(position);
                args.putString("chosen_record", recordName);
                args.putString("chosen_module", chosen_module);
                args.putString("chosen_id", chosen_id);
                try {
                    args.putString("id", record.getRecordArray().get(position).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /**Set argument to fragment*/
                fragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                /**Open fragment instead of previous view*/
                transaction.replace(R.id.content_frame, fragment, "RECORD_FRAGMENT");
                transaction.addToBackStack(null);
                transaction.commit();

                /** update selected item and title, then close the drawer*/
                //mListView.setItemChecked(position, true);

            }
        });

        try{
            new FetchJSONForFragment(ViewGroupFragment.this, getActivity().getApplicationContext(), bar).execute("http://192.168.1.151:8055/spartan/view.js?chosen_module=" + chosen_module);
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