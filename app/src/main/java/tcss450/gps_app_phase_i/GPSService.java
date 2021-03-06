/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {
    private SharedPreferences prefs;
    private LocalMapData location_data;
    private static final long MINIMUM_DISTANCE = 10; // in
    // Meters
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected BroadcastReceiver pc_receiver;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        IntentFilter pc_filter = new IntentFilter();
        pc_filter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        pc_filter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        prefs = this.getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);

        pc_receiver = new PowerConnectedReceiver();
        registerReceiver(pc_receiver, pc_filter);

        long min_time = prefs.getLong(getString(R.string.shared_preferences_interval), 0);
        float min_distance = 10; //10 meters is the minimum distance
        //Creates the actual location gathering part of the service
        location_data = new LocalMapData(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                min_time, min_distance, locationListener);
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String uid = prefs.getString(getString(R.string.shared_preferences_user_ID), null);
        if (location != null && uid != null) {
            location.getLongitude();
            location.getLatitude();
            location.getSpeed();
            location.getBearing();
            Log.i("GPSService", getString(R.string.movement_data_latitude) + ": " +
                    location.getLatitude() + " " + getString(R.string.movement_data_longitude) +
                    ": " + location.getLongitude() + " " + getString(R.string.movement_data_time) +
                    ": " + System.currentTimeMillis() + " Speed: " + location.getSpeed() +
                    " Heading: " + location.getBearing());
            location_data.add_point(uid, System.currentTimeMillis() / 1000, location.getLatitude(),
                    location.getLongitude(), location.getSpeed(), location.getBearing());
        }
    }

    /**
     * onDestroy is used by the GPSService to unregister the reciever.
     */
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(pc_receiver);
        locationManager.removeUpdates(locationListener);
    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
//            String uid = prefs.getString(getString(R.string.shared_preferences_user_ID), null);
//            if (uid != null) {
//                double longitude = location.getLongitude();
//                double latitude = location.getLatitude();
//                long timestamp = System.currentTimeMillis() / 1000L;
//                Log.i("GPSService", getString(R.string.movement_data_latitude) + ": " + latitude + " " + getString(R.string.movement_data_longitude) + ": " +
//                        longitude + " " + getString(R.string.movement_data_time) + ": " + timestamp);
//                location_data.add_point(uid, timestamp, latitude, longitude);
//            }
        }

        public void onStatusChanged(String s, int i, Bundle b)
        {
            //
        }

        /**
         * onProviderDisabled displays a toast
         * @param s string passed to the method
         */
        public void onProviderDisabled(String s) {
            Toast.makeText(GPSService.this,
                    getText(R.string.GPS_off),
                    Toast.LENGTH_LONG).show();
        }

        /**
         * onProviderEnabled displays a toast
         * @param s string passed to the method
         */
        public void onProviderEnabled(String s) {
            Toast.makeText(GPSService.this,
                    getText(R.string.GPS_on),
                    Toast.LENGTH_LONG).show();
        }
    }
}