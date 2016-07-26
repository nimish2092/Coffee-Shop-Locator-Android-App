package com.example.nimish.mapviewtrial;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient googleApiClient;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    MapView mapView;
    GoogleMap map;
    LatLng currentLocation;
    List<Cafe> cafeList = new ArrayList<>();
    DrawerLayout mDrawerLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) findViewById(R.id.mapview);
            mapView.onCreate(savedInstanceState);

            mDrawerLayout.setDrawerShadow(R.drawable.ic_shadow, GravityCompat.START);

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_initialize);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);

                    switch (menuItem.getItemId()) {

                        case R.id.navigation_item_attachment:

                            Intent intent = new Intent(MainActivity.this, ListOfCafe.class);
                            intent.putParcelableArrayListExtra("mylist", (ArrayList<? extends Parcelable>) cafeList);
                            startActivity(intent);
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "In default case", Toast.LENGTH_SHORT).show();
                    }
                    mDrawerLayout.closeDrawers();
                    return true;

                }
            });

            // Gets to GoogleMap from the MapView and does initialization stuff
            map = mapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(true);
            //map.led(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            try {
                MapsInitializer.initialize(this);
            } catch (Exception e) {
                e.printStackTrace();
            }



            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds


            // NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            //drawerFragment.setup((DrawerLayout)findViewById(R.id.drawer_layout));

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        else {
            handleNewLocation(location);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation,18);
            map.animateCamera(cameraUpdate);

            MarkerOptions marker = new MarkerOptions().position(currentLocation).title("I am here!");
            map.addMarker(marker);

        }

    }

    public void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        currentLocation = new LatLng(currentLatitude, currentLongitude);
        /*Bundle bundle = new Bundle();
        bundle.putString("message", ""+ currentLocation );
        MapViewFragment mapViewFragment = new MapViewFragment();
        mapViewFragment.setArguments(bundle);
        //mapViewFragment.FindcurrentLocation(currentLatitude,currentLongitude);*/

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }



    public void Search(String searchText){

        List<Address> results = null;
        if(isNetworkAvailable()) {
            if (searchText != null || !searchText.equals("")) {

                Geocoder geocoder = new Geocoder(this);
                try {
                    results = geocoder.getFromLocationName(searchText,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = results.get(0);
                LatLng searchedAddress = new LatLng(address.getLatitude(),address.getLongitude());
                map.clear();

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(searchedAddress,15);
                map.animateCamera(cameraUpdate);

                MarkerOptions marker = new MarkerOptions().position(searchedAddress).title("Searched address");
                map.addMarker(marker);

                double Lat = address.getLatitude();
                double Longi = address.getLongitude();


                findNearbyLocations(Lat,Longi);

            }
        }
        else{
            Snackbar.make(findViewById(R.id.drawer_layout), "No Network Connection!", Snackbar.LENGTH_LONG).setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            }).show();
        }
    }

    public void findNearbyLocations(double searchedLatitude, double searchedLongitude) {

        String APIKey = "AIzaSyCk6xfq4jFcg6Qlz5Nlhn2iUug8pndfIP8";
        String PlacesURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + searchedLatitude + "," + searchedLongitude + "&radius=1500&type=cafe&key=" + APIKey;



        Log.d("Lat:",""+searchedLatitude);
        Log.d("Log:",""+searchedLongitude);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(PlacesURL).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    Log.d("Failed:", "Failed");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String jasonObject = response.body().string();
                    Log.d("response:", response.body().string());
                    if (response.isSuccessful()) {

                        try {
                            parse(jasonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });



    }

    public void parse(String jsonObject) throws JSONException {
        JSONObject NearbyPlacesObject = new JSONObject(jsonObject);
        JSONArray jPlaces = NearbyPlacesObject.getJSONArray("results");
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
        placesList = getPlaces(jPlaces);
        final List<HashMap<String, String>> finalPlacesList = placesList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                placeMarkers(finalPlacesList);
            }
        });

    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> place = null;

        /** Taking each place, parses and adds to list object */
        for(int i=0; i<placesCount;i++){
            try {
                /** Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }



    private HashMap<String, String> getPlace(JSONObject jPlace){

        HashMap<String, String> place = new HashMap<String, String>();
        String placeName = "";
        String latitude = "";
        String longitude = "";
        String rating = "";

        try {
            // Extracting Place name, if available
            if(!jPlace.isNull("name")){
                placeName = jPlace.getString("name");
            }

            // Extracting Place Vicinity, if available
            if(!jPlace.isNull("rating")){
                rating = jPlace.getString("rating");
            }

            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");

            place.put("place_name", placeName);
            place.put("rating", rating);
            place.put("lat", latitude);
            place.put("lng", longitude);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }

    public void placeMarkers(List<HashMap<String,String>> list) {

        double rat = 0;
        //map.clear();

        for(int i=0;i<list.size();i++){

            Cafe newCafe = new Cafe();

            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> hmPlace = list.get(i);

            double lat = Double.parseDouble(hmPlace.get("lat"));

            double lng = Double.parseDouble(hmPlace.get("lng"));
            Log.d("Latlng:",""+lat+" : "+lng);

            String cfname = hmPlace.get("place_name");

            newCafe.name = cfname;

            if(!hmPlace.get("rating").equals("")) {

                 rat = Double.parseDouble(hmPlace.get("rating"));
            }
            Log.d("Rating:",""+rat);

            newCafe.rating = rat;

            cafeList.add(newCafe);

            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng);

            markerOptions.title(cfname);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            map.addMarker(markerOptions);
        }

        Collections.sort(cafeList, new Comparator<Cafe>() {
            @Override
            public int compare(Cafe c1, Cafe c2) {
                return Double.compare(c2.getRating(),c1.getRating());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            searchView= (android.support.v7.widget.SearchView) menu.findItem(R.id.action_websearch).getActionView();

            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d("DEBUG","Inside OnQueryTextSubmit");
                    //removeMarkers();
                    try {
                        Search(s);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
            searchView.setSubmitButtonEnabled(true);
            searchView.setIconified(true);
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

}
