package com.example.virtualscanner.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualscanner.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class CustomDialog extends Dialog implements View.OnClickListener {
    TextView save, cancel;
    EditText editText;
    private Activity activity;
    private PdfDocument document;
    private ArrayList<Bitmap> bitmapArrayList;
    private StoreUserData storeUserData;
    private FullScreenAdmanager fullScreenAdmanager;
    private Bitmap appLogo;

    public CustomDialog(Activity activity, ArrayList<Bitmap> bitmapArrayList) {
        super(activity);
        this.activity = activity;
        this.bitmapArrayList = bitmapArrayList;

    }

    private Bitmap getBitmap(int drwable) {
        Drawable drawable = activity.getResources().getDrawable(drwable);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(300, 400, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(appLogo);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_diolouge);
        document = new PdfDocument();
        storeUserData = new StoreUserData(activity);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fullScreenAdmanager = new FullScreenAdmanager(activity);
        save = findViewById(R.id.saveButtonD);
        cancel = findViewById(R.id.cancelButtonD);
        save = findViewById(R.id.saveButtonD);
        editText = findViewById(R.id.fileNameEditTxt);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

        appLogo = getBitmap(R.drawable.main_logo_black);


        if (!storeUserData.getString(Constants.FILENAME).equals("")) {
            editText.setText(storeUserData.getString(Constants.FILENAME));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancelButtonD:
                dismiss();
                break;
            case R.id.saveButtonD:

                fullScreenAdmanager.adMobFullScreen(new OnCompleteAdListener() {
                    @Override
                    public void onAdFinished() {
                        try {
                            saveImages();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                break;
        }


    }

    private void saveImages() throws IOException {

        /*  Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);*/
        String file_name = editText.getText().toString().trim();
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getResources().getString(R.string.app_name);
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String filePath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + activity.getResources().getString(R.string.app_name) + File.separator + file_name + ".pdf";
        //String filePath = audioDirectory + File.separator + name + "_" + id + ".obb";
        File file = new File(filePath2);
        if (!file.exists()) {
            for (int i = 0; i < bitmapArrayList.size(); i++) {
                File imageFile = null;
                Bitmap bitmap = null;
                if (storeUserData.getBoolean(Constants.SAVETOGALLERY)) {
                    try {
                        bitmap = bitmapArrayList.get(i);

                        int f = i + 1;
                        java.util.Date date = new java.util.Date();
                        imageFile = new File(folder.getAbsolutePath()
                                + File.separator
                                + file_name + "_"
                                + f + ".jpg");
                        //Toast.makeText(activity.getBaseContext(), "Your image saved", Toast.LENGTH_SHORT).show();
                        imageFile.createNewFile();
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

                bitmap = bitmapArrayList.get(i);
                int f = i + 1;
              /*  int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                Bitmap result = Bitmap.createBitmap(w, h, bitmap.getConfig());

                Canvas canvas1 = new Canvas(result);
                canvas1.drawBitmap(bitmap, 0, 0, null);*/
                Paint paint2 = new Paint();
                paint2.setAntiAlias(true);
                paint2.setFilterBitmap(true);
                paint2.setDither(true);

                // create a page description
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), f).create();
                // start a page
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                canvas.drawBitmap(bitmap, 0f, 0f, null);
                Paint paint = new Paint();
                paint.setColor(activity.getResources().getColor(R.color.colorPrimary));
                //paint.setAlpha(alpha);
                paint.setTextSize(50);
                paint.setAntiAlias(true);
                // paint.setUnderlineText(underline);
                canvas.drawBitmap(appLogo, 0, 0, paint2);
                canvas.drawText("Scanned by Virtual Scanner", 40, bitmap.getHeight() - 40, paint);
                document.finishPage(page);

            }
            document.writeTo(new FileOutputStream(filePath + File.separator + file_name + ".pdf"));
            document.close();
            activity.finish();

        } else {
            Toast.makeText(activity.getBaseContext(), "This name is occupied, please use different one.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
