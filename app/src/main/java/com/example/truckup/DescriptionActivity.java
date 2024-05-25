package com.example.truckup;

import static android.content.ContentValues.TAG;

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

public class DescriptionActivity extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        Post post = (Post) getIntent().getSerializableExtra("post");

        // Get a reference to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String loadingLocation = post.getLoadingLocation();
        String unloadingLocation = post.getUnloadingLocation();


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
         TextView packageType = findViewById(R.id.package_type);
         TextView quantity = findViewById(R.id.textView11);
         TextView beltQuantity = findViewById(R.id.textView13);
         ShapeableImageView profileImage = findViewById(R.id.profile_picture);
         TextView loadingLocationn = findViewById(R.id.loading_location);
         TextView unloadingLocationn = findViewById(R.id.unloading_location);



        // Populate your views with the post data
        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());
        loadingDate.setText(post.getDate());
        unloadingDate.setText(post.getUnloadingDate());
        weight.setText(String.valueOf(post.getWeight()));
        unit.setText(post.getUnit());
        volume.setText(String.valueOf(post.getVolume()));
        packageType.setText(post.getPackageType());
        quantity.setText(String.valueOf(post.getPackageQuantity()));
        beltQuantity.setText(String.valueOf(post.getBeltQuantity()));

        String loadingLocationAddress = post.getLoadingLocationAddress(this);
        if (loadingLocationAddress != null) {
            loadingLocationn.setText(loadingLocationAddress);
        }

        String unloadingLocationAddress = post.getUnLoadingLocationAddress(this);
        if(unloadingLocationAddress != null) {
            unloadingLocationn.setText(unloadingLocationAddress);
        }


        String userId = post.getUserId();


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
                        Glide.with(DescriptionActivity.this)
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
                .load(post.getImageUrl())
                .into(imageView);
    }

}