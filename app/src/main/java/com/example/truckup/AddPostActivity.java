package com.example.truckup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.imageview.ShapeableImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ShapeableImageView selectImage; // Define selectImage as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.variants_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        selectImage = findViewById(R.id.post_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    public void onImageButtonClick(View view) {
        // Create an Intent to navigate to SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class); // Use this instead of getActivity()
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
            Uri destinationUri = Uri.fromFile(new File(this.getCacheDir(), uniqueFileName)); // Use this.getCacheDir() instead of getContext().getCacheDir()
            // Start UCrop activity with the selected image URI and destination URI
            UCrop.of(selectedImageUri, destinationUri)
                    .withAspectRatio(5, 3)  // Set the aspect ratio as needed
                    .start(this); // Use this instead of getContext()
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // Handle the result after cropping
            final Uri resultUri = UCrop.getOutput(data);

            // Check for null before setting the image
            if (resultUri != null) {
                selectImage.setImageURI(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle the error that occurred during cropping (if needed)
            final Throwable cropError = UCrop.getError(data);
        }
    }
}