/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by Jake on 5/31/2015.
 */
public class PowerConnectedReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED))
        {
            Toast.makeText(context, "Power source connected", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor prefs_editor = (context.getSharedPreferences(
                    context.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE)).edit();
            //change the rate in shared preferences that the isjdf;lasjdf updates


            prefs_editor.commit();
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
        {
            Toast.makeText(context, "Power source disconnected", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor prefs_editor = (context.getSharedPreferences(
                    context.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE)).edit();
            //change the rate in shared preferences that the isjdf;lasjdf updates


            prefs_editor.commit();
        }
    }
}
