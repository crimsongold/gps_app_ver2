/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * Created by caleb on 5/9/15.
 */
public class GPSService extends IntentService {

    private LocalMapData location_data;
    private SharedPreferences prefs;

    private static final String TAG = "GPSService";
    public GPSService(){
        super("GPSService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        location_data = new LocalMapData(this);
        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);

        LocationManager locationManager = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        android.location.LocationListener locationListener = new android.location.LocationListener()
        {
            public void onLocationChanged(Location location) {
                String uid = prefs.getString("ID", "undef");
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                long timestamp = System.currentTimeMillis() / 1000L;
                Log.i(TAG, "Latitude: " + latitude + " Longitude: " + longitude + " Timestamp: " +
                        timestamp);
                location_data.add_point(uid, timestamp, latitude, longitude);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener,
                Looper.myLooper());

        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an Intent: " + intent);
    }
}
