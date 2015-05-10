/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

/**
 * Created by caleb on 5/9/15.
 */
public class GPSService extends IntentService {

    private LocalMapData location_data;
    private SharedPreferences prefs;

    private static final int alarmtime = 6000;  //replace with prefs time

    private static final String TAG = "GPSService";
    public GPSService(){
        super("GPSService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);
        location_data = new LocalMapData(this);

        Log.i(TAG, "service starting");
        LocationManager locationManager = (LocationManager) this.getSystemService(
                Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                long timestamp = System.currentTimeMillis() / 1000L;
                Log.i(TAG, "Latitude: " + latitude + " Longitude: " + longitude + " Timestamp: " +
                        timestamp);
                //location_data.add_point(prefs.getString("ID", "DEFAULT"), timestamp, latitude,
                //        longitude);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                60000, 10, locationListener);

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an Intent: " + intent);
    }

    /**
     * Used to set the service alarm
     * @param context context
     * @param active if the setting is to be enabled or disabled
     */
    public static void setServiceAlarm(Context context, boolean active){

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
}
