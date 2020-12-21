package com.example.virtualscanner.Adapter;

import android.app.Activity;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.virtualscanner.Model.GalleryModel;
import com.example.virtualscanner.R;
import com.example.virtualscanner.databinding.PreviousScannedListItemBinding;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.galleryViewHolder> {

    private Activity activity;
    private ArrayList<GalleryModel> galleryArrayList;

    public GalleryAdapter(Activity activity, ArrayList<GalleryModel> galleryArrayList) {
        this.activity = activity;
        this.galleryArrayList = galleryArrayList;
    }

    @NonNull
    @Override
    public galleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_scanned_list_item, parent, false);
        return new galleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull galleryViewHolder holder, int position) {
        String fromPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getString(R.string.app_name);

        Glide.with(activity).asBitmap().load(galleryArrayList.get(position).getPath()).placeholder(R.drawable.cam_logo_white).into(holder.binding.imgPreview);
        //holder.binding.imgPreview.setImageBitmap(galleryArrayList.get(position).getBitmap());
        holder.binding.txtFileDate.setText(galleryArrayList.get(position).getDate());
        holder.binding.txtFileName.setText(galleryArrayList.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return galleryArrayList.size();
    }

    public class galleryViewHolder extends RecyclerView.ViewHolder {

        private PreviousScannedListItemBinding binding;

        public galleryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PreviousScannedListItemBinding.bind(itemView);

        }
    }
}
