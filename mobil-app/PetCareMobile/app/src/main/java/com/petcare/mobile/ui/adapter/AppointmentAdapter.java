package com.petcare.mobile.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.petcare.mobile.R;
import com.petcare.mobile.model.AppointmentResponse;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private final List<AppointmentResponse> items;

    public AppointmentAdapter(List<AppointmentResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentResponse a = items.get(position);
        holder.vetName.setText("Dr. " + safe(a.getVetName()));
        holder.clinic.setText(safe(a.getClinicName()));
        holder.time.setText(formatTime(a.getAppointmentTime()));
        holder.status.setText(safe(a.getStatus()));
        holder.status.setBackgroundColor(statusColor(a.getStatus()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private String safe(String v) { return v == null || v.isEmpty() ? "-" : v; }

    private String formatTime(String dt) {
        if (dt == null) return "-";
        // "2026-06-15T10:00:00" → "15.06.2026  10:00"
        try {
            String[] parts = dt.split("T");
            String[] dateParts = parts[0].split("-");
            String time = parts.length > 1 ? parts[1].substring(0, 5) : "";
            return dateParts[2] + "." + dateParts[1] + "." + dateParts[0] + "  " + time;
        } catch (Exception e) {
            return dt;
        }
    }

    private int statusColor(String status) {
        if (status == null) return Color.parseColor("#78909C");
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> Color.parseColor("#43A047");
            case "CANCELLED" -> Color.parseColor("#E53935");
            default -> Color.parseColor("#FB8C00"); // PLANNED
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView vetName, clinic, time, status;

        ViewHolder(@NonNull View v) {
            super(v);
            vetName = v.findViewById(R.id.apptVetName);
            clinic = v.findViewById(R.id.apptClinic);
            time = v.findViewById(R.id.apptTime);
            status = v.findViewById(R.id.apptStatus);
        }
    }
}
