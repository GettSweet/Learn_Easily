package com.learneasily.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;

    @SerializedName("user_id") // Аннотация для поля id
    private int id;

    @SerializedName("student_id")
    private int student_id;

    @SerializedName("student")
    private Student student;

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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public static class Student {
        private int id;
        private String name;
        private String surname;
        private String email;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
