package com.learneasily.myapplication.api;

public class UserResponse {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String avatar_url;

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }
}
