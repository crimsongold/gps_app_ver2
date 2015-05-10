/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

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
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;


    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);
        prefs_editor = prefs.edit();
        Date start;

        EditText startBox = (EditText) findViewById(R.id.start_date_input);
        /*startBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                return false;
            }
        });*/

        EditText endBox = (EditText) findViewById(R.id.end_date_input);
        /*
        endBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                return false;
            }
        });
         */

        Button viewData = (Button) findViewById(R.id.view_data_button);
        viewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, MovementData.class);
                startActivity(intent);
            }
        });

        Button resetPass = (Button) findViewById(R.id.reset_password);
        resetPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                URL url;
                //TODO
                //this needs to have the email in it too
                //Email needs to be passed after login or stored locally

                try {
                    url = new URL("450.atwebpages.com/reset.php");


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });

        Button logOut = (Button) findViewById(R.id.logout_button);
        logOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //prefs_editor.clear();
                //prefs_editor.commit();
                //location_data.wipe_data();
                Intent intent = new Intent(MyAccount.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        /**
         * set the alarm on create of my account,
         * need to add some menu features to disable and enable alarm
         */

        Button alarmButton = (Button) findViewById(R.id.alarm_button);
        alarmButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {


                GPSService.setServiceAlarm(v.getContext(), true);
                ComponentName receiver = new ComponentName(MyAccount.this, GPSService.class);
                PackageManager pm = MyAccount.this.getPackageManager();

                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);

//                GPSService.setServiceAlarm(MyAccount.this, true);
//
//                ComponentName receiver = new ComponentName(MyAccount.this, GPSReceiver.class);
//                PackageManager pm = MyAccount.this.getPackageManager();
//
//                pm.setComponentEnabledSetting(receiver,
//                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                        PackageManager.DONT_KILL_APP);


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
