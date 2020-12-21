package com.example.virtualscanner.Fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.virtualscanner.Utils.Constants;
import com.example.virtualscanner.Utils.CustomeDialogSettings;
import com.example.virtualscanner.Utils.StoreUserData;
import com.example.virtualscanner.databinding.FragmentSettingBinding;


public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private Activity activity;
    private CustomeDialogSettings customDialog;
    private StoreUserData storeUserData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        activity = getActivity();
        customDialog = new CustomeDialogSettings(activity);
        storeUserData = new StoreUserData(activity);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (storeUserData.getBoolean(Constants.SAVETOGALLERY)) {
            binding.saveGallerySwitch.setChecked(true);
        }
        if (!storeUserData.getString(Constants.FILENAME).equals("")) {
            binding.defFilename.setText(storeUserData.getString(Constants.FILENAME));
        }

        //onSaveNameFromDialog
        customDialog.setDialogResult(result -> binding.defFilename.setText(result));

        //onClick
        binding.defNameLayout.setOnClickListener(v -> {
            customDialog.show();
        });
        binding.saveGallerySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            storeUserData.setBoolean(Constants.SAVETOGALLERY, isChecked);
        });

        binding.instaLayout.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://instagram.com/_u/vektiq/");
            Uri uri2 = Uri.parse("http://instagram.com/vektiq/");
            String pkg = "com.instagram.android";
            openIntent(uri, uri2, pkg);
        });

        binding.linkdLayout.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://in.linkedin.com/in/vektiq-designers-8539331b3/");
            Uri uri2 = Uri.parse("https://in.linkedin.com/in/vektiq-designers-8539331b3");
            String pkg = "com.linkedin.android";
            openIntent(uri, uri2, pkg);
        });

        binding.mailLayout.setOnClickListener(v -> {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            /* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"vektiq@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

            /* Send it off to the Activity-Chooser */
            activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        });

    }

    private void openIntent(Uri uri, Uri uri2, String pkg) {

        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage(pkg);

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri2));
        }
    }
}