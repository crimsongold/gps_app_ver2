/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

/**
 * Created by caleb on 5/9/15.
 */
public class GPSService extends IntentService {

    private static final int alarmtime = 60000;  //replace with prefs time

    private static final String TAG = "GPSService";
    public GPSService(){
        super("GPSService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "service starting");

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double longitude = location.getLongitude();
            System.out.println(longitude);
            double latitude = location.getLatitude();
            System.out.println(latitude);
            Log.i(TAG, "Latitude: " + latitude + " Longitude: " + longitude);
        }
//        final LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                double longitude = location.getLongitude();
//                double latitude = location.getLatitude();
//                Log.i(TAG+1, "Latitude: " + latitude + " Longitude: " + longitude);
//            }
//        };
//        long MIN_TIME = 2000; //miliseconds
//        float MIN_DISTANCE = 10;//meters
//         lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, (android.location.LocationListener) locationListener);
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
