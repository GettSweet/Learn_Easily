package com.learneasily.myapplication.api;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private int id; // Добавляем поле ID пользователя

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id; // Геттер для ID
    }

    public void setId(int id) {
        this.id = id; // Сеттер для ID
    }
}
