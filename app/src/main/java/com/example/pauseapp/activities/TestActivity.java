package com.example.pauseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.UserResponse;
import com.example.pauseapp.fragments.PreguntaFragment;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {
    private List<Pregunta> preguntas;
    private int preguntaActual = 0;
    private static float stressLvl;
    private AuthApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Inicializar nivel de estrés y servicio API
        stressLvl = 0;
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        preguntas = Arrays.asList(
                new Pregunta("¿Cuántas horas de sueño promedio tienes por noche?", Arrays.asList("Más de 7 horas",
                        "Entre 5 y 7 horas", "Entre 3 y 5 horas", "Menos de 3 horas")),
                new Pregunta("¿Con qué frecuencia sientes que no tienes tiempo suficiente para ti?",
                        Arrays.asList("Nunca", "A veces", "A menudo","Siempre")),
                new Pregunta("Cuando te enfrentas a situaciones estresantes, ¿cómo reaccionas principalmente?",
                        Arrays.asList("No me afecta mucho", "Me frustro o me irrito con facilidad",
                                "Intento mantener la calma y resolver el problema","Me paralizo y no sé qué hacer")),
                new Pregunta("¿Con qué frecuencia utilizas actividades de relajación o mindfulness?",
                        Arrays.asList("A diario", "Algunas veces a la semana","Rara vez","Nunca")),
                new Pregunta("En una escala del 1 al 10, ¿cómo evaluarías tu nivel de estrés actual?",
                        Arrays.asList("1-3", "4-6", "7-8", "9-10"))
        );

        mostrarSiguientePregunta();
    }

    public void mostrarSiguientePregunta() {
        if (preguntaActual < preguntas.size()) {
            PreguntaFragment fragment = PreguntaFragment.newInstance(
                    preguntas.get(preguntaActual), preguntaActual, preguntas.size());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        } else {
            // Al completar, actualizamos nivel de estrés en el backend
            actualizarNivelDeEstresEnServidor(stressLvl);
        }
    }

    private void actualizarNivelDeEstresEnServidor(float nivel) {
        String token = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE)
                .getString("user_token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        // Primero obtenemos el userId
        apiService.getUser("Bearer " + token)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Long userId = response.body().getId();
                            // Ahora actualizamos el nivel
                            apiService.actualizarNivelEstres(userId.intValue(), nivel, "Bearer " + token)
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> resp) {
                                            if (resp.isSuccessful()) {
                                                Log.d("TestActivity", "Nivel de estrés actualizado");
                                            } else {
                                                Log.e("TestActivity", "Error al actualizar nivel: " + resp.code());
                                            }
                                            // Navegar al perfil tras intentar actualizar
                                            irAPerfil();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Log.e("TestActivity", "Fallo conexión al actualizar", t);
                                            irAPerfil();
                                        }
                                    });
                        } else {
                            Toast.makeText(TestActivity.this,
                                    "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(TestActivity.this,
                                "Fallo de conexión al obtener usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irAPerfil() {
        Intent intent = new Intent(TestActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void avanzarPregunta() {
        preguntaActual++;
        mostrarSiguientePregunta();
    }

    public static float getStressLvl() {
        return stressLvl;
    }

    public static void setStressLvl(float stressLvl) {
        TestActivity.stressLvl = stressLvl;
    }
}