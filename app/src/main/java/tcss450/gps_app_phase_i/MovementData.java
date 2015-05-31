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
import android.widget.ArrayAdapter;
import android.widget.ListView;

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



public class MovementData extends ActionBarActivity {
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_data);

        mainListView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(this, R.layout.list_item);
        mainListView.setAdapter(listAdapter);

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.shared_preferences_name),
                Context.MODE_PRIVATE);

        (new GetPointsTask()).execute(prefs.getString(getString(R.string.shared_preferences_user_ID), ""),
                Long.toString(prefs.getLong(getString(R.string.shared_preferences_start), 0)),
                Long.toString(prefs.getLong(getString(R.string.shared_preferences_end), 0)));

    }




    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void onBackPressed() {
        Intent intent = new Intent(MovementData.this, MyAccount.class);
        startActivity(intent);
        finish();
    }

    class GetPointsTask extends AsyncTask<String, Void, String> {

        //Push the recent data from the database into the webservice database
        protected String doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(getString(R.string.web_service_protocol))
                    .authority(getString(R.string.web_service_url))
                    .appendPath(getString(R.string.web_service_view))
                    .appendQueryParameter(getString(R.string.web_service_uid), params[0])
                    .appendQueryParameter(getString(R.string.web_service_start), params[1])
                    .appendQueryParameter(getString(R.string.web_service_end), params[2]);
            String url = builder.build().toString();
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            try {
                HttpResponse response = client.execute(post);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), getString(R.string.web_service_string_format)));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(String result) {
            //result = "{\"result\":\"success\",\"error\":\"\",\"points\":[{\"lat\":\"65.96670000\",\"lon\":\"-18.53330000\",\"speed\":\"0.000\",\"heading\":\"0.000\",\"time\":1431369860}]}";
            JSONTokener tokener = new JSONTokener(result);
            JSONObject finalResult;
            try {
                finalResult = new JSONObject(tokener);
                String regResult = finalResult.getString(getString(R.string.web_service_result));
                if (regResult.equals(getString(R.string.web_service_success))) {

                    JSONArray points = finalResult.getJSONArray(getString(R.string.web_service_points));

                    for (int i = 0; i < points.length(); i++) {
                        JSONObject point = points.getJSONObject(i);
                        String tmp = getString(R.string.movement_data_latitude) + ": " + point.getString(getString(R.string.web_service_latitude)) + "\n" +
                                getString(R.string.movement_data_longitude) + ": " + point.getString(getString(R.string.web_service_longitude)) + "\n" +
                                getString(R.string.movement_data_time) + ": " + point.getString(getString(R.string.web_service_time));

                        listAdapter.add(tmp);

                    }

                } else {
                    listAdapter.add(finalResult.getString(getString(R.string.web_service_error)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listAdapter.add(getString(R.string.web_service_error_message));
            }

        }
    }
}
