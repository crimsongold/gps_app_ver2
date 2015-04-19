package tcss450.gps_app_phase_i;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


public class MyAccountActivity extends ActionBarActivity
{
    private AuthTable user_base;

    private boolean chk_start = false;
    private boolean chk_end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

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
                //Some logic to check that there are dates in the start and end date entryboxes
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

    @Override
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
