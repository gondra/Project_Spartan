package easset.naviapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fragment.HomeFragment;
import fragment.MainFragment;
import fragment.ViewFragment;


public class MainActivity extends ActionBarActivity implements MainFragment.OnMainFragmentInteractionListener{
    DrawerLayout mDrawerLayout;
    ListView mListView;
    ActionBarDrawerToggle mBarDrawerToggle;
    private ArrayList<String> menuListText;
    SQLiteDatabase mDb;
    Cursor mCursor;
    DBaseHelper dbHelper;
    ArrayList<HashMap<String, String>> logOnData;
    HashMap<String, String> userData;
    JSONObject userDataJSON, menuListJSON, contentJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        String userName = bundle.getString("user");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        logOnData = new ArrayList<>();

        //get menu list to generate list menu
        logOnData = initialMenu(getApplication(), userName);

        if(!logOnData.isEmpty()){
            userData = logOnData.get(0);

            try{
                userDataJSON = new JSONObject(userData.get("JSON"));

                //Convert menu to  ArrayAdapter
                menuListJSON = userDataJSON.getJSONObject("Modules");
                ArrayList<String> tempMenuList = new ArrayList<>();
                for(int i=1;i<=menuListJSON.length();i++){
                    String tmpStr = menuListJSON.getString(Integer.toString(i));
                    tempMenuList.add(tmpStr);

                }
                menuListText = tempMenuList;

                //Get content
                contentJSON = userDataJSON.getJSONObject("Secondary contents");

            }catch(JSONException e){
                Log.e("JSON convert error" ,e.getMessage());
            }

        }else{ //no data or first login
            String[] tmpMenu = new String[]{"Home","View","Edit","About"};
            menuListText = new ArrayList<>(Arrays.asList(tmpMenu));
        }


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mListView = (ListView)findViewById(R.id.left_drawer);
        mListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.drawer_list_item, menuListText));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                //select menu on the left sidebar
                selectMenu(contentJSON, position, mListView);
            }
        });

        mBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close){

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Spartan Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getTitle().toString());
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mBarDrawerToggle);

    }

    //Navigation drawer's controller
    private void selectMenu(JSONObject contentJSON, int position, ListView mListView){
        Fragment fragment = new MainFragment();
        Bundle args = new Bundle();

        //Store value for passing to fragment
        TextView itemView = (TextView)getViewByPosition(position, mListView);
        args.putString("content_json_str",contentJSON.toString());
        args.putString("chosen_menu", (String) itemView.getText());


        //Set argument to fragment
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //Open fragment instead of previous view
        transaction.replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mListView.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mListView);
    }

    public ArrayList<HashMap<String, String>> initialMenu(Context mContext, String userName){
        ArrayList<HashMap<String, String>> resultArray = new ArrayList<>();
        HashMap<String, String> result = new HashMap<>();
        try {
            dbHelper = new DBaseHelper(mContext);
            mDb = dbHelper.getWritableDatabase();
            mCursor = mDb.rawQuery(" SELECT " + DBaseHelper.COL_USER + ", " + DBaseHelper.COL_INFO_JSON+ "  FROM CUSTOMER_INFORMATION WHERE "+ DBaseHelper.COL_USER +" = '"+userName+"' ;", null);

            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                result.put("user", mCursor.getString(mCursor.getColumnIndex(DBaseHelper.COL_USER)));
                result.put("JSON",mCursor.getString(mCursor.getColumnIndex(DBaseHelper.COL_INFO_JSON)));
                resultArray.add(result);
                mCursor.moveToNext();
            }
        }catch (Exception e){
             Log.e("Database",e.getMessage());
        }finally{
            mCursor.close();
            mDb.close();
            dbHelper.close();
        }
        return  resultArray;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public String getActionBarTitle(){
        return (String)getSupportActionBar().getTitle();
    }

    @Override
    public void setTitle(String title) {
        try{
            super.setTitle(title);
        }catch(Exception e){
            Log.e("Set title ",e.getMessage());
        }

    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
