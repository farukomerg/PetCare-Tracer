package com.petcare.mobile.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.petcare.mobile.R;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.ui.PetDetailActivity;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private final List<PetResponse> items;
    private final ActivityResultLauncher<Intent> detailLauncher;

    public PetAdapter(List<PetResponse> items, ActivityResultLauncher<Intent> detailLauncher) {
        this.items = items;
        this.detailLauncher = detailLauncher;
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
        holder.petDetails.setText("Cinsiyet: " + safe(item.getGender()) + "   Kilo: "
                + (item.getCurrentWeight() == null ? "-" : item.getCurrentWeight() + " kg"));
        holder.petNotes.setText(safe(item.getNotes()));

        // Karta tıklayınca PetDetailActivity'ye git (launcher ile, silme sonrası listeyi yenilemek için)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PetDetailActivity.class);
            intent.putExtra(PetDetailActivity.EXTRA_PET_ID, item.getPetId());
            detailLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

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
            petName    = itemView.findViewById(R.id.petNameText);
            petMeta    = itemView.findViewById(R.id.petMetaText);
            petDetails = itemView.findViewById(R.id.petDetailsText);
            petNotes   = itemView.findViewById(R.id.petNotesText);
        }
    }
}

