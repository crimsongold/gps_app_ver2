/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jon on 5/4/2015.
 */
public class DataLog {
    public static final String TABLE_NAME = "DATA_TABLE";
    public static final String DATABASE_NAME = "DATA_LOG";
    public static final String lat = "LAT";
    public static final String lng = "LONG";
    public static final String uploaded = "UP";


    private Context ctxt;
    private Cursor crs;
    private DatabaseHelper my_helper;
    private SQLiteDatabase my_db;

    /**
     * sqlite does not have boolean values or a simple flag so integers are used for keeping track
     * of uploaded or not.  0 for not uploaded, 1 for uploaded.
     */
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " ( "
            + " ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            lat + " REAL NOT NULL, " +
            lng + "REAL NOT NULL, " +
            uploaded + " INTEGER NOT NULL )";


    public DataLog(Context ctxt) {
        this.ctxt = ctxt;
    }

    public void addPoint(double lat_p, double lng_p) {

        my_helper = new DatabaseHelper(ctxt);
        my_db = my_helper.getWritableDatabase();

        ContentValues init_vals = new ContentValues();
        init_vals.put(lat, lat_p);
        init_vals.put(lng, lng_p);
        init_vals.put(uploaded, 0);
        my_db.insert(TABLE_NAME, null, init_vals);
        my_helper.close();


    }

    public void clearCache() {

        this.my_db.delete(TABLE_NAME, "WHERE UP > 0", null);
    }

    public void setUploaded() {
        //this.my_db.update();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);


        }

        public void onCreate(SQLiteDatabase db) {
            Log.i("Database", "Creating Database...");
            db.execSQL(DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //No changes to make so far...
        }
    }


}
