/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Map extends Activity {

    private LocationManager locationManager;
    private Location location;
    private List<LatLng> markerList = new LinkedList<LatLng>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.shared_preferences_name),
                Context.MODE_PRIVATE);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (hasInternetAccess())
        {
            (new GetPointsTask()).execute(prefs.getString(getString(R.string.shared_preferences_user_ID), ""),
                    Long.toString(prefs.getLong(getString(R.string.shared_preferences_start), 0)),
                    Long.toString(prefs.getLong(getString(R.string.shared_preferences_end), 0)));

            createMap();
        }
    }

    private void placeMarkers() {

        LatLng temp = new LatLng(0.0, 0.0);
        LatLng temp2 = new LatLng(0.0, 0.0);
        LatLng temp3 = new LatLng(0.0, 0.0);

        int flag = 0;
        if (!markerList.isEmpty() ) {

            for (LatLng s : markerList) {
                if(flag == 0) {
                    //sets the first marker.
                    mMap.addMarker(new MarkerOptions().position(s).title("start"));
                    temp = s;
                    flag++;
                } else if (flag == 1){
                    //sets the second marker, draws a line between the first and second points
                    mMap.addMarker(new MarkerOptions().position(s).title("route"));
                    temp2 = s;
                    Polyline line = mMap.addPolyline(new PolylineOptions().add(temp, temp2).width(3).color(Color.BLUE).geodesic(true));
                    flag++;
                } else {
                    //draws a line between each new point and the previous point on the list.
                    mMap.addMarker(new MarkerOptions().position(s).title("route"));
                    temp3 = s;
                    Polyline line = mMap.addPolyline(new PolylineOptions().add(temp2, temp3).width(3).color(Color.BLUE).geodesic(true));
                    temp2 = s;
                }


            }
            /**
             * Draw directions from starting point to end point.
             */
            String url = getDirectionsUrl(temp,temp2);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

        }

    }


    private boolean hasInternetAccess()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * createMap will create a map object to be interacted with
     */
    private void createMap() {
        // check if Map has not been created yet
        if (mMap == null) {
            // attempt to make a fragment
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // if successful view the map, if not toast an error
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    //if there is a location, the map will zoom into the location, otherwise it will only view the map
                    LatLng myMarker = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myMarker, 9));
                } else {
                    //possibly zoom in on the first latlng location in the list
                }
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            } else
                Toast.makeText(getApplicationContext(), "There was an error creating the map", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This AsyncTask is used to get the list of points from the web service
     */
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
                while ((line = reader.readLine()) != null) {
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
                        String stringLat = point.getString(getString(R.string.web_service_latitude));
                        String stringLon = point.getString(getString(R.string.web_service_longitude));


                        Double lat = Double.parseDouble(stringLat);
                        Double lng = Double.parseDouble(stringLon);


                        LatLng position = new LatLng(lat, lng);
                        markerList.add(position);
                    }

                } else {
                    //listAdapter.add(finalResult.getString(getString(R.string.web_service_error)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //listAdapter.add(getString(R.string.web_service_error_message));
            }

            placeMarkers();

        }


    }












    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /**
     * Below is code taken from http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/
     */




    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }





    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

















}