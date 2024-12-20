package com.learneasily.myapplication.start_activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.learneasily.myapplication.MainActivity;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.api.ApiService;
import com.learneasily.myapplication.api.AppConfig;
import com.learneasily.myapplication.api.RegisterRequest;
import com.learneasily.myapplication.api.RegisterResponse;
import com.learneasily.myapplication.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_register, password_register, name_register, surname_register;
    private Button btn_register;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton backButton = findViewById(R.id.backLogin);
        backButton.setOnClickListener(v -> onBackPressed());

        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        name_register = findViewById(R.id.name_register);
        surname_register = findViewById(R.id.surname_register);
        btn_register = findViewById(R.id.btn_register);

        // Инициализация API
        apiService = RetrofitClient.getClient(AppConfig.BASE_URL).create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            // Если пользователь уже авторизован, перенаправляем на MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Прерываем выполнение метода
        }

        btn_register.setOnClickListener(v -> {
            String email = email_register.getText().toString();
            String password = password_register.getText().toString();
            String name = name_register.getText().toString();
            String surname = surname_register.getText().toString();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Не все поля заполнены", Toast.LENGTH_LONG).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(RegisterActivity.this, "Некорректный формат почты", Toast.LENGTH_LONG).show();
            } else {
                RegisterRequest request = new RegisterRequest(email, password, name, surname);
                apiService.register(request).enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            startLoginActivity();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Ошибка сервера", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        t.printStackTrace(); // Вывод ошибки в логах
                    }
                });
            }
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
