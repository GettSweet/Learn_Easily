package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.learneasily.myapplication.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_THEME = "is_dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Применяем сохранённую тему
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Кнопка для выхода
        findViewById(R.id.back_profile).setOnClickListener(v -> onBackPressed());

        // Переключатель темы
        SwitchCompat themeSwitch = findViewById(R.id.theme_switch);

        // Синхронизация состояния переключателя с текущей темой
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean(KEY_IS_DARK_THEME, false);
        themeSwitch.setChecked(isDarkTheme);

        // Слушатель переключения темы
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Сохранение нового выбора
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_IS_DARK_THEME, isChecked);
            editor.apply();

            // Применение новой темы
            setTheme(isChecked ? R.style.DarkTheme : R.style.LightTheme);

            // Обновление UI после изменения темы
            recreate();
        });
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean(KEY_IS_DARK_THEME, false);
        setTheme(isDarkTheme ? R.style.DarkTheme : R.style.LightTheme);
    }
}
