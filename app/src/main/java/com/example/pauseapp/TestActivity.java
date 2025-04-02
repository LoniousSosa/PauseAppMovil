package com.example.pauseapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;
import java.util.List;
public class TestActivity extends AppCompatActivity {
    private List<Pregunta> preguntas;
    private int preguntaActual = 0;
    private static int stressLvl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        stressLvl = 0;

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
            PreguntaFragment fragment = PreguntaFragment.newInstance(preguntas.get(preguntaActual), preguntaActual, preguntas.size());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        } else {
            Toast.makeText(this, "Nivel de estrés: "+getStressLvl(), Toast.LENGTH_SHORT).show();
            //Mostrar resultados
            finish();
        }
    }

    public void avanzarPregunta() {
        preguntaActual++;
        mostrarSiguientePregunta();
    }

    public static int getStressLvl() {
        return stressLvl;
    }

    public static void setStressLvl(int stressLvl) {
        TestActivity.stressLvl = stressLvl;
    }
}
