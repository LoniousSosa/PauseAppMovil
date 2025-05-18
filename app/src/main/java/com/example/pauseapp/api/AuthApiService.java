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
    @POST("user/{id}/stress-level")
    Call<StressLevelResponse> actualizarNivelEstres(
            @Path("id") long userId,
            @Body CreateStressLevel body,
            @Header("Authorization") String token
    );

    @GET("user/me")
    Call<UserResponse> getUser(@Header("Authorization") String token);

    @GET("user/{id}")
    Call<UserResponse> getUserById(
            @Path("id") long id,
            @Header("Authorization") String token
    );


    @GET("user")
    Call<List<UserResponse>> getAllUsers(@Header("Authorization") String token);

    // —— Activities —————————————————————————————————————————————

    /** Obtener todas o filtradas por tipo(s) */
    @GET("activity")
    Call<List<ActivityResponse>> getActivitiesByType(
            @Header("Authorization") String token,
            @Query("typeIds") List<Long> typeIds
    );

    /** Buscar actividades por nombre (startsWith) */
    @GET("activity/search")
    Call<List<ActivityResponse>> getActivitiesByName(
            @Header("Authorization") String token,
            @Query("name") String name
    );

    /** Actividades recomendadas basadas en historial */
    @GET("activity/recomended")
    Call<List<ActivityResponse>> getRecommendedActivities(@Header("Authorization") String token);

    /** Actividades premium */
    @GET("activity/premium")
    Call<List<ActivityResponse>> getPremiumActivities(@Header("Authorization") String token);

    @GET("activity/{id}")
    Call<ActivityResponse> getActivityById(
            @Path("id") Long activityId,
            @Header("Authorization") String token
    );

    // en AuthApiService
    @GET("activity/types")
    Call<List<ActivityTypeResponse>> getActivityTypes(@Header("Authorization") String token);


    @GET("user/{userId}/complete-activity/{activityId}")
    Call<ActivityRecordResponse> completeActivity(
            @Path("userId") long userId,
            @Path("activityId") long activityId,
            @Header("Authorization") String token
    );

    // —— Alertas ———————————————————————————————————————————————

    @GET("alert")
    Call<List<Alert>> getAllAlerts(@Header("Authorization") String token);

    @GET("alert/{id}")
    Call<Alert> getAlertById(
            @Path("id") Long id,
            @Header("Authorization") String token
    );

    @POST("alert")
    Call<AlertResponse> createAlert(
            @Body AlertCreateRequest request,
            @Header("Authorization") String token
    );


    // —— Usuarios ———————————————————————————————————————————

    @PATCH("user/{id}")
    Call<UserResponse> patchUser(
            @Path("id") Long userId,
            @Body UserUpdateRequest request,
            @Header("Authorization") String token
    );


    @DELETE("user/{id}")
    Call<Void> deleteUser(
            @Path("id") Long id,
            @Header("Authorization") String token
    );

    // —— Relaciones entre usuarios —————————————————————————————

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

    @DELETE("user/relations/{id}")
    Call<Void> deleteUserRelation(
            @Path("id") Long relationId,
            @Header("Authorization") String token
    );

    @GET("user/relations/{id}/friends")
    Call<List<UserResponse>> getFriends(
            @Path("id") Long userId,
            @Header("Authorization") String token
    );

    @GET("user/relations/{id}/friends/search")
    Call<List<UserResponse>> searchFriends(
            @Path("id") Long userId,
            @Query("query") String q,
            @Header("Authorization") String token
    );

    // —— ActivityRecord ——————————————————————————————————————————

    @POST("user/{id}/record")
    Call<ActivityRecordResponse> createUserActivityRecord(
            @Path("id") Long userId,
            @Body ActivityRecordCreateRequest request,
            @Header("Authorization") String token
    );

    @PATCH("user/record/{id}")
    Call<ActivityRecordResponse> updateUserActivityRecord(
            @Path("id") Long recordId,
            @Body ActivityRecordCreateRequest request,
            @Header("Authorization") String token
    );

    @GET("user/{userId}/record")
    Call<List<ActivityRecordResponse>> getUserActivityRecords(
            @Path("userId") Long userId,
            @Header("Authorization") String token
    );

    //Media
    @GET("/media/{id}")
    Call<MediaResponse> getMediaById(
            @Path("id") Long mediaId,
            @Header("Authorization") String bearerToken
    );
}