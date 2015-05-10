/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
    public static final String key_lat = "lat";                     //Stored as REAL
    public static final String key_long =  "lon";                   //Stored as REAL
    public static final String key_uid = "source";                  //Stored as TEXT
    public static final String key_datetime = "timestamp";          //"YYYY-MM-DD HH:MM:SS.SSS"

    public static final String log_point_url = "http://450.atwebpages.com/addLog.php";

    private Context ctxt;
    private Cursor crs;
    private DatabaseHelper my_helper;
    private SQLiteDatabase my_db;

    protected LocalMapData(Context context) { ctxt = context; }

    class PushTask extends AsyncTask<String, Void, String[]>
    {
        private String selected_pk;

        //Push the recent data from the database into the webservice database
        protected String[] doInBackground(String... params)
        {
            selected_pk = params[3];
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(ctxt.getString(R.string.web_service_protocol))
                    .authority(ctxt.getString(R.string.web_service_url))
                    .appendPath("addLog.php")
                    .appendQueryParameter("lat", params[0])
                    .appendQueryParameter("lon", params[1])
                    .appendQueryParameter("source", params[2])
                    .appendQueryParameter("timestamp", params[3]);
            String url = builder.build().toString();

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            try {
                HttpResponse response = client.execute(post);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                return new String[]{params[0],reader.readLine()};
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(String[] result)
        {
            JSONTokener tokener = new JSONTokener(result[1]);
            JSONObject finalResult = null;
            String regResult = "";

            try {
                finalResult = new JSONObject(tokener);
                regResult = finalResult.getString("result");
            } catch (JSONException e) {
                Log.i("PushTask", "ERROR");
                e.printStackTrace();
            }

            if (regResult.equals("success")) {
                // drop row with timestamp = ""
                my_db.execSQL("DELETE FROM " + table_name + " WHERE " + key_datetime + " == " +
                                selected_pk);
            } else {
                // do something
            }
        }
    }

    //places a new data point into the local data table
    protected void add_point(final String user_id, final long unix_datetime,
                             final double latitude, final double longitude)
    {
        my_helper = new DatabaseHelper(ctxt);
        my_db = my_helper.getWritableDatabase();

        ContentValues init_vals = new ContentValues();
        init_vals.put(key_uid, user_id);
        init_vals.put(key_datetime, unix_datetime);
        init_vals.put(key_long, longitude);
        init_vals.put(key_lat, latitude);

        my_db.insert(table_name, null, init_vals);
        my_helper.close();
    }

    protected void push_data()
    {
        crs = my_db.rawQuery("SELECT " + key_datetime + " FROM " + table_name, null);
        int rowCount = crs.getCount();
        crs = my_db.rawQuery("SELECT " + key_lat + ", " +  key_long + ", " + key_uid + ", " +
            key_datetime + " FROM " + table_name, null);
        crs.moveToFirst();

        String[] parameters = new String[4];

        for (int i = 0; i < rowCount; i+=4)
        {
            parameters[i] = Double.toString(crs.getDouble(i));
            parameters[i+1] = Double.toString(crs.getDouble(i+1));
            parameters[i+2] = crs.getString(i+2);
            parameters[i+3] = Integer.toString(crs.getInt(i+3));
            AsyncTask<String, Void, String[]> push = (new PushTask().execute(parameters));
        }
    }

    protected boolean isTableEmpty()
    {
        crs = my_db.rawQuery("SELECT * FROM " + table_name, null);
        return crs.moveToFirst();
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
                    "lat REAL, " +
                    "lon REAL, " +
                    "source TEXT," +
                    "timestamp TEXT PRIMARY KEY)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //No changes to make so far...
        }
    }
}
