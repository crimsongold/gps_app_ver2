package tcss450.gps_app_phase_i;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


public class RegisterActivity extends ActionBarActivity
{
    private EditText mEmailView;
    private EditText mPassPrompt;
    private EditText mSecQuestion;
    private EditText mSecAnswer;

    private AuthTable user_base;

    private String m_pass_prompt_string;
    private boolean chk_email = false;
    private boolean chk_pass_prompt = false;
    private boolean chk_pass_confirm = false;
    private boolean chk_sec_question = false;
    private boolean chk_sec_answer = false;
    private boolean chk_ToS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_base = new AuthTable(this);

        mEmailView = (EditText) findViewById(R.id.email_prompt);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().contains("@"))
                {
                    chk_email = true;
                    return true;
                } else
                {
                    //Somehow display that the email is not valid
                    return false;
                }
            }
        });

        mPassPrompt = (EditText) findViewById(R.id.pass_prompt);
        mPassPrompt.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().length() > 4)
                {
                    chk_pass_prompt = true;
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
                        chk_pass_confirm = true;
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

        mSecQuestion = (EditText) findViewById(R.id.security_question);
        mSecQuestion.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().length() > 1)
                {
                    chk_sec_question = true;
                    return true;
                } else
                {
                    //Somehow display that there was no question entered
                    return false;
                }
            }
        });

        mSecAnswer = (EditText) findViewById(R.id.security_answer);
        mSecAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (textView.getText().toString().length() > 1)
                {
                    chk_sec_answer = true;
                    return true;
                } else
                {
                    //Somehow display that the answer is not long enough
                    return false;
                }
            }
        });

        CheckBox terms_chkBox = (CheckBox) findViewById(R.id.terms_check);
        terms_chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if(isChecked)
                {
                    chk_ToS = true;
                } else
                {
                    chk_ToS = false;
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
                if (chk_email && chk_pass_prompt && chk_pass_confirm && chk_sec_question &&
                        chk_sec_answer && chk_ToS)
                {
                    register();
                }
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

    private void register()
    {
        String email = mEmailView.getText().toString();
        String pass = mPassPrompt.getText().toString();
        String secQuestion = mSecQuestion.getText().toString();
        String secAnswer = mSecAnswer.getText().toString();

        user_base.add_user(email, pass, secQuestion, secAnswer);

        Intent intent = new Intent(RegisterActivity.this, MyAccountActivity.class);
        startActivity(intent);
        finish();
    }
}
