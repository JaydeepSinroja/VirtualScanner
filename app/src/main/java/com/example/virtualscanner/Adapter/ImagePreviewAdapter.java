package com.example.virtualscanner.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualscanner.R;

import java.util.ArrayList;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImageViewHolder> {
    private Context context;
    private ArrayList<Bitmap> bitmapArrayList;
    private imagePreviewInterface imagePreviewInterface;
    public static int INDEX;
    public ImagePreviewAdapter(Context context, ArrayList<Bitmap> bitmapArrayList)
    {
        this.context = context;
        this.bitmapArrayList = bitmapArrayList;
        this.INDEX = INDEX;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_preview_list,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        holder.imageView.setImageBitmap(bitmapArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return bitmapArrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageView = itemView.findViewById(R.id.imagePreviewList);
            imageView.setOnClickListener(v -> {
                bitmapArrayList.remove(getAdapterPosition());
                imagePreviewInterface.onClickImage(bitmapArrayList);
                notifyDataSetChanged();
            });
        }
    }

    public void setImagePreview(imagePreviewInterface anInterface)
    {
        imagePreviewInterface = anInterface;
    }

    public interface imagePreviewInterface
    {
        void onClickImage(ArrayList<Bitmap> arrayList);
    }
}
