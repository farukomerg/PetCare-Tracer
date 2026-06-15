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
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.model.UpdatePetRequest;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.storage.SessionManager;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPetActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID = "edit_pet_id";
    public static final String EXTRA_PET_NAME = "edit_pet_name";
    public static final String EXTRA_SPECIES = "edit_species";
    public static final String EXTRA_BREED = "edit_breed";
    public static final String EXTRA_GENDER = "edit_gender";
    public static final String EXTRA_BIRTH_DATE = "edit_birth_date";
    public static final String EXTRA_WEIGHT = "edit_weight";
    public static final String EXTRA_NOTES = "edit_notes";

    private static final String[] GENDER_OPTIONS = {"Belirtilmedi", "MALE", "FEMALE"};

    private long petId;
    private TextInputEditText petNameInput;
    private TextInputEditText speciesInput;
    private TextInputEditText breedInput;
    private TextInputEditText birthDateInput;
    private TextInputEditText weightInput;
    private TextInputEditText notesInput;
    private Spinner genderSpinner;
    private Button saveButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        sessionManager = new SessionManager(this);
        petId = getIntent().getLongExtra(EXTRA_PET_ID, -1);
        if (petId == -1) {
            finish();
            return;
        }

        bindViews();
        prefillForm();
    }

    private void bindViews() {
        petNameInput = findViewById(R.id.editPetName);
        speciesInput = findViewById(R.id.editSpecies);
        breedInput = findViewById(R.id.editBreed);
        genderSpinner = findViewById(R.id.editGenderSpinner);
        birthDateInput = findViewById(R.id.editBirthDate);
        weightInput = findViewById(R.id.editWeight);
        notesInput = findViewById(R.id.editNotes);
        saveButton = findViewById(R.id.saveEditButton);

        ImageButton backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> finish());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, GENDER_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        birthDateInput.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> savePet());
    }

    private void prefillForm() {
        petNameInput.setText(getIntent().getStringExtra(EXTRA_PET_NAME));
        speciesInput.setText(getIntent().getStringExtra(EXTRA_SPECIES));
        breedInput.setText(getIntent().getStringExtra(EXTRA_BREED));
        birthDateInput.setText(getIntent().getStringExtra(EXTRA_BIRTH_DATE));
        weightInput.setText(getIntent().getStringExtra(EXTRA_WEIGHT));
        notesInput.setText(getIntent().getStringExtra(EXTRA_NOTES));

        String gender = getIntent().getStringExtra(EXTRA_GENDER);
        if ("MALE".equals(gender)) {
            genderSpinner.setSelection(1);
        } else if ("FEMALE".equals(gender)) {
            genderSpinner.setSelection(2);
        } else {
            genderSpinner.setSelection(0);
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        String existing = text(birthDateInput);
        if (existing.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] parts = existing.split("-");
            cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        }
        new DatePickerDialog(this,
                (view, year, month, day) -> birthDateInput.setText(
                        String.format("%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void savePet() {
        String petName = text(petNameInput);
        String species = text(speciesInput);
        String breed = text(breedInput);
        String birthDate = text(birthDateInput);
        String weightStr = text(weightInput);
        String notes = text(notesInput);
        String gender = genderSpinner.getSelectedItemPosition() == 0
                ? null : GENDER_OPTIONS[genderSpinner.getSelectedItemPosition()];

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
                return;
            }
        }

        saveButton.setEnabled(false);
        saveButton.setText(R.string.saving_pet_button);

        UpdatePetRequest req = new UpdatePetRequest(
                sessionManager.getUserId(),
                petName,
                species,
                TextUtils.isEmpty(breed) ? null : breed,
                gender,
                TextUtils.isEmpty(birthDate) ? null : birthDate,
                weight,
                TextUtils.isEmpty(notes) ? null : notes
        );

        ApiClient.getApiService().updatePet(petId, req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PetResponse>> call,
                                   @NonNull Response<ApiResponse<PetResponse>> response) {
                saveButton.setEnabled(true);
                saveButton.setText(R.string.save_changes_button);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditPetActivity.this,
                            getString(R.string.pet_updated_success, response.body().getData().getPetName()),
                            Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    return;
                }

                String msg = response.body() != null ? response.body().getMessage() : "Guncelleme basarisiz.";
                Toast.makeText(EditPetActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PetResponse>> call, @NonNull Throwable t) {
                saveButton.setEnabled(true);
                saveButton.setText(R.string.save_changes_button);
                Toast.makeText(EditPetActivity.this, "Baglanti hatasi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
