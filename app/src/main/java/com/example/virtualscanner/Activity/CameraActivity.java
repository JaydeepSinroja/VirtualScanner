package com.example.virtualscanner.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.virtualscanner.Adapter.ImagePreviewAdapter;
import com.example.virtualscanner.R;
import com.example.virtualscanner.Utils.CustomDialog;
import com.example.virtualscanner.Utils.OnSwipeTouchListener;
import com.example.virtualscanner.databinding.ActivityCameraBinding;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.scanlibrary.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private ActivityCameraBinding binding;
    private Activity activity;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    private int focusAreaSize;
    private Matrix matrix;
    private int REQUEST_CODE = 99;
    private int RESULT_LOAD_IMAGE = 199;
    private ArrayList<Bitmap> bitmapArrayList;
    private ArrayList<Bitmap> tempArrayList = new ArrayList<>();
    private ImagePreviewAdapter imagePreviewAdapter;

    private int INDEX = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (!bitmapArrayList.isEmpty() && bitmapArrayList != null) {
           /* tempArrayList.clear();
            tempArrayList.addAll(bitmapArrayList);
            tempArrayList.remove(bitmapArrayList.size()-1);*/
            imagePreviewAdapter = new ImagePreviewAdapter(activity, bitmapArrayList);
            binding.imageRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.imageRecycler.setHasFixedSize(true);
            binding.imageRecycler.setAdapter(imagePreviewAdapter);

            imagePreviewAdapter.setImagePreview((arrayList) -> {

                if (bitmapArrayList.size()==0)
                {
                    binding.cancelButton.setVisibility(View.VISIBLE);
                }

                if (arrayList.size()!=0) {
                    binding.imagePreview.setImageBitmap(arrayList.get(arrayList.size() - 1));
                }
                else
                {
                    bitmapArrayList.clear();
                    binding.imagePreview.setVisibility(View.GONE);
                    binding.addMoreLayout.setVisibility(View.GONE);
                    binding.cameraControlLayout.setVisibility(View.VISIBLE);
                }

            });
        }
        if (bitmapArrayList.size()>0)
        {
            binding.cancelButton.setVisibility(View.GONE);
        }

    }
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        focusAreaSize = getResources().getDimensionPixelSize(R.dimen.camera_focus_area_size);
        matrix = new Matrix();
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bitmapArrayList = new ArrayList<>();
        surfaceHolder = binding.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        cameraFocus();

        //////////////////////////////////////////////////////////////////
        //click_listner/////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        binding.backButtonCamera.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.fab.setOnClickListener(v -> {
            takeImage();
        });

        binding.cancelButton.setOnClickListener(v -> {
            bitmapArrayList.clear();
            binding.imagePreview.setVisibility(View.GONE);
            binding.addMoreLayout.setVisibility(View.GONE);
            binding.cameraControlLayout.setVisibility(View.VISIBLE);
        });
        binding.flashButton.setOnClickListener(v -> {
            //flashOnButton();
            if (flashmode) {
                flashmode = false;
                //binding.flashButton.setBackgroundColor(getColor(R.color.colorAccent));
                binding.flashOnIndicator.setVisibility(View.INVISIBLE);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } else {
                flashmode = true;
                //binding.flashButton.setBackgroundColor(getColor(R.color.colorPrimary));
                binding.flashOnIndicator.setVisibility(View.VISIBLE);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                camera.setParameters(parameters);
            }
        });

        binding.galleryButton.setOnClickListener(v -> {
            /*Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);*/
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
        });

        binding.addFab.setOnClickListener(v -> {
            binding.imagePreview.setVisibility(View.GONE);
            binding.cameraControlLayout.setVisibility(View.VISIBLE);
            binding.addMoreLayout.setVisibility(View.INVISIBLE);
        });

        binding.saveButton.setOnClickListener(v -> {
            CustomDialog customDialog = new CustomDialog(activity, bitmapArrayList);
            customDialog.show();
        });
        
        /////////////////////////////////////////////////////////////
        //swipe_listner//////////////////////////////////////////////
        /////////////////////////////////////////////////////////////

        binding.imagePreview.setOnTouchListener(new OnSwipeTouchListener(activity)
        {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (INDEX<bitmapArrayList.size()-1) {


                    INDEX++;
                    binding.imagePreview.setImageBitmap(bitmapArrayList.get(INDEX));


                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (INDEX>0) {

                    INDEX--;
                    binding.imagePreview.setImageBitmap(bitmapArrayList.get(INDEX));



                }
            }
        });


       
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cameraFocus() {
        binding.surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (camera != null) {
                    //Camera mcamera = camera.getCamera();
                    camera.cancelAutoFocus();
                    Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

                    Camera.Parameters parameters = camera.getParameters();
                    if (parameters.getFocusMode().equals(
                            Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }

                    if (parameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                        mylist.add(new Camera.Area(focusRect, 1000));
                        parameters.setFocusAreas(mylist);
                    }

                    try {
                        camera.cancelAutoFocus();
                        camera.setParameters(parameters);
                        camera.startPreview();
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (!camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                    Camera.Parameters parameters = camera.getParameters();
                                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                    if (parameters.getMaxNumFocusAreas() > 0) {
                                        parameters.setFocusAreas(null);
                                    }
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
      /*  ArrayList<Camera.Area> focusAreaSize = new ArrayList<Camera.Area>(1);
        focusAreaSize.add(new Camera.Area(new Rect(-1000, -1000, 1000, 0), 750));*/
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, binding.surfaceView.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, binding.surfaceView.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            //buttonTakePicture.setEnabled(true);
        }
    };

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);

                //camera.autoFocus(myAutoFocusCallback);
                camera.setErrorCallback(new Camera.ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {
                        //to show the error message.
                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            Toast.makeText(activity, "error to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        parameters.setPictureSize(size.width, size.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        c.setParameters(params);

        // showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
    }

    private void takeImage() {
        camera.takePicture(null, null, new Camera.PictureCallback() {

            private File imageFile;

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    // convert byte array into bitmap
                    Bitmap loadedImage = null;
                    Bitmap rotatedBitmap = null;
                    loadedImage = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    // rotate Image
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                            loadedImage.getWidth(), loadedImage.getHeight(),
                            rotateMatrix, false);
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

                        imageFile.createNewFile();
                    } else {
                        Toast.makeText(getBaseContext(), "Image Not saved", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                    // save image into gallery
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                    FileOutputStream fout = new FileOutputStream(imageFile);
                    fout.write(ostream.toByteArray());
                    fout.close();
                    /*ContentValues values = new ContentValues();

                    values.put(MediaStore.Images.Media.DATE_TAKEN,
                            System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.DATA,
                            imageFile.getAbsolutePath());

                    CameraActivity.this.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*/

                    Uri fileUri = Uri.fromFile(imageFile);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    AssetFileDescriptor fileDescriptor = null;
                    fileDescriptor = getContentResolver().openAssetFileDescriptor(fileUri, "r");
                    Bitmap original
                            = BitmapFactory.decodeFileDescriptor(
                            fileDescriptor.getFileDescriptor(), null, options);
                    Uri uri = Utils.getUri(activity, original);
                    //rotatedBitmap.recycle();
                    imageFile.delete();

                    Intent intent = new Intent(activity, ScanActivity.class);
                    intent.putExtra("selected_bitmap", uri);
                    startActivityForResult(intent, REQUEST_CODE);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
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

                bitmapArrayList.add(bitmap);
                binding.imagePreview.setVisibility(View.VISIBLE);
                binding.imagePreview.setImageBitmap(bitmap);
                INDEX = bitmapArrayList.size()-1;
                binding.cameraControlLayout.setVisibility(View.INVISIBLE);
                binding.addMoreLayout.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Uri uri = Utils.getUri(activity, BitmapFactory.decodeFile(picturePath));

            Intent intent = new Intent(activity, ScanActivity.class);
            intent.putExtra("selected_bitmap", uri);
            startActivityForResult(intent, REQUEST_CODE);
            cursor.close();
            //rotatedBitmap.recycle();

        }
    }
}