package tcss450.gps_app_phase_i;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tcss450.gps_app_phase_i.GPSService;

public class GPSReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, GPSService.class);
        context.startService(i);
    }
}