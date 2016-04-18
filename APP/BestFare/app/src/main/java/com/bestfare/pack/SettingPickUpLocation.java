package com.bestfare.pack;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;

/**
 * Created by Rishabhkokra on 4/14/2016.
 */
public class SettingPickUpLocation extends ActionBarActivity{
String TAG="OUTPUT";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_pickup_location_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            //setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationIcon(R.drawable.back_arrow);

            toolbar.setTitle("Setup Location");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),
                            MapsActivity.class);
                    startActivity(intent);

                }
            });


        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Set pickup location..");



        autocompleteFragment
                .setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        Toast.makeText(getApplicationContext(),
                                place.getName(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Place: " + place.getName());
                        Intent in=new Intent(getApplicationContext(),MapsActivity.class);
                        in.putExtra("source_lat",place.getLatLng().latitude);
                        in.putExtra("source_lng",place.getLatLng().longitude);
                        startActivity(in);




                    }

                    @Override
                    public void onError(Status status) {
                        Log.i(TAG, "An error occurred: " + status);
                    }

					/*
					 * @Override public void onError(AsyncTask.Status status) {
					 * // TODO: Handle the error. Log.i(TAG,
					 * "An error occurred: " + status); }
					 */
                });

    }
    }
