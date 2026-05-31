package com.petcare.mobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.petcare.mobile.R;
import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.network.ApiClient;
import com.petcare.mobile.storage.SessionManager;
import com.petcare.mobile.ui.adapter.PetAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetListActivity extends AppCompatActivity {

    private final List<PetResponse> petItems = new ArrayList<>();
    private PetAdapter petAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyStateText;
    private SessionManager sessionManager;

    /** AddPetActivity'den dönen sonucu yakalar; başarılıysa listeyi yeniler. */
    private final ActivityResultLauncher<Intent> addPetLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == Activity.RESULT_OK) fetchPets(); }
    );

    /** PetDetailActivity'den dönen sonucu yakalar (silme sonrası yenile). */
    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == Activity.RESULT_OK) fetchPets(); }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        sessionManager = new SessionManager(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateText = findViewById(R.id.emptyStateText);
        RecyclerView recyclerView = findViewById(R.id.petRecyclerView);

        petAdapter = new PetAdapter(petItems, detailLauncher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(petAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchPets);

        // FAB — Hayvan Ekle
        FloatingActionButton addPetFab = findViewById(R.id.addPetFab);
        addPetFab.setOnClickListener(v ->
                addPetLauncher.launch(new Intent(this, AddPetActivity.class))
        );

        fetchPets();
    }

    private void fetchPets() {
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getApiService().getPetsByUserId(sessionManager.getUserId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<PetResponse>>> call,
                                   @NonNull Response<ApiResponse<List<PetResponse>>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PetListActivity.this, "Pet listesi alinamadi.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<List<PetResponse>> apiResponse = response.body();
                if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
                    Toast.makeText(PetListActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                petItems.clear();
                petItems.addAll(apiResponse.getData());
                petAdapter.notifyDataSetChanged();
                emptyStateText.setVisibility(petItems.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<PetResponse>>> call, @NonNull Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(PetListActivity.this, "Baglanti hatasi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
