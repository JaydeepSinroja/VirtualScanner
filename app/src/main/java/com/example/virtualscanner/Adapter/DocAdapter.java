package com.example.virtualscanner.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualscanner.Model.GalleryModel;
import com.example.virtualscanner.R;
import com.example.virtualscanner.databinding.DocRecyclerListBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.docViewHolder> {

    private Activity activity;
    private ArrayList<GalleryModel> docArrayList;
    private boolean menuItem;

    public DocAdapter(Activity activity, ArrayList<GalleryModel> docArrayList, boolean menuItem) {
        this.activity = activity;
        this.docArrayList = docArrayList;
        this.menuItem = menuItem;
    }


    @NonNull
    @Override
    public docViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_recycler_list, parent, false);
        return new docViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull docViewHolder holder, int position) {

        holder.binding.fileNameText.setText(docArrayList.get(position).getName());
        holder.binding.fileDateText.setText(docArrayList.get(position).getDate());
        holder.binding.docMainLayout.setOnClickListener(v -> {
            Intent intent;
            String mFilePath = "";
            if (menuItem) {
                mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getString(R.string.app_name) + File.separator + docArrayList.get(position).getName();

            } else {
                mFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + ".virtual scanner" + File.separator + docArrayList.get(position).getName();

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File file = new File(mFilePath);
                Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mFilePath), "application/pdf");
                intent = Intent.createChooser(intent, "Open File");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return docArrayList.size();
    }

    private void moveToLocker(String fromPath, String toPath, String fileName, int pos) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(toPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            long createdDate = new File(fromPath + File.separator + fileName).lastModified();

            in = new FileInputStream(fromPath + File.separator + fileName);
            out = new FileOutputStream(toPath + File.separator + fileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            File file = new File(toPath + File.separator + fileName);
            file.setLastModified(createdDate);
            docArrayList.remove(pos);
            // delete the original file
            new File(fromPath + File.separator + fileName).delete();
            notifyDataSetChanged();

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public class docViewHolder extends RecyclerView.ViewHolder {
        private DocRecyclerListBinding binding;

        public docViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DocRecyclerListBinding.bind(itemView);
            binding.moreOption.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(activity, binding.moreOption);
                popupMenu.inflate(R.menu.document_menu);
                if (menuItem) {
                    popupMenu.getMenu().removeItem(R.id.remove);
                } else {
                    popupMenu.getMenu().removeItem(R.id.move);
                }
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.move:
                            String fromPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator +
                                    activity.getString(R.string.app_name);
                            String toPath = Environment.getExternalStorageDirectory() + File.separator + ".virtual scanner";
                            moveToLocker(fromPath, toPath, docArrayList.get(getAdapterPosition()).getName(), getAdapterPosition());
                            return true;

                        case R.id.remove:
                            String toPath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator +
                                    activity.getString(R.string.app_name);
                            String fromPath2 = Environment.getExternalStorageDirectory() + File.separator + ".virtual scanner";
                            moveToLocker(fromPath2, toPath2, docArrayList.get(getAdapterPosition()).getName(), getAdapterPosition());
                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.show();
            });
        }
    }
}
