package com.example.virtualscanner.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.virtualscanner.Fragment.DocumentFragment;
import com.example.virtualscanner.Fragment.GalleryFragment;
import com.example.virtualscanner.Fragment.LokerFragment;
import com.example.virtualscanner.Fragment.SettingFragment;
import com.example.virtualscanner.R;
import com.example.virtualscanner.Utils.StoreUserData;
import com.example.virtualscanner.databinding.ActivityMainBinding;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 10;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 20;
    public static boolean docFragOpen = false;
    int REQUEST_CODE = 99;
    private ActivityMainBinding binding;
    private Activity activity;
    private StoreUserData storeUserData;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        storeUserData = new StoreUserData(activity);
        GalleryFragment galleryFragment = new GalleryFragment();
        switchToFragment(galleryFragment);
        binding.bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Galley:
                    GalleryFragment fragmentA = new GalleryFragment();
                    switchToFragment(fragmentA);
                    return true;
                case R.id.Documents:
                    docFragOpen = true;
                    DocumentFragment documentFragment = new DocumentFragment();
                    switchToFragment(documentFragment);
                    return true;
                case R.id.Loker:
                    docFragOpen = false;
                    LokerFragment lokerFragment = new LokerFragment();
                    switchToFragment(lokerFragment);
                    return true;
                case R.id.Settings:
                    SettingFragment settingFragment = new SettingFragment();
                    switchToFragment(settingFragment);
                    return true;
                default:
                    GalleryFragment galleryFragment1 = new GalleryFragment();
                    switchToFragment(galleryFragment1);
                    return true;
            }
        });

        binding.fab.setOnClickListener(view -> {

            if (checkSelfPermission(Manifest.permission.CAMERA) +
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) +
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                startActivity(new Intent(this, CameraActivity.class));
                   /* int preference = ScanConstants.OPEN_CAMERA;
                    Intent intent = new Intent(this, ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                    startActivityForResult(intent, REQUEST_CODE);*/
            }

           /* int preference = ScanConstants.OPEN_CAMERA;
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, REQUEST_CODE);*/

        });
    }

    public void switchToFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.layout_container, fragment).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalFile = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeExternalFile) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    startActivity(new Intent(this, CameraActivity.class));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "Please grant camera permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            File imageFile;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                //scannedImageView.setImageBitmap(bitmap);
                String state = Environment.getExternalStorageState();
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getResources().getString(R.string.app_name);
                File folder = new File(filePath);
                  /*  if (state.contains(Environment.MEDIA_MOUNTED)) {
                        folder = new File(filePath);
                    } else {
                        folder = new File(filePath);
                    }*/

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    java.util.Date date = new java.util.Date();
                    imageFile = new File(folder.getAbsolutePath()
                            + File.separator
                            + new Timestamp(date.getTime()).toString()
                            + "Image.jpg");
                    Toast.makeText(getBaseContext(), "Your image saved", Toast.LENGTH_SHORT).show();
                    imageFile.createNewFile();
                } else {
                    Toast.makeText(getBaseContext(), "Image Not saved", Toast.LENGTH_SHORT).show();
                    return;
                }

                ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                // save image into gallery
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                FileOutputStream fout = new FileOutputStream(imageFile);
                fout.write(ostream.toByteArray());
                fout.close();
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.DATE_TAKEN,
                        System.currentTimeMillis());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA,
                        imageFile.getAbsolutePath());

                activity.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

}
