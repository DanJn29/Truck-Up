package com.example.truckup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
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

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, userName,weight,KgOrTonnes;
        ImageView imageView5;
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
        }
    }
}
