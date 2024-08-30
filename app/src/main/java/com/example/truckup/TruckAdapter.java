
package com.example.truckup;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

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
        holder.weight.setText(String.valueOf(truck.getWeight()));
        holder.KgOrTonnes.setText(truck.getUnit());
        holder.Date.setText(truck.getDate());
        holder.UnLoadingDate.setText(truck.getUnloadingDate());
        holder.price.setText(truck.getPrice());

        setCountryFlags(holder, truck.getLoadingLocationAddress(context), truck.getUnLoadingLocationAddress(context));



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



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TruckDescription.class);
                intent.putExtra("truck", truck);
                v.getContext().startActivity(intent);
            }
        });
    }

        private void setCountryFlags(TruckAdapter.TruckViewHolder holder, String fromLocation, String toLocation) {
        String fromCountryCode = getCountryCodeFromLocation(fromLocation);
        String toCountryCode = getCountryCodeFromLocation(toLocation);

        downloadFlagImage(fromCountryCode);
        downloadFlagImage(toCountryCode);

        String fromFlagPath = new File(context.getFilesDir(), fromCountryCode + ".png").getAbsolutePath();
        String toFlagPath = new File(context.getFilesDir(), toCountryCode + ".png").getAbsolutePath();

        Glide.with(context)
                .load(fromFlagPath)
                .into(holder.fromCountryFlag);

        Glide.with(context)
                .load(toFlagPath)
                .into(holder.toCountryFlag);
    }

    private String getCountryCodeFromLocation(String location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String countryCode = addresses.get(0).getCountryCode().toLowerCase();
                Log.d("PostAdapter", "Country Code for location " + location + ": " + countryCode);
                return countryCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    private void downloadFlagImage(String countryCode) {
        String flagUrl = "https://flagcdn.com/w2560/" + countryCode + ".png"; // Replace with the actual URL
        String fileName = countryCode + ".png";
        File file = new File(context.getFilesDir(), fileName);

        new Thread(() -> {
            try {
                URL url = new URL(flagUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void deleteTruck(Truck truck, int position) {
        // Use the user ID associated with the post
        String postUserId = truck.getUserId();
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("users")
                .child(postUserId)
                .child("trucks")
                .child(truck.getId());
        postRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                truckList.remove(position);
                notifyItemRemoved(position);
                Log.d("TruckAdapter", "Truck deleted: " + truck.getId());
            } else {
                Log.e("TruckAdapter", "Failed to delete truck: " + truck.getId());
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
        TextView textViewTitle, textViewDescription,weight,KgOrTonnes,Date,UnLoadingDate,loadingLocationn,unloadingLocationn,price;
        ImageView fromCountryFlag, toCountryFlag;
        ImageButton favoriteButton;
        MaterialCardView cardView;

        public TruckViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.post_title);
            textViewDescription = itemView.findViewById(R.id.post_description);
            fromCountryFlag = itemView.findViewById(R.id.from_country_flag);
            toCountryFlag = itemView.findViewById(R.id.to_country_flag);
            cardView = itemView.findViewById(R.id.card_view);
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
