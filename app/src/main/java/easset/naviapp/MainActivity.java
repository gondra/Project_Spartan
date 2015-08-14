package easset.naviapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import Utils.LoadImageFromURI;
import fragment.ContentFragment;
import fragment.RecordFragment;
import fragment.ViewGroupFragment;



public class MainActivity extends ActionBarActivity implements RecordFragment.OnMainFragmentInteractionListener, ContentFragment.OnMainFragmentInteractionListener, ViewGroupFragment.OnMainFragmentInteractionListener{
    DrawerLayout mDrawerLayout;
    ListView mListView;
    ActionBarDrawerToggle mBarDrawerToggle;
    private ArrayList<String> menuListText, myOpenTaskText, todayCalendersText, notificationsText;
    private ArrayList<JSONObject> menuList, myOpenTask, todayCalenders, notifications;
    SQLiteDatabase mDb;
    Cursor mCursor;
    DBaseHelper dbHelper;
    ArrayList<HashMap<String, String>> logOnData;
    String userName, token, tokenExpiryDate;
    ImageView profilePic;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = (ProgressBar) this.findViewById(R.id.mainprogressBar);

        //initial value
        logOnData = new ArrayList<>();
        menuList = new ArrayList<>();
        myOpenTask = new ArrayList<>();
        todayCalenders = new ArrayList<>();
        notifications = new ArrayList<>();

        /** Test JSON */
        SharedPreferences sp = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        String menuJson =  sp.getString("logOnJSON", "");
        Bundle bundle = getIntent().getExtras();
        userName = bundle.getString("user");
        /** Test JSON */

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        /**get menu list to generate list menu*/
        //logOnData = initialMenu(getApplication(), userName);
        initMenuWithJSON(menuJson);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mListView = (ListView)findViewById(R.id.left_drawer);
        mListView.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.list_row_menu, menuListText));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                /**select menu on the left sidebar*/
                selectMenu(position, mListView);
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String uri = "https://fbcdn-sphotos-g-a.akamaihd.net/hphotos-ak-xft1/v/t1.0-9/10431553_1503442033268897_2112438036426837056_n.jpg?oh=ebc31a183de8ae3391afbdf890139f15&oe=55D7C08A&__gda__=1440212203_5ffcf60f61b4d92618caf6c3de298135";
                    profilePic = (ImageView)findViewById(R.id.profilePic);
                    new LoadImageFromURI(getApplicationContext(), profilePic, bar).execute(uri);
                }
            });

    }

    /**Navigation drawer's controller*/
    private void selectMenu(int position, ListView mListView){
        //Fragment fragment = new RecordFragment();
        Fragment fragment = new ViewGroupFragment();
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
        //transaction.replace(R.id.content_frame, fragment, "RECORD_FRAGMENT");
        profilePic.setVisibility(View.GONE);
        transaction.replace(R.id.content_frame, fragment, "VIEW_GROUP_FRAGMENT");
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

    private void initMenuWithJSON(String menuJSON){
        try{
            JSONObject userData = new JSONObject(menuJSON);
            userName = userData.getString("Username");
            token = userData.getString("Token");
            tokenExpiryDate = userData.getString("TokenExpiryDate");

            /**Convert menu to  ArrayAdapter*/
            JSONArray menuListJSONArray = userData.getJSONArray("Modules");
            JSONObject tempMenuListObject;
            ArrayList<String> tempMenuList = new ArrayList<>();
            for(int i=0;i<menuListJSONArray.length();i++){
                tempMenuListObject = menuListJSONArray.getJSONObject(i);
                String module = tempMenuListObject.getString("module");
                String label = tempMenuListObject.getString("label");
                String labelTH = tempMenuListObject.getString("labelTH");
                tempMenuList.add(labelTH);
                menuList.add(tempMenuListObject);

            }
            menuListText = tempMenuList;

            JSONArray myOpenTaskArray = userData.getJSONArray("My Open Tasks");
            JSONObject myOpenTaskObject;
            ArrayList<String> tempMyOpenTask = new ArrayList<>();
            for(int i=0;i<myOpenTaskArray.length();i++){
                myOpenTaskObject = myOpenTaskArray.getJSONObject(0);
                /*String tmpStr = myOpenTaskObject.getString(Integer.toString(i));*/
                tempMyOpenTask.add(/*tmpStr*/"");
                myOpenTask.add(myOpenTaskObject);

            }
            myOpenTaskText = tempMyOpenTask;

            JSONArray todayCalendersArray = userData.getJSONArray("Today's Calendar");
            JSONObject todayCalendersObject;
            ArrayList<String> tempTodayCalenders = new ArrayList<>();
            for(int i=0;i<todayCalendersArray.length();i++){
                todayCalendersObject = todayCalendersArray.getJSONObject(0);
                /*String tmpStr = todayCalendersObject.getString(Integer.toString(i));*/
                tempTodayCalenders.add(/*tmpStr*/"");
                todayCalenders.add(todayCalendersObject);
            }
            todayCalendersText = tempTodayCalenders;

            JSONArray notificationsArray = userData.getJSONArray("Notifications");
            JSONObject notificationsObject ;
            ArrayList<String> tempNotifications = new ArrayList<>();
            for(int i=0;i<notificationsArray.length();i++){
                notificationsObject = notificationsArray.getJSONObject(0);
                /*String tmpStr = notificationsObject.getString(Integer.toString(i));*/
                tempNotifications.add(/*tmpStr*/"");
                notifications.add(notificationsObject);
            }
            notificationsText = tempNotifications;

        }catch(JSONException e){
            Log.e("JSON convert error" ,e.getMessage());
        }
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
        if(f!=null){
            if (f instanceof ContentFragment){
                inflater.inflate(R.menu.menu_content, menu);
            }else if(f instanceof RecordFragment){
                inflater.inflate(R.menu.menu_record, menu);
            }else if(f instanceof ViewGroupFragment){
                inflater.inflate(R.menu.menu_view, menu);
            }
            else{
                inflater.inflate(R.menu.menu_main, menu);
            }
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
                if(getFragmentManager().getBackStackEntryCount() == 1){
                    profilePic.setVisibility(View.VISIBLE);
                }
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
    public CallAPI(Context mContext, String uri){
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
