package com.example.pauseapp.api;

import com.example.pauseapp.model.LoginRequest;
import com.example.pauseapp.model.LoginResponse;
import com.example.pauseapp.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("/api/auth/register") // Ajusta según tu endpoint en Spring Boot
    Call<Void> registerUser(@Body User user);

    @POST("/api/auth/login") // Ruta de login en tu backend Spring Boot
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @PUT("users/{id}/stress-level")
    Call<Void> actualizarNivelEstres(@Path("id") int userId, @Query("stressLevel") int stressLevel);
}
