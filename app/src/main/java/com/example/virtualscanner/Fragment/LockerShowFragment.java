package com.example.virtualscanner.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.virtualscanner.Adapter.DocAdapter;
import com.example.virtualscanner.Model.GalleryModel;
import com.example.virtualscanner.R;
import com.example.virtualscanner.databinding.FragmentLockerShowBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class LockerShowFragment extends Fragment {
    

    public LockerShowFragment() {
        // Required empty public constructor
    }
    
    private FragmentLockerShowBinding binding;
    private ArrayList<GalleryModel> lockerArrayList;
    private Activity activity;
    private GalleryModel model;
    private DocAdapter docAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLockerShowBinding.inflate(inflater,container,false);
        activity = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lockerArrayList = new ArrayList<>();
        lockerArrayList.clear();
        String filePath = Environment.getExternalStorageDirectory() + File.separator + ".virtual scanner";
        File directory = new File(filePath);
        if (!directory.exists())
        {
            directory.mkdir();
        }
        File[] files = directory.listFiles();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        binding.lockerRecycler.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        if (  files!=null && files.length!=0 ) {
            for (int i = 0; i < files.length; i++) {
                model = new GalleryModel();

                Date lastModDate = new Date(files[i].lastModified());
                String date = format.format(lastModDate);
                model.setDate(date);
                model.setName(files[i].getName());
                lockerArrayList.add(model);
            }
        }

        docAdapter = new DocAdapter(activity, lockerArrayList,false);
        binding.lockerRecycler.setAdapter(docAdapter);

    }
}