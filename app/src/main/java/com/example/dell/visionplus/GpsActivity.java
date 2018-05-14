package com.example.dell.visionplus;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.dell.visionplus.AppConfig.GEOMETRY;
import static com.example.dell.visionplus.AppConfig.GOOGLE_BROWSER_API_KEY;
import static com.example.dell.visionplus.AppConfig.ICON;
import static com.example.dell.visionplus.AppConfig.LATITUDE;
import static com.example.dell.visionplus.AppConfig.LOCATION;
import static com.example.dell.visionplus.AppConfig.LONGITUDE;
import static com.example.dell.visionplus.AppConfig.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.example.dell.visionplus.AppConfig.MIN_TIME_BW_UPDATES;
import static com.example.dell.visionplus.AppConfig.NAME;
import static com.example.dell.visionplus.AppConfig.OK;
import static com.example.dell.visionplus.AppConfig.PLACE_ID;
import static com.example.dell.visionplus.AppConfig.PLACE_NAME;
import static com.example.dell.visionplus.AppConfig.PLAY_SERVICES_RESOLUTION_REQUEST;
import static com.example.dell.visionplus.AppConfig.PROXIMITY_RADIUS;
import static com.example.dell.visionplus.AppConfig.REFERENCE;
import static com.example.dell.visionplus.AppConfig.STATUS;
import static com.example.dell.visionplus.AppConfig.SUPERMARKET_ID;
import static com.example.dell.visionplus.AppConfig.VICINITY;
import static com.example.dell.visionplus.AppConfig.ZERO_RESULTS;

public class GpsActivity extends AppCompatActivity implements OnMapReadyCallback,

        LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    TextToSpeech tto_s;
    double latitude, longitude;
    private final int REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"speak nearby place you want to find");
        startActivityForResult(i,REQUEST_CODE);


        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {

            }
        };

        // creating object of TextToSpeech
        tto_s = new TextToSpeech(GpsActivity.this , listener);
        tto_s.setSpeechRate(0);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // showLocationSettings();
        }
    }


    protected void onActivityResult(int request_code, int result_code, Intent data){

        super.onActivityResult(request_code,result_code,data);

        if(request_code == REQUEST_CODE && result_code == RESULT_OK){

            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String text = result.get(0);

            loadNearByPlaces(latitude,longitude,text);

        }

    }


    @Override

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,

                android.Manifest.permission.ACCESS_COARSE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        showCurrentLocation();
    }

    private void showCurrentLocation() {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this,

                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, MIN_TIME_BW_UPDATES,

                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    private void loadNearByPlaces(double latitude, double longitude, String type) {
//YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works


        StringBuilder googlePlacesUrl =

                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type.toLowerCase());
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_BROWSER_API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(),

                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject result) {

                        Log.i("", "onResponse: Result= " + result.toString());
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override                    public void onErrorResponse(VolleyError error) {
                        Log.e("", "onErrorResponse: Error= " + error);
                        Log.e("", "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(GpsActivity.this).add(request);

    }

    private void parseLocationResult(JSONObject result) {

        String id , place_id , placeName = null, reference , icon , vicinity = null , foundPlaces="";
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                mMap.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LONGITUDE);
                    reference = place.getString(REFERENCE);
                    icon = place.getString(ICON);

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);

                    mMap.addMarker(markerOptions);
                    foundPlaces = foundPlaces + " , " + placeName;

                }
                tto_s.speak("places found arr ", TextToSpeech.QUEUE_ADD, null);

                final String[] singleplace = foundPlaces.split(" , ");


                new Handler().post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {

                        for (int i = 0; i < singleplace.length; i++) {

                            tto_s.speak(singleplace[i], TextToSpeech.QUEUE_ADD, null);

                            tto_s.playSilentUtterance(1500, TextToSpeech.QUEUE_ADD, null);

                        }
                    }
                });


            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e("", "parseLocationResult: Error=" + e.getMessage());
        }
    }

    @Override

    public void onLocationChanged(Location location) {
         latitude = location.getLatitude();
         longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }

    @Override

    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override

    public void onProviderEnabled(String s) {
    }

    @Override

    public void onProviderDisabled(String s) {
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        tto_s.stop();
    }
}
