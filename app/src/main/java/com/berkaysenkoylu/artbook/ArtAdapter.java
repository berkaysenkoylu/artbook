package com.berkaysenkoylu.artbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berkaysenkoylu.artbook.databinding.ArtRowBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    ArrayList<Art> artArrayList;

    public ArtAdapter(ArrayList<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtRowBinding artRowBinding = ArtRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArtHolder(artRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
        holder.artRowBinding.artNameView.setText(this.artArrayList.get(position).artname);

        holder.artRowBinding.artNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ArtDetailActivity.class);
                intent.putExtra("Art Name", artArrayList.get(position).artname);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.artRowBinding.artEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), AddEditArtActivity.class);
                intent.putExtra("Edit Mode", true);
                intent.putExtra("Art to edit", artArrayList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.artRowBinding.artDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int artToDelete = artArrayList.get(position).id;

                DBHandler dbHandler = new DBHandler(holder.itemView.getContext());
                int itemDeleted = dbHandler.deleteArtWithId(artToDelete);

                if (itemDeleted != 0) {
                    artArrayList.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.artArrayList.size();
    }

    class ArtHolder extends RecyclerView.ViewHolder {
        ArtRowBinding artRowBinding;

        public ArtHolder(ArtRowBinding binding) {
            super(binding.getRoot());
            this.artRowBinding = binding;
        }
    }
}
