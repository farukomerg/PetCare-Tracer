package com.petcare.mobile.ui;

import android.app.Activity;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.petcare.mobile.model.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID = "pet_id";

    private long petId;
    private PetResponse currentPet;
    private final List<AppointmentResponse> appointments = new ArrayList<>();
    private AppointmentAdapter appointmentAdapter;
    private List<UserResponse> vetList = new ArrayList<>();

    private TextView petNameDetail;
    private TextView petSpeciesBreed;
    private TextView petGender;
    private TextView petBirthDate;
    private TextView petWeight;
    private TextView petNotesDetail;
    private TextView noAppointmentsText;

    private final ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadPetDetail();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        petId = getIntent().getLongExtra(EXTRA_PET_ID, -1);
        if (petId == -1) {
            finish();
            return;
        }

        bindViews();
        setupRecyclerView();
        loadPetDetail();
        loadAppointments();
    }

    private void bindViews() {
        petNameDetail = findViewById(R.id.petNameDetail);
        petSpeciesBreed = findViewById(R.id.petSpeciesBreed);
        petGender = findViewById(R.id.petGender);
        petBirthDate = findViewById(R.id.petBirthDate);
        petWeight = findViewById(R.id.petWeight);
        petNotesDetail = findViewById(R.id.petNotesDetail);
        noAppointmentsText = findViewById(R.id.noAppointmentsText);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button editBtn = findViewById(R.id.editPetButton);
        editBtn.setOnClickListener(v -> openEditPet());

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

    private void loadPetDetail() {
        ApiClient.getApiService().getPetById(petId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PetResponse>> call,
                                   @NonNull Response<ApiResponse<PetResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(PetDetailActivity.this, "Hayvan bilgisi alinamadi.", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPet = response.body().getData();
                bindPetData(currentPet);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PetResponse>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Baglanti hatasi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindPetData(PetResponse pet) {
        petNameDetail.setText(pet.getPetName());
        String breedText = pet.getBreed() == null || pet.getBreed().isBlank() ? "" : " - " + pet.getBreed();
        petSpeciesBreed.setText(pet.getSpecies() + breedText);
        petGender.setText(emptyAsDash(pet.getGender()));
        petBirthDate.setText(emptyAsDash(pet.getBirthDate()));
        petWeight.setText(pet.getCurrentWeight() != null ? pet.getCurrentWeight() + " kg" : "-");
        petNotesDetail.setText(emptyAsDash(pet.getNotes()));
    }

    private void loadAppointments() {
        ApiClient.getApiService().getAppointmentsByPetId(petId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<AppointmentResponse>>> call,
                                   @NonNull Response<ApiResponse<List<AppointmentResponse>>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                appointments.clear();
                if (response.body().getData() != null) {
                    appointments.addAll(response.body().getData());
                }
                appointmentAdapter.notifyDataSetChanged();
                noAppointmentsText.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<AppointmentResponse>>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Randevular yuklenemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEditPet() {
        if (currentPet == null) {
            Toast.makeText(this, "Hayvan bilgisi henuz yuklenmedi.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, EditPetActivity.class);
        intent.putExtra(EditPetActivity.EXTRA_PET_ID, currentPet.getPetId());
        intent.putExtra(EditPetActivity.EXTRA_PET_NAME, currentPet.getPetName());
        intent.putExtra(EditPetActivity.EXTRA_SPECIES, currentPet.getSpecies());
        intent.putExtra(EditPetActivity.EXTRA_BREED, currentPet.getBreed());
        intent.putExtra(EditPetActivity.EXTRA_GENDER, currentPet.getGender());
        intent.putExtra(EditPetActivity.EXTRA_BIRTH_DATE, currentPet.getBirthDate());
        intent.putExtra(EditPetActivity.EXTRA_WEIGHT,
                currentPet.getCurrentWeight() != null ? String.valueOf(currentPet.getCurrentWeight()) : "");
        intent.putExtra(EditPetActivity.EXTRA_NOTES, currentPet.getNotes());
        editLauncher.launch(intent);
    }

    private void showAddAppointmentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_appointment, null);

        Spinner vetSpinner = dialogView.findViewById(R.id.vetSpinner);
        TextInputEditText clinicInput = dialogView.findViewById(R.id.clinicNameInput);
        TextInputEditText dateTimeInput = dialogView.findViewById(R.id.appointmentDateTimeInput);
        TextInputEditText noteInput = dialogView.findViewById(R.id.appointmentNoteInput);

        dateTimeInput.setOnClickListener(v -> pickDateTime(dateTimeInput));

        // Veterinerleri çek
        ApiClient.getApiService().getVets().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<UserResponse>>> call, @NonNull Response<ApiResponse<List<UserResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    vetList = response.body().getData();
                    List<String> vetNames = new ArrayList<>();
                    for (UserResponse v : vetList) {
                        vetNames.add("Dr. " + v.getFullName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PetDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, vetNames);
                    vetSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(PetDetailActivity.this, "Veterinerler yuklenemedi.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<UserResponse>>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Veteriner API hatasi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_appointment_title)
                .setView(dialogView)
                .setPositiveButton("Kaydet", (dialog, which) -> {
                    int selectedPosition = vetSpinner.getSelectedItemPosition();
                    if (selectedPosition == AdapterView.INVALID_POSITION || vetList.isEmpty()) {
                        Toast.makeText(this, "Lutfen bir veteriner secin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UserResponse selectedVet = vetList.get(selectedPosition);
                    Long vetId = selectedVet.getUserId();
                    String vetName = selectedVet.getFullName();

                    String clinic = text(clinicInput);
                    String dateTime = text(dateTimeInput);
                    String note = text(noteInput);

                    if (dateTime.isEmpty()) {
                        Toast.makeText(this, R.string.error_vet_required, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createAppointment(vetId, vetName, clinic, dateTime, note);
                })
                .setNegativeButton("Iptal", null)
                .show();
    }

    private void pickDateTime(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (dateView, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            new TimePickerDialog(this, (timeView, hour, minute) ->
                    target.setText(date + "T" + String.format("%02d:%02d:00", hour, minute)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createAppointment(Long vetId, String vetName, String clinic, String dateTime, String note) {
        ApiClient.getApiService()
                .createAppointment(new CreateAppointmentRequest(petId, vetId, vetName, clinic, dateTime, "PLANNED", note))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<AppointmentResponse>> call,
                                           @NonNull Response<ApiResponse<AppointmentResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(PetDetailActivity.this, R.string.appointment_added_msg, Toast.LENGTH_SHORT).show();
                            loadAppointments();
                            return;
                        }
                        Toast.makeText(PetDetailActivity.this, "Randevu eklenemedi.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<AppointmentResponse>> call, @NonNull Throwable t) {
                        Toast.makeText(PetDetailActivity.this, "Baglanti hatasi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmDeletePet() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_pet_confirm_title)
                .setMessage(R.string.delete_pet_confirm_msg)
                .setPositiveButton("Sil", (dialog, which) -> deletePet())
                .setNegativeButton("Iptal", null)
                .show();
    }

    private void deletePet() {
        ApiClient.getApiService().deletePet(petId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                   @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PetDetailActivity.this, R.string.pet_deleted_msg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                Toast.makeText(PetDetailActivity.this, "Hayvan silinemedi.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Silinemedi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String text(TextInputEditText v) {
        return v.getText() == null ? "" : v.getText().toString().trim();
    }

    private String emptyAsDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
