package com.petcare.mobile.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
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

/**
 * Kayıt ekranı — USER veya VET rolüyle hesap oluşturur.
 * LoginActivity'den EXTRA_ROLE intent ile rol belirlenir.
 */
public class RegisterActivity extends AppCompatActivity {

    /** LoginActivity'nin geçtiği rol bilgisi */
    public static final String EXTRA_ROLE = "register_role";

    private TextInputEditText fullNameInput, emailInput, phoneInput, passwordInput;
    private ProgressBar progressBar;
    private Button registerButton;
    private String selectedRole = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        selectedRole = getIntent().getStringExtra(EXTRA_ROLE);
        if (selectedRole == null) selectedRole = "USER";

        fullNameInput  = findViewById(R.id.fullNameInput);
        emailInput     = findViewById(R.id.emailInput);
        phoneInput     = findViewById(R.id.phoneInput);
        passwordInput  = findViewById(R.id.passwordInput);
        progressBar    = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.registerButton);

        // RadioGroup Tanımlaması ve Seçimi
        RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGroup);
        if ("VET".equals(selectedRole)) {
            roleRadioGroup.check(R.id.radioVet);
        } else {
            roleRadioGroup.check(R.id.radioUser);
        }

        TextView titleView = findViewById(R.id.registerTitleText);
        if (titleView != null) {
            titleView.setText("VET".equals(selectedRole)
                    ? "🩺  Veteriner Hesabı Oluştur"
                    : "👤  Kullanıcı Hesabı Oluştur");
        }

        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedRole = (checkedId == R.id.radioVet) ? "VET" : "USER";
            if (titleView != null) {
                titleView.setText("VET".equals(selectedRole)
                        ? "🩺  Veteriner Hesabı Oluştur"
                        : "👤  Kullanıcı Hesabı Oluştur");
            }
        });

        registerButton.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String fullName = textOf(fullNameInput);
        String email    = textOf(emailInput);
        String phone    = textOf(phoneInput);
        String password = textOf(passwordInput);

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ad, e-posta ve şifre zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        RegisterRequest req = new RegisterRequest(fullName, email, password, phone, selectedRole);

        ApiClient.getApiService().register(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse>> call,
                                   @NonNull Response<ApiResponse<UserResponse>> response) {
                setLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegisterActivity.this, "Kayıt yapılamadı.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiResponse<UserResponse> apiResponse = response.body();
                if (!apiResponse.isSuccess()) {
                    Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String roleMsg = "VET".equals(selectedRole) ? "Veteriner" : "Kullanıcı";
                Toast.makeText(RegisterActivity.this,
                        roleMsg + " hesabı oluşturuldu. Giriş yapabilirsiniz.",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        registerButton.setEnabled(!loading);
        registerButton.setText(loading ? "Kaydediliyor…" : "Kaydı Tamamla");
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
