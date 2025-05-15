package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityTypeResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitialFiltersActivity extends AppCompatActivity {

    private static final String PREFS_NAME       = "PauseAppPrefs";
    private static final String KEY_DONE         = "initial_filters_done";
    private static final String KEY_TYPE_CHOICES = "initial_filters_type_ids";

    private SharedPreferences prefs;
    private String userToken;
    private AuthApiService apiService;
    private Set<Long> selectedTypeIds = new HashSet<>();

    private LinearLayout filtersContainer;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Si ya completó los filtros, salta directamente al lobby
        if (prefs.getBoolean(KEY_DONE, false)) {
            launchLobby(null);
            return;
        }

        setContentView(R.layout.activity_initial_filters);

        filtersContainer = findViewById(R.id.filtersContainer);
        btnContinue      = findViewById(R.id.btnContinue);
        userToken = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE)
                .getString("auth_token", "");

        apiService = RetrofitClient.getClient().create(AuthApiService.class);
        loadActivityTypes();

        btnContinue.setOnClickListener(view -> {
            if (selectedTypeIds.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un filtro", Toast.LENGTH_SHORT).show();
                return;
            }
            // Guardar elecciones y marcar completado
            prefs.edit()
                    .putStringSet(KEY_TYPE_CHOICES, toStringSet(selectedTypeIds))
                    .putBoolean(KEY_DONE, true)
                    .apply();
            // Lanzar lobby con los filtros elegidos
            launchLobby(new ArrayList<>(toStringSet(selectedTypeIds)));
        });
    }

    private void loadActivityTypes() {
        apiService.getActivityTypes("Bearer " + userToken).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ActivityTypeResponse>> call,
                                   Response<List<ActivityTypeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateFilters(response.body());
                } else {
                    Toast.makeText(InitialFiltersActivity.this,
                            "Error al cargar filtros", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActivityTypeResponse>> call, Throwable t) {
                Toast.makeText(InitialFiltersActivity.this,
                        "Fallo de conexión al cargar filtros", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFilters(List<ActivityTypeResponse> types) {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (ActivityTypeResponse type : types) {
            View row = inflater.inflate(R.layout.filter_row, filtersContainer, false);
            TextView label = row.findViewById(R.id.filterText);
            label.setText(type.getName());
            row.setOnClickListener(view -> toggleSelection(row, type.getId()));
            filtersContainer.addView(row);
        }
    }

    private void toggleSelection(View row, Long typeId) {
        if (selectedTypeIds.contains(typeId)) {
            selectedTypeIds.remove(typeId);
            row.setAlpha(1f);
        } else {
            selectedTypeIds.add(typeId);
            row.setAlpha(0.6f);
        }
    }

    /**
     * Lanza LobbyActivity, pasándole la lista de typeIds (o null).
     */
    private void launchLobby(ArrayList<String> typeIdStrings) {
        Intent intent = new Intent(this, LobbyActivity.class);
        if (typeIdStrings != null) {
            intent.putStringArrayListExtra("INITIAL_FILTER_TYPE_IDS", typeIdStrings);
        }
        startActivity(intent);
        finish();
    }

    private Set<String> toStringSet(Set<Long> longSet) {
        Set<String> strSet = new HashSet<>();
        for (Long l : longSet) strSet.add(String.valueOf(l));
        return strSet;
    }
}