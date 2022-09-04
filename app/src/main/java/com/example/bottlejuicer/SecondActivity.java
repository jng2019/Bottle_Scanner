package com.example.bottlejuicer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class SecondActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_url;

    private ImageView image;

    private String analyze = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        image = findViewById(R.id.imageView);

        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        showImageImportDialog();
    }

    private void showImageImportDialog() {
        //items to display
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //set Title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    //camera option clicked
                    if (!checkCameraPermission()) {
                        //camera permission not allowed, request it
                        requestCameraPermision();
                    } else {
                        //permission allowed
                        pickCamera();
                    }
                }
                if (which == 1) {
                    //gallery option clicked
                    if (!checkStoragePermission()) {
                        //camera permission not allowed, request it
                        requestStoragePermision();
                    } else {
                        //permission allowed
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show(); // show dialog
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_url);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private void requestStoragePermision() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean resultl = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return resultl;
    }

    private void requestCameraPermision() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultl = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultl;
    }

    //handle result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_GALLERY_CODE)
        {
            image.setImageURI(data.getData());

            BitmapDrawable bitmapDrawable = (BitmapDrawable)image.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            image.setVisibility(View.INVISIBLE);
            TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            if (!recognizer.isOperational()) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = recognizer.detect(frame);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < items.size(); i++)
                {
                    TextBlock myItem = items.valueAt(i);
                    sb.append(myItem.getValue());
                    sb.append(" ");
                }
                analyze.concat(sb.toString());

                Intent intent = new Intent(SecondActivity.this,MainActivity2.class);
                startActivity(intent);

            }
        }
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CAMERA_CODE)
        {
            image.setImageURI(image_url);

            BitmapDrawable bitmapDrawable = (BitmapDrawable)image.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            image.setVisibility(View.INVISIBLE);

            TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            if (!recognizer.isOperational()) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = recognizer.detect(frame);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < items.size(); i++)
                {
                    TextBlock myItem = items.valueAt(i);
                    sb.append(myItem.getValue());
                    sb.append(" ");
                }
                analyze.concat(sb.toString());


                Intent intent = new Intent(SecondActivity.this,MainActivity2.class);
                startActivity(intent);
            }
        }
    }
}