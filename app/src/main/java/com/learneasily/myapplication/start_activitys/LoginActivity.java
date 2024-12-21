package com.learneasily.myapplication.start_activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.learneasily.myapplication.MainActivity;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.api.ApiService;
import com.learneasily.myapplication.api.AppConfig;
import com.learneasily.myapplication.api.AuthResponse;
import com.learneasily.myapplication.api.LoginRequest;
import com.learneasily.myapplication.api.RetrofitClient;

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
        apiService = RetrofitClient.getClient(AppConfig.BASE_URL).create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            // Если пользователь уже авторизован, перенаправляем на MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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
                            AuthResponse.Student student = authResponse.getStudent();

                            saveUserData(student.getName(), student.getSurname(), student.getEmail(), student.getId());

                            startMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveUserData(String name, String surname, String email, int userId) {
        Log.d("SaveUserData", "Name: " + name + ", Surname: " + surname + ", Email: " + email);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_name", name);
        editor.putString("user_surname", surname);
        editor.putString("user_email", email);
        editor.putInt("user_id", userId);
        editor.apply();
    }
    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
