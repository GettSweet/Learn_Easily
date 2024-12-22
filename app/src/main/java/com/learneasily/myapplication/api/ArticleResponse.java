package com.learneasily.myapplication.api;

import java.util.List;

public class ArticleResponse {
    private int id;
    private String title;
    private String content;
    private String author_name;
    private String author_surname;
    private String created_at;
    private List<ArticleImage> images;
    private String authorAvatar;
    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public static class ArticleImage {
        private int id;
        private String image;

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return author_name;
    }

    public String getAuthorSurname() {
        return author_surname;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public List<ArticleImage> getImages() {
        return images;
    }
}
