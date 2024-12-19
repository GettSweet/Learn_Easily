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

import androidx.appcompat.app.AppCompatActivity;

import com.learneasily.myapplication.MainActivity;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.database.ApiService;
import com.learneasily.myapplication.database.AuthResponse;
import com.learneasily.myapplication.database.LoginRequest;
import com.learneasily.myapplication.database.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login, password_login;
    private Button btn_login;
    private ImageButton showPasswordButton;
    private TextView register_txt;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.btn_login);
        register_txt = findViewById(R.id.register_txt);
        showPasswordButton = findViewById(R.id.show_password_button);

        // Инициализация API
        apiService = RetrofitClient.getClient("http://192.168.0.10:8000/").create(ApiService.class);

        showPasswordButton.setOnClickListener(v -> {
            if (password_login.getTransformationMethod() == null) {
                password_login.setTransformationMethod(new PasswordTransformationMethod());
                showPasswordButton.setImageResource(R.drawable.invisible);
            } else {
                password_login.setTransformationMethod(null);
                showPasswordButton.setImageResource(R.drawable.visible);
            }
            password_login.setSelection(password_login.length());
        });

        register_txt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btn_login.setOnClickListener(v -> {
            String email = email_login.getText().toString();
            String password = password_login.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Не указаны почта или пароль", Toast.LENGTH_LONG).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(LoginActivity.this, "Некорректный формат почты", Toast.LENGTH_LONG).show();
            } else {
                LoginRequest request = new LoginRequest(email, password);
                apiService.login(request).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse authResponse = response.body();
                            if (authResponse.isSuccess()) {
                                saveToken(authResponse.getToken());
                                Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_LONG).show();
                                startMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace(); // Вывод ошибки в логах
                    }
                });
            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveToken(String token) {
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
