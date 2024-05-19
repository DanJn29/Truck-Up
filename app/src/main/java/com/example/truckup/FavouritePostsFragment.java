package com.example.truckup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritePostsFragment extends Fragment {
    private static final String TAG = "FavouritePostsFragment";

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_posts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView); // Replace with your RecyclerView's ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavouritePosts();

        return view;
    }

    private void loadFavouritePosts() {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get the current user's liked posts
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("likedPosts").child(currentUserId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                }

                // Update your RecyclerView with the posts
                postAdapter = new PostAdapter(getContext(), posts);
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error getting documents: ", databaseError.toException());
            }
        });
    }
}