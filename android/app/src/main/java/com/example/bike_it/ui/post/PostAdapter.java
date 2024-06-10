package com.example.bike_it.ui.post;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bike_it.MainActivity;
import com.example.bike_it.ui.post.Post;
import com.example.bike_it.R;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postId = post.getId();
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewText.setText(post.getText());
        holder.textViewDate.setText(post.getDate());
        holder.textViewAuthor.setText(post.getAuthor());

        if(post.getImage() != null)
        {
            holder.postImageView.setImageBitmap(post.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int postId;
        Context context;
        TextView textViewTitle;
        TextView textViewText;
        TextView textViewDate;
        TextView textViewAuthor;

        ImageView postImageView;

        public PostViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewText = itemView.findViewById(R.id.textViewText);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            postImageView = itemView.findViewById(R.id.postImageView);

            LinearLayout postLayout = (LinearLayout)itemView.findViewById(R.id.postLayout);
            float x = postLayout.getX();
            float y = postLayout.getY();
            float width = postLayout.getWidth();
            float height = postLayout.getHeight();

            Button redirectButton = (Button)itemView.findViewById(R.id.postRedirectButton);
            // redirectButton.setVisibility(View.INVISIBLE);

            // redirectButton.setX(x);
            // redirectButton.setY(y);
            // redirectButton.setWidth((int)width);
            redirectButton.setHeight((int)height);


            redirectButton.setOnClickListener(this);
        }

        public void onClick(View view)
        {
            Intent i = new Intent(context, PostActivity.class);
            i.putExtra("post_id", postId);
            context.startActivity(i);
        }
    }
}
