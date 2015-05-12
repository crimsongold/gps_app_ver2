/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * This is the main screen for the application. It allows the user to enter in a start and end
 * date for the tracking data to be displayed, and then view it in another activity. Additionally,
 * it allows the user to reset the password
 */
public class MyAccount extends ActionBarActivity {

    private LocalMapData location_data;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;
    //static Date tempDate;
    static Date start;
    static Date end;
    static String dateFlag;


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

        startService(new Intent(this, GPSService.class));

        /*GPSService.setServiceAlarm(this, true);
        ComponentName receiver = new ComponentName(MyAccount.this, GPSService.class);
        PackageManager pm = MyAccount.this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/


        Button startDate = (Button) findViewById(R.id.start_button);
        startDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                dateFlag = "start";

            }

        });


        Button endDate = (Button) findViewById(R.id.end_button);
        endDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                dateFlag = "end";

            }
        });


        Button viewData = (Button) findViewById(R.id.view_data_button);
        viewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //set shared preferences date

                prefs.edit().remove("startTime").commit();
                prefs.edit().remove("endTime").commit();

                if (start == null || end == null) {

                    if (start == null && end == null) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(new Date());
                        end = cal.getTime();

                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        start = cal.getTime();


                        Context context = getApplicationContext();
                        CharSequence text = "Last 7 days shown";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, MovementData.class);
                        startActivity(intent);

                        prefs_editor.putLong("startTime", start.getTime() / 1000);
                        prefs_editor.putLong("endTime", end.getTime() / 1000);
                        prefs_editor.commit();

                        start = null;
                        end = null;


                    }

                    if (start == null && end != null) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(end);
                        cal.add(Calendar.DAY_OF_YEAR, -1);
                        start = cal.getTime();

                        Context context = getApplicationContext();
                        CharSequence text = "Showing 1 day before end date";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, MovementData.class);
                        startActivity(intent);

                        prefs_editor.putLong("startTime", start.getTime() / 1000);
                        prefs_editor.putLong("endTime", end.getTime() / 1000);
                        prefs_editor.commit();
                        start = null;
                        end = null;


                    }


                    if (end == null && start != null) {

                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(start);


                        cal.add(Calendar.DAY_OF_YEAR, 1);
                        end = cal.getTime();

                        Context context = getApplicationContext();
                        CharSequence text = "Showing 1 day past start date";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, MovementData.class);
                        startActivity(intent);

                        prefs_editor.putLong("startTime", start.getTime() / 1000);
                        prefs_editor.putLong("endTime", end.getTime() / 1000);
                        prefs_editor.commit();
                        start = null;
                        end = null;
                    }


                } else {

                    prefs_editor.putLong("startTime", start.getTime() / 1000);
                    prefs_editor.putLong("endTime", end.getTime() / 1000);
                    prefs_editor.commit();
                    start = null;
                    end = null;
                    Intent intent = new Intent(MyAccount.this, MovementData.class);
                    startActivity(intent);
                }

            }
        });

        Button logOut = (Button) findViewById(R.id.logout_button);
        logOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs_editor.clear();
                prefs_editor.commit();
                stopService(new Intent(MyAccount.this, GPSService.class));

                location_data.push_data();
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


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = new GregorianCalendar();
            cal.set(year, month, day);

            if (dateFlag.equals("start")) {
                start = cal.getTime();

            } else if (dateFlag.equals("end")) {

                end = cal.getTime();

            }

        }
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "Start Date");

    }


    public void showDatePickerDialogEnd(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "End Date");

    }


}
