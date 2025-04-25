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
    @GET("activities/{id}")
    Call<ActivityResponse> getActivityById(
            @Path("id") Long activityId,
            @Header("Authorization") String token
    );

    @GET("alert")
    Call<List<Alert>> getAllAlerts();

    @DELETE("/user/{id}")
    Call<Void> deleteUser(@Path("id") int id, @Header("Authorization") String token);

    @GET("user/relations/friends/{userId}/search")
    Call<List<UserRelation>> searchFriends(
            @Path("userId") Long userId,
            @Query("query") String query,
            @Header("Authorization") String token
    );

    @GET("user/relations/sent/{id}")
    Call<List<UserRelation>> getSentRelations(
            @Path("id") Long userId,
            @Header("Authorization") String token
    );
    @GET("user/relations/received/{id}")
    Call<List<UserRelation>> getReceivedRelations(
            @Path("id") Long userId,
            @Header("Authorization") String token
    );

    @POST("user/relations")
    Call<UserRelation> createUserRelation(
            @Body UserRelationCreationRequest request,
            @Header("Authorization") String token
    );

    @retrofit2.http.PATCH("user/relations/{id}")
    Call<UserRelation> updateUserRelation(
            @Path("id") Long relationId,
            @Body UserRelationUpdateRequest request,
            @Header("Authorization") String token
    );
    // Comprueba nombre único
    @GET("users/check/{username}")
    Call<Void> checkUsername(
            @Path("username") String username
    );

    // Búsqueda más amplia (opcional)
    @GET("users/search")
    Call<List<UserResponse>> searchUsers(
            @Query("q") String query,
            @Header("Authorization") String token
    );

    @POST("user/{userId}/record")
    Call<ActivityRecordResponse> createUserActivityRecord(
                    @Path("userId") Long userId,
                    @Body ActivityRecordCreateRequest request,
                    @Header("Authorization") String token
    );

    @GET("user/{userId}/record")
    Call<List<ActivityRecordResponse>> getUserActivityRecords(
            @Path("userId") Long userId,
            @Header("Authorization") String token
    );
    @POST("alert")
    Call<AlertResponse> createAlert(
            @Body AlertCreateRequest request,
            @Header("Authorization") String token
    );
}


