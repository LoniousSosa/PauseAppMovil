// --- FriendsActivity.java ---
package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pauseapp.R;
import com.example.pauseapp.adapters.FriendAdapter;
import com.example.pauseapp.adapters.SuggestionAdapter;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.UserRelation;
import com.example.pauseapp.model.UserRelationCreationRequest;
import com.example.pauseapp.model.UserRelationUpdateRequest;
import com.example.pauseapp.model.UserResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends MenuFunction {
    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_TOKEN = "auth_token";

    private long me;
    private String token;

    private EditText    searchInput;
    private RecyclerView rvRequests, rvFriends, rvAllUsers;

    private FriendAdapter      requestsAdapter;
    private FriendAdapter      friendsAdapter;
    private SuggestionAdapter  suggestionsAdapter;

    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setupNavigationNoBottom();

        searchInput = findViewById(R.id.searchBar);
        rvRequests = findViewById(R.id.recyclerViewSolucitudes);
        rvFriends = findViewById(R.id.recyclerViewAmigos);
        rvAllUsers = findViewById(R.id.recyclerViewAllUsers);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvAllUsers.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        me = prefs.getLong("user_id", 0L);
        token = prefs.getString(KEY_TOKEN, "");

        requestsAdapter = new FriendAdapter(
                new ArrayList<>(),
                me,
                getRequestListener(token),
                R.layout.item_friend_add
        );
        rvRequests.setAdapter(requestsAdapter);

        friendsAdapter = new FriendAdapter(
                new ArrayList<>(),
                me,
                getFriendListener(token),
                R.layout.item_added_friend
        );
        rvFriends.setAdapter(friendsAdapter);

        suggestionsAdapter = new SuggestionAdapter(
                new ArrayList<>(),
                me,
                getSuggestionListener(token)
        );
        rvAllUsers.setAdapter(suggestionsAdapter);

        fetchReceivedRelations("");

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchReceivedRelations(s.toString());
            }
        });
    }

    private void fetchReceivedRelations(String filter) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long   me    = prefs.getLong("user_id", 0L);
        String token = prefs.getString(KEY_TOKEN, "");

        authApiService.getReceivedRelations(me, "Bearer " + token)
                .enqueue(new Callback<List<UserRelation>>() {
                    @Override public void onResponse(Call<List<UserRelation>> call,
                                                     Response<List<UserRelation>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) {
                            Toast.makeText(FriendsActivity.this,
                                    "No se encontraron solicitudes", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<UserRelation> all = resp.body();

                        // 1) Desduplicar PENDIENTES (status != "ACCEPTED") por sender.id
                        Map<Long, UserRelation> pendingMap = new LinkedHashMap<>();
                        for (UserRelation r : all) {
                            String status = r.getStatus();
                            if (status == null || !status.equalsIgnoreCase("ACCEPTED")) {
                                long senderId = r.getSender().getId();
                                pendingMap.putIfAbsent(senderId, r);
                            }
                        }
                        List<UserRelation> pending = new ArrayList<>(pendingMap.values());

                        // 2) Lista de ACEPTADAS (status == "ACCEPTED")
                        List<UserRelation> accepted = new ArrayList<>();
                        for (UserRelation r : all) {
                            if ("ACCEPTED".equalsIgnoreCase(r.getStatus())) {
                                accepted.add(r);
                            }
                        }

                        // 3) Actualizar adapters
                        if (requestsAdapter == null) {
                            requestsAdapter = new FriendAdapter(
                                    pending, me, getRequestListener(token), R.layout.item_friend_add
                            );
                            rvRequests.setAdapter(requestsAdapter);
                        } else {
                            requestsAdapter.updateData(pending);
                        }

                        if (friendsAdapter == null) {
                            friendsAdapter = new FriendAdapter(
                                    accepted, me, getFriendListener(token), R.layout.item_added_friend
                            );
                            rvFriends.setAdapter(friendsAdapter);
                        } else {
                            friendsAdapter.updateData(accepted);
                        }

                        // 4) Controlar visibilidad según tengan datos o no
                        rvRequests.setVisibility(pending.isEmpty()  ? View.GONE : View.VISIBLE);
                        rvFriends .setVisibility(accepted.isEmpty() ? View.GONE : View.VISIBLE);

                        // 5) Cargar sugerencias (tu lógica actual)
                        fetchSuggestions(pending, accepted);
                    }

                    @Override public void onFailure(Call<List<UserRelation>> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchSuggestions(
            List<UserRelation> pending,
            List<UserRelation> accepted
    ) {
        // 1) obtener todos los usuarios
        authApiService.getAllUsers("Bearer " + token)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(
                            Call<List<UserResponse>> call,
                            Response<List<UserResponse>> resp
                    ) {
                        if (!resp.isSuccessful() || resp.body() == null) return;
                        List<UserResponse> all = resp.body();

                        // 2) excluir “me”, remitentes y receptores de las relaciones
                        Set<Long> exclude = new HashSet<>();
                        exclude.add(me);
                        for (UserRelation r : pending) {
                            exclude.add(r.getSender().getId());
                            exclude.add(r.getReceiver().getId());
                        }
                        for (UserRelation r : accepted) {
                            exclude.add(r.getSender().getId());
                            exclude.add(r.getReceiver().getId());
                        }

                        List<UserResponse> suggestions = new ArrayList<>();
                        for (UserResponse u : all) {
                            if (!exclude.contains(u.getId())) suggestions.add(u);
                        }

                        suggestionsAdapter.updateData(suggestions);
                        rvAllUsers.setVisibility(
                                suggestions.isEmpty() ? RecyclerView.GONE : RecyclerView.VISIBLE
                        );
                    }

                    @Override
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        // no petamos si falla
                    }
                });
    }

    // escucha para “solicitudes”
    private FriendAdapter.OnFriendActionListener getRequestListener(String token) {
        return new FriendAdapter.OnFriendActionListener() {
            @Override public void onAccept(UserRelation r) {
                authApiService.updateUserRelation(
                        r.getId(),
                        new UserRelationUpdateRequest("ACCEPTED"),
                        "Bearer " + token
                ).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserRelation> c, Response<UserRelation> r2) {
                        if (r2.isSuccessful()) fetchReceivedRelations("");
                    }

                    @Override
                    public void onFailure(Call<UserRelation> c, Throwable t) {
                    }
                });
            }
            @Override public void onDeny(UserRelation r) {
                authApiService.updateUserRelation(
                        r.getId(),
                        new UserRelationUpdateRequest("REJECTED"),
                        "Bearer " + token
                ).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserRelation> c, Response<UserRelation> r2) {
                        if (r2.isSuccessful()) fetchReceivedRelations("");
                    }

                    @Override
                    public void onFailure(Call<UserRelation> c, Throwable t) {
                    }
                });
            }
            @Override public void onOpenProfile(UserRelation r) {
                long otherId = (r.getSender().getId() == me)
                        ? r.getReceiver().getId()
                        : r.getSender().getId();
                Intent i = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                i.putExtra("other_user_id", otherId);
                startActivity(i);
            }
            @Override public void onChat(UserRelation r) { /* … */ }
        };
    }

    // escucha para “amigos aceptados”
    private FriendAdapter.OnFriendActionListener getFriendListener(String token) {
        return new FriendAdapter.OnFriendActionListener() {
            @Override public void onAccept(UserRelation r) { }
            @Override public void onDeny(UserRelation r)   { }
            @Override public void onOpenProfile(UserRelation r) {
                long otherId = (r.getSender().getId() == me)
                        ? r.getReceiver().getId()
                        : r.getSender().getId();
                Intent i = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                i.putExtra("other_user_id", otherId);
                startActivity(i);
            }
            @Override public void onChat(UserRelation r) { /* … */ }
        };
    }

    // escucha para “sugerencias” (usuarios)
    private SuggestionAdapter.OnSuggestionClickListener getSuggestionListener(String token) {
        long me = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getLong("user_id", 0L);

        return new SuggestionAdapter.OnSuggestionClickListener() {
            @Override
            public void onAdd(UserResponse user) {
                // ahora sí user.getId() existe en UserResponse
                UserRelationCreationRequest req =
                        new UserRelationCreationRequest(me, user.getId());
                authApiService
                        .createUserRelation(req, "Bearer " + token)
                        .enqueue(new Callback<UserRelation>() {
                            @Override public void onResponse(Call<UserRelation> c, Response<UserRelation> r) {
                                if (r.isSuccessful()) {
                                    fetchReceivedRelations("");
                                }
                            }
                            @Override public void onFailure(Call<UserRelation> c, Throwable t) {
                                Toast.makeText(FriendsActivity.this,
                                        "Error al enviar solicitud",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onViewProfile(UserResponse user) {
                // Aquí abres el perfil del usuario sugerido
                Intent i = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                i.putExtra("other_user_id", user.getId());
                startActivity(i);
            }
        };
    }

}