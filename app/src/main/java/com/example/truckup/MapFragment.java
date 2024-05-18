package com.example.truckup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private MapView map = null;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Configuration.getInstance().setUserAgentValue("com.example.truckup");

        map = (MapView) view.findViewById(R.id.map);
        map.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    onLocationChanged(location);
                }
            }
        };

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            startLocationUpdates();
        }

        return view;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onLocationChanged(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        map.getController().setCenter(startPoint);
        map.getController().zoomTo(18.0);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("My location");
        startMarker.setIcon(getResources().getDrawable(R.drawable.location));
        map.getOverlays().add(startMarker);

        // After adding the marker, re-center the map to the marker's position and zoom in
        map.getController().animateTo(startMarker.getPosition());
        map.getController().zoomTo(18.0);
        addServiceStationMarkers(location);
    }
    private void addServiceStationMarkers(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Overpass API query
        String overpassQuery = "[out:json][timeout:25];" +
                "(" +
                "node[\"amenity\"=\"fuel\"](" +
                (latitude - 0.1) + "," + (longitude - 0.1) + "," +
                (latitude + 0.1) + "," + (longitude + 0.1) +
                ");" +
                ");" +
                "out body;";

        // URL for the Overpass API
        String overpassUrl = "https://overpass-api.de/api/interpreter?data=" + Uri.encode(overpassQuery);

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Create a new JSON request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, overpassUrl, null,
                response -> {
                    try {
                        JSONArray elements = response.getJSONArray("elements");
                        for (int i = 0; i < elements.length(); i++) {
                            JSONObject element = elements.getJSONObject(i);
                            JSONObject tags = element.getJSONObject("tags");
                            String name = tags.has("name") ? tags.getString("name") : "Unnamed";
                            double lat = element.getDouble("lat");
                            double lon = element.getDouble("lon");

                            // Add a marker for the service station
                            GeoPoint point = new GeoPoint(lat, lon);
                            Marker marker = new Marker(map);
                            marker.setPosition(point);
                            marker.setTitle(name);
                            // Set the custom icon
                            marker.setIcon(getResources().getDrawable(R.drawable.baseline_local_gas_station_24));

                            map.getOverlays().add(marker);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Volley", error.toString())
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}