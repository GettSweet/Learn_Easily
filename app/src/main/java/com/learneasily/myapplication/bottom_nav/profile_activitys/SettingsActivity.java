package com.learneasily.myapplication.bottom_nav.profile_activitys;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

import com.learneasily.myapplication.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Кнопка для выхода из настроек
        ImageButton backButton = findViewById(R.id.back_profile);
        backButton.setOnClickListener(v -> onBackPressed());

    }
}
