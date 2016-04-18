package com.bestfare.pack;

import android.Manifest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestfare.pack.adapter.CustomListAdapter;

import com.bestfare.pack.navigationDrawer.RecyclerViewAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class MapsActivity extends AppCompatActivity implements LocationListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener ,OnMapReadyCallback{
    boolean found = false;
    private GoogleMap mMap; // Might be null if Google Play services APK is not
    // available.
    String bestProvider;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    public DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    String navTitles[];
    TypedArray navIcons;
    RecyclerView.Adapter recyclerViewAdapter;
    android.support.v7.app.ActionBarDrawerToggle drawerToggle;

    LocationListener locationListener;
    private String TAG = "LOG";
    LocationResult locationResult;

    private MarkerOptions markerOptions;
    boolean network_enabled = false, locationChange = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    boolean nonGpsLocation = false;
    // The minimum time between updates in milliseconds
    Polyline polyline;

    private static LatLng DESTINATION_LOCATION;
    private ProgressDialog progressDialog;
    private static LatLng SOURCE_LOCATION;
    private Context context;
    private Dialog settingsDialog;
    private Location location;
    Toolbar toolbar;
    boolean removePolyLine;
    GPSTracker gpsTracker;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    String desnationTo;
    Handler handler;
    private double Slatitude = 0;
    private double Slongitude = 0;

    ArrayList<LatLng> markerPoints;

    CustomListAdapter adapter;
    GoogleApiClient mLocationClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    private Address add;
    List<Address> addressListSource;
    Marker markerDestination;
    Marker markerSource;
    ProgressBar progressBar;
    boolean isSetLocationPressed = false;
    AnimatedVectorDrawable gif;
    Runnable runnable;

    double setPickupLat;
    double setPickupLng;
    ListView list;
    String[] itemname ={

    };

    Integer[] imgid={
            R.drawable.marker_source,
            R.drawable.marker_destination,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);




        setUpMapIfNeeded();

        setUpToolBar();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

             setPickupLat = bundle.getDouble("source_lat");
           setPickupLng = bundle.getDouble("source_lng");
            Toast.makeText(getApplicationContext(),"bundle"+setPickupLat+setPickupLng,Toast.LENGTH_LONG).show();
            SOURCE_LOCATION = new LatLng(setPickupLat,setPickupLng);
            isSetLocationPressed = true;
            setPickupLocation();


        }
        // createLocationRequest();

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Set destination..");


        autocompleteFragment
                .setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        Toast.makeText(getApplicationContext(),
                                place.getName(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Place: " + place.getName());
                        DESTINATION_LOCATION = place.getLatLng();
                        found = true;
                        if (polyline != null && markerDestination != null) {
                            polyline.remove();
                            markerDestination.remove();


                        }


                        try {
                            setUpMap();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Status status) {
                        Log.i(TAG, "An error occurred: " + status);
                    }


                });

        recyclerView  = (RecyclerView) findViewById(R.id.recyclerView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Setup Titles and Icons of Navigation Drawer
        navTitles = getResources().getStringArray(R.array.navDrawerItems);
        navIcons = getResources().obtainTypedArray(R.array.navDrawerIcons);



        recyclerViewAdapter = new RecyclerViewAdapter(navTitles,navIcons,this);
        recyclerView.setAdapter(recyclerViewAdapter);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Finally setup ActionBarDrawerToggle
        setupDrawerToggle();


        //Add the Very First i.e Squad Fragment to the Container
        /*Fragment squadFragment = new SquadFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView,squadFragment,null);
        fragmentTransaction.commit();
*/




    }
    void setUpToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_drawer);

            toolbar.setTitle("Book Ride");

        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }
    void setupDrawerToggle(){
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }
    @Override
    protected void onResume() {
        super.onResume();

        getIntent();

        setUpMapIfNeeded();
        /*Intent in=new Intent(this,MapsActivity.class);
        startActivity(in);*/
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // Place your dialog code here to display the dialog
            Intent shortcutIntent = new Intent(getApplicationContext(), MapsActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);
            Toast.makeText(getApplicationContext(), "Shortcut Created!", Toast.LENGTH_LONG).show();


            settingsDialog = new Dialog(this);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.one_time_dialog_after_installing
                    , null));
            settingsDialog.show();
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    settingsDialog.dismiss(); // when the task active then close the dialog
                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                }
            }, 4000);

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            setupDrawerToggle();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                if (markerSource != null) {
                    markerSource.remove();
                }
                mMap.getUiSettings().setMapToolbarEnabled(false);
            } else {
                // Show rationale and request permission.
            }
            PopupAdapter customInfoWindow = new PopupAdapter(
                    getLayoutInflater());

            mMap.setInfoWindowAdapter(customInfoWindow);

            try {
                setUpMap();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpMap() throws IOException {
        if (found) {

            destinationMapSetup();

            if (DESTINATION_LOCATION != null && SOURCE_LOCATION != null) {
                makeURL(SOURCE_LOCATION.latitude, SOURCE_LOCATION.longitude,
                        DESTINATION_LOCATION.latitude,
                        DESTINATION_LOCATION.longitude);
            } else {
                Toast.makeText(getApplicationContext(), "Destination & SOURCE are null", Toast.LENGTH_LONG).show();
            }

        }  else {

                if (isSetLocationPressed == false) {
                    nonGpsLocation();
                }


            }

        }



    void setPickupLocation() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }

        isSetLocationPressed = false;
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (locationManager != null) {

            locationManager.removeUpdates(MapsActivity.this);
        }


        mMap.setMyLocationEnabled(false);
        SOURCE_LOCATION = new LatLng(setPickupLat, setPickupLng);


if(markerSource!=null) {
    markerSource.remove();
}



        markerPoints = new ArrayList<LatLng>();
        markerPoints.add(SOURCE_LOCATION);


        try {


            markerSource = mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(setPickupLat,
                                            setPickupLng))
                            .title("Set pickup location")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)));
markerSource.showInfoWindow();


           addressListSource = new Geocoder(MapsActivity.this, Locale.getDefault())
                    .getFromLocation(SOURCE_LOCATION.latitude, SOURCE_LOCATION.longitude, 1);

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }
        String address;

        if (addressListSource.size() > 0 && addressListSource != null) {
            add = addressListSource.get(0);
            itemname = new String[2];
            if (desnationTo != null) {
                address = add.getAddressLine(0)+" " + add.getAdminArea() + " " + add.getSubAdminArea() + " " +add.getLocality() + " " + add.getSubLocality() ;
                itemname[0] = address;
                itemname[1] = desnationTo;
                adapter = new CustomListAdapter(this, itemname, imgid);
                list.setAdapter(adapter);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(SOURCE_LOCATION) // Sets the center of the map to
                                // current location view
                        .zoom(12) // Sets the zoom
                        .bearing(90) // Sets the orientation of the camera to
                                // east
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            } else {
                address = add.getAddressLine(0)+" " + add.getAdminArea() + " " + add.getSubAdminArea() + " " +add.getLocality() + " " + add.getSubLocality() ;
                itemname = new String[]{address, " "};
                adapter = new CustomListAdapter(this, itemname, imgid);

                list.setAdapter(adapter);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(SOURCE_LOCATION) // Sets the center of the map to
                                // current location view
                        .zoom(12) // Sets the zoom
                        .bearing(90) // Sets the orientation of the camera to
                                // east
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }
            markerPoints=null;
        }
    }
    void nonGpsLocation() throws IOException

    {
        nonGpsLocation = true;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        mMap.setMyLocationEnabled(true);
        Criteria criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        checkFirstRun();
        location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {

            Slatitude = location.getLatitude();
            Slongitude = location.getLongitude();
            SOURCE_LOCATION = new LatLng(Slatitude, Slongitude);
        }
        locationManager.requestLocationUpdates(bestProvider, 7000, 0, this);
        addressListSource = new Geocoder(MapsActivity.this, Locale.getDefault())
                .getFromLocation(Slatitude, Slongitude, 1);

        markerOptions = new MarkerOptions();

        try {

            addressListSource = new Geocoder(MapsActivity.this,
                    Locale.getDefault()).getFromLocation(
                    SOURCE_LOCATION.latitude, SOURCE_LOCATION.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressListSource.size() > 0 && addressListSource != null) {

            add = addressListSource.get(0);
            if(markerSource!=null){
                markerSource.remove();
            }



            markerSource=  mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))

                            .title("Set pickup location")
                            .snippet(" ")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)));

            markerSource.showInfoWindow();



        } else {
            markerSource=  mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))
                            .title("Set pickup location")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)));
            markerSource.showInfoWindow();


        }
        itemname= new String[]{ add.getAddressLine(0)+" " + add.getAdminArea() + " " + add.getSubAdminArea() + " " +add.getLocality() + " " + add.getSubLocality()};
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid);
        list=(ListView)findViewById(R.id.listView_source_desti);
        list.setAdapter(adapter);


        mMap.setOnInfoWindowClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(SOURCE_LOCATION) // Sets the center of the map to
                        // current location view
                .zoom(8) // Sets the zoom
                .bearing(90) // Sets the orientation of the camera to
                        // east
                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }


    private void destinationMapSetup() {
        String result = null;
        try {
            if(polyline!=null && markerDestination!=null){
                polyline.remove();
                markerDestination.remove();
            }
            List<Address> addressList = new Geocoder(MapsActivity.this,
                    Locale.getDefault()).getFromLocation(
                    DESTINATION_LOCATION.latitude,
                    DESTINATION_LOCATION.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(", ");
                }
                sb.append(address.getLocality()).append(", ");
                sb.append(address.getPostalCode()).append(",");
                sb.append(address.getCountryName());
                desnationTo = sb.toString();
                itemname[1]= String.valueOf(desnationTo);
                list.setAdapter(adapter);
            }
            // Getting URL to the Google Directions API

            if(setPickupLat!=0 && setPickupLng!=0){
                String url = makeURL(setPickupLat,
                        setPickupLng, DESTINATION_LOCATION.latitude,
                        DESTINATION_LOCATION.longitude);
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }else {
                String url = makeURL(SOURCE_LOCATION.latitude,
                        SOURCE_LOCATION.longitude, DESTINATION_LOCATION.latitude,
                        DESTINATION_LOCATION.longitude);
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }

        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);

        }
        markerPoints = new ArrayList<LatLng>()
        ;markerPoints.add(DESTINATION_LOCATION);

        markerDestination=  mMap.addMarker(new MarkerOptions()
                .position(
                        new LatLng(markerPoints.get(0).latitude,
                                markerPoints.get(0).longitude))
                .title(result)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.marker_destination)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(DESTINATION_LOCATION.latitude,
                        DESTINATION_LOCATION.longitude)) // Sets the
                        // center of
                        // the map
                        // to
                        // current
                        // location
                        // view
                .zoom(13) // Sets the zoom
                .bearing(90) // Sets the orientation of the camera to east
                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        found = false;

    }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString
                .append("https://maps.googleapis.com/maps/api/directions/json");

        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyC-egPDy-ZNqrmgQZV9xjI1pdhgcLFdfuw");
        return urlString.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Slatitude = location.getLatitude();
            Slongitude = location.getLongitude();
            mLastLocation = location;
            locationChange = true;

            SOURCE_LOCATION = new LatLng(Slatitude, Slongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(SOURCE_LOCATION));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            if(isSetLocationPressed==false) {
                updateUI();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Can't retrieve your location please set a pickup location!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exceptio while download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        // arg0.showInfoWindow();
        try {
            Intent in=new Intent(getApplicationContext(),SettingPickUpLocation.class);
            startActivity(in);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(getApplicationContext(), "in onConnected",
                Toast.LENGTH_LONG).show();
        // Set the fastest update interval to 1 second
        // mLocationRequest.setFastestInterval(1000 * 1);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);

        startLocationUpdates();

        if (mLastLocation != null) {

            Slatitude = mLastLocation.getLatitude();
            Slongitude = mLastLocation.getLongitude();
            SOURCE_LOCATION = new LatLng(Slatitude, Slongitude);

        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest,
                (com.google.android.gms.location.LocationListener) this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Connection suspended!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MapsActivity.this,
                "Connection failed check your internet connectivity.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            Log.d("WebService Response: ", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);

            }


            // Drawing polyline in the Google Map for the i-th route

            polyline= mMap.addPolyline(lineOptions);

        }
    }

    void updateUI() {

        Toast.makeText(getApplicationContext(), "inUpdate UI",
                Toast.LENGTH_LONG).show();



        if(markerSource!=null ){
            markerSource.remove();
        }

        if (SOURCE_LOCATION != null && mMap!=null && markerOptions!=null) {
            markerOptions=new MarkerOptions();
            markerSource= mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))

                            .title("Set pickup location")
                            .snippet(" ")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)));
            markerSource.showInfoWindow();

        }
        try {
            addressListSource = new Geocoder(MapsActivity.this, Locale.getDefault())
                    .getFromLocation(SOURCE_LOCATION.latitude, SOURCE_LOCATION.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

if(setPickupLat!=0){
    setPickupLocation();
}
        if (addressListSource.size() > 0 && addressListSource != null) {
            add = addressListSource.get(0);
            if (desnationTo!=null){
                itemname= new String[]{ add.getAddressLine(0)+" " + add.getAdminArea() + " " + add.getSubAdminArea() + " " +add.getLocality() + " " + add.getSubLocality() ,desnationTo};
                adapter=new CustomListAdapter(this, itemname, imgid);
                list=(ListView)findViewById(R.id.listView_source_desti);
                list.setAdapter(adapter);
            }
            else{
                itemname= new String[]{ add.getAddressLine(0)+" " + add.getAdminArea() + " " + add.getSubAdminArea() + " " +add.getLocality() + " " + add.getSubLocality() ," "};
                adapter=new CustomListAdapter(this, itemname, imgid);
                list=(ListView)findViewById(R.id.listView_source_desti);
                list.setAdapter(adapter);
            }



        } else {
            getIntent();
            finish();
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        // 1. connect the client.

        if (mLocationClient.isConnected()) {
            mLocationClient.connect();
            locationResult.getLocations();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 1. disconnecting the client invalidates it.
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLocationClient.disconnect();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
