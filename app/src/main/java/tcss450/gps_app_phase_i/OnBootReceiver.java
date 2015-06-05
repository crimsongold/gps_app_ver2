/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class is used starting after the platform has been booted
 */
public class OnBootReceiver  extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
            if (prefs.getString(context.getString(R.string.shared_preferences_user_ID),
                    null) != null)
            {
                Intent serviceLauncher = new Intent(context, GPSService.class);
                context.startService(serviceLauncher);
                Log.i("BootRec", "Service started after boot.");
            }
        }
    }
}
