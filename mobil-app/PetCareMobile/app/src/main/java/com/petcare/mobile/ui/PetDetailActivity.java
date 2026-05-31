package com.petcare.mobile.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.AppointmentResponse;
import com.petcare.mobile.model.CreateAppointmentRequest;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.ui.adapter.AppointmentAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hayvan detay ekranı — pet bilgileri + randevu listesi + silme.
 * PetListActivity'den PET_ID intent extra ile açılır.
 */
public class PetDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID = "pet_id";

    private long petId;
    private final List<AppointmentResponse> appointments = new ArrayList<>();
    private AppointmentAdapter appointmentAdapter;

    private TextView petNameDetail, petSpeciesBreed, petGender, petBirthDate, petWeight, petNotesDetail;
    private TextView noAppointmentsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        petId = getIntent().getLongExtra(EXTRA_PET_ID, -1);
        if (petId == -1) { finish(); return; }

        bindViews();
        setupRecyclerView();
        loadPetDetail();
        loadAppointments();
    }

    private void bindViews() {
        petNameDetail    = findViewById(R.id.petNameDetail);
        petSpeciesBreed  = findViewById(R.id.petSpeciesBreed);
        petGender        = findViewById(R.id.petGender);
        petBirthDate     = findViewById(R.id.petBirthDate);
        petWeight        = findViewById(R.id.petWeight);
        petNotesDetail   = findViewById(R.id.petNotesDetail);
        noAppointmentsText = findViewById(R.id.noAppointmentsText);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button addApptBtn = findViewById(R.id.addAppointmentButton);
        addApptBtn.setOnClickListener(v -> showAddAppointmentDialog());

        Button deleteBtn = findViewById(R.id.deletePetButton);
        deleteBtn.setOnClickListener(v -> confirmDeletePet());
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.appointmentRecyclerView);
        appointmentAdapter = new AppointmentAdapter(appointments);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(appointmentAdapter);
    }

    // ─── Veri Yükleme ───────────────────────────────────────────

    private void loadPetDetail() {
        ApiClient.getApiService().getPetById(petId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PetResponse>> call,
                                   @NonNull Response<ApiResponse<PetResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(PetDetailActivity.this, "Hayvan bilgisi alınamadı.", Toast.LENGTH_SHORT).show();
                    return;
                }
                bindPetData(response.body().getData());
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PetResponse>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Bağlantı hatası.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindPetData(PetResponse p) {
        petNameDetail.setText(p.getPetName());
        petSpeciesBreed.setText(p.getSpecies() + (p.getBreed() != null ? "  ·  " + p.getBreed() : ""));
        petGender.setText(p.getGender() != null ? p.getGender() : "-");
        petBirthDate.setText(p.getBirthDate() != null ? p.getBirthDate() : "-");
        petWeight.setText(p.getCurrentWeight() != null ? p.getCurrentWeight() + " kg" : "-");
        petNotesDetail.setText(p.getNotes() != null ? p.getNotes() : "");
    }

    private void loadAppointments() {
        ApiClient.getApiService().getAppointmentsByPetId(petId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<AppointmentResponse>>> call,
                                   @NonNull Response<ApiResponse<List<AppointmentResponse>>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                appointments.clear();
                if (response.body().getData() != null)
                    appointments.addAll(response.body().getData());
                appointmentAdapter.notifyDataSetChanged();
                noAppointmentsText.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<AppointmentResponse>>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Randevular yüklenemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Randevu Ekleme Dialogu ─────────────────────────────────

    private void showAddAppointmentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_appointment, null);

        TextInputEditText vetInput      = dialogView.findViewById(R.id.vetNameInput);
        TextInputEditText clinicInput   = dialogView.findViewById(R.id.clinicNameInput);
        TextInputEditText dateTimeInput = dialogView.findViewById(R.id.appointmentDateTimeInput);
        TextInputEditText noteInput     = dialogView.findViewById(R.id.appointmentNoteInput);

        dateTimeInput.setOnClickListener(v -> pickDateTime(dateTimeInput));

        new AlertDialog.Builder(this)
                .setTitle("Randevu Ekle")
                .setView(dialogView)
                .setPositiveButton("Kaydet", (d, w) -> {
                    String vet      = text(vetInput);
                    String clinic   = text(clinicInput);
                    String dateTime = text(dateTimeInput);
                    String note     = text(noteInput);

                    if (vet.isEmpty() || dateTime.isEmpty()) {
                        Toast.makeText(this, "Veteriner adı ve tarih zorunludur.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createAppointment(vet, clinic, dateTime, note);
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    /** Tarih + saat seçici zinciri */
    private void pickDateTime(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (dv, y, m, d) -> {
            String date = String.format("%04d-%02d-%02d", y, m + 1, d);
            new TimePickerDialog(this, (tv, h, min) -> {
                String dt = date + "T" + String.format("%02d:%02d:00", h, min);
                target.setText(dt);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createAppointment(String vet, String clinic, String dateTime, String note) {
        CreateAppointmentRequest req = new CreateAppointmentRequest(petId, vet, clinic, dateTime, "PLANNED", note);
        ApiClient.getApiService().createAppointment(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AppointmentResponse>> call,
                                   @NonNull Response<ApiResponse<AppointmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(PetDetailActivity.this, "Randevu eklendi.", Toast.LENGTH_SHORT).show();
                    loadAppointments();
                } else {
                    Toast.makeText(PetDetailActivity.this, "Randevu eklenemedi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AppointmentResponse>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Bağlantı hatası.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Hayvan Silme ───────────────────────────────────────────

    private void confirmDeletePet() {
        new AlertDialog.Builder(this)
                .setTitle("Hayvanı Sil")
                .setMessage("Bu hayvan ve tüm verileri silinecek. Emin misiniz?")
                .setPositiveButton("Sil", (d, w) -> deletePet())
                .setNegativeButton("İptal", null)
                .show();
    }

    private void deletePet() {
        ApiClient.getApiService().deletePet(petId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                   @NonNull Response<ApiResponse<Void>> response) {
                Toast.makeText(PetDetailActivity.this, "Hayvan silindi.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Silinemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String text(TextInputEditText v) {
        return v.getText() == null ? "" : v.getText().toString().trim();
    }
}
