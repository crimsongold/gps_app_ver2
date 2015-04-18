package tcss450.gps_app_phase_i;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RegisterActivity extends ActionBarActivity
{

    private String m_pass_prompt_string;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText mEmailView = (EditText) findViewById(R.id.email_prompt);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().contains("@"))
                {
                    return true;
                } else
                {
                    //Somehow display that the email is not valid
                    return false;
                }
            }
        });

        EditText mPassPrompt = (EditText) findViewById(R.id.pass_prompt);
        mPassPrompt.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().length() > 4)
                {
                    m_pass_prompt_string = textView.getText().toString();
                    return true;
                } else
                {
                    //Somehow display that the password is not long enough
                    return false;
                }
            }
        });

        EditText mPassConfirm = (EditText) findViewById(R.id.pass_confirm);
        mPassConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().length() > 4)
                {
                    if (m_pass_prompt_string == textView.getText())
                    {
                        return true;
                    } else
                    {
                        return false;
                        //Somehow display that the passwords do not match
                    }
                } else
                {
                    //Somehow display that the password is not long enough
                    return false;
                }
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //register();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
