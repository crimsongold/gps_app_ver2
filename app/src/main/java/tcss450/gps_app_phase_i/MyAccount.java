/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


/**
 * This is the main screen for the application. It allows the user to enter in a start and end
 * date for the tracking data to be displayed, and then view it in another activity. Additionally,
 * it allows the user to reset the password
 */
public class MyAccount extends ActionBarActivity {
    private LocalMapData location_data;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;


    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        location_data = new LocalMapData(this);
        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);
        prefs_editor = prefs.edit();
        startAlarm();

        //Datepicker will not render in Android_Studio

        EditText enter_start_date = (EditText) findViewById(R.id.enter_start_date);
        /*startBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                return false;
            }
        });*/

        EditText enter_start_time = (EditText) findViewById(R.id.enter_start_time);
        /*startBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                return false;
            }
        });*/

        EditText enter_end_date = (EditText) findViewById(R.id.enter_end_date);
        /*startBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                return false;
            }
        });*/

        EditText enter_end_time = (EditText) findViewById(R.id.enter_end_time);
        /*startBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                return false;
            }
        });*/

        Button viewData = (Button) findViewById(R.id.view_data_button);
        viewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                location_data.push_data();
                Intent intent = new Intent(MyAccount.this, MovementData.class);
                startActivity(intent);
            }
        });

        Button logOut = (Button) findViewById(R.id.logout_button);
        logOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs_editor.clear();
                prefs_editor.commit();
                stopAlarm();
                location_data.wipe_data();
                Intent intent = new Intent(MyAccount.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        findViewById(R.id.action_settings);
        return true;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MyAccount.this, Settings.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startAlarm()
    {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), GPSReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, GPSReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                2000, pIntent);
    }

    public void stopAlarm()
    {
        Intent intent = new Intent(getApplicationContext(), GPSReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, GPSReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}
