package com.example.virtualscanner.Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.virtualscanner.Adapter.DocAdapter;
import com.example.virtualscanner.Adapter.GalleryAdapter;
import com.example.virtualscanner.Model.GalleryModel;
import com.example.virtualscanner.R;
import com.example.virtualscanner.databinding.FragmentDocumentBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DocumentFragment extends Fragment {


    public DocumentFragment() {
        // Required empty public constructor
    }

    private FragmentDocumentBinding binding;
    private ArrayList<GalleryModel> docArrayList;
    private Activity activity;
    private GalleryModel model;
    private DocAdapter docAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDocumentBinding.inflate(inflater,container,false);
        activity = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        docArrayList = new ArrayList<>();
        docArrayList.clear();
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getString(R.string.app_name);
        File directory = new File(filePath);
        File[] files = directory.listFiles();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        binding.docRecycler.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        if (  files!=null && files.length!=0 ) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".pdf")) {
                    model = new GalleryModel();

                    Date lastModDate = new Date(files[i].lastModified());
                    String date = format.format(lastModDate);
                    model.setDate(date);
                    model.setName(files[i].getName());
                    docArrayList.add(model);
                }
            }
        }

        docAdapter = new DocAdapter(activity, docArrayList,true);
        binding.docRecycler.setAdapter(docAdapter);
    }
}