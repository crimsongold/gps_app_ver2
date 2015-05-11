/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;


public class MovementData extends ActionBarActivity
{
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_data);

        mainListView = (ListView) findViewById( R.id.listView );
        listAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
        mainListView.setAdapter(listAdapter);

        SharedPreferences prefs = this.getSharedPreferences("tcss450.gps_app_phase_i",
                Context.MODE_PRIVATE);

//        (new GetPointsTask()).execute(prefs.getString("ID", ""),
//                Long.toString(prefs.getLong("startTime", 0)),
//                Long.toString(prefs.getLong("endTime", 0)));
        
    }


    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movement_data, menu);
        return true;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void onBackPressed()
    {
        Intent intent = new Intent(MovementData.this, MyAccount.class);
        startActivity(intent);
        finish();
    }
    class GetPointsTask extends AsyncTask<String, Void, String>
    {

        //Push the recent data from the database into the webservice database
        protected String doInBackground(String... params)
        {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(getString(R.string.web_service_protocol))
                    .authority(getString(R.string.web_service_url))
                    .appendPath("view.php")
                    .appendQueryParameter("uid", params[0])
                    .appendQueryParameter("start", params[1])
                    .appendQueryParameter("end", params[2]);
            String url = builder.build().toString();

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            try {
                HttpResponse response = client.execute(post);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                return reader.readLine();
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(String result)
        {
            //result = "{ \"result\": \"success\", \"error\": \"\", \"points\": [ { \"lat\": \"51.000\", \"lon\": \"51.000\", \"speed\": \" 70.000\", \"heading\": \"10.000\", \"time\": 1430118118 }, { \"lat\": \"51.000\", \"lon\": \"51.000\", \"speed\": \"70.000\", \"heading\": \"10.000\", \"time\": 1430118118 } ] }";
            JSONTokener tokener = new JSONTokener(result);
            JSONObject finalResult = null;

            try {
                finalResult = new JSONObject(tokener);
                String regResult = finalResult.getString("result");
                if (regResult.equals("success")){
                    JSONArray points = finalResult.getJSONArray("points");

                    String r = "";
                    for (int i = 0; i < points.length(); i++) {
                        JSONObject point = points.getJSONObject(i);
                        String tmp = "Latitude: " + point.getString("lat") + "\n" +
                                "Longitude: " + point.getString("lon") + "\n"+
                                "TimeStamp: " + point.getString("time");

                        listAdapter.add(tmp);

                    }

                }else{
                    listAdapter.add(finalResult.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listAdapter.add("Server Error Please Try Again Later");
            }

        }
    }
}
