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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

public class DescriptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Post post = (Post) getIntent().getSerializableExtra("post");

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


        String userId = post.getUserId();

        // Get a reference to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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