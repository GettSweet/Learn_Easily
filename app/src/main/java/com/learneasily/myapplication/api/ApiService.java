package com.learneasily.myapplication.api;
import com.squareup.okhttp.RequestBody;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("login/") // Эндпоинт для авторизации
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("register/") // Эндпоинт для регистрации
    Call<RegisterResponse> register(@Body RegisterRequest request);

}
