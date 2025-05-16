package com.example.pauseapp.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pauseapp.R;
import com.example.pauseapp.activities.ShopActivity;
import com.example.pauseapp.model.ActivityResponse;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PremiumDialogFragment extends DialogFragment {
    private static final String ARG_THUMB_URL = "thumb_url";

    /**
     * Crea una nueva instancia del diálogo con la URL de la miniatura.
     */
    public static PremiumDialogFragment newInstance(String thumbnailUrl) {
        PremiumDialogFragment fragment = new PremiumDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_THUMB_URL, thumbnailUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Usamos un tema translucido para fullscreen con overlay
        Dialog dlg = new Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar);
        dlg.setContentView(R.layout.dialog_premium_promt);
        dlg.setCancelable(true);

        ImageView ivBg = dlg.findViewById(R.id.ivDialogBg);
        ImageButton btnClose = dlg.findViewById(R.id.btnCloseDialog);
        Button btnBuy = dlg.findViewById(R.id.btnBuySubscription);

        // Carga la miniatura con Glide y aplica blur
        String url = requireArguments().getString(ARG_THUMB_URL);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                .into(ivBg);

        btnClose.setOnClickListener(v -> dismiss());

        btnBuy.setOnClickListener(v -> {
            // Lanza la pantalla de suscripción
            startActivity(new Intent(requireContext(), ShopActivity.class));
            dismiss();
        });

        return dlg;
    }
}

