package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Locale;

import GenerateJSON.FetchJSONForFragment;
import GenerateJSON.JSONUtils;
import easset.naviapp.AddActivity;
import easset.naviapp.R;
import model.RecordM;

public class RecordFragment extends Fragment implements FetchJSONForFragment.AsyncResponse{

    private OnMainFragmentInteractionListener mListener;
    private FetchJSONForFragment.AsyncResponse mAsyncResponse;
    private ListView mListView;
    private RecordAdapter mAdaptor;
    private RecordM record;
    private String chosen_module;
    private String chosen_id;
    EditText searchBox;
    DrawerLayout mDrawerLayout;
    private ProgressBar bar;
    private JSONUtils jsonUtils;

    public interface OnMainFragmentInteractionListener {
        void setTitle(String title,Activity childActivity);
        View getViewByPosition(int position, ListView mListView);
    }

    @Override
    public void processFinish(String jsonResult) {
        jsonUtils.generateRecordData(record, jsonResult);
        mAdaptor.setItemList(record.getRecordArray());
        mAdaptor.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_record, container, false);
        setHasOptionsMenu(true);
        record = new RecordM();
        jsonUtils = new JSONUtils();
        mAsyncResponse = this;
        searchBox = (EditText)rootView.findViewById(R.id.search_record);
        searchBox.setVisibility(View.GONE); //default state
        bar = (ProgressBar)rootView.findViewById(R.id.recordProgressBar);

        final FragmentManager fragmentManager = getFragmentManager();

        chosen_module = getArguments().getString("chosen_module");
        chosen_id = getArguments().getString("chosen_id");
        //generateRecordData(record, chosen_module, chosen_id); /**Convert Record JSON to Record Data */
        mDrawerLayout = (DrawerLayout)rootView.findViewById(R.id.drawer_layout);

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

        // Capture Text in EditText
        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());
                mAdaptor.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        try{
            new FetchJSONForFragment(RecordFragment.this, getActivity().getApplicationContext(), bar).execute("http://192.168.1.151:8055/spartan/record.js?chosen_module="+chosen_module+"?id="+chosen_id);
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
            mListener.setTitle(chosen_module+" Record",getActivity());
        }

        mAdaptor.notifyDataSetChanged();
        /*
        set value of adapter
        and
        fetching a new record JSON
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent addIntent = new Intent(getActivity().getApplicationContext(), AddActivity.class);
                addIntent.putExtra("chosen_module", chosen_module);
                getActivity().startActivity(addIntent);
                return true;
            case R.id.action_search:
                if(searchBox.getVisibility() == View.VISIBLE){
                    searchBox.setVisibility(View.GONE);
                }else{
                    searchBox.setVisibility(View.VISIBLE);
                }


            default:
                break;
        }
        return true;
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
