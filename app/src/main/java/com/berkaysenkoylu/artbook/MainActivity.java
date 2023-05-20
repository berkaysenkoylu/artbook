package com.berkaysenkoylu.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.berkaysenkoylu.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    // TODO: This is for testing purposes. This is to be removed later.
    ArrayList<Art> artArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.toolbar.getRoot();
        setSupportActionBar(toolbar);

        // Initialize the DB
        DBHandler dbHandler = new DBHandler(MainActivity.this);
        // dbHandler.addNewArt("Mona Lisa", "Leonardo da Vinci", "file:///storage/emulated/0/Android/data/com.berkaysenkoylu.artbook/files/DCIM/IMG_20230519_180350084.jpg", "2003");

        artArrayList = new ArrayList<Art>();
        artArrayList = dbHandler.getArtList();

        binding.artList.setLayoutManager(new LinearLayoutManager(this));
        ArtAdapter artAdapter = new ArtAdapter(artArrayList);
        binding.artList.setAdapter(artAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_right_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(MainActivity.this, AddEditArtActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}