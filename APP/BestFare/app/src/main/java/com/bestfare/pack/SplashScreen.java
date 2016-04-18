package com.bestfare.pack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestfare.pack.connection.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashScreen extends Activity {

	String now_playing, earned;
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	Button signUpButton,signIn;
	TextView tag,loading,appName,terms;
	private SessionManager session;
	ImageView logo;
	GIFView gifView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_splash);
		//gifView=(GIFView)findViewById(R.id.gifview);
		//gifView.setVisibility(R.string.visible);
/*

		 * Showing splashscreen while making network calls to download necessary
		 * data before launching the app Will use AsyncTask to make http call
*/


		new PrefetchData().execute();
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {
			// Internet Connection is Present
			// make HTTP requests
			Toast.makeText(getApplicationContext(), "Connecting Shortly....", Toast.LENGTH_LONG).show();
		} else {

Intent in=new Intent(getApplicationContext(),NoInternetConnection.class);
			startActivity(in);
			finish();
			}
	}
/*
public void showAlertDialog( Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting alert dialog icon
		alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}


	 * Async Task to make http call
*/

	public void slideToTop(View view){
		TranslateAnimation animate = new TranslateAnimation(0,0,0,-60);
		view.setVisibility(View.VISIBLE);
		animate.setDuration(1000);
		animate.setFillAfter(true);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
	}
	public void stopAnimation(View view){
		view.setVisibility(View.INVISIBLE);
	}
	public void slideToRight(View view){
		TranslateAnimation animate = new TranslateAnimation(0,60,0,0);
		animate.setDuration(500);
		animate.setFillAfter(true);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
	}
	public void slideToLeft(View view){
		TranslateAnimation animate = new TranslateAnimation(0,-100,0,0);
		animate.setDuration(500);
		animate.setFillAfter(true);
		view.startAnimation(animate);
		//view.setVisibility(View.GONE);


	}
	public void slideToTopVisible(View view){
		TranslateAnimation animate = new TranslateAnimation(0,0,0,-60);
		view.setVisibility(View.VISIBLE);
		animate.setDuration(1000);
		animate.setFillAfter(true);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
	}
	public void slideToTopTAG(View view){
		TranslateAnimation animate = new TranslateAnimation(0,0,0,-90);
		animate.setDuration(1000);
		animate.setFillAfter(true);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
	}

	private class PrefetchData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// before making http calls
			Log.e("JSON", "Pre execute");


		}

		@Override
		protected Void doInBackground(Void... arg0) {
			/* * Will make http call here This call will download required data
			 * before launching the app 
			 * example: 
			 * 1. Downloading and storing SQLite 
			 * 2. Downloading images 
			 * 3. Parsing the xml / json 
			 * 4. Sending device information to server 
			 * 5. etc.,*/


			// get Internet status

			JsonParser jsonParser = new JsonParser();
			String json = jsonParser
					.getJSONFromUrl(AppConfig.JSON_URL);

			Log.e("Response: ", "> " + json);

			if (json != null) {
				try {
					JSONObject jObj = new JSONObject(json)
							.getJSONObject("game_stat");
					now_playing = jObj.getString("now_playing");
					earned = jObj.getString("earned");

					Log.e("JSON", "> " + now_playing + earned);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// After completing http call
			// will close this activity and lauch main activity

			if(isInternetPresent) {
			/*	Intent i = new Intent(SplashScreen.this, MapsActivity.class);
				i.putExtra("now_playing", now_playing);
				i.putExtra("earned", earned);

				startActivity(i);
*/session = new SessionManager(getApplicationContext());
				// close this activity
				if (session.isLoggedIn()) {
					// User is already logged in. Take him to main activity
					Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}

				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//stopAnimation(gifView);

				appName=(TextView)findViewById(R.id.name);
				//slideToRight(appName);
				tag=(TextView)findViewById(R.id.tag);
				slideToTopTAG(tag);
				logo=(ImageView)findViewById(R.id.logo);
				//slideToLeft(logo);
				slideToTopVisible(logo);



				signUpButton=(Button)findViewById(R.id.signUpButton);
				slideToTop(signUpButton);
				signIn=(Button)findViewById(R.id.signIn);
				slideToTop(signIn);
				signIn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i=new Intent(SplashScreen.this,LoginActivity.class);
						startActivity(i);
						finish();
					}
				});



				terms=(TextView)findViewById(R.id.terms);
				slideToTop(terms);
				signUpButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							signUpButton.setBackgroundColor(getColor(R.color.onClickColor));


						}
						Intent i=new Intent(SplashScreen.this,RegisterActivity.class);
						startActivity(i);
					}
				});



			}
		}


	}

}
