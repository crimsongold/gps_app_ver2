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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        startAlarm();

        //Date start;
        //Date end;

        Button startDate = (Button) findViewById(R.id.start_button);
        startDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                dateFlag = "start";
                //start = new Date(tempDate.getDate());
                //start = tempDate;
            }

        });


        Button endDate = (Button) findViewById(R.id.end_button);
        endDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                dateFlag = "end";
                //end = new Date(tempDate.getDate());
                //end = tempDate;
            }
        });






        Button viewData = (Button) findViewById(R.id.view_data_button);
        viewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //set shared preferences date

                if(start == null || end == null){

                    if(start == null && end == null) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        start = cal.getTime();

                        end = new Date(Calendar.DATE);

                        Context context = getApplicationContext();
                        CharSequence text = "Last 7 days shown";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, MovementData.class);
                        startActivity(intent);

                        prefs_editor.putLong("startTime", start.getTime());
                        prefs_editor.putLong("endTime", end.getTime());
                        prefs_editor.commit();


                    }

                    if(start == null && end != null) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(end);
                        cal.set(Calendar.DATE, -1);
                        start = cal.getTime();

                        Context context = getApplicationContext();
                        CharSequence text = "Showing 1 day before end date";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, MovementData.class);
                        startActivity(intent);

                        prefs_editor.putLong("startTime", start.getTime());
                        prefs_editor.putLong("endTime", end.getTime());
                        prefs_editor.commit();


                        //cal.add(end.getDay(), -1);

                        //start = new Date(end.getDate());

//                        Calendar cal = GregorianCalendar.getInstance();
//                        cal.setTime(new Date());
//                        cal.add(Calendar.DAY_OF_YEAR, -1);
//                        start = cal.getTime();
                    }



                    if(end == null && start != null) {



                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(start);
                        cal.set(Calendar.DATE, 1);
                        end = cal.getTime();

                        Context context = getApplicationContext();
                        CharSequence text = "Showing 1 day past start date";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, MovementData.class);
                        startActivity(intent);

                        prefs_editor.putLong("startTime", start.getTime());
                        prefs_editor.putLong("endTime", end.getTime());
                        prefs_editor.commit();
                    }



//                    Context context = getApplicationContext();
//                    CharSequence text = "Last 7 days shown";
//                    int duration = Toast.LENGTH_SHORT;
//
//                    Toast toast = Toast.makeText(context, text, duration);
//                    toast.show();
//
//                    Intent intent = new Intent(MyAccount.this, MovementData.class);
//                    startActivity(intent);


                } else {

                    prefs_editor.putLong("startTime", start.getTime());
                    prefs_editor.putLong("endTime", end.getTime());
                    prefs_editor.commit();
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
            if(dateFlag.equals("start"))
            {
                start = new Date(year, month, day);
            } else if (dateFlag.equals("end")){
                end = new Date(year, month, day);
            }
            // Do something with the date chosen by the user
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
