
package com.example.truckup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.TruckViewHolder> {

    private Context context;
    private List<Truck> truckList;

    public TruckAdapter(Context context, List<Truck> truckList) {
        this.context = context;
        this.truckList = truckList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public TruckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.truck_item, parent, false);
        return new TruckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TruckViewHolder holder, int position) {
        Truck truck = truckList.get(position);
        holder.textViewTitle.setText(truck.getTitle());
        holder.textViewDescription.setText(truck.getDescription());
        holder.userName.setText(truck.getUsername());
        holder.weight.setText(String.valueOf(truck.getWeight()));
        holder.KgOrTonnes.setText(truck.getUnit());
        holder.Date.setText(truck.getDate());
        holder.UnLoadingDate.setText(truck.getUnloadingDate());
        holder.price.setText(truck.getPrice());


        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the likedPosts node of the current user
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("likedTrucks");

        // Check if the post is in the likedPosts node of the current user
        dbRef.child(truck.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the post is in the likedPosts node of the current user, set the image of the favoriteButton to the filled heart icon
                    holder.favoriteButton.setImageResource(R.drawable.baseline_favorite_24); // Replace with the name of your filled heart icon
                } else {
                    // If the post is not in the likedPosts node of the current user, set the image of the favoriteButton to the empty heart icon
                    holder.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24); // Replace with the name of your empty heart icon
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the post
                Truck truck = truckList.get(holder.getAdapterPosition());

                // Get the current user's ID
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Get a reference to the likedPosts node of the current user
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("likedTrucks");

                // Check if the post is in the likedPosts node of the current user
                dbRef.child(truck.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // If the post is in the likedPosts node of the current user, unlike it
                            // Remove the post from the current user's liked posts node
                            dbRef.child(truck.getId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    truck.setFavorite(false);

                                    // Change the icon of the ImageButton to the empty heart icon
                                    holder.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24); // Replace with the name of your empty heart icon

                                    // Notify the adapter that the item at this position has been changed
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            });
                        } else {
                            // If the post is not in the likedPosts node of the current user, like it
                            // Add the post to the current user's liked posts node
                            dbRef.child(truck.getId()).setValue(truck).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    truck.setFavorite(true);

                                    // Change the icon of the ImageButton to the filled heart icon
                                    holder.favoriteButton.setImageResource(R.drawable.baseline_favorite_24); // Replace with the name of your filled heart icon

                                    // Notify the adapter that the item at this position has been changed
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                    }
                });
            }
        });


        String loadingLocationAddress = truck.getLoadingLocationAddress(context);
        if (loadingLocationAddress != null) {
            holder.loadingLocationn.setText(loadingLocationAddress.substring(0, loadingLocationAddress.length() / 2));
        } else {
            holder.loadingLocationn.setText("Loading Location not set");
        }

        String unloadingLocationAddress = truck.getUnLoadingLocationAddress(context);
        if(unloadingLocationAddress != null) {
            holder.unloadingLocationn.setText(unloadingLocationAddress.substring(0, unloadingLocationAddress.length() / 2));
        } else {
            holder.unloadingLocationn.setText("Unloading Location not set");
        }

        // Download the image from the URL and set it to the ImageView
        Glide.with(context)
                .load(truck.getImageUrl())
                .into(holder.imageView5);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TruckDescription.class);
                intent.putExtra("truck", truck);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int itemCount = truckList.size();
        return itemCount;
    }
    @Override
    public long getItemId(int position) {
        // Assuming that getId() returns a unique ID for each post
        return truckList.get(position).getId().hashCode();
    }

    public class TruckViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, userName,weight,KgOrTonnes,Date,UnLoadingDate,loadingLocationn,unloadingLocationn,price;
        ImageView imageView5;
        ImageButton favoriteButton;
        MaterialCardView cardView;

        public TruckViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.post_title);
            textViewDescription = itemView.findViewById(R.id.post_description);
            imageView5 = itemView.findViewById(R.id.imageView5);
            cardView = itemView.findViewById(R.id.card_view);
            userName = itemView.findViewById(R.id.username);
            weight = itemView.findViewById(R.id.cargo_weight);
            KgOrTonnes = itemView.findViewById(R.id.kg_or_tonnes);
            Date = itemView.findViewById(R.id.date);
            favoriteButton = itemView.findViewById(R.id.favorite);
            UnLoadingDate = itemView.findViewById(R.id.unloading_date);
            loadingLocationn = itemView.findViewById(R.id.textView3);
            unloadingLocationn = itemView.findViewById(R.id.textView4);
            price = itemView.findViewById(R.id.textView7);
        }
    }
}
