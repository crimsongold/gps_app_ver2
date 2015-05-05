/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class Login extends Activity implements LoaderCallbacks<Cursor>
{

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView;

    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = this.getSharedPreferences("tcss450.gps_app_phase_i", Context.MODE_PRIVATE);
        prefs_editor = prefs.edit();

        if (prefs.contains("Email") && prefs.contains("Password"))
        {
            Intent intent = new Intent(Login.this, MyAccount.class);
            startActivity(intent);
            finish();
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                register();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);


    }

    private void populateAutoComplete()
    {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin()
    {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        //TODO: Make strings for this.
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        int valid = Verification.isEmailValid(email);
        if(valid == Verification.VALID_EMAIL){
        }else if(valid == Verification.BLANK) {
            mEmailView.setError("Email Can't be blank");
            mEmailView.requestFocus();
            return;
        }else if(valid == Verification.INVALID_SYMBOLS){
            mEmailView.setError("Email can't contain +-,!#$%^&*();\\/|<>\"'");
            mEmailView.requestFocus();
            return;
        }else if(valid == Verification.NO_USER){
            mEmailView.setError("Email needs to have a user");
            mEmailView.requestFocus();
            return;
        }else if(valid == Verification.NO_AT){
            mEmailView.setError("Email needs an '@' symbol");
            mEmailView.requestFocus();
            return;
        }else if(valid == Verification.NO_DOMAIN_NAME){
            mEmailView.setError("Email needs a domain name");
            mEmailView.requestFocus();
            return;
        }else if(valid == Verification.NO_DOT){
            mEmailView.setError("Email needs a '.' after the domain name");
            mEmailView.requestFocus();
            return;
        }else if(valid == Verification.NO_TOP_DOMAIN){
            mEmailView.setError("Email needs a valid top level domain");
            mEmailView.requestFocus();
            return;
        }

        valid = Verification.isPasswordValid(password);
        if(valid == Verification.VALID_PASSWORD){

        }else if(valid == Verification.SHORT_PASSWORD){
            mPasswordView.setError("Password Is Too Short");
            mPasswordView.requestFocus();
            return;
        }else if(valid == Verification.NO_UPPER){
            mPasswordView.setError("Password Must contain an upper case letter");
            mPasswordView.requestFocus();
            return;
        }else if(valid == Verification.NO_LOWER){
            mPasswordView.setError("Password Must contain a Lower case letter");
            mPasswordView.requestFocus();
            return;
        }else if(valid == Verification.NO_SPECIAL){
            mPasswordView.setError("Password Must contain one of the following: +\\-.,!@#$%^&*();\\\\/|<>\"'");
            mPasswordView.requestFocus();
            return;
        }
        AsyncTask<String, Void, String[]> var = (new LoginTask()).execute(new String[]{email, password});
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            //Log.i("mAuthTask", "AuthTask reached...");

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }

    /**
     * Redirects the user from the Login to the Registration.
     */
    public void register()
    {
        Intent intent = new Intent(Login.this, Registration.class);
        startActivity(intent);
        finish();
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection)
    {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(Login.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery
    {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class  LoginTask extends AsyncTask<String,Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            //TODO: Make strings for these.
            builder.scheme(getString(R.string.web_service_protocol))
                    .authority(getString(R.string.web_service_url))
                    .appendPath("login.php")
                    .appendQueryParameter("email",params[0] )
                    .appendQueryParameter("password", params[1]);
            String url = builder.build().toString();

//            String url = "http://450.atwebpages.com/login.php?email=" + params[0]+"&password=" + params[1];
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            try {
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                return new String[]{params[0],reader.readLine()};
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void login(final String email, final String ID)
        {
            prefs_editor.putString("Email", email);
            prefs_editor.commit();
            prefs_editor.putString("ID", ID);
            prefs_editor.commit();
            Intent intent = new Intent(Login.this, MyAccount.class);
            startActivity(intent);
            finish();
        }
        protected void onPostExecute(String[] result)
        {
            Log.i("mAuthTask", result[1]);
            //showProgress(false);
            //Test For successful login
            //result[1] = "{\"result\": \"success\", \"userid\": \"5567899878\"}";
            JSONTokener tokener = new JSONTokener(result[1]);
            JSONObject finalResult = null;
            String success = "";
            try {
                finalResult = new JSONObject(tokener);
                success = finalResult.getString("result");
            } catch (JSONException e) {
                Log.i("mAuthTask", "ERROR");
                e.printStackTrace();
            }
            //TODO: Make strings for this.
            if (success.equals("success"))
            {
                try {
                    login(result[0],finalResult.getString("userid"));
                } catch (JSONException e) {
                    mPasswordView.setError("Internal Error");
                    return;
                }
                Intent intent = new Intent(Login.this, MyAccount.class);
                startActivity(intent);
                finish();
            } else
            {
                try {
                    mPasswordView.setError(finalResult.getString("error"));
                } catch (JSONException e) {
                    //TODO: Make strings for this.
                    mPasswordView.setError("Incorrect Email/Password combination");
                    e.printStackTrace();
                }
                mPasswordView.requestFocus();
            }
        }
    }
}