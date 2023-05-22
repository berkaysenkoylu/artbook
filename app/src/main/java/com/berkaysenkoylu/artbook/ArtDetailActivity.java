package com.berkaysenkoylu.artbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.berkaysenkoylu.artbook.databinding.ActivityArtDetailBinding;

public class ArtDetailActivity extends AppCompatActivity {

    ActivityArtDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar.getRoot();
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String artName = intent.getStringExtra("Art Name");
        DBHandler dbHandler = new DBHandler(ArtDetailActivity.this);
        Art art = dbHandler.getOneArtWithName(artName);

        byte[] imageBlob = art.imageBlob;
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

        binding.imageView.setImageBitmap(imageBitmap);
        binding.artNameText.setText(art.artname);
        binding.painterNameText.setText(art.painterName);
        binding.yearText.setText(art.year);
    }
}