/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by caleb on 5/10/15.
 */
public class Settings extends ActionBarActivity {
    private LocalMapData location_data;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        location_data = new LocalMapData(this);
        prefs = this.getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        prefs_editor = prefs.edit();
        TextView head = (TextView) findViewById(R.id.textViewSettings);
        head.setText(prefs.getString(getString(R.string.shared_preferences_user_email),getString(R.string.web_service_error)));
        Switch location = (Switch) findViewById(R.id.switchLocation);
        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Location is enabled
                } else {
                    // Location is disabled
                }
            }
        });
        Switch wifi = (Switch) findViewById(R.id.switchData);
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // WIFI only
                } else {
                    // data as well as wifi
                }
            }
        });
        Switch power = (Switch) findViewById(R.id.switchPower);
        power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // save power
                } else {
                    // go crazy
                }
            }
        });
        Button logout = (Button) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs_editor.clear();
                prefs_editor.commit();
                stopService(new Intent(Settings.this, GPSService.class));
                location_data.push_data();
                location_data.wipe_data();
                Intent intent = new Intent(Settings.this, Login.class);
                startActivity(intent);
            }
        });
        SeekBar mSeekbar = (SeekBar) findViewById(R.id.seekBarSeconds);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView seconds = (TextView) findViewById(R.id.textViewSeconds);
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seconds.setText(Integer.toString(progress + 10));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
