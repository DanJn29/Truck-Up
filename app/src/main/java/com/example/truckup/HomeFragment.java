package com.example.truckup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Get a reference to the likedPosts node of the current user
        DatabaseReference likedPostsRef = FirebaseDatabase.getInstance().getReference("likedPosts").child(currentUserId);

// Get a reference to the posts node
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

// Listen for changes in the posts node
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        // Check if the post is in the likedPosts node of the current user
                        likedPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(post.getId())) {
                                    // If the post is in the likedPosts node of the current user, set isFavorite to true
                                    post.setFavorite(true);
                                } else {
                                    // If the post is not in the likedPosts node of the current user, set isFavorite to false
                                    post.setFavorite(false);
                                }
                                postList.add(post);
                                Collections.reverse(postList); // Reverse the list
                                postAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle possible errors.
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        // Get a reference to the users node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

// Listen for changes in the users node
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Get a reference to the posts node of the current user
                    DatabaseReference postsRef = usersRef.child(userSnapshot.getKey()).child("posts");

                    // Listen for changes in the posts node of the current user
                    postsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Post post = postSnapshot.getValue(Post.class);
                                if (post != null) {
                                    // Check if the post is in the likedPosts node of the current user
                                    likedPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(post.getId())) {
                                                // If the post is in the likedPosts node of the current user, set isFavorite to true
                                                post.setFavorite(true);
                                            } else {
                                                // If the post is not in the likedPosts node of the current user, set isFavorite to false
                                                post.setFavorite(false);
                                            }
                                            postList.add(post);
                                            Collections.reverse(postList); // Reverse the list
                                            postAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle possible errors.
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors.
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        return view;
    }
}