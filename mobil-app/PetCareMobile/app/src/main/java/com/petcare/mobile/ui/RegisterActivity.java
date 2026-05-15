package com.petcare.mobile.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.RegisterRequest;
import com.petcare.mobile.model.UserResponse;
import com.petcare.mobile.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText fullNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText passwordInput;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        progressBar = findViewById(R.id.progressBar);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> attemptRegister());
    }

    private void attemptRegister() {
        String fullName = textOf(fullNameInput);
        String email = textOf(emailInput);
        String phone = textOf(phoneInput);
        String password = textOf(passwordInput);

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ad, e-posta ve sifre zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ApiClient.getApiService().register(new RegisterRequest(fullName, email, password, phone)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse>> call,
                                   @NonNull Response<ApiResponse<UserResponse>> response) {
                setLoading(false);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegisterActivity.this, "Kayit yapilamadi.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<UserResponse> apiResponse = response.body();
                if (!apiResponse.isSuccess()) {
                    Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(RegisterActivity.this, "Kayit tamamlandi. Giris yapabilirsiniz.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable throwable) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Baglanti hatasi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
