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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TrucksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TruckAdapter truckAdapter;
    private ArrayList<Truck> truckList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trucks, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        truckList = new ArrayList<>();
        truckAdapter = new TruckAdapter(getContext(), truckList);
        recyclerView.setAdapter(truckAdapter);

        // Get a reference to the users node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Listen for changes in the users node
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                truckList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Get a reference to the posts node of the current user
                    DatabaseReference trucksRef = usersRef.child(userSnapshot.getKey()).child("trucks");

                    // Listen for changes in the posts node of the current user
                    trucksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<Truck> tempList = new ArrayList<>();
                            for (DataSnapshot truckSnapshot : dataSnapshot.getChildren()) {
                                Truck truck = truckSnapshot.getValue(Truck.class);
                                if (truck != null) {
                                    tempList.add(truck);
                                }
                            }
                            Collections.reverse(tempList);
                            truckList.addAll(tempList);
                            truckAdapter.notifyDataSetChanged();
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