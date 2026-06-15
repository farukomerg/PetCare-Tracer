package com.petcare.mobile.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.AppointmentResponse;
import com.petcare.mobile.model.CreateReminderRequest;
import com.petcare.mobile.network.ApiClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Veteriner Dashboard'u için randevu adapter.
 * Her kart: "Tamamlandı" ve "Hatırlatma Gönder" butonları içerir.
 */
public class VetAppointmentAdapter extends RecyclerView.Adapter<VetAppointmentAdapter.VH> {

    private final List<AppointmentResponse> items;
    private final OnStatusChangedListener listener;

    public interface OnStatusChangedListener {
        void onChanged();
    }

    public VetAppointmentAdapter(List<AppointmentResponse> items, OnStatusChangedListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vet_appointment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        AppointmentResponse a = items.get(pos);
        Context ctx = h.itemView.getContext();

        h.petInfo.setText("Pet ID: " + a.getPetId() + "  ·  " + safe(a.getVetName()));
        h.clinic.setText(safe(a.getClinicName()));
        h.time.setText(formatTime(a.getAppointmentTime()));
        h.status.setText(safe(a.getStatus()));
        h.status.setBackgroundColor(statusColor(a.getStatus()));

        // Not
        if (a.getNote() != null && !a.getNote().isEmpty()) {
            h.note.setVisibility(View.VISIBLE);
            h.note.setText("Not: " + a.getNote());
        } else {
            h.note.setVisibility(View.GONE);
        }

        // Tamamlandı butonu — sadece PLANNED olanlar için
        boolean isPlanned = "PLANNED".equalsIgnoreCase(a.getStatus());
        h.btnCompleted.setEnabled(isPlanned);
        h.btnCompleted.setAlpha(isPlanned ? 1f : 0.4f);
        h.btnCompleted.setOnClickListener(v -> {
            if (!isPlanned) return;
            Map<String, String> body = new HashMap<>();
            body.put("status", "COMPLETED");
            ApiClient.getApiService()
                    .updateAppointmentStatus(a.getAppointmentId(), body)
                    .enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<AppointmentResponse>> call,
                                               @NonNull Response<ApiResponse<AppointmentResponse>> response) {
                            Toast.makeText(ctx, "Randevu tamamlandı olarak işaretlendi.", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onChanged();
                        }
                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<AppointmentResponse>> call, @NonNull Throwable t) {
                            Toast.makeText(ctx, "İşlem başarısız: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Hatırlatma Gönder butonu
        h.btnReminder.setOnClickListener(v -> showReminderDialog(ctx, a));
    }

    private void showReminderDialog(Context ctx, AppointmentResponse a) {
        new AlertDialog.Builder(ctx)
                .setTitle("Hatırlatma Gönder")
                .setMessage("Pet ID " + a.getPetId() + " için randevu hatırlatması gönderilsin mi?\n\n"
                        + "Tarih: " + formatTime(a.getAppointmentTime()))
                .setPositiveButton("Gönder", (d, w) -> sendReminder(ctx, a))
                .setNegativeButton("İptal", null)
                .show();
    }

    private void sendReminder(Context ctx, AppointmentResponse a) {
        // remindAt = randevu saatinden 1 gün önce
        String remindAt = a.getAppointmentTime(); // ISO string olarak geliyor
        CreateReminderRequest req = new CreateReminderRequest(
                a.getPetId(),
                "APPOINTMENT",
                "Yarın randevunuz var: " + safe(a.getClinicName()),
                remindAt,
                "PENDING"
        );
        ApiClient.getApiService().createReminder(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<com.petcare.mobile.model.ApiResponse<com.petcare.mobile.model.ReminderResponse>> call,
                                   @NonNull Response<com.petcare.mobile.model.ApiResponse<com.petcare.mobile.model.ReminderResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ctx, "Hatırlatma başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Hatırlatma oluşturulamadı.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<com.petcare.mobile.model.ApiResponse<com.petcare.mobile.model.ReminderResponse>> call, @NonNull Throwable t) {
                Toast.makeText(ctx, "Bağlantı hatası.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─── Yardımcı Metotlar ────────────────────────────────────────────────────

    private String safe(String v) { return v == null ? "-" : v; }

    private String formatTime(String dt) {
        if (dt == null) return "-";
        try {
            String[] parts = dt.split("T");
            String[] d = parts[0].split("-");
            String t = parts.length > 1 ? parts[1].substring(0, 5) : "";
            return d[2] + "." + d[1] + "." + d[0] + "  " + t;
        } catch (Exception e) { return dt; }
    }

    private int statusColor(String s) {
        if (s == null) return Color.GRAY;
        return switch (s.toUpperCase()) {
            case "COMPLETED" -> Color.parseColor("#43A047");
            case "CANCELLED" -> Color.parseColor("#E53935");
            default          -> Color.parseColor("#E07B39");
        };
    }

    // ─── ViewHolder ──────────────────────────────────────────────────────────

    static class VH extends RecyclerView.ViewHolder {
        TextView petInfo, clinic, time, status, note;
        Button btnCompleted, btnReminder;

        VH(@NonNull View v) {
            super(v);
            petInfo      = v.findViewById(R.id.vetApptPetInfo);
            clinic       = v.findViewById(R.id.vetApptClinic);
            time         = v.findViewById(R.id.vetApptTime);
            status       = v.findViewById(R.id.vetApptStatus);
            note         = v.findViewById(R.id.vetApptNote);
            btnCompleted = v.findViewById(R.id.btnMarkCompleted);
            btnReminder  = v.findViewById(R.id.btnSendReminder);
        }
    }
}
