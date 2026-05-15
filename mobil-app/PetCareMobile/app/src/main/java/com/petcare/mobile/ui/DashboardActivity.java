package com.petcare.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.petcare.mobile.R;
import com.petcare.mobile.storage.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView welcomeText = findViewById(R.id.welcomeText);
        TextView emailText = findViewById(R.id.emailText);
        Button petsButton = findViewById(R.id.petsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        welcomeText.setText(getString(R.string.welcome_user, sessionManager.getFullName()));
        emailText.setText(sessionManager.getEmail());

        petsButton.setOnClickListener(view -> startActivity(new Intent(this, PetListActivity.class)));
        logoutButton.setOnClickListener(view -> {
            sessionManager.clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
