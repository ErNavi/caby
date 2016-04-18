package com.bestfare.pack;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bestfare.pack.connection.AppConfig;
import com.bestfare.pack.connection.SQLiteHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rishabhkokra on 4/12/2016.
 */
public class OtpVerification extends ActionBarActivity {
    ProgressDialog pDialog;
    EditText receivedOtp;
    private ProgressBar progressBar;
    private SQLiteHandler db;
    Button btn_otp_verification;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        progressBar.setVisibility(View.VISIBLE);
        if (toolbar != null) {


            toolbar.setNavigationIcon(R.drawable.back_arrow);

            toolbar.setTitle("Verification");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),
                            RegisterActivity.class);
                    startActivity(intent);

                }
            });
        }
        btn_otp_verification=(Button)findViewById(R.id.btn_otp_verification);
        progressBar.setVisibility(View.VISIBLE);
        receivedOtp=(EditText)findViewById(R.id.otp);
        btn_otp_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp=receivedOtp.getText().toString();
                Bundle bundle = getIntent().getExtras();
                String user_id= bundle.getString("user_id");



                Toast.makeText(getApplicationContext(),user_id,Toast.LENGTH_LONG).show();
                otpVerification(otp, user_id);
            }
        });
    }
    public void receivedSms(String message)
    {
        try
        {
            receivedOtp.setText(message);
            String otp=receivedOtp.getText().toString();
            Bundle bundle = getIntent().getExtras();
           String g= bundle.getString("user_id");
           String user_id= db.getUserDetails().get("user_id");

            Toast.makeText(getApplicationContext(),"db:"+user_id+" bundle:"+g,Toast.LENGTH_LONG).show();
            otpVerification(otp, g);

        }
        catch (Exception e)
        {
        }
    }

    private void otpVerification(final String otp,final String user_id) {

        class OtpVerifyAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog.setMessage("Verifying OTP....");
                pDialog.show();


            }

            @Override
            protected String doInBackground(String... params) {
                String code = params[0];

                String session_id=params[1];
                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("otp", otp));
                nameValuePairs.add(new BasicNameValuePair("user_id", user_id));

                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            AppConfig.OTP_VERIFICATION_URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();
                progressBar.setVisibility(View.INVISIBLE);
pDialog.hide();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                if (s.equalsIgnoreCase("failure")) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                } else {
                    //db.deleteSession();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    String userName = s;



                    //session.KEY_NAME=result;
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);


                    startActivity(intent);
                    finish();


                }
            }
        }
        OtpVerifyAsync la = new OtpVerifyAsync();
        la.execute(otp,user_id);
    }


}
