package com.learneasily.myapplication.api;

public class UserResponse {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String group;
    private String avatar_url;

    private boolean teacher;

    public boolean isTeacher() {
        return teacher;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

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
