package com.example.pauseapp.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.pauseapp.R;

public class PrivacyDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_privacy, container, false);

        Button consentirButton = view.findViewById(R.id.consentirButton);
        Button noConsentirButton = view.findViewById(R.id.noConsentirButton);

        consentirButton.setOnClickListener(v ->{
            Toast.makeText(requireContext(), "Gracias por permitirnos guardar tu información",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        });
        noConsentirButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Entendemos tu decisión, no guardaremos tus datos",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
