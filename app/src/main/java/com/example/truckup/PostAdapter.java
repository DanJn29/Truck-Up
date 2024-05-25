
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
        holder.userName.setText(post.getUsername());
        holder.weight.setText(String.valueOf(post.getWeight()));
        holder.KgOrTonnes.setText(post.getUnit());
        holder.Date.setText(post.getDate());
        holder.UnLoadingDate.setText(post.getUnloadingDate());


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

        // Download the image from the URL and set it to the ImageView
        Glide.with(context)
                .load(post.getImageUrl())
                .into(holder.imageView5);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DescriptionActivity.class);
                intent.putExtra("post", post); // Make sure Post class implements Serializable or Parcelable
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int itemCount = postList.size();
        return itemCount;
    }
    @Override
    public long getItemId(int position) {
        // Assuming that getId() returns a unique ID for each post
        return postList.get(position).getId().hashCode();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, userName,weight,KgOrTonnes,Date,UnLoadingDate;
        ImageView imageView5;
        ImageButton favoriteButton;
        MaterialCardView cardView;

        public PostViewHolder(@NonNull View itemView) {
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
        }
    }
}
