package com.example.nimish.mapviewtrial;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by NIMISH on 23-07-2016.
 */
public class MapViewFragment extends Fragment  {
    MapView mapView;
    GoogleMap map;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_view_layout, container, false);



        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9),15);
        map.animateCamera(cameraUpdate);

        MarkerOptions marker = new MarkerOptions().position(new LatLng(43.1, -87.9)).title("Nimish");
        map.addMarker(marker);*/

        return v;
    }

    public void FindcurrentLocation(LatLng currentLocation){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation,15);
        map.animateCamera(cameraUpdate);

        MarkerOptions marker = new MarkerOptions().position(currentLocation).title("I am here!");
        map.addMarker(marker);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
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
}
