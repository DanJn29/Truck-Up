package com.example.truckup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.truckup.R;

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

        // Populate your views with the post data
        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());
        Glide.with(this)
                .load(post.getImageUrl())
                .into(imageView);
    }
}