package com.learneasily.myapplication.bottom_nav.articles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.learneasily.myapplication.R;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton back_news = findViewById(R.id.back_news);
        back_news.setOnClickListener(v -> onBackPressed());

        ZoomableImageView zoomableImageView = findViewById(R.id.zoomable_image);

        // Загрузка изображения (замените путь или источник)
        String imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this).load(imageUrl).into(zoomableImageView);
    }
}