/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons,
 * and Caleb Jaeger. The collective content within was created by them and them alone to fulfill
 * the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
        mEmailView = (EditText) findViewById(R.id.email_prompt);
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = mEmailView.getText().toString();
                    if (text.contains("@") && text.contains(".")) {
                        chk_email = true;
                        return;
                    } else {
                        mEmailView.setError(getString(R.string.validate_email_not_valid));
                        chk_email = false;
                        return;
                    }
                }}});

                mPassPrompt = (EditText) findViewById(R.id.pass_prompt);
                mPassPrompt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                        if (mPassPrompt.getText().toString().length() > 4) {
                            chk_pass_prompt = true;
                            m_pass_prompt_string = mPassPrompt.getText().toString();
                            return;
                        } else {
                            mPassPrompt.setError(getString(R.string.validate_password_short));
                            chk_pass_prompt = false;
                            return;
                        }
                    }
                }});

                final EditText mPassConfirm = (EditText) findViewById(R.id.pass_confirm);
                mPassConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                        if (mPassConfirm.getText().toString().length() > 4) {
                            if (m_pass_prompt_string.equals(mPassConfirm.getText())) {
                                chk_pass_confirm = true;
                                return;
                            } else {
                                chk_pass_confirm = false;
                                mPassConfirm.setError(getString(R.string.validate_password_no_match));
                                return;
                            }
                        } else {
                            chk_pass_confirm = false;
                            mPassConfirm.setError(getString(R.string.validate_password_not_valid));
                            return;
                        }
                    }
                }});

                mSecQuestion = (EditText) findViewById(R.id.security_question);
                mSecQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                        if (mSecQuestion.getText().toString().length() > 1) {
                            chk_sec_question = true;
                            return;
                        } else {
                            chk_sec_question = false;
                            //Somehow display that there was no question entered
                            mSecQuestion.setError(getString(R.string.validate_question));
                            return;
                        }
                    }
                }});

                mSecAnswer = (EditText) findViewById(R.id.security_answer);
                mSecAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                        if (mSecAnswer.getText().toString().length() > 1) {
                            chk_sec_answer = true;
                            return;
                        } else {
                            chk_sec_answer = false;
                            mSecAnswer.setError(getString(R.string.validate_answer));
                            return;
                        }
                    }
                }});

                CheckBox terms_chkBox = (CheckBox) findViewById(R.id.terms_check);
                terms_chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        chk_ToS = isChecked;
                    }
                });


                Button registerButton = (Button) findViewById(R.id.register_button);
                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        attemptRegister();
                    }
                });
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


            public void attemptRegister() {

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
                    mSecQuestion.setError(getString(R.string.validate_question));
                    validCheck = false;
                }

                if (mSecAnswer.getText().toString().length() > 2) {
                    validCheck = true;
                } else {
                    mSecAnswer.setError(getString(R.string.validate_answer));
                    validCheck = false;
                }


                int valid = Verification.isEmailValid(email);
                if (valid == Verification.VALID_EMAIL) {
                    valid = Verification.isPasswordValid(password);
                    if (valid == Verification.VALID_PASSWORD) {
                        (new registerUser()).execute(email, password, question, answer);
                    } else if (valid == Verification.SHORT_PASSWORD) {
                        validCheck = false;
                        mPassPrompt.setError(getString(R.string.validate_password_short));
                        mPassPrompt.requestFocus();
                    } else if (valid == Verification.NO_UPPER) {
                        validCheck = false;
                        mPassPrompt.setError(getString(R.string.validate_password_upper_case));
                        mPassPrompt.requestFocus();
                    } else if (valid == Verification.NO_LOWER) {
                        validCheck = false;
                        mPassPrompt.setError(getString(R.string.validate_password_lower_case));
                        mPassPrompt.requestFocus();
                    } else if (valid == Verification.NO_SPECIAL) {
                        validCheck = false;
                        mPassPrompt.setError(
                                getString(R.string.validate_password_symbol));
                        mPassPrompt.requestFocus();
                    }
                } else if (valid == Verification.BLANK) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_blank));
                    mEmailView.requestFocus();
                } else if (valid == Verification.INVALID_SYMBOLS) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_invalid_symbols));
                    mEmailView.requestFocus();
                } else if (valid == Verification.NO_USER) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_no_user));
                    mEmailView.requestFocus();
                } else if (valid == Verification.NO_AT) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_no_at));
                    mEmailView.requestFocus();
                } else if (valid == Verification.NO_DOMAIN_NAME) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_no_domain_name));
                    mEmailView.requestFocus();
                } else if (valid == Verification.NO_DOT) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_no_dot));
                    mEmailView.requestFocus();
                } else if (valid == Verification.NO_TOP_DOMAIN) {
                    validCheck = false;
                    mEmailView.setError(getString(R.string.validate_email_no_top_domain));
                    mEmailView.requestFocus();
                }
            }

            class registerUser extends AsyncTask<String, Void, String> {

                protected String doInBackground(String... params) {
                    Uri.Builder builder = new Uri.Builder();

                    builder.scheme(getString(R.string.web_service_protocol))
                            .authority(getString(R.string.web_service_url))
                            .appendPath(getString(R.string.web_service_add_user))
                            .appendQueryParameter(getString(R.string.web_service_parameter_email), params[0])
                            .appendQueryParameter(getString(R.string.web_service_parameter_password), params[1])
                            .appendQueryParameter(getString(R.string.web_service_parameter_question), params[2])
                            .appendQueryParameter(getString(R.string.web_service_parameter_answer), params[3]);
                    String url = builder.build().toString();
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);

                    /**
                     * Link example
                     * 450.atwebpages.com/adduser.php?email=smith@aol.com&password=mypass&question=favorite%20color%3F&answer=blue
                     */

                    try {
                        HttpResponse response = client.execute(get);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                response.getEntity().getContent(), getString(R.string.web_service_string_format)));
                        return reader.readLine();
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    } catch (ClientProtocolException e) {
                        return null;
                    } catch (IOException e) {
                        return null;
                    }
                }

                protected void onPostExecute(String result) {

                    JSONTokener tokener = new JSONTokener(result);
                    JSONObject finalResult = null;
                    String regResult = "";

                    try {
                        finalResult = new JSONObject(tokener);
                        regResult = finalResult.getString(getString(R.string.web_service_result));
                        if (regResult.equals(getString(R.string.web_service_success))) {
                            Context context = getApplicationContext();
                            CharSequence text = getString(R.string.web_service_success);
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            //if success, url has been posted and user has been created, send activity to login
                            //may want to auto complete email field on login activity from here
                            Intent i = new Intent(Registration.this, Login.class);
                            startActivity(i);
                            finish();
                        } else {
                            String err = finalResult.getString(getString(R.string.web_service_error));
                            //temp error.  replace with json error message
                            Context context = getApplicationContext();
                            CharSequence text = getString(R.string.web_service_error_message);
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, err, duration);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.web_service_error_message), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
