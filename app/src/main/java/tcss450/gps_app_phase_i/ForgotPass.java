/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
 * Created by Jon on 5/10/2015.
 */
public class ForgotPass extends Activity {

    private EditText forgotEmailText;
    private String email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotEmailText = (EditText) this.findViewById(R.id.forgot_email_text);

        Button submit = (Button) findViewById(R.id.send_pw);
        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Button cancel = (Button) findViewById(R.id.forgot_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPass.this, Login.class);
                startActivity(intent);
            }
        });
    }

    class forgotPass extends AsyncTask<String, Void, String> {
        /**
         * used to send the url for a forgotten password
         *
         * @param params
         * @return
         */
        protected String doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(getString(R.string.web_service_protocol))
                    .authority(getString(R.string.web_service_url))
                    .appendPath(getString(R.string.web_service_reset))
                            .appendQueryParameter(getString(R.string.web_service_parameter_email), params[0]);
            String url = builder.build().toString();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);

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
                    CharSequence text = getString(R.string.web_service_success_message);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    //if success, url has been posted and user has been created, send activity to login
                    //may want to auto complete email field on login activity from here
                    Intent i = new Intent(ForgotPass.this, Login.class);
                    startActivity(i);
                    finish();
                } else {
                    String err = finalResult.getString(getString(R.string.web_service_error));
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, err, duration);
                    toast.show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.web_service_error_message), Toast.LENGTH_LONG).show();
            }
        }
    }


    public void sendEmail() {
        forgotEmailText.setError(null);
        email = forgotEmailText.getText().toString();
        boolean flag = true;

        int valid = Verification.isEmailValid(email);
        if (valid == Verification.VALID_EMAIL) {
        } else if (valid == Verification.BLANK) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_blank));
            forgotEmailText.requestFocus();
        } else if (valid == Verification.INVALID_SYMBOLS) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_invalid_symbols));
            forgotEmailText.requestFocus();
        } else if (valid == Verification.NO_USER) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_no_user));
            forgotEmailText.requestFocus();
        } else if (valid == Verification.NO_AT) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_no_at));
            forgotEmailText.requestFocus();
        } else if (valid == Verification.NO_DOMAIN_NAME) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_no_domain_name));
            forgotEmailText.requestFocus();
        } else if (valid == Verification.NO_DOT) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_no_dot));
            forgotEmailText.requestFocus();
        } else if (valid == Verification.NO_TOP_DOMAIN) {
            flag = false;
            forgotEmailText.setError(getString(R.string.validate_email_no_top_domain));
            forgotEmailText.requestFocus();
        }

        if (flag) {
            (new forgotPass()).execute(email);
        }
    }
}
