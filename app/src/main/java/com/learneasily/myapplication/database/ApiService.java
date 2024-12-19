package com.learneasily.myapplication.database;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login/") // Эндпоинт для авторизации
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("register/") // Эндпоинт для регистрации
    Call<RegisterResponse> register(@Body RegisterRequest request);
}
