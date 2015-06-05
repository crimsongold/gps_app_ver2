/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver
{
    private LocalMapData location_data;

    /**
     * The onReceive method is used for the alarm, when it receives a signal, it will push data
     * @param context the context
     * @param intent the intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (location_data == null)
            location_data = new LocalMapData(context);
        location_data.push_data();
    }
}
