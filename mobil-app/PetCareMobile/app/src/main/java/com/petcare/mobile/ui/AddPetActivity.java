package com.petcare.mobile.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.CreatePetRequest;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.storage.SessionManager;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Yeni evcil hayvan ekleme ekranı.
 * Zorunlu alanlar: petName, species
 * Opsiyonel alanlar: breed, gender, birthDate, currentWeight, notes
 */
public class AddPetActivity extends AppCompatActivity {

    private TextInputEditText petNameInput;
    private TextInputEditText speciesInput;
    private TextInputEditText breedInput;
    private Spinner genderSpinner;
    private TextInputEditText birthDateInput;
    private TextInputEditText weightInput;
    private TextInputEditText notesInput;
    private Button savePetButton;
    private SessionManager sessionManager;

    private static final String[] GENDER_OPTIONS = {"Belirtilmedi", "MALE", "FEMALE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        sessionManager = new SessionManager(this);

        // Görünümleri bağla
        petNameInput   = findViewById(R.id.petNameInput);
        speciesInput   = findViewById(R.id.speciesInput);
        breedInput     = findViewById(R.id.breedInput);
        genderSpinner  = findViewById(R.id.genderSpinner);
        birthDateInput = findViewById(R.id.birthDateInput);
        weightInput    = findViewById(R.id.weightInput);
        notesInput     = findViewById(R.id.notesInput);
        savePetButton  = findViewById(R.id.savePetButton);

        // Geri butonu
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Cinsiyet spinner'ı doldur
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, GENDER_OPTIONS);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Tarih seçici
        birthDateInput.setOnClickListener(v -> showDatePicker());

        // Kaydet
        savePetButton.setOnClickListener(v -> savePet());
    }

    /** DatePickerDialog açarak kullanıcıdan doğum tarihi seçmesini sağlar. */
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    birthDateInput.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /** Formu validate eder ve backend'e gönderir. */
    private void savePet() {
        String petName = text(petNameInput);
        String species = text(speciesInput);
        String breed   = text(breedInput);
        String gender  = genderSpinner.getSelectedItemPosition() == 0 ? null : GENDER_OPTIONS[genderSpinner.getSelectedItemPosition()];
        String birthDate = text(birthDateInput);
        String weightStr = text(weightInput);
        String notes   = text(notesInput);

        // Zorunlu alan kontrolü
        if (TextUtils.isEmpty(petName)) {
            petNameInput.setError(getString(R.string.error_pet_name_required));
            petNameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(species)) {
            speciesInput.setError(getString(R.string.error_species_required));
            speciesInput.requestFocus();
            return;
        }

        Double weight = null;
        if (!TextUtils.isEmpty(weightStr)) {
            try {
                weight = Double.parseDouble(weightStr);
            } catch (NumberFormatException e) {
                weightInput.setError(getString(R.string.error_invalid_weight));
                weightInput.requestFocus();
                return;
            }
        }

        // Butonun devre dışı bırak (çift tıklamayı önle)
        savePetButton.setEnabled(false);
        savePetButton.setText(R.string.saving_pet_button);

        CreatePetRequest request = new CreatePetRequest(
                sessionManager.getUserId(),
                petName,
                species,
                TextUtils.isEmpty(breed) ? null : breed,
                gender,
                TextUtils.isEmpty(birthDate) ? null : birthDate,
                weight,
                TextUtils.isEmpty(notes) ? null : notes
        );

        ApiClient.getApiService().createPet(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PetResponse>> call,
                                   @NonNull Response<ApiResponse<PetResponse>> response) {
                savePetButton.setEnabled(true);
                savePetButton.setText(R.string.save_pet_button);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddPetActivity.this,
                            getString(R.string.pet_saved_success, response.body().getData().getPetName()),
                            Toast.LENGTH_SHORT).show();
                    // Kaydedilince listeyi yenilemesi için RESULT_OK döndür
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Hata oluştu.";
                    Toast.makeText(AddPetActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PetResponse>> call, @NonNull Throwable t) {
                savePetButton.setEnabled(true);
                savePetButton.setText(R.string.save_pet_button);
                Toast.makeText(AddPetActivity.this,
                        getString(R.string.connection_error, t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    /** TextInputEditText'ten null-safe metin alır. */
    private String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
