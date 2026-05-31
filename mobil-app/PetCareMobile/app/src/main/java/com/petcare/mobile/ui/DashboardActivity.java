package com.petcare.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.storage.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView statPetCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        // Header
        TextView welcomeText = findViewById(R.id.welcomeText);
        TextView emailText   = findViewById(R.id.emailText);
        welcomeText.setText(getString(R.string.welcome_user, sessionManager.getFullName()));
        emailText.setText(sessionManager.getEmail());

        // Stats
        statPetCount = findViewById(R.id.statPetCount);
        loadPetCount();

        // Navigation
        Button petsButton   = findViewById(R.id.petsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        petsButton.setOnClickListener(v ->
                startActivity(new Intent(this, PetListActivity.class)));

        logoutButton.setOnClickListener(v -> {
            sessionManager.clear();
            goToLogin();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Pet listesi değişmişse sayacı güncelle
        if (sessionManager.isLoggedIn()) {
            loadPetCount();
        }
    }

    private void loadPetCount() {
        ApiClient.getApiService()
                .getPetsByUserId(sessionManager.getUserId())
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<PetResponse>>> call,
                                           @NonNull Response<ApiResponse<List<PetResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            int count = response.body().getData().size();
                            statPetCount.setText(String.valueOf(count));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<PetResponse>>> call,
                                          @NonNull Throwable t) {
                        statPetCount.setText("?");
                    }
                });
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
