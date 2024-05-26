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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private Map<String, Integer> postPositions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postPositions = new HashMap<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        if (currentUserId != null) {
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("posts");
            DatabaseReference likedPostsRef = FirebaseDatabase.getInstance().getReference("likedPosts").child(currentUserId);

            postsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        updateFavoriteStatus(post, likedPostsRef);
                        postPositions.put(post.getId(), postList.size() - 1);

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        updateFavoriteStatus(post, likedPostsRef);
                        Integer position = postPositions.get(post.getId());
                        if (position != null) {
                            postAdapter.notifyItemChanged(position);
                        }

                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.remove(post);
                        postAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    // Handle post move
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        return view;
    }

    private void updateFavoriteStatus(Post post, DatabaseReference likedPostsRef) {
        likedPostsRef.addValueEventListener(new ValueEventListener() {
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