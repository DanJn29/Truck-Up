package com.example.truckup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class AddPostActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ShapeableImageView selectImage; // Define selectImage as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        Button postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from input fields
                String imageUrl = selectImage.getDrawable().toString(); // Replace this with actual image URL if you have one
                String title = ((EditText) findViewById(R.id.post_title)).getText().toString();
                String description = ((EditText) findViewById(R.id.post_description)).getText().toString();
                double weight = Double.parseDouble(((EditText) findViewById(R.id.cargo_weight)).getText().toString());
                double volume = Double.parseDouble(((EditText) findViewById(R.id.cargo_volume)).getText().toString());
                String packageType = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();
                int packageQuantity = Integer.parseInt(((EditText) findViewById(R.id.package_quantity)).getText().toString());
                int beltQuantity = Integer.parseInt(((EditText) findViewById(R.id.belt_quantity)).getText().toString());

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Create new Post object
                String postId = databaseReference.child("users").child(userId).child("posts").push().getKey();
                Post post = new Post(postId, userId, imageUrl, title, description, beltQuantity, packageQuantity, packageType, volume, weight);
                // Write Post object to Firebase database under the current user's node
                databaseReference.child("users").child(userId).child("posts").child(postId).setValue(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddPostActivity.this, "Post added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddPostActivity.this, "Failed to add post", Toast.LENGTH_SHORT).show();
                            }
                        });


                // Navigate back to MainActivity
                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(intent);


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
                    .withAspectRatio(3, 2)  // Set the aspect ratio as needed
                    .start(this); // Use this instead of getContext()
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // Handle the result after cropping
            final Uri resultUri = UCrop.getOutput(data);

            // Check for null before setting the image
            if (resultUri != null) {
                selectImage.setImageURI(resultUri);
                // Get an instance of FirebaseStorage
                FirebaseStorage storage = FirebaseStorage.getInstance();

// Create a storage reference
                StorageReference storageRef = storage.getReference();

// Create a reference to 'images/{filename}'
                Uri selectedImageUri = resultUri;
                String filename = selectedImageUri.getLastPathSegment();
                StorageReference imageRef = storageRef.child("images/" + filename);

// Upload the file to the path "images/{filename}"
                UploadTask uploadTask = imageRef.putFile(selectedImageUri);

// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(AddPostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // You can also get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Now you can use downloadUri.toString() as the imageUrl for your Post
                                Toast.makeText(AddPostActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle the error that occurred during cropping (if needed)
            final Throwable cropError = UCrop.getError(data);
        }
    }
}