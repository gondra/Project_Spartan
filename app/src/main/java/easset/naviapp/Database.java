package easset.naviapp;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

class DBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "SPARTAN";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "CUSTOMER_INFORMATION";
    public static final String COL_USER = "USER_NAME";
    public static final String COL_INFO_JSON = "INFORMATION";
    public static final String ADMIN_JSON = "{\"Token\":\"4263f106-d63f-4ec2-a835-a355ee1d3c8d\",\"Modules\":{\"1\":\"Lead\",\"2\":\"Accounts\",\"3\":\"Contact\",\"4\":\"Opportunity\",\"5\":\"Quote\",\"6\":\"Product\"},\"Title\":\"Opportunity\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Opportunity Name\",\"Value\":\"Opportunity01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Opportunity Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"2\",\"Type\":\"DropDownList\",\"Name\":\"Owner\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"3\",\"Type\":\"DropDownList\",\"Name\":\"Account Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"3\",\"Type\":\"TextBox\",\"Name\":\"Amount\",\"Values\":\"\",\"Required\":\"false\",\"FieldType\":\"number\"}]}";
    public static final String START_JSON = "{\"Token\":\"4263f106-d63f-4ec2-a835-a355ee1d3c8d\",\"Modules\":{\"1\":\"Lead\",\"2\":\"Accounts\",\"3\":\"Contact\",\"4\":\"Opportunity\",\"5\":\"Quote\",\"6\":\"Product\"},\"Secondary contents\":{\"Lead\":{\"Title\":\"Lead\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Lead Name\",\"Value\":\"Lead01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Lead Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"}]},\"Accounts\":{\"Title\":\"Accounts\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Accounts Name\",\"Value\":\"Accounts01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Accounts Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"2\",\"Type\":\"DropDownList\",\"Name\":\"Owner\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"}]},\"Contact\":{\"Title\":\"Contact\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Contact Name\",\"Value\":\"Contact01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Contact Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"2\",\"Type\":\"DropDownList\",\"Name\":\"Owner\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"3\",\"Type\":\"DropDownList\",\"Name\":\"Account Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"}]},\"Opportunity\":{\"Title\":\"Opportunity\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Opportunity Name\",\"Value\":\"Opportunity01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Opportunity Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"2\",\"Type\":\"DropDownList\",\"Name\":\"Owner\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"3\",\"Type\":\"DropDownList\",\"Name\":\"Account Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"4\",\"Type\":\"TextBox\",\"Name\":\"Amount\",\"Values\":\"\",\"Required\":\"false\",\"FieldType\":\"number\"}]},\"Quote\":{\"Title\":\"Quote\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Quote Name\",\"Value\":\"Quote01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Quote Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"},{\"Id\":\"2\",\"Type\":\"DropDownList\",\"Name\":\"Owner\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"}]},\"Product\":{\"Title\":\"Product\",\"Columns\":[{\"Id\":\"1\",\"Name\":\"Product Name\",\"Value\":\"Product01\"}],\"Fields\":[{\"Id\":\"1\",\"Type\":\"TextBox\",\"Name\":\"Product Name\",\"Values\":\"\",\"Required\":\"true\",\"FieldType\":\"string\"}]}}}";

    public DBaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String startJson = "";
        String adminJson = "";

        try{
            JSONObject jsonObject = new JSONObject(START_JSON);
            startJson = jsonObject.toString();
            jsonObject = new JSONObject(ADMIN_JSON);
            adminJson = jsonObject.toString();

        }catch(Exception e){
            Log.e("JSON",e.getMessage());
        }
        db.execSQL("CREATE TABLE " + TABLE_NAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER + " TEXT, " + COL_INFO_JSON + " TEXT);");

        db.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_USER + ", " + COL_INFO_JSON + ") VALUES ('admin', '"+adminJson+"');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_USER + ", " + COL_INFO_JSON + ") VALUES ('start', '"+startJson+"');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

public class Database { }
