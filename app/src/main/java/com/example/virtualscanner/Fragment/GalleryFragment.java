package com.example.virtualscanner.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.virtualscanner.Adapter.GalleryAdapter;
import com.example.virtualscanner.Model.GalleryModel;
import com.example.virtualscanner.R;
import com.example.virtualscanner.databinding.FragmentGalleryBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryModel model;
    private Activity activity;
    private ArrayList<GalleryModel> galleryArrayList;
    private GalleryAdapter galleryAdapter;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        activity = getActivity();


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getString(R.string.app_name);
        File directory = new File(filePath);
        File[] files = directory.listFiles();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        //   Log.d("Files", "Size: "+ files.length);
        galleryArrayList = new ArrayList<>();
        galleryArrayList.clear();
        binding.galleryRecycler.setLayoutManager(new GridLayoutManager(activity, 2));
        if (files != null && files.length != 0) {
            binding.prevScanText.setVisibility(View.VISIBLE);
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".jpg")) {
                    model = new GalleryModel();
                    // Log.d("Files", "FileName:" + files[i].getName());
                    //generateImageFromPdf(Uri.parse(files[i].getPath()));

                    model.setPath(files[i].getAbsolutePath());
                    model.setName(files[i].getName());
                    Date lastModDate = new Date(files[i].lastModified());
                    String date = format.format(lastModDate);
                    model.setDate(date);
                    //
                    galleryArrayList.add(model);
                }
            }
        }

        galleryAdapter = new GalleryAdapter(activity, galleryArrayList);
        binding.galleryRecycler.setAdapter(galleryAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    /*private void generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(getActivity());
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            ParcelFileDescriptor fd = activity.getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            model.setBitmap(bmp);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //todo with exception
        }
        
    }*/

}