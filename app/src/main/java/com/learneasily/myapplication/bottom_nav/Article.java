package com.learneasily.myapplication.bottom_nav;

public class Article {

    private String title;
    private String body;
    private int likes;
    private int views;

    public Article() {
        // Пустой конструктор для Firebase
    }

    public Article(String title, String body, int likes, int views) {
        this.title = title;
        this.body = body;
        this.likes = likes;
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
