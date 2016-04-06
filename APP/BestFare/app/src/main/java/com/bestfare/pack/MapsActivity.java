package com.bestfare.pack;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    boolean found = false;
    private GoogleMap mMap; // Might be null if Google Play services APK is not
    // available.
    String bestProvider;
    LocationListener locationListener;
    private String TAG = "LOG";
    LocationResult locationResult;
    boolean gps_enabled = false;
    private MarkerOptions markerOptions;
    boolean network_enabled = false, locationChange = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    boolean nonGpsLocation = false;
    // The minimum time between updates in milliseconds

    private LatLng DESTINATION_LOCATION;
    private ProgressDialog progressDialog;
    private static LatLng SOURCE_LOCATION;
    private Context context;
    private Location location;
    private double Slatitude = 0;
    private double Slongitude = 0;
    TextView sourceAddress;
    ArrayList<LatLng> markerPoints;
    FloatingActionButton fab;
    GoogleApiClient mLocationClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    private Address add;
    List<Address> addressListSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sourceAddress = (TextView) findViewById(R.id.source_textView);
		/*
		 * fab = (FloatingActionButton) findViewById(R.id.fab);
		 * fab.setOnClickListener(new View.OnClickListener() {
		 *
		 * @Override public void onClick(View view) {
		 * Toast.makeText(getApplicationContext
		 * (),"Clicked on button",Toast.LENGTH_LONG).show(); } });
		 */
        setUpMapIfNeeded();

        createLocationRequest();
        showDialog();
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Set destination..");

        markerPoints = new ArrayList<LatLng>();
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

					/*
					 * @Override public void onError(AsyncTask.Status status) {
					 * // TODO: Handle the error. Log.i(TAG,
					 * "An error occurred: " + status); }
					 */
                });

        // 4. create & set LocationRequest for Location update

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        /*Intent in=new Intent(this,MapsActivity.class);
        startActivity(in);*/
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play
     * services APK is correctly installed) and the map has not already been
     * instantiated.. This will ensure that we only ever call
     * {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
     * for the user to install/update the Google Play services APK on their
     * device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and
     * correctly installing/updating/enabling the Google Play services. Since
     * the FragmentActivity may not have been completely destroyed during this
     * process (it is likely that it would only be stopped or paused),
     * {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
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
            showDialog();
            if(DESTINATION_LOCATION!=null && SOURCE_LOCATION!=null) {
                Log.d("######My URL", makeURL(SOURCE_LOCATION.latitude, SOURCE_LOCATION.longitude,
                        DESTINATION_LOCATION.latitude,
                        DESTINATION_LOCATION.longitude));
            }
            else{
                Toast.makeText(getApplicationContext(),"Destination & SOURCE are null",Toast.LENGTH_LONG).show();
            }

        } else {
            GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);
            if (gpsTracker.canGetLocation()) {

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                Slatitude = latitude;
                Slongitude = longitude;
                SOURCE_LOCATION = new LatLng(Slatitude, Slongitude);
                GpsLocation();

                // \n is for new line
                Toast.makeText(
                        getApplicationContext(),
                        "Your Location is - \nLat: " + latitude + "\nLong: "
                                + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
                nonGpsLocation();

            }

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
        location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {

            Slatitude = location.getLatitude();
            Slongitude = location.getLongitude();
            SOURCE_LOCATION = new LatLng(Slatitude, Slongitude);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
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

            updateUI();

            mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))

                            .title("Set pickup location")
                            .snippet(" ")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)))
                    .showInfoWindow();

        } else {
            mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))
                            .title("Set pickup location")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)))
                    .showInfoWindow();

        }
        markerOptions.draggable(true);

        mMap.setOnInfoWindowClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(SOURCE_LOCATION) // Sets the center of the map to
                        // current location view
                .zoom(13) // Sets the zoom
                .bearing(90) // Sets the orientation of the camera to
                        // east
                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    void GpsLocation() throws IOException {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        MarkerOptions markerOptions = new MarkerOptions();

        addressListSource = new Geocoder(MapsActivity.this, Locale.getDefault())
                .getFromLocation(SOURCE_LOCATION.latitude,
                        SOURCE_LOCATION.longitude, 1);

        if (addressListSource.size() > 0 && addressListSource != null) {

            add = addressListSource.get(0);

            updateUI();
            mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))

                            .title("Set pickup location")
                            .snippet(" ")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)))
                    .showInfoWindow();

        } else {
            mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))
                            .title("Set pickup location")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)))
                    .showInfoWindow();
            updateUI();
        }
        markerOptions.draggable(true);

        mMap.setOnInfoWindowClickListener(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(SOURCE_LOCATION) // Sets the center of the map to
                        // current location view
                .zoom(13) // Sets the zoom
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
                result = sb.toString();
            }
            // Getting URL to the Google Directions API
            String url = makeURL(SOURCE_LOCATION.latitude,
                    SOURCE_LOCATION.longitude, DESTINATION_LOCATION.latitude,
                    DESTINATION_LOCATION.longitude);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);

        }

        mMap.addMarker(new MarkerOptions()
                .position(
                        new LatLng(DESTINATION_LOCATION.latitude,
                                DESTINATION_LOCATION.longitude))
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
            updateUI();
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
            Toast.makeText(MapsActivity.this, "Click Info Window",
                    Toast.LENGTH_LONG).show();

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

    protected void stopLocationUpdates() {
        if (mLocationClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mLocationClient,
                    (com.google.android.gms.location.LocationListener) this);

        }
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

            mMap.addPolyline(lineOptions);
        }
    }

    void updateUI() {

        Toast.makeText(getApplicationContext(), "inUpdate UI",
                Toast.LENGTH_LONG).show();

        if (SOURCE_LOCATION != null && mMap!=null && markerOptions!=null) {

/*
            mMap.addMarker(
                    markerOptions
                            .position(
                                    new LatLng(SOURCE_LOCATION.latitude,
                                            SOURCE_LOCATION.longitude))

                            .title("Set pickup location")
                            .snippet(" ")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker_source)))
                    .showInfoWindow();

*/
        }

        if (addressListSource.size() > 0 && addressListSource != null) {
            add = addressListSource.get(0);
            sourceAddress.setText("pickup location: " + add.getSubAdminArea()
                    + "," + add.getSubLocality() + "," + add.getAdminArea()
                    + "," + add.getLocality());
        } else {
            sourceAddress.setText("Unable to find pickup location");
        }

    }

    private void showDialog() {
        progressDialog = new ProgressDialog(this);

        progressDialog.setCancelable(false);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
