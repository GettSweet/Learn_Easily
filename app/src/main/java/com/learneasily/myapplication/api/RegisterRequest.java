package com.learneasily.myapplication.api;

public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String surname;

    public RegisterRequest(String email, String password, String name, String surname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}
