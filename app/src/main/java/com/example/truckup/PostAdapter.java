package com.example.truckup;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewDescription.setText(post.getDescription());
        holder.weight.setText(String.valueOf(post.getWeight()));
        holder.KgOrTonnes.setText(post.getUnit());
        holder.Date.setText(post.getDate());
        holder.UnLoadingDate.setText(post.getUnloadingDate());
        holder.price.setText(post.getPrice());

        setCountryFlags(holder, post.getLoadingLocationAddress(context), post.getUnLoadingLocationAddress(context));

        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the likedPosts node of the current user
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("likedPosts");

        // Check if the post is in the likedPosts node of the current user
        dbRef.child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Post post = postList.get(holder.getAdapterPosition());

                // Get the current user's ID
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Get a reference to the likedPosts node of the current user
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("likedPosts");

                // Check if the post is in the likedPosts node of the current user
                dbRef.child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // If the post is in the likedPosts node of the current user, unlike it
                            // Remove the post from the current user's liked posts node
                            dbRef.child(post.getId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    post.setFavorite(false);

                                    // Change the icon of the ImageButton to the empty heart icon
                                    holder.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24); // Replace with the name of your empty heart icon

                                    // Notify the adapter that the item at this position has been changed
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            });
                        } else {
                            // If the post is not in the likedPosts node of the current user, like it
                            // Add the post to the current user's liked posts node
                            dbRef.child(post.getId()).setValue(post).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    post.setFavorite(true);

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

        String loadingLocationAddress = post.getLoadingLocationAddress(context);
        if (loadingLocationAddress != null) {
            holder.loadingLocationn.setText(loadingLocationAddress.substring(0, loadingLocationAddress.length() / 2));
        } else {
            holder.loadingLocationn.setText("Loading Location not set");
        }

        String unloadingLocationAddress = post.getUnLoadingLocationAddress(context);
        if(unloadingLocationAddress != null) {
            holder.unloadingLocationn.setText(unloadingLocationAddress.substring(0, unloadingLocationAddress.length() / 2));
        } else {
            holder.unloadingLocationn.setText("Unloading Location not set");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DescriptionActivity.class);
                intent.putExtra("post", post); // Make sure Post class implements Serializable or Parcelable
                v.getContext().startActivity(intent);
            }
        });
    }

    private void setCountryFlags(PostViewHolder holder, String fromLocation, String toLocation) {
        String fromCountryCode = getCountryCodeFromLocation(fromLocation);
        String toCountryCode = getCountryCodeFromLocation(toLocation);

        Log.d("PostAdapter", "From Country Code: " + fromCountryCode);
        Log.d("PostAdapter", "To Country Code: " + toCountryCode);

        String fromFlagPath = "file:///android_asset/flags/" + fromCountryCode + ".png";
        String toFlagPath = "file:///android_asset/flags/" + toCountryCode + ".png";

        Log.d("PostAdapter", "From Flag Path: " + fromFlagPath);
        Log.d("PostAdapter", "To Flag Path: " + toFlagPath);

        AssetManager assetManager = context.getAssets();
        try {
            String[] fromFlagFiles = assetManager.list("flags");
            String[] toFlagFiles = assetManager.list("flags");

            boolean fromFlagExists = false;
            boolean toFlagExists = false;

            if (fromFlagFiles != null) {
                for (String fileName : fromFlagFiles) {
                    if (fileName.equals(fromCountryCode + ".png")) {
                        fromFlagExists = true;
                        break;
                    }
                }
            }

            if (toFlagFiles != null) {
                for (String fileName : toFlagFiles) {
                    if (fileName.equals(toCountryCode + ".png")) {
                        toFlagExists = true;
                        break;
                    }
                }
            }

            if (fromFlagExists) {
                Log.d("PostAdapter", "From Flag File exists");
            } else {
                Log.d("PostAdapter", "From Flag File does not exist");
            }

            if (toFlagExists) {
                Log.d("PostAdapter", "To Flag File exists");
            } else {
                Log.d("PostAdapter", "To Flag File does not exist");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public long getItemId(int position) {
        return postList.get(position).getId().hashCode();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, weight, KgOrTonnes, Date, UnLoadingDate, loadingLocationn, unloadingLocationn, price;
        ImageView fromCountryFlag, toCountryFlag;
        ImageButton favoriteButton;
        MaterialCardView cardView;

        public PostViewHolder(@NonNull View itemView) {
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