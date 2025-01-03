package com.learneasily.myapplication.bottom_nav.articles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.learneasily.myapplication.R;
import com.learneasily.myapplication.adapter.ImagePagerAdapter;

import java.util.List;

public class FullscreenImageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        viewPager = findViewById(R.id.view_pager);

        // Получение данных из Intent
        List<String> imageUrls = getIntent().getStringArrayListExtra("images");
        int currentIndex = getIntent().getIntExtra("current_index", 0);

        if (imageUrls == null || imageUrls.isEmpty()) {
            Toast.makeText(this, "Нет изображений для отображения", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Установка адаптера
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex, false);

        // Настройка детектора жестов
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                    finish(); // Выход из полноэкранного режима при вертикальном свайпе
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }
                return false;
            }
        });

        // Перехват касаний для обработки жестов
        viewPager.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
