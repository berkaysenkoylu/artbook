package com.berkaysenkoylu.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.berkaysenkoylu.artbook.databinding.ActivityAddEditArtBinding;
import com.google.android.material.snackbar.Snackbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.security.Permission;
import java.sql.SQLOutput;

public class AddEditArtActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private ImageView imgShowcase;
    private byte[] imgBlob;
    private Bitmap imgBitmap;
    private int selectedArtId;
    private boolean isEditMode = false;

    ActivityAddEditArtBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditArtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imgShowcase = binding.selectedImageView;

        registerLaunchers();

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("Edit Mode", false);
        if (isEditMode) {
            Art artToEdit = (Art) intent.getSerializableExtra("Art to edit");
            selectedArtId = artToEdit.id;
            imgBlob = artToEdit.imageBlob;
            imgBitmap = BitmapFactory.decodeByteArray(imgBlob, 0, imgBlob.length);

            imgShowcase.setImageBitmap(imgBitmap);
            binding.artNameText.setText(artToEdit.artname);
            binding.painterNameText.setText(artToEdit.painterName);
            binding.dateText.setText(artToEdit.year);
        }
    }

    public void onImagePickerPressed(View view) {
        String permissionStr = "";
        if (Build.VERSION.SDK_INT >= 33) {
            permissionStr = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permissionStr = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permissionStr) != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted by the user yet.
            // Android can decide whether to give the user more info regarding why we need this permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionStr)) {
                // Show the user the info in a snack bar
                Snackbar.make(view, "Permission is required to access gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request the permission
                        String permissionStr = "";
                        if (Build.VERSION.SDK_INT >= 33) {
                            permissionStr = Manifest.permission.READ_MEDIA_IMAGES;
                        } else {
                            permissionStr = Manifest.permission.READ_EXTERNAL_STORAGE;
                        }
                        permissionLauncher.launch(permissionStr);
                    }
                }).show();
            } else {
                // Request the permission
                permissionLauncher.launch(permissionStr);
            }

        } else {
            // Permission is granted. We can access the gallery now.
            goToGallery();
        }
    }

    private void goToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(galleryIntent);
    }

    private void registerLaunchers() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri uri = intentFromResult.getData();

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                                imgBitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            }
                            binding.selectedImageView.setImageBitmap(imgBitmap);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    // Permission granted
                    goToGallery();
                } else {
                    // Permission denied
                    Toast.makeText(AddEditArtActivity.this, "Permission is not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Bitmap getImgBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            // Landscape image
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            // Portrait image
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return image.createScaledBitmap(image, width, height, true);
    }

    public void onSaveButtonPressed(View view) {
        // Check if any field is empty. If so, show an error.
        String artName = binding.artNameText.getText().toString();
        String painterName = binding.painterNameText.getText().toString();
        String date = binding.dateText.getText().toString();

        byte[] byteArray = new byte[] {};

        if (imgBitmap != null) {
            Bitmap smallImage = getImgBitmap(imgBitmap, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byteArray = outputStream.toByteArray();
        }

        if (artName.matches("") || painterName.matches("") || byteArray.length == 0 || date.matches("") ){
            Toast.makeText(this, "You cannot leave any fields empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Extra verification for input fields can be done.
        DBHandler dbHandler = new DBHandler(AddEditArtActivity.this);

        if (!isEditMode) {
            dbHandler.addNewArt(artName, painterName, byteArray, date);
        } else {
            // Edit the existing one
            dbHandler.editArt(selectedArtId, artName, painterName, byteArray, date);
        }

        Intent intent = new Intent(AddEditArtActivity.this, MainActivity.class);
        // Close other activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}