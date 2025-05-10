package com.example.pauseapp.api;

import com.example.pauseapp.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApiService {
    // —— Públicos — no requieren token —————————————————————————————

    @POST("auth/register")
    Call<Void> registerUser(@Body User user);

    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("user/check/{username}")
    Call<Void> checkUsername(@Path("username") String username);

    // —— Protegidos — necesitan Authorization: "Bearer <token>" ————————

    @PUT("user/{id}/stress-level")
    Call<Void> actualizarNivelEstres(
            @Path("id") int userId,
            @Query("stressLevel") float stressLevel,
            @Header("Authorization") String token
    );

    @GET("user/{id}/get-stress")
    Call<StressLevelResponse> getStressLvl(
            @Path("id") int userId,
            @Header("Authorization") String token
    );

    @GET("user/me")
    Call<UserResponse> getUser(@Header("Authorization") String token);

    @GET("user")
    Call<List<UserResponse>> getAllUsers(
            @Header("Authorization") String token
    );


    @GET("activity")
    Call<List<ActivityResponse>> getActivities(
            @Header("Authorization") String token,
            @Query("filter") String filter,
            @Query("search") String search
    );

    @GET("activity/{id}")
    Call<ActivityResponse> getActivityById(
            @Path("id") Long activityId,
            @Header("Authorization") String token
    );

    @GET("alert")
    Call<List<Alert>> getAllAlerts(@Header("Authorization") String token);

    @POST("alert")
    Call<AlertResponse> createAlert(
            @Body AlertCreateRequest request,
            @Header("Authorization") String token
    );

    @DELETE("user/{id}")
    Call<Void> deleteUser(
            @Path("id") int id,
            @Header("Authorization") String token
    );

    // —— Relaciones entre usuarios ————————————————————————————————

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

    @PATCH("user/relations/{id}")
    Call<UserRelation> updateUserRelation(
            @Path("id") Long relationId,
            @Body UserRelationUpdateRequest request,
            @Header("Authorization") String token
    );

    /** Borra una relación pendiente (opcional) */
    @DELETE("user/relations/{id}")
    Call<Void> deleteUserRelation(
            @Path("id") Long relationId,
            @Header("Authorization") String token
    );

    /** Devuelve sólo los amigos ya aceptados */
    @GET("user/relations/{id}/friends")
    Call<List<User>> getFriends(
            @Path("id") Long userId,
            @Header("Authorization") String token
    );

    /** Busca dentro de los amigos ya aceptados */
    @GET("user/relations/{id}/friends/search")
    Call<List<User>> searchFriends(
            @Path("id") Long userId,
            @Query("query") String q,
            @Header("Authorization") String token
    );

    // —— ActivityRecord ——————————————————————————————————————————

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
}
