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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
 * Created by Caleb on 5/29/2015.
 */
public class Agreement  extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        Button reject = (Button)findViewById(R.id.buttonReject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button accept = (Button)findViewById(R.id.buttonAccept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Agreement.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });
        AsyncTask<Void, Void, String> var = (new agreementTask()).execute();
    }
    class agreementTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            Uri.Builder builder = new Uri.Builder();

            builder.scheme(getString(R.string.web_service_protocol))
                    .authority(getString(R.string.web_service_url))
                    .appendPath(getString(R.string.web_service_agreement));
            String url = builder.build().toString();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);

           try {
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), getString(R.string.web_service_string_format)));
               StringBuilder sb = new StringBuilder();
               String line = null;
               while ((line = reader.readLine()) != null)
               {
                   sb.append(line);
               }
               return sb.toString();
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
                regResult = finalResult.getString(getString(R.string.agreement));
                TextView agr = (TextView)findViewById(R.id.textViewAgreement);
                agr.setText(Html.fromHtml(regResult));
                Button accept = (Button)findViewById(R.id.buttonAccept);
                accept.setEnabled(true);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getString(R.string.web_service_error_message), Toast.LENGTH_LONG).show();
            }
        }
    }

}
