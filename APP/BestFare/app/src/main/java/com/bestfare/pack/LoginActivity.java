
package com.bestfare.pack;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.transition.Fade;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.ActionBarActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bestfare.pack.MapsActivity;
import com.bestfare.pack.R;
import com.bestfare.pack.RegisterActivity;
import com.bestfare.pack.SessionManager;
import com.bestfare.pack.connection.AppConfig;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bestfare.pack.connection.SQLiteHandler;
public class LoginActivity extends ActionBarActivity {

    private Button btnLogin;
    private Button btnLinkToRegister;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
private String TAG="OUTPUT";
private AnimationDrawable animation;
    private LoginButton loginButton;
    private ViewGroup mRootView;
    private Dialog loadingDialog;
    GIFView gifView;
    private Fade mFade;
    private  String email,password;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    TextView forgotPassword;
private AccessToken accessToken;
    CallbackManager callbackManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
       //setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationIcon(R.drawable.back_arrow);
            //toolbar.getNavigationIcon().
            toolbar.setTitle("Sign in");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
Intent intent=new Intent(LoginActivity.this,SplashScreen.class);
                    startActivity(intent);

                }
            });
        }
forgotPassword=(TextView)findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(in);
            }
        });

        btnLinkToRegister=(Button)findViewById(R.id.new_link_signup);
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            String f_name, m_name, l_name, full_name, facebook_id, email_id;


            @Override
            public void onSuccess(LoginResult loginResult) {

                final Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    // facebook_id=profile.getId();
                    f_name = profile.getFirstName();

                    m_name = profile.getMiddleName();
                    l_name = profile.getLastName();
                    full_name = profile.getName();


                    Toast.makeText(LoginActivity.this, f_name, Toast.LENGTH_SHORT).show();

                }
                //Toast.makeText(FacebookLogin.this,"Wait...",Toast.LENGTH_SHORT).show();
                final GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    Bundle bFacebookData = getFacebookData(object);

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {


            }

            @Override
            public void onError(FacebookException error) {

            }
        });





// Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        inputEmail=(TextInputLayout)findViewById(R.id.login_input_email);
        inputPassword=(TextInputLayout)findViewById(R.id.login_input_password);
        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

            // Login button Click Event
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);

            }
        });
btnLogin=(Button)findViewById(R.id.signIn_btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {



            public void onClick(View view) {

                final Dialog dialog = new Dialog(LoginActivity.this);


               email = inputEmail.getEditText().getText().toString();
                 password = inputPassword.getEditText().getText().toString();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    // session.createLoginSession(email,password);

                    checkLogin(email, password);
                } else {
                        // Prompt user to enter credentials
                    CoordinatorLayout Clayout = (CoordinatorLayout) findViewById(R.id.fab);
                    Snackbar.make(Clayout, "All fields are mandatory.",
                            Snackbar.LENGTH_LONG).show();
                    }


            }

            });

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void displayMessage(Profile profile){
        if(profile != null){
            Toast.makeText(getApplicationContext(), profile.getName(), Toast.LENGTH_LONG).show();
        }
    }
        @Override
    public void onBackPressed()
    {
        Toast.makeText(getApplicationContext(), "back button pressed!", Toast.LENGTH_LONG).show();
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);

        }
    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {

            String id = object.getString("id");
/*
            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }*/

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
Toast.makeText(getApplicationContext(),object.getString("first_name")+ object.getString("email"),Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;

    }

    private void checkLogin(final String email, final String password) {
        class LoginAsync extends AsyncTask<String, Void, String>{

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Loading...");

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

                        session.setLogin(true);

                        String name = jObj.getString("user_name").toString();

                        String status=jObj.getString("status").toString();

                        Toast.makeText(getApplicationContext(),name+id+status,Toast.LENGTH_LONG).show();
                       /* if(status.equalsIgnoreCase("0")){
                                Intent intent = new Intent(LoginActivity.this,
                                OtpVerification.class);
                            intent.putExtra("user_id",id);
                        startActivity(intent);
                        finish();
                        }else {*/

                            // Inserting row in users table
                            db.addUser(id, name, email, password);

                            // Launch main activity
                      Intent intent = new Intent(LoginActivity.this,
                                MapsActivity.class);
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

}

