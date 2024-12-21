package com.learneasily.myapplication.api;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import okhttp3.RequestBody;


public interface ApiService {

    @GET("id/{id}")
    Call<UserResponse> getUserDetails(@Path("id") int id);
    @POST("login/") // Эндпоинт для авторизации
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("register/") // Эндпоинт для регистрации
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @GET("articles/")
    Call<List<ArticleResponse>> getArticles();

    @Multipart
    @POST("/upload-avatar/")
    Call<Void> uploadAvatar(
            @Part("id") RequestBody studentId,
            @Part MultipartBody.Part avatar
    );
}