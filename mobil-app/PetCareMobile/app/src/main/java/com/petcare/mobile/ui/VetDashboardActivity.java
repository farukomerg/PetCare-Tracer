package com.petcare.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.AppointmentResponse;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.storage.SessionManager;
import com.petcare.mobile.ui.adapter.VetAppointmentAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Veteriner Dashboard Ekranı.
 *
 * Özellikler:
 * - Hoş geldin mesajı (ad + e-posta)
 * - Toplam / bekleyen randevu istatistiği
 * - Veteriner adına düşen randevuların listesi
 * - Her randevuda: "Tamamlandı" işaretle + "Hatırlatma Gönder"
 */
public class VetDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private final List<AppointmentResponse> appointments = new ArrayList<>();
    private VetAppointmentAdapter adapter;

    private TextView statTotal, statPlanned, noApptText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_dashboard);

        sessionManager = new SessionManager(this);

        // Veteriner değilse geri yönlendir
        if (!sessionManager.isLoggedIn() || !sessionManager.isVet()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bindViews();
        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) loadAppointments();
    }

    private void bindViews() {
        TextView welcomeText = findViewById(R.id.vetWelcomeText);
        TextView emailText   = findViewById(R.id.vetEmailText);
        welcomeText.setText("Dr. " + sessionManager.getFullName());
        emailText.setText(sessionManager.getEmail());

        statTotal   = findViewById(R.id.statTotalAppt);
        statPlanned = findViewById(R.id.statPlannedAppt);
        noApptText  = findViewById(R.id.vetNoApptText);

        RecyclerView rv = findViewById(R.id.vetAppointmentRecyclerView);
        adapter = new VetAppointmentAdapter(appointments, this::loadAppointments);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        Button refreshBtn   = findViewById(R.id.refreshVetApptBtn);
        Button logoutButton = findViewById(R.id.vetLogoutButton);

        refreshBtn.setOnClickListener(v -> loadAppointments());
        logoutButton.setOnClickListener(v -> {
            sessionManager.clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Veteriner kendi id'siyle eşleşen randevuları çeker.
     * Endpoint: GET /api/appointments/vet/{vetId}
     */
    private void loadAppointments() {
        long vetId = sessionManager.getUserId();

        ApiClient.getApiService()
                .getAppointmentsByVetId(vetId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<AppointmentResponse>>> call,
                                           @NonNull Response<ApiResponse<List<AppointmentResponse>>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(VetDashboardActivity.this,
                                    "Randevular alınamadı.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<AppointmentResponse> data = response.body().getData();
                        appointments.clear();
                        if (data != null) appointments.addAll(data);
                        adapter.notifyDataSetChanged();

                        // İstatistikler
                        long planned = appointments.stream()
                                .filter(a -> "PLANNED".equalsIgnoreCase(a.getStatus()))
                                .count();
                        statTotal.setText(String.valueOf(appointments.size()));
                        statPlanned.setText(String.valueOf(planned));
                        noApptText.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<AppointmentResponse>>> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(VetDashboardActivity.this,
                                "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
