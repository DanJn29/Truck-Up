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
import java.util.Collections;
import java.util.List;

public class FavouritePostsFragment extends Fragment {
    private static final String TAG = "FavouritePostsFragment";

    private RecyclerView recyclerView, recyclerViewTrucks;
    private PostAdapter postAdapter;
    private TruckAdapter truckAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_posts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView); // Replace with your RecyclerView's ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTrucks = view.findViewById(R.id.recyclerViewTrucks);
        recyclerViewTrucks.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavouritePosts();
        loadFavouriteTrucks();


        return view;
    }

    private void loadFavouritePosts() {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the likedPosts node of the current user
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("likedPosts");

        // Listen for changes in the likedPosts node of the current user
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setFavorite(true);
                        posts.add(post);
                    }
                }

                // Update your RecyclerView with the posts
                Collections.reverse(posts);
                postAdapter = new PostAdapter(getContext(), posts);
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error getting documents: ", databaseError.toException());
            }
        });
    }

    private void loadFavouriteTrucks() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("likedTrucks");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Truck> trucks = new ArrayList<>();
                for (DataSnapshot truckSnapshot : dataSnapshot.getChildren()) {
                    Truck truck = truckSnapshot.getValue(Truck.class);
                    if (truck != null) {
                        trucks.add(truck);
                    }
                }

                if (trucks.isEmpty()) {
                    recyclerViewTrucks.setVisibility(View.GONE);
                } else {
                    recyclerViewTrucks.setVisibility(View.VISIBLE);
                    Collections.reverse(trucks);
                    truckAdapter = new TruckAdapter(getContext(), trucks);
                    recyclerViewTrucks.setAdapter(truckAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error getting documents: ", databaseError.toException());
            }
        });
    }

}