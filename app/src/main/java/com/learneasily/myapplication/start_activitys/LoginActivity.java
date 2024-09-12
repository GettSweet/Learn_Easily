package com.learneasily.myapplication.start_activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.learneasily.myapplication.MainActivity;
import com.learneasily.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login;
    private EditText password_login;
    private Button btn_login;
    private TextView register_txt;
    private ImageButton showPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.btn_login);
        register_txt = findViewById(R.id.register_txt);
        showPasswordButton = findViewById(R.id.show_password_button);

        mAuth = FirebaseAuth.getInstance();

        // Проверка состояния авторизации при запуске приложения
        if (isUserLoggedIn()) {
            startMainActivity();
        }

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // При каждом нажатии кнопки меняем тип ввода
                if (password_login.getTransformationMethod() == null) {
                    // Если тип ввода null, то устанавливаем тип ввода для пароля
                    password_login.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordButton.setImageResource(R.drawable.invisible);
                } else {
                    // Иначе, если тип ввода установлен для пароля, то убираем его
                    password_login.setTransformationMethod(null);
                    showPasswordButton.setImageResource(R.drawable.visible);
                }

                // Перемещаем курсор в конец текста
                password_login.setSelection(password_login.length());
            }
        });

        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_login.getText().toString();
                String password = password_login.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Не указаны почта или пароль", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Некорректный формат почты", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Сохраняем состояние авторизации
                                        saveUserState(true);
                                        startMainActivity();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Неверная почта или пароль", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    // Метод для проверки правильности формата электронной почты
    private boolean isValidEmail(String email) {
        // Используем класс Patterns для проверки формата почты
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    // Метод для сохранения состояния авторизации
    private void saveUserState(boolean isLoggedIn) {
        SharedPreferences preferences = getSharedPreferences("user_state", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", isLoggedIn);
        editor.apply();
    }

    // Метод для проверки состояния авторизации
    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("user_state", MODE_PRIVATE);
        return preferences.getBoolean("is_logged_in", false);
    }

    // Метод для запуска MainActivity
    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Закрываем текущую активити, чтобы пользователь не мог вернуться на экран авторизации
    }
}