/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

/**
 * Created by caleb on 5/9/15.
 */
public class GPSService extends IntentService {
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
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.i(TAG+1, "Latitude: " + latitude + " Longitude: " + longitude);
            }
        };
        long MIN_TIME = 2000; //miliseconds
        float MIN_DISTANCE = 10;//meters
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, (android.location.LocationListener) locationListener);
    }

}
