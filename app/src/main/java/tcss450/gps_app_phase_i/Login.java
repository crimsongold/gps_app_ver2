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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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
public class Login extends Activity implements LoaderCallbacks<Cursor> {

    private SharedPreferences.Editor prefs_editor;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    /**
     * {@inheritDoc}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        prefs_editor = prefs.edit();

        if (prefs.contains(getString(R.string.shared_preferences_user_email)) && prefs.contains(getString(R.string.shared_preferences_user_ID))) {
            Intent intent = new Intent(Login.this, MyAccount.class);
            startActivity(intent);
            finish();
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Agree();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        Button forgotPass = (Button) findViewById(R.id.forgot_pass_button);
        forgotPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPass.class);
                startActivity(intent);
            }
        });
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        boolean flag = true;

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        int valid = Verification.isEmailValid(email);
        if (valid == Verification.VALID_EMAIL) {
        } else if (valid == Verification.BLANK) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_blank));
            mEmailView.requestFocus();
        } else if (valid == Verification.INVALID_SYMBOLS) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_invalid_symbols));
            mEmailView.requestFocus();
        } else if (valid == Verification.NO_USER) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_no_user));
            mEmailView.requestFocus();
        } else if (valid == Verification.NO_AT) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_no_at));
            mEmailView.requestFocus();
        } else if (valid == Verification.NO_DOMAIN_NAME) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_no_domain_name));
            mEmailView.requestFocus();
        } else if (valid == Verification.NO_DOT) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_no_dot));
            mEmailView.requestFocus();
        } else if (valid == Verification.NO_TOP_DOMAIN) {
            flag = false;
            mEmailView.setError(getString(R.string.validate_email_no_top_domain));
            mEmailView.requestFocus();
        }

        valid = Verification.isPasswordValid(password);
        if (valid == Verification.VALID_PASSWORD) {

        } else if (valid == Verification.SHORT_PASSWORD) {
            flag = false;
            mPasswordView.setError(getString(R.string.validate_password_short));
            mPasswordView.requestFocus();
        } else if (valid == Verification.NO_UPPER) {
            flag = false;
            mPasswordView.setError(getString(R.string.validate_password_upper_case));
            mPasswordView.requestFocus();
        } else if (valid == Verification.NO_LOWER) {
            flag = false;
            mPasswordView.setError(getString(R.string.validate_password_lower_case));
            mPasswordView.requestFocus();
        } else if (valid == Verification.NO_SPECIAL) {
            flag = false;
            mPasswordView.setError(getString(R.string.validate_password_symbol));
            mPasswordView.requestFocus();
        }

        if (flag == true) {
            AsyncTask<String, Void, String[]> var =
                    (new LoginTask()).execute(email, password);
        }
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Redirects the user from the Login to the Agreement.
     */
    private void Agree() {
        Intent intent = new Intent(Login.this, Agreement.class);
        startActivity(intent);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(Login.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
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
    class LoginTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(getString(R.string.web_service_protocol))
                    .authority(getString(R.string.web_service_url))
                    .appendPath(getString(R.string.web_service_login))
                    .appendQueryParameter(getString(R.string.web_service_parameter_email), params[0])
                    .appendQueryParameter(getString(R.string.web_service_parameter_password), params[1]);
            String url = builder.build().toString();
            Log.i("meow", url);

//String url = "http://450.atwebpages.com/login.php?email=" + params[0]+"&password=" + params[1];
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            try {
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent(), getString(R.string.web_service_string_format)));
                return new String[]{params[0], reader.readLine()};
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void login(final String email, final String ID) {
            prefs_editor.putString(getString(R.string.shared_preferences_user_email), email);
            prefs_editor.commit();
            prefs_editor.putString(getString(R.string.shared_preferences_user_ID), ID); //uid for logging stored as ID
            prefs_editor.commit();
            //Adds the interval to the shared preferences for updating in settings
            prefs_editor.putLong(getString(R.string.shared_preferences_interval),  60000);
            prefs_editor.commit();
            prefs_editor.putLong(getString(R.string.shared_preferences_push_interval), 6000000);
            Intent intent = new Intent(Login.this, MyAccount.class);
            startActivity(intent);
            finish();
        }

        protected void onPostExecute(String[] result) {
            Log.i("caleb", result[1]);
            JSONTokener tokener = new JSONTokener(result[1]);
            JSONObject finalResult = null;
            String success = "";
            try {
                finalResult = new JSONObject(tokener);
                success = finalResult.getString(getString(R.string.web_service_result));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success.equals(getString(R.string.web_service_success))) {
                try {
                    login(result[0], finalResult.getString(getString(R.string.web_service_userid)));
                } catch (JSONException e) {
                    mPasswordView.setError(getString(R.string.web_service_error_message));
                    return;
                }
                Intent intent = new Intent(Login.this, MyAccount.class);
                showProgress(false);
                startActivity(intent);
                finish();
            } else {
                try {
                    mPasswordView.setError(finalResult.getString(getString(R.string.web_service_error)));
                } catch (JSONException e) {
                    mPasswordView.setError(getString(R.string.validate_bad_email_password));
                    e.printStackTrace();
                }
                mPasswordView.requestFocus();
            }
            showProgress(false);
        }
    }
}