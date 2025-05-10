package com.example.pauseapp.fragments;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pauseapp.R;
import com.example.pauseapp.activities.TestActivity;
import com.example.pauseapp.activities.Pregunta;
import java.util.List;

public class PreguntaFragment extends Fragment {

    private static final String ARG_PREGUNTA = "pregunta";
    private static final String ARG_INDEX = "index";
    private static final String ARG_TOTAL = "total";

    private Pregunta pregunta;
    private int preguntaIndex;
    private int totalPreguntas;
    private TestActivity testActivity;

    public static PreguntaFragment newInstance(Pregunta pregunta, int index, int total) {
        PreguntaFragment fragment = new PreguntaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PREGUNTA, pregunta);
        args.putInt(ARG_INDEX, index);
        args.putInt(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pregunta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            pregunta = (Pregunta) getArguments().getSerializable(ARG_PREGUNTA);
            preguntaIndex = getArguments().getInt(ARG_INDEX);
            totalPreguntas = getArguments().getInt(ARG_TOTAL);
        }

        TextView txtPregunta = view.findViewById(R.id.txtPregunta);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        Button btnContinuar = view.findViewById(R.id.btnContinuar);

        txtPregunta.setText((preguntaIndex + 1) + "/" + totalPreguntas + " " + pregunta.getTexto());

        List<String> opciones = pregunta.getOpciones();
        for (String opcion : opciones) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(opcion);
            radioButton.setTextColor(Color.WHITE);
            radioButton.setTextSize(20);
            radioButton.setPadding(0,40,0,40);
            radioButton.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            radioGroup.addView(radioButton);
        }

        btnContinuar.setOnClickListener(v -> {
            if (getActivity() instanceof TestActivity) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
                    int selectedIndex = radioGroup.indexOfChild(selectedRadioButton);
                    switch (selectedIndex) {
                        case 0:
                            break;
                        case 1:
                            TestActivity.setStressLvl(TestActivity.getStressLvl() + 10f);
                            break;
                        case 2:
                            TestActivity.setStressLvl(TestActivity.getStressLvl() + 15f);
                            break;
                        case 3:
                            TestActivity.setStressLvl(TestActivity.getStressLvl() + 20f);
                            break;
                    }
                    ((TestActivity) getActivity()).avanzarPregunta();
                } else {
                    Toast.makeText(getContext(), "Debes seleccionar una opción", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
