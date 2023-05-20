package com.berkaysenkoylu.artbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.berkaysenkoylu.artbook.databinding.ActivityAddEditArtBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.SQLOutput;

public class AddEditArtActivity extends AppCompatActivity {

    private static final int GALLERY_IMAGE_REQ_CODE = 102;
    private ImageView imgGallery;
    private String imgUri = "";
    private int selectedArtId;
    private boolean isEditMode = false;

    ActivityAddEditArtBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditArtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imgGallery = binding.selectedImageView;

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("Edit Mode", false);
        if (isEditMode) {
            Art artToEdit = (Art) intent.getSerializableExtra("Art to edit");
            selectedArtId = artToEdit.id;
            imgUri = artToEdit.imageUri;

            imgGallery.setImageURI(Uri.parse(imgUri));
            binding.artNameText.setText(artToEdit.artname);
            binding.painterNameText.setText(artToEdit.painterName);
            binding.dateText.setText(artToEdit.year);
        }
    }

    public void onImagePickerPressed(View view) {
        ImagePicker.with(this)
                // Crop Image(User can choose Aspect Ratio)
                .crop()
                // User can only select image from Gallery
                .galleryOnly()

                .galleryMimeTypes(new String[]{"image/png",
                        "image/jpg",
                        "image/jpeg"
                })
                // Image resolution will be less than 1080 x 1920
                .maxResultSize(1080, 1920)
                // .saveDir(getExternalFilesDir(null))
                .start(GALLERY_IMAGE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            Uri uri = data.getData();
            System.out.println(uri.toString());

            switch (requestCode) {
                case GALLERY_IMAGE_REQ_CODE:
                    imgGallery.setImageURI(uri);
                    imgUri = uri.toString();
                    break;
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveButtonPressed(View view) {
        // Check if any field is empty. If so, show an error.
        String artName = binding.artNameText.getText().toString();
        String painterName = binding.painterNameText.getText().toString();
        String date = binding.dateText.getText().toString();

        if (artName.matches("") || painterName.matches("") || imgUri.matches("") || date.matches("") ){
            Toast.makeText(this, "Hey this is test", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Extra verification for input fields can be done.
        DBHandler dbHandler = new DBHandler(AddEditArtActivity.this);

        if (!isEditMode) {
            dbHandler.addNewArt(artName, painterName, imgUri, date);
        } else {
            // Edit the existing one
            dbHandler.editArt(selectedArtId, artName, painterName, imgUri, date);
        }

        Intent intent = new Intent(AddEditArtActivity.this, MainActivity.class);
        startActivity(intent);
    }
}