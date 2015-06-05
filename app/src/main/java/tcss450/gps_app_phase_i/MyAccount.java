/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

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
    private OnBootReceiver boot_rec;
    private AlarmManager alarm;
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
        prefs = this.getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        prefs_editor = prefs.edit();

        alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alarm_intent = new Intent(MyAccount.this, AlarmReceiver.class);

        IntentFilter br_filter = new IntentFilter();
        br_filter.addAction("android.intent.action.BOOT_COMPLETED");
        boot_rec = new OnBootReceiver();
        registerReceiver(boot_rec, br_filter);

        startService(new Intent(this, GPSService.class));

        Button startDate = (Button) findViewById(R.id.start_button);
        startDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                dateFlag = getString(R.string.web_service_start);

            }

        });


        Button endDate = (Button) findViewById(R.id.end_button);
        endDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
                dateFlag = getString(R.string.web_service_end);

            }
        });


        Button viewData = (Button) findViewById(R.id.view_data_button);
        viewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //set shared preferences date
                Log.i(getString(R.string.my_account_view_data),
                        getString(R.string.my_account_view_msg));
                prefs.edit().remove(getString(R.string.shared_preferences_start)).commit();
                prefs.edit().remove(getString(R.string.shared_preferences_end)).commit();
                if (start == null || end == null) {
                    if (start == null && end == null) {
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(new Date());
                        end = cal.getTime();

                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        start = cal.getTime();


                        Context context = getApplicationContext();
                        CharSequence text = getString(R.string.date_picker_last_seven);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, Map.class);
                        startActivity(intent);

                        prefs_editor.putLong(getString(R.string.shared_preferences_start), start.getTime() / 1000);
                        prefs_editor.putLong(getString(R.string.shared_preferences_end), end.getTime() / 1000);
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
                        CharSequence text = getString(R.string.date_picker_day_before);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, Map.class);
                        startActivity(intent);

                        prefs_editor.putLong(getString(R.string.shared_preferences_start), start.getTime() / 1000);
                        prefs_editor.putLong(getString(R.string.shared_preferences_end), end.getTime() / 1000);
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
                        CharSequence text = getString(R.string.date_picker_day_after);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(MyAccount.this, Map.class);
                        startActivity(intent);

                        prefs_editor.putLong(getString(R.string.shared_preferences_start), start.getTime() / 1000);
                        prefs_editor.putLong(getString(R.string.shared_preferences_end), end.getTime() / 1000);
                        prefs_editor.commit();
                        start = null;
                        end = null;
                    }
                } else {

                    prefs_editor.putLong(getString(R.string.shared_preferences_start), start.getTime() / 1000);
                    prefs_editor.putLong(getString(R.string.shared_preferences_end), end.getTime() / 1000);
                    prefs_editor.commit();
                    start = null;
                    end = null;
                    Intent intent = new Intent(MyAccount.this, Map.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(boot_rec);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This class is used for the datepicker
     */
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

            if (dateFlag.equals(getString(R.string.web_service_start))) {
                start = cal.getTime();

            } else if (dateFlag.equals(getString(R.string.web_service_end))) {

                end = cal.getTime();

            }

        }
    }

    /**
     * This will show the date picker in the activity for the start date
     * @param v the view the date picker will be contained in
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), getString(R.string.date_picker_start));

    }

    /**
     * This will show the date picker in the activity for the end date
     * @param v the view the date picker will be contained in
     */
    public void showDatePickerDialogEnd(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), getString(R.string.date_picker_end));

    }



}
