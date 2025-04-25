package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pauseapp.*;
import com.example.pauseapp.adapters.FriendAdapter;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.model.User;
import com.example.pauseapp.model.UserRelation;
import com.example.pauseapp.model.UserRelationUpdateRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends MenuFunction {
    private EditText searchInput;
    AuthApiService authApiService;

    FriendAdapter friendAdapter;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setupNavigationNoBottom();
        searchInput = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.recyclerViewAmigos);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFriends(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        List<UserRelation> userRelations = new ArrayList<>();
        long currentUserId = getSharedPreferences("PauseAppPrefs",
                MODE_PRIVATE).getLong("user_id",0);

        /**
         * Código temporal para probar el barchart
         */

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
            startActivity(intent);
        });
    }

    private void searchFriends(String query) {
        SharedPreferences sharedPreferences = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);

        Long userId = sharedPreferences.getLong("user_id", 0);
        String token = sharedPreferences.getString("auth_token", "");

        Call<List<UserRelation>> call = authApiService.searchFriends(userId, query, "Bearer " + token);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserRelation>> call, Response<List<UserRelation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserRelation> friends = response.body();

                    if (friendAdapter == null) {
                        friendAdapter = new FriendAdapter(friends, userId, getFriendActionListener(token));
                        recyclerView.setAdapter(friendAdapter);
                    } else {
                        friendAdapter.updateData(friends);
                    }
                } else {
                    Toast.makeText(FriendsActivity.this, "No se encontraron amigos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserRelation>> call, Throwable t) {
                Toast.makeText(FriendsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FriendAdapter.OnFriendActionListener getFriendActionListener(String token) {
        return new FriendAdapter.OnFriendActionListener() {
            @Override
            public void onAccept(UserRelation relation) {
                Call<UserRelation> call = authApiService.updateUserRelation(
                        relation.getId(),
                        new UserRelationUpdateRequest("ACCEPTED"),
                        "Bearer " + token
                );
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserRelation> call, Response<UserRelation> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FriendsActivity.this, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                            searchFriends(""); // refrescar
                        }
                    }
                    @Override
                    public void onFailure(Call<UserRelation> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this, "Error al aceptar solicitud", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDeny(UserRelation relation) {
                Call<UserRelation> call = authApiService.updateUserRelation(
                        relation.getId(),
                        new UserRelationUpdateRequest("REJECTED"),
                        "Bearer " + token
                );
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserRelation> call, Response<UserRelation> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FriendsActivity.this, "Solicitud denegada", Toast.LENGTH_SHORT).show();
                            searchFriends(""); // refrescar
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRelation> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this, "Error al denegar solicitud", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onOpenProfile(UserRelation relation) {
                Toast.makeText(FriendsActivity.this, "Abrir perfil de " + obtenerNombreDelOtroUsuario(relation), Toast.LENGTH_SHORT).show();
                // TODO: Abrir actividad de perfil
                Intent intent = new Intent (FriendsActivity.this,FriendProfileActivity.class);
                startActivity(intent);
            }

            @Override
            public void onChat(UserRelation relation) {

            }
        };
    }


    private String obtenerNombreDelOtroUsuario(UserRelation relation) {
        Long userId = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).getLong("user_id", 0);
        if (relation.getSender().getId() == userId) {
            return relation.getReceiver().getUsername();
        } else {
            return relation.getSender().getUsername();
        }
    }


}