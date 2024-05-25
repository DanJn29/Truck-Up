package com.example.truckup;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

public class TruckDescription extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_description);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        Truck truck = (Truck) getIntent().getSerializableExtra("truck");

        // Get a reference to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String loadingLocation = truck.getLoadingLocation();
        String unloadingLocation = truck.getUnloadingLocation();


        // Split the locations into latitude and longitude
        String[] loadingCoordinates = loadingLocation.split(",");
        double loadingLatitude = Double.parseDouble(loadingCoordinates[0]);
        double loadingLongitude = Double.parseDouble(loadingCoordinates[1]);

        String[] unloadingCoordinates = unloadingLocation.split(",");
        double unloadingLatitude = Double.parseDouble(unloadingCoordinates[0]);
        double unloadingLongitude = Double.parseDouble(unloadingCoordinates[1]);

        // Create GeoPoint objects for the loading and unloading locations
        GeoPoint loadingPoint = new GeoPoint(loadingLatitude, loadingLongitude);
        GeoPoint unloadingPoint = new GeoPoint(unloadingLatitude, unloadingLongitude);


        // Add markers at the loading and unloading locations
        Marker loadingMarker = new Marker(map);
        loadingMarker.setPosition(loadingPoint);
        loadingMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(loadingMarker);

        Marker unloadingMarker = new Marker(map);
        unloadingMarker.setPosition(unloadingPoint);
        unloadingMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(unloadingMarker);

        // Draw a route between the loading and unloading locations
        Polyline line = new Polyline();
        line.addPoint(loadingPoint);
        line.addPoint(unloadingPoint);
        line.setColor(Color.parseColor("#48D532")); // Set the color of the line to a custom color
        map.getOverlays().add(line);


        // Zoom in to the loading location
        map.getController().setZoom(10.0);
        map.getController().setCenter(loadingPoint);


        // Find your views
        TextView textViewTitle = findViewById(R.id.title);
        TextView textViewDescription = findViewById(R.id.description);
        ImageView imageView = findViewById(R.id.imageView);
        TextView loadingDate = findViewById(R.id.loading_date);
        TextView unloadingDate = findViewById(R.id.unloading_date);
        TextView weight = findViewById(R.id.cargo_weight);
        TextView unit = findViewById(R.id.unit);
        TextView volume = findViewById(R.id.textView5);
        TextView bodyType = findViewById(R.id.package_type);
        ShapeableImageView profileImage = findViewById(R.id.profile_picture);
        TextView loadingLocationn = findViewById(R.id.loading_location);
        TextView unloadingLocationn = findViewById(R.id.unloading_location);



        // Populate your views with the post data
        textViewTitle.setText(truck.getTitle());
        textViewDescription.setText(truck.getDescription());
        loadingDate.setText(truck.getDate());
        unloadingDate.setText(truck.getUnloadingDate());
        weight.setText(String.valueOf(truck.getWeight()));
        unit.setText(truck.getUnit());
        volume.setText(String.valueOf(truck.getVolume()));
        bodyType.setText(truck.getBodyType());

        String loadingLocationAddress = truck.getLoadingLocationAddress(this);
        if (loadingLocationAddress != null) {
            loadingLocationn.setText(loadingLocationAddress);
        }

        String unloadingLocationAddress = truck.getUnLoadingLocationAddress(this);
        if(unloadingLocationAddress != null) {
            unloadingLocationn.setText(unloadingLocationAddress);
        }


        String userId = truck.getUserId();


        // Retrieve the user's information from the database
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    // Get the profile image URL from the User object
                    String profileImageUrl = user.getProfileImageUrl();
                    TextView username = findViewById(R.id.textView14);
                    username.setText(user.getUsername());
                    TextView phoneNumber = findViewById(R.id.textView9);
                    phoneNumber.setText(user.getPhoneNumber());

                    // Download and display the profile image from the URL
                    if (profileImageUrl != null) {
                        Glide.with(TruckDescription.this)
                                .load(profileImageUrl)
                                .into(profileImage);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });



        Glide.with(this)
                .load(truck.getImageUrl())
                .into(imageView);
    }

}