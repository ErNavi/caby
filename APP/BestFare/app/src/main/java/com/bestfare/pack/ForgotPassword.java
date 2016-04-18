package com.bestfare.pack;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bestfare.pack.connection.AppConfig;
import com.facebook.FacebookSdk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rishabhkokra on 4/14/2016.
 */
public class ForgotPassword extends ActionBarActivity
{
    private TextInputLayout inputForgotEmail;
    private Button submit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.forgot_password_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        inputForgotEmail=(TextInputLayout)findViewById(R.id.forgot_email);
        submit=(Button)findViewById(R.id.btn_forgot_submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Under construction!",Toast.LENGTH_LONG).show();
            }
        });
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.back_arrow);

            toolbar.setTitle("Forgot Password");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                }
            });
        }

    }
/*

    private void checkLogin(final String email) {
        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog = ProgressDialog.show(ForgotPassword.this, "Please wait", "Loading...");

            }

            @Override
            protected String doInBackground(String... params) {
                String em = params[0];
                String pass = params[1];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", pass));
                String result = null;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            AppConfig.LOGIN_URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line+"\n");
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
            protected void onPostExecute(String result){
                loadingDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(result);

                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session

                        //JSONObject user = jObj.getJSONObject("user");
                        // Now store the user in SQLite
                        String id=jObj.getString("id").toString();



                        String name = jObj.getString("user_name").toString();

                        String status=jObj.getString("status").toString();

                        Toast.makeText(getApplicationContext(), name + id + status, Toast.LENGTH_LONG).show();
                       */
/* if(status.equalsIgnoreCase("0")){
                                Intent intent = new Intent(LoginActivity.this,
                                OtpVerification.class);
                            intent.putExtra("user_id",id);
                        startActivity(intent);
                        finish();
                        }else {*//*


                        // Inserting row in users table


                        // Launch main activity
                        Intent intent = new Intent(ForgotPassword.this,
                                OtpVerification.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        // }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(email, password);

    }
*/

}
