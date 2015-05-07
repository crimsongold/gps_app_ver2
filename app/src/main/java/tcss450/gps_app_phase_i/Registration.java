/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * An activity that provides a user with the opportunity to register. The user can create a
 * username (email), password, security question, and security answer. After confirming that the
 * terms of service have been read, the user information is added to the authentication database.
 */
public class Registration extends ActionBarActivity {
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
    private View mRegisterFormView;

    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_register);

        user_base = new AuthTable(this.getApplicationContext());

        mEmailView = (EditText) findViewById(R.id.email_prompt);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (textView.getText().toString().contains("@")) {
                    chk_email = true;
                    return true;
                } else {
                    //Somehow display that the email is not valid
                    chk_email = false;
                    return false;
                }
            }
        });

        mPassPrompt = (EditText) findViewById(R.id.pass_prompt);
        mPassPrompt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (textView.getText().toString().length() > 4) {
                    chk_pass_prompt = true;
                    m_pass_prompt_string = textView.getText().toString();
                    return true;
                } else {
                    //Somehow display that the password is not long enough
                    chk_pass_prompt = false;
                    return false;
                }
            }
        });

        final EditText mPassConfirm = (EditText) findViewById(R.id.pass_confirm);
        mPassConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (textView.getText().toString().length() > 4) {
                    if (m_pass_prompt_string.equals(textView.getText())) {
                        chk_pass_confirm = true;
                        return true;
                    } else {
                        chk_pass_confirm = false;
                        mPassConfirm.setError("Passwords do not match");
                        return false;
                    }
                } else {
                    chk_pass_confirm = false;
                    mPassConfirm.setError("Invalid password entered.");
                    return false;
                }
            }
        });

        mSecQuestion = (EditText) findViewById(R.id.security_question);
        mSecQuestion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (textView.getText().toString().length() > 1) {
                    chk_sec_question = true;
                    return true;
                } else {
                    chk_sec_question = false;
                    //Somehow display that there was no question entered
                    mSecQuestion.setError("No question has been entered.");
                    return false;
                }
            }
        });

        mSecAnswer = (EditText) findViewById(R.id.security_answer);
        mSecAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (textView.getText().toString().length() > 1) {
                    chk_sec_answer = true;
                    return true;
                } else {
                    chk_sec_answer = false;
                    mSecAnswer.setError("Answer is too short.");
                    return false;
                }
            }
        });

        CheckBox terms_chkBox = (CheckBox) findViewById(R.id.terms_check);
        terms_chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chk_ToS = true;
                } else {
                    chk_ToS = false;
                }
            }
        });









        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptRegister();



//
//                if (mEmailView.getText().toString().contains("@")) {
//                    chk_email = true;
//                } else {
//                    mEmailView.setError("Missing Fields...");
//                }
//
//                if (mPassPrompt.getText().toString().length() > 4) {
//                    chk_pass_prompt = true;
//                } else {
//                    mPassPrompt.setError("Length");
//                }
//
//                if (mSecQuestion.getText().toString().length() > 2) {
//                    chk_sec_question = true;
//                } else {
//                    mSecQuestion.setError("Length");
//                }
//
//                if (mSecAnswer.getText().toString().length() > 2) {
//                    chk_sec_answer = true;
//                } else {
//                    mSecAnswer.setError("Length");
//                }
//
//                /**
//                 * Doesn't work currently (some shit different for commit)
//                 */
//                if (mPassPrompt.getText().toString().equals(mPassPrompt.getText().toString())) {
//                    chk_pass_confirm = true;
//                } else {
//                    mPassPrompt.setError("Passwords do not match");
//                }
//
//                //chk_pass_confirm = true;
//                if (chk_email && chk_pass_prompt && chk_pass_confirm && chk_sec_question &&
//                        chk_sec_answer && chk_ToS) {
//                    register();
//                } else {
//                    mEmailView.setError("Missing Fields...");
//                }
            }
        });
    }


    @Override
    /**
     * {@inheritDoc}
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    /**
     * Gets the strings for all of the entry fields in the activity. Then uses the strings to
     * enter all of the information into a new row in the database. Afterward, the activity
     * is switched to the MyAccountActivity and the Register Activity is ended.
     */
    private void register() {
        String email = mEmailView.getText().toString();
        String pass = mPassPrompt.getText().toString();
        String secQuestion = mSecQuestion.getText().toString();
        String secAnswer = mSecAnswer.getText().toString();

        user_base.add_user(email, pass, secQuestion, secAnswer);

        Intent intent = new Intent(Registration.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void onBackPressed() {
        Intent intent = new Intent(Registration.this, Login.class);
        startActivity(intent);
        finish();
    }


    public void attemptRegister(){

        String email = mEmailView.getText().toString();
        String password = mPassPrompt.getText().toString();
        String question = mSecQuestion.getText().toString();
        String answer = mSecAnswer.getText().toString();

        boolean validCheck = true;

        // Reset errors.
        mEmailView.setError(null);
        mPassPrompt.setError(null);



        /**
         * Left over code from initial validation of question and answer length.
         */
        if (mSecQuestion.getText().toString().length() > 2) {
            validCheck = true;
        } else {
            mSecQuestion.setError("Length");
            validCheck = false;
        }

        if (mSecAnswer.getText().toString().length() > 2) {
            validCheck = true;
        } else {
            mSecAnswer.setError("Length");
            validCheck = false;
        }


        int valid = Verification.isEmailValid(email);
        if(valid == Verification.VALID_EMAIL){
        }else if(valid == Verification.BLANK) {
            validCheck = false;
            mEmailView.setError("Email Can't be blank");
            mEmailView.requestFocus();
        }else if(valid == Verification.INVALID_SYMBOLS){
            validCheck = false;
            mEmailView.setError("Email can't contain +-,!#$%^&*();\\/|<>\"'");
            mEmailView.requestFocus();
        }else if(valid == Verification.NO_USER){
            validCheck = false;
            mEmailView.setError("Email needs to have a user");
            mEmailView.requestFocus();
        }else if(valid == Verification.NO_AT){
            validCheck = false;
            mEmailView.setError("Email needs an '@' symbol");
            mEmailView.requestFocus();
        }else if(valid == Verification.NO_DOMAIN_NAME){
            validCheck = false;
            mEmailView.setError("Email needs a domain name");
            mEmailView.requestFocus();
        }else if(valid == Verification.NO_DOT){
            validCheck = false;
            mEmailView.setError("Email needs a '.' after the domain name");
            mEmailView.requestFocus();
        }else if(valid == Verification.NO_TOP_DOMAIN){
            validCheck = false;
            mEmailView.setError("Email needs a valid top level domain");
            mEmailView.requestFocus();
        }

        valid = Verification.isPasswordValid(password);
        if(valid == Verification.VALID_PASSWORD){

        }else if(valid == Verification.SHORT_PASSWORD){
            validCheck = false;
            mPassPrompt.setError("Password Is Too Short");
            mPassPrompt.requestFocus();
        }else if(valid == Verification.NO_UPPER){
            validCheck = false;
            mPassPrompt.setError("Password Must contain an upper case letter");
            mPassPrompt.requestFocus();
        }else if(valid == Verification.NO_LOWER){
            validCheck = false;
            mPassPrompt.setError("Password Must contain a Lower case letter");
            mPassPrompt.requestFocus();
        }else if(valid == Verification.NO_SPECIAL){
            validCheck = false;
            mPassPrompt.setError(
                    "Password Must contain one of the following: +\\-.,!@#$%^&*();\\\\/|<>\"'");
            mPassPrompt.requestFocus();
        } else {

            if(validCheck == true) {
                showProgress(true);
                AsyncTask<String, Void, String[]> var =
                        (new registerUser()).execute(new String[]{email, password, question, answer});
            }
        }






        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.










    }

    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    class registerUser extends AsyncTask<String, Void, String[]> {


        protected String[] doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();

            builder.scheme("http")
                    .authority("450.atwebpages.com")
                    .appendPath("adduser.php")
                    .appendQueryParameter("email", params[0])
                    .appendQueryParameter("password", params[1])
                    .appendQueryParameter("question", params[2])
                    .appendQueryParameter("answer", params[3]);
            String url = builder.build().toString();


            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);


            /**
             * Link example
             * 450.atwebpages.com/adduser.php?email=smith@aol.com&password=mypass& question=favorite%20color%3F&answer=blue
             */

            


            try {
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                return new String[]{params[0], reader.readLine()};
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(String[] result) {

            JSONTokener tokener = new JSONTokener(result[1]);
            JSONObject finalResult = null;
            String regResult = "";
            try {
                finalResult = new JSONObject(tokener);
                regResult = finalResult.getString("result");
            } catch (JSONException e) {
                Log.i("mAuthTask", "ERROR");
                e.printStackTrace();
            }


            if (regResult.equals("success")) {


                Context context = getApplicationContext();
                CharSequence text = "Registration complete";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                //if success, url has been posted and user has been created, send activity to login
                //may want to auto complete email field on login activity from here
                Intent i = new Intent(Registration.this, Login.class);
                startActivity(i);
                finish();


            } else {

                //temp error.  replace with json error message
                Context context = getApplicationContext();
                CharSequence text = "Generic Error message";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }


        }


    }
}
