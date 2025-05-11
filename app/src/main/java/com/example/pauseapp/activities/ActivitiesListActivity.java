package com.example.pauseapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pauseapp.R;
import com.example.pauseapp.adapters.ActivityAdapter;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityResponse;
import com.example.pauseapp.model.ActivityTypeResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitiesListActivity extends MenuFunction {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private AuthApiService apiService;
    private EditText searchBar;
    private Button btnMindfulness, btnMeditacion, btnEjercicio, btnIntrospeccion;
    private String userToken;
    private final Map<String, Long> activityTypeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);

        // 1. Binding de vistas (antes de cualquier uso)
        searchBar       = findViewById(R.id.searchBar);
        recyclerView    = findViewById(R.id.recyclerViewActividades);
        btnMindfulness  = findViewById(R.id.btnMindfulness);
        btnMeditacion   = findViewById(R.id.btnMeditacion);
        btnEjercicio    = findViewById(R.id.btnEjercicio);
        btnIntrospeccion= findViewById(R.id.btnIntrospeccion);

        // 2. RecyclerView + Adapter (inicializado vacío)
        adapter = new ActivityAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3. Token y navegación
        userToken = getUserToken();
        if (userToken.isEmpty()) {
            goToLogin();  // tu implementación para redirigir al login
            finish();
            return;
        }
        setupNavigation();

        // 4. Cliente Retrofit
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        // 5. Carga de tipos para filtros
        loadActivityTypes();

        // 6. Listeners de botones de filtro
        btnMindfulness.setOnClickListener(v -> filterByType("mindfulness"));
        btnMeditacion.setOnClickListener(v -> filterByType("meditacion"));
        btnEjercicio.setOnClickListener(v -> filterByType("ejercicio"));
        btnIntrospeccion.setOnClickListener(v -> filterByType("introspeccion"));

        // 7. Live search (busca por nombre si hay texto,
        //    si no, muestra todos)
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String q = s.toString().trim();
                if (!q.isEmpty()) {
                    fetchActivitiesByName(q);
                } else {
                    fetchAllActivities();
                }
            }
        });

        // 8. Carga inicial sin filtro
        fetchAllActivities();
    }

    // --- Métodos de carga ---

    /** Carga tipos desde /activity/types */
    private void loadActivityTypes() {
        Call<List<ActivityTypeResponse>> call = apiService.getActivityTypes();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ActivityTypeResponse>> call,
                                   Response<List<ActivityTypeResponse>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ActivityTypeResponse type : resp.body()) {
                        activityTypeMap.put(type.getName().toLowerCase(), type.getId());
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ActivityTypeResponse>> call, Throwable t) {
                Toast.makeText(ActivitiesListActivity.this,
                        "Error al cargar tipos de actividad",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Filtra por nombre de tipo (usa el map nombre→ID) */
    private void filterByType(String typeName) {
        Long typeId = activityTypeMap.get(typeName.toLowerCase());
        if (typeId != null) {
            fetchActivitiesByType(typeId);
        } else {
            Toast.makeText(this,
                    "Tipo de actividad no disponible",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Carga todas las actividades (sin filter) */
    private void fetchAllActivities() {
        Call<List<ActivityResponse>> call =
                apiService.getActivitiesByType(
                        "Bearer " + userToken,
                        /* typeIds= */ null
                );
        call.enqueue(commonCallback());
    }

    /** Carga actividades de un tipo concreto */
    private void fetchActivitiesByType(Long typeId) {
        Call<List<ActivityResponse>> call =
                apiService.getActivitiesByType(
                        "Bearer " + userToken,
                        typeId == null
                                ? null
                                : Collections.singletonList(typeId)
                );
        call.enqueue(commonCallback());
    }

    /** Busca actividades por nombre */
    private void fetchActivitiesByName(String name) {
        Call<List<ActivityResponse>> call =
                apiService.getActivitiesByName(
                        "Bearer " + userToken,
                        name
                );
        call.enqueue(commonCallback());
    }

    /** Callback común que actualiza el adapter */
    private Callback<List<ActivityResponse>> commonCallback() {
        return new Callback<>() {
            @Override
            public void onResponse(Call<List<ActivityResponse>> call,
                                   Response<List<ActivityResponse>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    adapter.updateData(resp.body());
                } else {
                    Toast.makeText(ActivitiesListActivity.this,
                            "Error al obtener actividades",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ActivityResponse>> call, Throwable t) {
                Toast.makeText(ActivitiesListActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    /** Recupera el token del SharedPreferences */
    private String getUserToken() {
        return getSharedPreferences("PauseAppPrefs", MODE_PRIVATE)
                .getString("user_token", "");
    }

    /** Placeholder: implementa aquí la redirección al login */
    private void goToLogin() {
        // Intent i = new Intent(this, LoginActivity.class);
        // startActivity(i);
    }
}
