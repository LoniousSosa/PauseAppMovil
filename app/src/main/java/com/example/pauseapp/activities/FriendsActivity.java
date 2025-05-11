package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pauseapp.R;
import com.example.pauseapp.adapters.AcceptedFriendAdapter;
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
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends MenuFunction {
    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_TOKEN = "auth_token";

    private long me;
    private String token;
    private String lastFilter = "";

    private EditText searchInput;
    private RecyclerView rvRequests, rvFriends, rvAllUsers;

    private FriendAdapter requestsAdapter;
    private AcceptedFriendAdapter acceptedAdapter;
    private SuggestionAdapter suggestionsAdapter;

    private final List<UserRelation> pendingRelations = new ArrayList<>();
    private final List<UserResponse> acceptedFriends = new ArrayList<>();

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

        // Adapter para solicitudes pendientes
        requestsAdapter = new FriendAdapter(
                new ArrayList<>(), me, getRequestListener(),
                R.layout.item_friend_add
        );
        rvRequests.setAdapter(requestsAdapter);

        // Adapter para amigos aceptados
        acceptedAdapter = new AcceptedFriendAdapter(
                new ArrayList<>(),
                new AcceptedFriendAdapter.OnFriendClickListener() {
                    @Override
                    public void onOpenProfile(UserResponse u) {
                        Intent i = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                        i.putExtra("other_user_id", u.getId());
                        startActivity(i);
                    }

                    @Override
                    public void onChat(UserResponse u) {
                        // Implementar chat si procede
                    }
                }
        );
        rvFriends.setAdapter(acceptedAdapter);

        // Adapter para sugerencias
        suggestionsAdapter = new SuggestionAdapter(
                new ArrayList<>(), me, getSuggestionListener()
        );
        rvAllUsers.setAdapter(suggestionsAdapter);

        // Listener de búsqueda
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastFilter = s.toString().trim();
                refreshAll(lastFilter);
            }
        });

        // Carga inicial
        refreshAll(lastFilter);
    }

    private void refreshAll(String filter) {
        fetchReceivedRelations(filter);
        fetchAcceptedFriends(filter);
    }

    private void fetchReceivedRelations(String filter) {
        authApiService.getReceivedRelations(me, "Bearer " + token)
                .enqueue(new Callback<List<UserRelation>>() {
                    @Override
                    public void onResponse(Call<List<UserRelation>> call, Response<List<UserRelation>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) {
                            Toast.makeText(FriendsActivity.this,
                                    "No se encontraron solicitudes", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<UserRelation> all = resp.body();
                        pendingRelations.clear();
                        for (UserRelation r : all) {
                            Boolean status = r.getStatus();
                            if (status == null || !status) {
                                String other = getOtherName(r);
                                if (filter.isEmpty() || other.toLowerCase().contains(filter.toLowerCase())) {
                                    pendingRelations.add(r);
                                }
                            }
                        }
                        requestsAdapter.updateData(pendingRelations);
                        rvRequests.setVisibility(pendingRelations.isEmpty() ? View.GONE : View.VISIBLE);
                        // Actualiza sugerencias tras cambiar pendientes
                        fetchSuggestions();
                    }

                    @Override
                    public void onFailure(Call<List<UserRelation>> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this,
                                "Error al cargar solicitudes: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FriendsActivity", "fetchReceivedRelations failure", t);
                    }
                });
    }

    private void fetchAcceptedFriends(String filter) {
        authApiService.getFriends(me, "Bearer " + token)
                .enqueue(new Callback<List<UserResponse>>() {
                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) return;
                        List<UserResponse> all = resp.body();
                        acceptedFriends.clear();
                        for (UserResponse u : all) {
                            if (filter.isEmpty() || u.getUsername().toLowerCase().contains(filter.toLowerCase())) {
                                acceptedFriends.add(u);
                            }
                        }
                        acceptedAdapter.updateData(acceptedFriends);
                        rvFriends.setVisibility(acceptedFriends.isEmpty() ? View.GONE : View.VISIBLE);
                        // Actualiza sugerencias tras cambiar aceptados
                        fetchSuggestions();
                    }

                    @Override
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        Log.e("FriendsActivity", "fetchAcceptedFriends failure", t);
                    }
                });
    }

    private void fetchSuggestions() {
        authApiService.getAllUsers("Bearer " + token)
                .enqueue(new Callback<List<UserResponse>>() {
                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) return;
                        List<UserResponse> all = resp.body();
                        Set<Long> exclude = new HashSet<>();
                        exclude.add(me);
                        // Excluimos pendientes
                        for (UserRelation r : pendingRelations) {
                            exclude.add(r.getSender().getId());
                            exclude.add(r.getReceiver().getId());
                        }
                        // Excluimos aceptados
                        for (UserResponse u : acceptedFriends) {
                            exclude.add(u.getId());
                        }
                        List<UserResponse> suggestions = new ArrayList<>();
                        for (UserResponse u : all) {
                            if (!exclude.contains(u.getId())) {
                                suggestions.add(u);
                            }
                        }
                        suggestionsAdapter.updateData(suggestions);
                        rvAllUsers.setVisibility(suggestions.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        Log.e("FriendsActivity", "fetchSuggestions failure", t);
                    }
                });
    }

    private String getOtherName(UserRelation r) {
        return r.getSender().getId() == me
                ? r.getReceiver().getUsername()
                : r.getSender().getUsername();
    }

    private FriendAdapter.OnFriendActionListener getRequestListener() {
        return new FriendAdapter.OnFriendActionListener() {
            @Override
            public void onAccept(UserRelation r) {
                authApiService.updateUserRelation(
                        r.getId(), new UserRelationUpdateRequest(true),
                        "Bearer " + token
                ).enqueue(new Callback<UserRelation>() {
                    @Override
                    public void onResponse(Call<UserRelation> call, Response<UserRelation> resp) {
                        if (resp.isSuccessful()) {
                            refreshAll(lastFilter);
                        } else {
                            Toast.makeText(FriendsActivity.this,
                                    "No autorizado para aceptar (403)", Toast.LENGTH_LONG).show();
                            Log.e("FriendsActivity", "accept 403 for " + r.getId());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRelation> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this,
                                "Error al aceptar: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FriendsActivity", "onAccept failure", t);
                    }
                });
            }

            @Override
            public void onDeny(UserRelation r) {
                authApiService.deleteUserRelation(
                        r.getId(), "Bearer " + token
                ).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> resp) {
                        if (resp.isSuccessful()) {
                            refreshAll(lastFilter);
                        } else {
                            Toast.makeText(FriendsActivity.this,
                                    "Error al rechazar: " + resp.code(), Toast.LENGTH_LONG).show();
                            Log.e("FriendsActivity", "deny code=" + resp.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this,
                                "Error al rechazar: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FriendsActivity", "onDeny failure", t);
                    }
                });
            }

            @Override
            public void onOpenProfile(UserRelation r) {
                long otherId = r.getSender().getId() == me
                        ? r.getReceiver().getId()
                        : r.getSender().getId();
                Intent intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                intent.putExtra("other_user_id", otherId);
                startActivity(intent);
            }

            @Override public void onChat(UserRelation r) {
                // Chat no implementado aún
            }
        };
    }

    private SuggestionAdapter.OnSuggestionClickListener getSuggestionListener() {
        return new SuggestionAdapter.OnSuggestionClickListener() {
            @Override
            public void onAdd(UserResponse u) {
                authApiService.createUserRelation(
                        new UserRelationCreationRequest(me, u.getId()),
                        "Bearer " + token
                ).enqueue(new Callback<UserRelation>() {
                    @Override
                    public void onResponse(Call<UserRelation> call, Response<UserRelation> resp) {
                        if (resp.isSuccessful()) refreshAll(lastFilter);
                    }

                    @Override
                    public void onFailure(Call<UserRelation> call, Throwable t) {
                        Toast.makeText(FriendsActivity.this,
                                "Error al enviar solicitud: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("FriendsActivity", "onAdd failure", t);
                    }
                });
            }

            @Override
            public void onViewProfile(UserResponse u) {
                Intent intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                intent.putExtra("other_user_id", u.getId());
                startActivity(intent);
            }
        };
    }
}