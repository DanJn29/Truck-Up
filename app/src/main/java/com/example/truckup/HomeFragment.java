package com.example.truckup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference userPostsRef = usersRef.child(userSnapshot.getKey()).child("posts");
                    userPostsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                try {
                                    Post post = postSnapshot.getValue(Post.class);
                                    if (post != null) {
                                        Log.d("HomeFragment", "Post: " + post.toString());
                                        postList.add(post);
                                    } else {
                                        Log.d("HomeFragment", "Failed to parse post: " + postSnapshot.toString());
                                    }
                                } catch (Exception e) {
                                    Log.e("HomeFragment", "Error parsing post", e);
                                }
                            }
                            postAdapter.notifyDataSetChanged();
                            Log.d("HomeFragment", "Number of posts: " + postList.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Log the error
                            Log.d("HomeFragment", "Failed to read posts: ", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log the error
                Log.d("HomeFragment", "Failed to read users: ", databaseError.toException());
            }
        });

        return view;
    }
}