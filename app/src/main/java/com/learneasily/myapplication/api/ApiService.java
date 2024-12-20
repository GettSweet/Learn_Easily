package com.learneasily.myapplication.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @GET("user/{id}")
    Call<UserResponse> getUserDetails(@Path("id") int id);
    @POST("login/") // Эндпоинт для авторизации
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("register/") // Эндпоинт для регистрации
    Call<RegisterResponse> register(@Body RegisterRequest request);



    @Multipart
    @POST("user/{id}/avatar")
    Call<Void> uploadAvatar(@Path("id") int userId, @Part MultipartBody.Part avatar);

}