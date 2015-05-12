/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {
    private SharedPreferences prefs;
    private LocalMapData location_data;
    private static final long MINIMUM_DISTANCE = 10; // in
    // Meters
    private static final long MINIMUM_TIME = 2000; // in
    // Milliseconds
    protected LocationManager locationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);
        location_data = new LocalMapData(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINIMUM_TIME, 0, new MyLocationListener());
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String uid = prefs.getString("ID", null);
        if (location != null && uid != null) {
            location.getLongitude();
            location.getLatitude();
        }
    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            String uid = prefs.getString("ID", null);
            if (uid != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                long timestamp = System.currentTimeMillis() / 1000L;
                Log.i("GPSService", "Latitude: " + latitude + " Longitude: " + longitude + " Timestamp: " +
                        timestamp);
                location_data.add_point(uid, timestamp, latitude, longitude);
                location_data.push_data();
            }
        }

        public void onStatusChanged(String s, int i, Bundle b) {
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(GPSService.this,
                    "GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(GPSService.this,
                    "GPS turned on",
                    Toast.LENGTH_LONG).show();
        }
    }
}


/**
 * Created by caleb on 5/9/15.
 */
/*public class GPSService extends IntentService {

    private LocalMapData location_data;
    private SharedPreferences prefs;

    private static final int alarmtime = 6000;  //replace with prefs time

    private static final String TAG = "GPSService";
    public GPSService(){
        super("GPSService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an Intent: " + intent);
        location_data = new LocalMapData(this);
        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);

        LocationManager locationManager = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location location) {
                String uid = prefs.getString("ID", "default");
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                long timestamp = System.currentTimeMillis() / 1000L;
                Log.i(TAG, "Latitude: " + latitude + " Longitude: " + longitude + " Timestamp: " +
                        timestamp);
                location_data.add_point(uid, timestamp, latitude, longitude);
                location_data.push_data();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider)
            {
                location_data.push_data();
            }

            public void onProviderDisabled(String provider) {}
        };
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000, 10, locationListener);
    }*/

/**
 * Used to set the service alarm
 * @param context context
 * @param active if the setting is to be enabled or disabled
 */
    /*public static void setServiceAlarm(Context context, boolean active){

        Intent i = new Intent(context, GPSService.class);

        //this is breaking
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        if(active == true) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()
                    , alarmtime, pendingIntent);
        }  else  {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}*/
