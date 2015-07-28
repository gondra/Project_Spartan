package easset.naviapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fragment.ContentFragment;
import fragment.RecordFragment;


public class MainActivity extends ActionBarActivity implements RecordFragment.OnMainFragmentInteractionListener, ContentFragment.OnMainFragmentInteractionListener{
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

        /**get menu list to generate list menu*/
        logOnData = initialMenu(getApplication(), userName);

        if(!logOnData.isEmpty()){
            userData = logOnData.get(0);

            try{
                userDataJSON = new JSONObject(userData.get("JSON"));

                /**Convert menu to  ArrayAdapter*/
                menuListJSON = userDataJSON.getJSONObject("Modules");
                ArrayList<String> tempMenuList = new ArrayList<>();
                for(int i=1;i<=menuListJSON.length();i++){
                    String tmpStr = menuListJSON.getString(Integer.toString(i));
                    tempMenuList.add(tmpStr);

                }
                menuListText = tempMenuList;

                /**Get content*/
                contentJSON = userDataJSON.getJSONObject("Secondary contents");

            }catch(JSONException e){
                Log.e("JSON convert error" ,e.getMessage());
            }

        }else{ /**no data or first login*/
            String[] tmpMenu = new String[]{"Home","View","Edit","About"};
            menuListText = new ArrayList<>(Arrays.asList(tmpMenu));
        }


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mListView = (ListView)findViewById(R.id.left_drawer);
        mListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.list_row_menu, menuListText));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                /**select menu on the left sidebar*/
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
                invalidateOptionsMenu(); /** creates call to onPrepareOptionsMenu()*/
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getTitle().toString());
                invalidateOptionsMenu(); /** creates call to onPrepareOptionsMenu()*/
            }
        };

        mBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mBarDrawerToggle);

        /*Button apiBtn = (Button)findViewById(R.id.test);
        apiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CallAPI(getApplicationContext()).execute("");
            }
        });*/

    }

    /**Navigation drawer's controller*/
    private void selectMenu(JSONObject contentJSON, int position, ListView mListView){
        Fragment fragment        = new RecordFragment();
        Bundle args = new Bundle();

        /**Store value for passing to fragment*/
        TextView itemView = (TextView)getViewByPosition(position, mListView);
        //args.putString("content_json_str",contentJSON.toString());
        args.putString("chosen_module", (String) itemView.getText());


        /**Set argument to fragment*/
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        /**Open fragment instead of previous view*/

        transaction.replace(R.id.content_frame, fragment, "RECORD_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();

        /** update selected item and title, then close the drawer*/
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
    protected void onResume() {
        setTitle("Spartan");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Fragment f = getFragmentManager().findFragmentById(R.id.content_frame);
        if (f instanceof ContentFragment){
            inflater.inflate(R.menu.menu_content, menu);
        }else if(f instanceof RecordFragment){
            inflater.inflate(R.menu.menu_record, menu);
        }else{
            inflater.inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    public String getActionBarTitle(){
        return (String)getSupportActionBar().getTitle();
    }

    @Override
    public void setTitle(String title,Activity childActivity) {
        try{
            super.setTitle(title);
            getSupportActionBar().setTitle(getTitle().toString());
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

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mListView)){
            mDrawerLayout.closeDrawer(mListView);
        }else{
            if (getFragmentManager().getBackStackEntryCount() > 0 ){
                getFragmentManager().popBackStack();
            } else {
                AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                exitDialog.setTitle("Exit?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                exitDialog.create().show();
//            super.onBackPressed();
            }
        }

    }




}

//Fetching JSON from rest api
class CallAPI extends AsyncTask<String, String, String> {
    //public final static String urlString = "http://192.168.1.151:8080/FreeWriting/login";
    public final static String urlString = "http://www.pantip.com";
    Context mContext;
    public CallAPI(Context mContext){
        this.mContext = mContext;
    }
    @Override
    protected String doInBackground(String... params) {

        String resultToDisplay = "";

        InputStream in = null;

        // HTTP Get
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());

        } catch (Exception e ) {

            System.out.println(e.getMessage());

            return e.getMessage();

        }

        return "Test";

    }

    protected void onPostExecute(String result) {
        Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();
    }

} // end CallAPI
