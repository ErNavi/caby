package com.bestfare.pack;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bestfare.pack.connection.AppConfig;
import com.bestfare.pack.connection.SQLiteHandler;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Rishabhkokra on 4/9/2016.
 */
public class RegisterActivity extends ActionBarActivity {

    TextInputLayout userName, contactNum, userEmail, userPassword;
    Spinner userGender;
    Button registerButton;
    String user_Name;
    ProgressDialog pDialog;
    UserDetails userDetails;
    String TAG = "OUTPUT";
    CheckBox termsPolicy;
    Context context;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (toolbar != null) {


            toolbar.setNavigationIcon(R.drawable.back_arrow);

            toolbar.setTitle("Sign up");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RegisterActivity.this,
                            SplashScreen.class);
                    startActivity(intent);

                }
            });
        }
        userDetails = new UserDetails();
        userName = (TextInputLayout) findViewById(R.id.input_name);
        contactNum = (TextInputLayout) findViewById(R.id.input_contactNum);
        userEmail = (TextInputLayout) findViewById(R.id.input_email);
        userEmail.setHint("Email");
        EditText et = new EditText(getApplicationContext());


        et.setText(getEmailAccount());

        userEmail.addView(et);


        userPassword = (TextInputLayout) findViewById(R.id.input_password);




        termsPolicy = (CheckBox) findViewById(R.id.termsCheck);
        registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                user_Name = userName.getEditText().getText()
                        .toString();
                userDetails.setUserName(user_Name);


                userDetails.setUserContactNum(contactNum.getEditText()
                        .getText().toString());


                userDetails.setEmailId(userEmail.getEditText().getText()
                        .toString());

                userGender = (Spinner) findViewById(R.id.input_gender);

                userDetails.setUserGender(userGender.getSelectedItem()
                        .toString());


                userDetails.setUserPassword(userPassword.getEditText()
                        .getText().toString());

                if (termsPolicy.isChecked()) {

                    if (!user_Name.isEmpty()
                            && userPassword!=null
                            && userDetails.getUserContactNum() != null
                            && userDetails.getUserPassword() != null) {


                        registerUser(user_Name,
                                userDetails.getEmailId(),
                                userDetails.getUserGender(),
                                userDetails.getUserPassword(),
                                userDetails.getUserContactNum());
                    } else {
                        CoordinatorLayout Clayout = (CoordinatorLayout) findViewById(R.id.fab);
                        Snackbar.make(Clayout, "Please fill all fields.",
                                Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    CoordinatorLayout Clayout = (CoordinatorLayout) findViewById(R.id.fab);
                    Snackbar.make(Clayout, "Please check terms and policy.",
                            Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private String getEmailAccount(){
        String possibleEmail=null;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;

            }
        }
        return possibleEmail;
    }

    private void registerUser(final String name, final String email,
                              final String gender, final String password, final String contact) {
        class RegisterAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog.setMessage("Registration is in progress....");
                pDialog.show();

            }

            @Override
            protected String doInBackground(String... params) {

                String name = params[0];
                String email = params[1];
                String gender = params[2];
                String password = params[3];
                String contact = params[4];
                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("gender", gender));
                nameValuePairs
                        .add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("contact", contact));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(AppConfig.URL_REGISTER);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"), 8);
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
                pDialog.dismiss();

                String registeredUserId = result.trim();
                CoordinatorLayout Clayout = (CoordinatorLayout) findViewById(R.id.fab);
                if (registeredUserId.equalsIgnoreCase("failure")) {

                    Snackbar.make(Clayout, "Email or contact no. already exists! Try new..",
                            Snackbar.LENGTH_LONG).show();

                } else {

                    Log.d(TAG, "response from result" + registeredUserId);
                    Toast.makeText(getApplicationContext(), registeredUserId,
                            Toast.LENGTH_LONG).show();

                    db.deleteUsers();
                 /* String str[]=registeredUserId.split("SUCCESS");
                    db.deleteSession();
                    db.storeSession(str[1]);
                    db.addUser(str[1],name, email, password);
                    Intent in=new Intent(getApplicationContext(),OtpVerification.class);
                   in.putExtra("user_id",str[1]);
                   startActivity(in);
                    finish();*/

					/*
					 *
					 * Intent in=new
					 * Intent(getApplicationContext(),MapsActivity.class);
					 * startActivity(in); finish();
					 */

                }
            }

        }

        RegisterAsync la = new RegisterAsync();
        la.execute(name, email, gender, password, contact);
        // Adding request to request queue

    }




    }


