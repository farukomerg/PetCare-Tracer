package com.petcare.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.LoginRequest;
import com.petcare.mobile.model.LoginResponse;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.storage.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openDashboard();
            return;
        }

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        progressBar = findViewById(R.id.progressBar);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);

        loginButton.setOnClickListener(view -> attemptLogin());
        registerLink.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = textOf(emailInput);
        String password = textOf(passwordInput);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "E-posta ve sifre zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ApiClient.getApiService().login(new LoginRequest(email, password)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<LoginResponse>> call,
                                   @NonNull Response<ApiResponse<LoginResponse>> response) {
                setLoading(false);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Giris yapilamadi.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<LoginResponse> apiResponse = response.body();
                if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
                    Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                sessionManager.saveLogin(apiResponse.getData());
                Toast.makeText(LoginActivity.this, "Giris basarili.", Toast.LENGTH_SHORT).show();
                openDashboard();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Throwable throwable) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Baglanti hatasi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
