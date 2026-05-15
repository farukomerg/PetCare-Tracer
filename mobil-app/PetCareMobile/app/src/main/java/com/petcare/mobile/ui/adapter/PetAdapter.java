package com.petcare.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.petcare.mobile.R;
import com.petcare.mobile.model.PetResponse;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private final List<PetResponse> items;

    public PetAdapter(List<PetResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        PetResponse item = items.get(position);
        holder.petName.setText(item.getPetName());
        holder.petMeta.setText(item.getSpecies() + " • " + safe(item.getBreed()));
        holder.petDetails.setText("Cinsiyet: " + safe(item.getGender()) + "   Kilo: " + (item.getCurrentWeight() == null ? "-" : item.getCurrentWeight()));
        holder.petNotes.setText(safe(item.getNotes()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String safe(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        private final TextView petName;
        private final TextView petMeta;
        private final TextView petDetails;
        private final TextView petNotes;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.petNameText);
            petMeta = itemView.findViewById(R.id.petMetaText);
            petDetails = itemView.findViewById(R.id.petDetailsText);
            petNotes = itemView.findViewById(R.id.petNotesText);
        }
    }
}
