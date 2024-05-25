package com.example.truckup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Trucks_postsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TruckAdapter truckAdapter;
    private List<Truck> truckList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trucks_posts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        truckList = new ArrayList<>();
        truckAdapter = new TruckAdapter(getContext(), truckList);
        recyclerView.setAdapter(truckAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        if (currentUserId != null) {
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("trucks");
            DatabaseReference likedTrucksRef = FirebaseDatabase.getInstance().getReference("likedTrucks").child(currentUserId);

            postsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    truckList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Truck truck = postSnapshot.getValue(Truck.class);
                        if (truck != null) {
                            // Check if the post is in the likedPosts node of the current user
                            likedTrucksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(truck.getId())) {
                                        // If the post is in the likedPosts node of the current user, set isFavorite to true
                                        truck.setFavorite(true);
                                    } else {
                                        // If the post is not in the likedPosts node of the current user, set isFavorite to false
                                        truck.setFavorite(false);
                                    }
                                    truckList.add(truck);
                                    Collections.reverse(truckList); // Reverse the list
                                    truckAdapter.notifyDataSetChanged();
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
                    // Handle error
                }
            });
        }

        return view;
    }
}