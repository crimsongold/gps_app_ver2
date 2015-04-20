/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;


/**
 * This is the main screen for the application. It allows the user to enter in a start and end
 * date for the tracking data to be displayed, and then view it in another activity. Additionally,
 * it allows the user to reset the password
 */
public class MyAccount extends ActionBarActivity
{
    private AuthTable user_base;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;


    private boolean chk_start = false;
    private boolean chk_end = false;

    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        user_base = new AuthTable(this.getApplicationContext());
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
        viewData.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MyAccount.this, MovementData.class);
                startActivity(intent);
            }
        });

        Button resetPass = (Button) findViewById(R.id.reset_password);
        /*resetPass.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //do nothing for now
            }
        });*/

        Button logOut = (Button) findViewById(R.id.logout_button);
        logOut.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prefs_editor.clear();
                prefs_editor.commit();
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}