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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Jake on 5/7/2015.
 */
public class LocalMapData {
    /*
    Longitude ranges from -180 to +180 degrees.
    Latitude ranges from -90 to 90 degrees.
    4 decimal places of accuracy is plenty accurate.
    Therefore: the data points will be doubles with 4 decimal places.
     */

    public static final String db_name = "LOCALDATA_DB";
    public static final String table_name = "LOCAL_MAP_DATA";
    public static final String key_long =  "Longitude";         //Stored as REAL
    public static final String key_lat = "Latitude";            //Stored as REAL
    public static final String key_datetime = "Datetime";       //"YYYY-MM-DD HH:MM:SS.SSS"

    private Context ctxt;
    private Cursor crs;
    private DatabaseHelper my_helper;
    private SQLiteDatabase my_db;

    protected LocalMapData(Context context) { ctxt = context; }

    protected void push_data (final String user_id)
    {
        //Push the recent data from the database into the webservice database
    }

    protected void pull_data()
    {
        //Pull all of the data down from the database for a specific user.
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
    }

    //places a new data point into the local data table
    protected void add_point(final Calendar datetime, final double longitude, final double latitude)
    {
        my_helper = new DatabaseHelper(ctxt);
        my_db = my_helper.getWritableDatabase();

        String string_datetime = datetime.get(Calendar.DAY_OF_MONTH) + "/" +
                datetime.get(Calendar.MONTH) + "/" + datetime.get(Calendar.YEAR) + " " +
                datetime.get(Calendar.HOUR_OF_DAY) + ":" + datetime.get(Calendar.MINUTE);

        ContentValues init_vals = new ContentValues();
        init_vals.put(key_datetime, string_datetime);
        init_vals.put(key_long, longitude);
        init_vals.put(key_lat, latitude);

        my_db.insert(table_name, null, init_vals);
        my_helper.close();
    }

    protected void wipe_data()
    {
        my_db.execSQL("DROP TABLE + " + table_name);
    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) { super(context, db_name, null, 1); }

        public void onCreate(SQLiteDatabase db)
        {
            Log.i("Database", "Creating Database...");
            db.execSQL("CREATE TABLE IF NOT EXISTS LOCAL_MAP_DATA (" +
                    "Datetime TEXT PRIMARY KEY, " +
                    "Latitude REAL, " +
                    "Longitude REAL)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //No changes to make so far...
        }
    }
}
