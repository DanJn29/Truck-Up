package com.example.truckup;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;
import com.yalantis.ucrop.UCrop;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;


public class ProfileFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    ShapeableImageView profileImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(myViewPagerAdapter);


        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Posts");
                    break;
                case 1:
                    tab.setText("Trucks");
                    break;
                case 2:
                    tab.setText("Favourites");
                    break;
            }
        }).attach();

        ImageButton settingsImageButton = view.findViewById(R.id.settings);
        settingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageButtonClick(v);
            }
        });

        profileImage = view.findViewById(R.id.profile_picture);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    public void onImageButtonClick(View view) {
        // Create an Intent to navigate to SettingsActivity
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        // Start the new activity
        startActivity(intent);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            // Generate a unique file name for the cropped image
            String uniqueFileName = "croppedImage_" + System.currentTimeMillis() + ".jpg";
            Uri destinationUri = Uri.fromFile(new File(getContext().getCacheDir(), uniqueFileName));

            // Start UCrop activity with the selected image URI and destination URI
            UCrop.of(selectedImageUri, destinationUri)
                    .withAspectRatio(1, 1)  // Set the aspect ratio as needed
                    .start(getContext(), this);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // Handle the result after cropping
            final Uri resultUri = UCrop.getOutput(data);

            // Check for null before setting the image
            if (resultUri != null) {
                profileImage.setImageURI(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle the error that occurred during cropping (if needed)
            final Throwable cropError = UCrop.getError(data);
        }
    }




}