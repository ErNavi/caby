package com.bestfare.pack;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

/**
 * Created by Rishabhkokra on 4/14/2016.
 */
public class NoInternetConnection extends ActionBarActivity {
    private CoordinatorLayout coordinatorLayout;
   private  ConnectionDetector cd;
    FloatingActionButton refreshButton;
    Boolean isInternetPresent = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.no_internet_connection_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshButton = (FloatingActionButton) findViewById(R.id.refresh_button);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
if(isInternetPresent){
    Intent in=new Intent(getApplicationContext(),SplashScreen.class);
    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(in);
    finish();
}else {
    refreshButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(getIntent());
            finish();
        } });



}
    }
}