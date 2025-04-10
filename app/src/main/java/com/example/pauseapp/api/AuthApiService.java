package com.example.pauseapp.api;

import com.example.pauseapp.model.*;
import com.example.pauseapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("/register") // Ajusta según tu endpoint en Spring Boot
    Call<Void> registerUser(@Body User user);

    @POST("/login") // Ruta de login en tu backend Spring Boot
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    //Todas las llamadas menos register y login tendrán que pasar un token

    @PUT("users/{id}/stress-level")
    Call<Void> actualizarNivelEstres(
            @Path("id") int userId,
            @Query("stressLevel") int stressLevel,
            @Header("Authorization") String token);

    @GET("users/{id}/get-stress")
    Call<StressLevelResponse> getStressLvl(@Path("id") int userId,@Header("Authorization") String token);

    @GET("users/me")
    Call<UserResponse> getUser(@Header("Authorization") String token);

    @GET("activities")
    Call<List<ActivityResponse>> getActivities(
            @Header("Authorization") String token,
            @Query("filter") String filter,
            @Query("search") String search
    );
    @GET("alert")
    Call<List<Alert>> getAllAlerts();

    @DELETE("/user/{id}")
    Call<Void> deleteUser(@Path("id") int id, @Header("Authorization") String token);}
