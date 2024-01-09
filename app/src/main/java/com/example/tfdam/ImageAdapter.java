package com.example.tfdam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfdam.model.Image;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Context ctx;
    List<Image> data = new ArrayList<>();
    //private Handler handler;

    public ImageAdapter (Context ctx, List<Image> data){
        this.ctx = ctx;
        this.data.addAll(data);
        /*this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                notifyDataSetChanged();
            }
        };*/

        //new Thread(new ImagePreloadTask()).start();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout (Giving a look to our rows)
        View view = LayoutInflater.from(ctx).inflate(R.layout.image_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // assigning values to the views we created in the recycler_view_row layout file based on the position of the recycler view
        holder.imageView.setImageBitmap(data.get(position).getBitmap());
        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        // recycler view wants to know the number of items you want displayed
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_image);
            imageView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Toast.makeText((HomeActivity)ctx,"Clicked on position: " + position + " with id: " + data.get(position).getId(), Toast.LENGTH_SHORT).show();
                ((Activity)ctx).runOnUiThread(() -> {
                    Intent intent = new Intent(ctx, ImageInfoActivity.class);
                    intent.putExtra("imgId", data.get(position).getId());
                    (ctx).startActivity(intent);
                });
            });
        }
    }
}
