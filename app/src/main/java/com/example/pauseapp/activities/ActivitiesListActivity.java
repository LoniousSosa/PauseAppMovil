package com.example.pauseapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import com.example.pauseapp.R;
import com.example.pauseapp.adapters.ActivityAdapter;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitiesListActivity extends MenuFunction {

    private RecyclerView recyclerView;
    private Button btnMindfulness, btnMeditacion, btnEjercicio, btnIntrospeccion;

    private ActivityAdapter adapter;
    private AuthApiService activityApiService;
    private EditText searchInput;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);

        btnMindfulness = findViewById(R.id.btnMindfulness);
        btnMeditacion = findViewById(R.id.btnMeditacion);
        btnEjercicio = findViewById(R.id.btnEjercicio);
        btnIntrospeccion = findViewById(R.id.btnIntrospeccion);

        btnMindfulness.setOnClickListener(v -> fetchActivities("mindfulness", searchInput.getText().toString()));
        btnMeditacion.setOnClickListener(v -> fetchActivities("meditacion", searchInput.getText().toString()));
        btnEjercicio.setOnClickListener(v -> fetchActivities("ejercicio", searchInput.getText().toString()));
        btnIntrospeccion.setOnClickListener(v -> fetchActivities("introspeccion", searchInput.getText().toString()));

        recyclerView = findViewById(R.id.recyclerViewActividades);
        searchInput = findViewById(R.id.searchBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userToken = getUserToken();

        setupNavigation();

        activityApiService = RetrofitClient.getClient().create(AuthApiService.class);
        fetchActivities(null, null);

        /**
         * Esto permite que el search view funcione como un live search.
         * En el onTextChanged, mientras se escribe se irá leyendo la secuencia de
         * caracteres y se hará un fetch.
         *
         * Si esto entorpece o realentiza mucho la aplicación se cambiará a una
         * única búsqueda al pulsar Enter.
         */

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchActivities(null, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void fetchActivities(String filter, String search) {
        Call<List<ActivityResponse>> call = activityApiService.getActivities("Bearer " + userToken, filter, search);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ActivityResponse>> call, Response<List<ActivityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActivityResponse> activities = response.body();
                    adapter = new ActivityAdapter(activities);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ActivitiesListActivity.this, "Error al obtener actividades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActivityResponse>> call, Throwable t) {
                Toast.makeText(ActivitiesListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserToken() {
        return getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).getString("user_token", "");
    }

}
