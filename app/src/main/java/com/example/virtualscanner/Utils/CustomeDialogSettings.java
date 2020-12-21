package com.example.virtualscanner.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.virtualscanner.R;

public class CustomeDialogSettings extends Dialog implements View.OnClickListener {
    TextView save, cancel;
    EditText editText;
    private Activity activity;
    private StoreUserData storeUserData;
    private OnMyDialogResult onMyDialogResult;

    public CustomeDialogSettings(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_diolouge);
        storeUserData = new StoreUserData(activity);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        save = findViewById(R.id.saveButtonD);
        cancel = findViewById(R.id.cancelButtonD);
        save = findViewById(R.id.saveButtonD);
        editText = findViewById(R.id.fileNameEditTxt);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
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
                saveFileName();
                dismiss();
                break;
        }
    }

    private void saveFileName() {

        String fileName = editText.getText().toString().trim();
        storeUserData.setString(Constants.FILENAME, fileName);
        onMyDialogResult.finish(fileName);

    }


    public void setDialogResult(OnMyDialogResult dialogResult) {
        onMyDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(String result);
    }

}
