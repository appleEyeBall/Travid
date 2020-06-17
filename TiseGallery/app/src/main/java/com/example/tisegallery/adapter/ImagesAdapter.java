package com.example.tisegallery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tisegallery.ImageDataModel;
import com.example.tisegallery.MainActivity;
import com.example.tisegallery.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Holder> {

    ArrayList<ImageDataModel> images;
    Context context;

    public ImagesAdapter(ArrayList<ImageDataModel> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_holder, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ImageDataModel image = images.get(position);

        String url = image.getImgUrl();
        Glide.with(context).load(url).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        if (images == null) return 0;

        return images.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_card);
        }
    }
}
